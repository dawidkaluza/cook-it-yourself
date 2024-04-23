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

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void authCode_unauthenticated_redirectToSignIn() {
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=ciy-web&state=1234xyz&code_challenge=MMRGwBwWyq4DLBuYbwPHRF6HGyVnN_UAUDnQ8GVGjn8&code_challenge_method=S256";
        given()
            .redirects().follow(false)
        .when()
            .get(oauthAuthorizeUrl)
        .then()
            .statusCode(302)
            .header("Location", endsWith("/web/sign-in"));
    }

    @Test
    void authCode_invalidWebClientRequest_redirectBackWithErrors() {
        // Given
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=ciy-web&state=1234xyz";

        // When
        var authCodeResponse = given()
            .redirects().follow(false)
            .get(oauthAuthorizeUrl);

        authCodeResponse.then()
            .statusCode(302)
            .header("Location", startsWith("http://webapp/sign-in"))
            .header("Location", matchesPattern(".*error=invalid_request.*"));
    }

    @Test
    void authCode_authenticated_redirectWithCode() {
        signUp("dawid@d.c", "password", "Dawid");
        var signInResponse = signIn("dawid@d.c", "password");

        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=ciy-web&state=1234xyz&code_challenge=MMRGwBwWyq4DLBuYbwPHRF6HGyVnN_UAUDnQ8GVGjn8&code_challenge_method=S256";
        given()
            .redirects().follow(false)
            .cookies(signInResponse.getDetailedCookies())
        .when()
            .get(oauthAuthorizeUrl)
        .then()
            .statusCode(302)
            .header("Location", startsWith("http://webapp/sign-in?code"));
    }
}
