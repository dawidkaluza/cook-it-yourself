package pl.dkaluza.userservice;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;
import pl.dkaluza.userservice.config.JdbiFacade;
import pl.dkaluza.userservice.config.JedisFacade;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;


/*
oauth2 auth code tests:
- given unauthorized user, when user requests for auth code, then redirect user to sign in page (RA)
- given authorized user, when user requests for auth code with invalid params, then redirect to web app with error code (RA)
- given authorized user, when user requests for auth code, then redirect to web app with auth code (RA)

sign in tests:
given user on sign in page, when user sends invalid sign in request, show errors
given user on sign in page, when user sends valid sign in request, return success and redirect to last visited page

sign in tests (RA):
given user on sign in page with CSRF token in a cookie, when user sends invalid sign in request, return errors
given user on sign in page with CSRF token in a cookie, when user sends valid sign in request, return success and redirect to last visiteg page

sign up tests:
- given user on sign up page, when user sends invalid sign up request, show errors
- given user on sign up page, when user sends valid sign up request, return success and redirect to sign in page

sign up tests (RA):
given user on sign up page with CSRF token in a cookie, when user sends invalid sign up request, return errors
given user on sign up page with CSRF token in a cookie, when user sends valid sign up request, return success and redirect to last visiteg page

requirements:
- go thru redirections
- submit forms
-

solutions:
rest assured:
form authentication - submit authentication via form before performing a request (https://github.com/rest-assured/rest-assured/wiki/Usage#form-authentication)
csrf token - send get request, get csrf token, send POST request with CSRF token (https://github.com/rest-assured/rest-assured/wiki/Usage#csrf-form-token P.S. does not support csrf token being stored in cookies, but same can be accomplished via Filters: https://github.com/rest-assured/rest-assured/wiki/Usage#filters)
issue - CORS policy, can be overriden

Summary:
Rest Assured can try to simulate opening a page and sending a request, but it does not really do that so what is being tested is only service endpoints, not web UI - which is not bad since UI automated tests are not that straightforward, hence it might be better to have UI tests in e2e tests

 */

@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAuthenticationTest {
    private JdbiFacade jdbiFacade;
    private JedisFacade jedisFacade;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();

        jedisFacade = new JedisFacade();
        jedisFacade.start();

        baseURI = "http://localhost:" + port;

        var handle = jdbiFacade.getHandle();
        handle.execute("DELETE FROM users");

        var req = new RequestSpecBuilder()
            .setContentType(ContentType.JSON)
            .setBody(Map.of(
                "email", "dawid@d.c",
                "password", "password",
                "name", "Dawid"
            ))
            .build();
        var res = given().spec(req).post("/user/sign-up");
        if (res.statusCode() != 201) {
            throw new IllegalStateException("Sign up failed.\n > Status code: " + res.statusCode() + "\n > Body: " + res.body().asPrettyString());
        }
    }

    @AfterEach
    void afterAll() {
        jdbiFacade.stop();
        jedisFacade.stop();
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "" })
    void signIn_invalidCsrfToken_returnUnauthorized(String csrfToken) {
        given()
            .filter(csrfCookieFilter("/web/sign-in"))
            .header("X-XSRF-TOKEN", csrfToken)
            .formParam("username", "dawid@d.c")
            .formParam("password", "password")
        .when()
            .post("/sign-in")
        .then()
            .statusCode(403);
    }

    @ParameterizedTest
    @CsvSource({
        "gabriel@d.c, 12345",
        "dawid@d.c, passwd",
    })
    void signIn_invalidEmailOrPassword_returnUnauthorized(String email, String password) {
        given()
            .filter(csrfCookieFilter("/web/sign-in"))
            .formParam("username", email)
            .formParam("password", password)
        .when()
            .post("/sign-in")
        .then()
            .statusCode(403);
    }

    @Test
    void signIn_unauthenticated_authenticate() {
        // Given
        var request = given()
            .filter(csrfCookieFilter("/web/sign-in"))
            .formParam("username", "dawid@d.c")
            .formParam("password", "password");

        // When
        var response = request.post("/sign-in");

        // Then
        response.then()
            .statusCode(200)
            .body("redirectUrl", notNullValue());

        var sessionId = response.getCookie("SESSION");
        assertThat(sessionId)
            .isNotBlank();

        var decodedSessionId = new String(Base64.getDecoder().decode(sessionId));
        var jedis = jedisFacade.getJedis();
        assertThat(jedis.keys("*" + decodedSessionId))
            .hasSize(1);
    }

    @Test
    void signIn_redirectedToSignIn_authenticateAndRedirectBack() {
        // Given
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=ciy-web&state=1234xyz&code_challenge=MMRGwBwWyq4DLBuYbwPHRF6HGyVnN_UAUDnQ8GVGjn8&code_challenge_method=S256";
        var oauthAuthorizeResponse = given()
            .redirects().follow(false)
            .contentType(ContentType.JSON)
            .get(oauthAuthorizeUrl);

        var request = given()
            .filter(csrfCookieFilter("/web/sign-in", oauthAuthorizeResponse.detailedCookies()))
            .formParam("username", "dawid@d.c")
            .formParam("password", "password");

        // When
        var response = request.post("/sign-in");

        // Then
        response.then()
            .statusCode(200)
            .body("redirectUrl", containsString("/oauth2/authorize"))
            .cookie("SESSION", not(blankOrNullString()));
    }

    @Test
    void signIn_alreadyAuthenticated_reauthenticate() {

    }

    @Test
    void signOut_invalidCsrfToken_returnForbidden() {

    }

    @Test
    void signOut_unauthenticated_accept() {

    }

    @Test
    void signOut_authenticated_invalidateAuthentication() {

    }

    private String base64Decode(String input) {
        return new String(Base64.getDecoder().decode(input));
    }

    private Filter csrfCookieFilter(String path) {
        return new CsrfCookieFilter(path, new Cookies());
    }

    private Filter csrfCookieFilter(String path, Cookies cookies) {
        return new CsrfCookieFilter(path, cookies);
    }

    private static class CsrfCookieFilter implements Filter {
        private final String path;
        private final Cookies cookies;

        private CsrfCookieFilter(String path, Cookies cookies) {
            this.path = path;
            this.cookies = cookies;
        }

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            var res = given().cookies(cookies).get(path);

            var cookiesList = new ArrayList<Cookie>();
            cookiesList.addAll(cookies.asList());
            cookiesList.addAll(res.getDetailedCookies().asList());
            requestSpec.cookies(new Cookies(cookiesList));

            var token = res.getCookie("XSRF-TOKEN");
            requestSpec.header("X-XSRF-TOKEN", token);
            return ctx.next(requestSpec, responseSpec);
        }
    }
}


