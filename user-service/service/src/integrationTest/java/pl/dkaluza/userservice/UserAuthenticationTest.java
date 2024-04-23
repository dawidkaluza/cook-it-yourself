package pl.dkaluza.userservice;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;
import pl.dkaluza.userservice.config.JdbiFacade;
import pl.dkaluza.userservice.config.JedisFacade;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

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
            .setAccept(ContentType.JSON)
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
    @CsvSource({
        "/sign-in, 123",
        "/sign-in, ''",
        "/sign-out, 123",
    })
    void anyAuthenticationRequest_invalidCsrfToken_returnUnauthorized(String endpoint, String csrfToken) {
        given()
            .filter(csrfCookieFilter("/web" + endpoint))
            .header("X-XSRF-TOKEN", csrfToken)
            .accept(ContentType.JSON)
            .formParam("username", "dawid@d.c")
            .formParam("password", "password")
        .when()
            .post(endpoint)
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
            .accept(ContentType.JSON)
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
            .accept(ContentType.JSON)
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
            .get(oauthAuthorizeUrl);

        if (oauthAuthorizeResponse.statusCode() != 302 || !oauthAuthorizeResponse.getHeader("Location").endsWith("/web/sign-in")) {
            throw new IllegalStateException("Response from authorize request is not a redirection to sign in page (status code=302 and Location=*/web/sign-in conditions not met)");
        }

        var request = given()
            .filter(csrfCookieFilter("/web/sign-in", oauthAuthorizeResponse.detailedCookies()))
            .accept(ContentType.JSON)
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
        // Given
        var firstSessionId = given()
            .filter(csrfCookieFilter("/web/sign-in"))
            .accept(ContentType.JSON)
            .formParam("username", "dawid@d.c")
            .formParam("password", "password")
            .post("/sign-in")
            .getCookie("SESSION");

        var request = given()
            .filter(csrfCookieFilter("/web/sign-in"))
            .accept(ContentType.JSON)
            .formParam("username", "dawid@d.c")
            .formParam("password", "password");

        // When
        var response = request.post("/sign-in");

        // Then
        response.then()
            .statusCode(200)
            .body("redirectUrl", notNullValue())
            .cookie("SESSION", not(blankOrNullString()));

        var secondSessionId = response.getCookie("SESSION");

        assertThat(secondSessionId)
            .isNotBlank()
            .isNotEqualTo(firstSessionId);
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
            cookiesList.addAll(res.getDetailedCookies().asList());
            cookiesList.addAll(
                cookies.asList().stream().filter(
                    cookie -> !res.getCookies().containsKey(cookie.getName())
                ).toList()
            );

            var reqCookies = new Cookies(cookiesList);
            requestSpec.cookies(reqCookies);

            var token = reqCookies.getValue("XSRF-TOKEN");
            requestSpec.header("X-XSRF-TOKEN", token);
            return ctx.next(requestSpec, responseSpec);
        }
    }
}


