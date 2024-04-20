package pl.dkaluza.userservice;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.awaitility.Awaitility;
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

import java.time.Duration;
import java.util.Map;

import static io.restassured.RestAssured.*;


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

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();

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
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "" })
    void signIn_invalidCsrfToken_returnForbidden(String csrfToken) {
        given()
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
//        "dawid@d.c, passwd",
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

    }

    @Test
    void signIn_redirectedToSignIn_authenticateAndRedirectBack() {

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

    private Filter csrfCookieFilter(String path) {
        return new CsrfCookieFilter(path);
    }

    private static class CsrfCookieFilter implements Filter {
        private final String path;

        private CsrfCookieFilter(String path) {
            this.path = path;
        }

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            var res = given().auth().none().disableCsrf().cookies(requestSpec.getCookies()).get(path);
            var token = res.getCookie("XSRF-TOKEN");
            requestSpec.header("X-XSRF-TOKEN", token);
            return ctx.next(requestSpec, responseSpec);
        }
    }
}


