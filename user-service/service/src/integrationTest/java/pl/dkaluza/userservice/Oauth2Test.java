package pl.dkaluza.userservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static pl.dkaluza.userservice.RestAssuredUtils.signIn;
import static pl.dkaluza.userservice.RestAssuredUtils.signUp;

@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Oauth2Test {
    private final String redirectUrl = "http://api-gateway/login/oauth2/code/ciy";
    private String baseUrl;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:" + port;
        RestAssured.baseURI = baseUrl;
    }

    @Test
    void authCode_unauthenticated_redirectToSignIn() {
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=api-gateway";
        given()
            .redirects().follow(false)
        .when()
            .get(oauthAuthorizeUrl)
        .then()
            .statusCode(302)
            .header("Location", equalTo(baseUrl + "/web/sign-in"));
    }

    @Test
    void authCode_invalidRequestParams_redirectBackWithErrors() {
        // Given
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=api-gateway&scope=root";

        // When
        var authCodeResponse = given()
            .redirects().follow(false)
            .get(oauthAuthorizeUrl);

        authCodeResponse.then()
            .statusCode(302)
            .header("Location", startsWith(redirectUrl))
            .header("Location", matchesPattern(".*error=invalid_scope.*"));
    }

    @Test
    void authCode_possiblyMaliciousRequest_redirectToSignIn() {
        // Given
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=api-gateway&redirect_uri=http%3A%2F%2Fwebapp%2Flogin";

        // When
        var authCodeResponse = given()
            .redirects().follow(false)
            .get(oauthAuthorizeUrl);

        authCodeResponse.then()
            .statusCode(302)
            .header("Location", equalTo(baseUrl + "/web/sign-in"));
    }

    @Test
    void authCode_authenticatedAndValidRequest_redirectWithCode() {
        signUp("dawid@d.c", "password", "Dawid");
        var signInResponse = signIn("dawid@d.c", "password");

        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=api-gateway";
        given()
            .redirects().follow(false)
            .cookies(signInResponse.getDetailedCookies())
        .when()
            .get(oauthAuthorizeUrl)
        .then()
            .statusCode(302)
            .header("Location", startsWith(redirectUrl + "?code"));
    }
}
