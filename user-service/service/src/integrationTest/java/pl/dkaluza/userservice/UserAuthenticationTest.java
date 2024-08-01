package pl.dkaluza.userservice;

import io.restassured.http.ContentType;
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

import java.util.Base64;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.dkaluza.userservice.RestAssuredUtils.*;

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
        signUp("dawid@d.c", "password", "Dawid");
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
        signIn(email, password, false)
            .then()
            .statusCode(403);
    }

    @Test
    void signIn_unauthenticated_authenticate() {
        // Given, when
        var response = signIn("dawid@d.c", "password");

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
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=api-gateway";
        var oauthAuthorizeResponse = given()
            .redirects().follow(false)
            .get(oauthAuthorizeUrl);

        if (oauthAuthorizeResponse.statusCode() != 302 || !oauthAuthorizeResponse.getHeader("Location").endsWith("/web/sign-in")) {
            throw new IllegalStateException("Response from authorize request is not a redirection to sign in page (status code=302 and Location=*/web/sign-in conditions not met)");
        }

        // When
        var response = signIn("dawid@d.c", "password", oauthAuthorizeResponse.detailedCookies());

        // Then
        response.then()
            .statusCode(200)
            .body("redirectUrl", containsString("/oauth2/authorize"))
            .cookie("SESSION", not(blankOrNullString()));
    }

    @Test
    void signIn_alreadyAuthenticated_reauthenticate() {
        // Given
        var firstSessionId = signIn("dawid@d.c", "password").getCookie("SESSION");

        // When
        var response = signIn("dawid@d.c", "password");

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
}


