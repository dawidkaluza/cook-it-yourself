package pl.dkaluza.userservice;

import com.nimbusds.jose.JWSObject;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;

import java.text.ParseException;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.dkaluza.userservice.RestAssuredUtils.*;

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
    void authorizationRequest_unauthenticated_redirectToSignIn() {
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
    void authorizationRequest_invalidRequestParams_redirectBackWithErrors() {
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
    void authorizationRequest_possiblyMaliciousRequest_redirectToSignIn() {
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

    @ParameterizedTest
    @CsvSource(value = {
        "'/oauth2/authorize?response_type=code&client_id=api-gateway', NULL",
        "'/oauth2/authorize?response_type=code&client_id=api-gateway&redirect_uri=http%3A%2F%2F127.0.0.1%3A8888%2Flogin%2Foauth2%2Fcode%2Fciy&scope=openid&state=123xyz', 123xyz"
    }, nullValues = "NULL")
    void authorizationRequest_authenticatedAndValidRequest_redirectWithCode(String oauthAuthorizeUrl, String expectedStateValue) {
        signUp("dawid@d.c", "password", "Dawid");
        var signInResponse = signIn("dawid@d.c", "password");

        var response = given()
            .redirects().follow(false)
            .cookies(signInResponse.getDetailedCookies())
        .when()
            .get(oauthAuthorizeUrl)
        .then();

        response
            .statusCode(302)
            .header("Location", startsWith(redirectUrl))
            .header("Location", contains("code="));

        if (expectedStateValue != null) {
            response.header("Location", contains("state=" + expectedStateValue));
        }
    }

    @Test
    void tokenRequest_unauthenticatedClient_returnError() {
        signUp("dawid@d.c", "password", "Dawid");
        var signInResponse = signIn("dawid@d.c", "password");
        var code = authorize(signInResponse);

        given()
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", code)
            .param("redirect_uri", "http%3A%2F%2F127.0.0.1%3A8888%2Flogin%2Foauth2%2Fcode%2Fciy")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(403);
    }

    @Test
    void tokenRequest_invalidRedirectUri_returnError() {
        signUp("dawid@d.c", "password", "Dawid");
        var signInResponse = signIn("dawid@d.c", "password");
        var code = authorize(signInResponse);

        given()
            .auth()
            .preemptive().basic("api-gateway", "crm")
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", code)
            .param("redirect_uri", "http%3A%2F%2Fwebapp%3A8888%2Flogin%2Foauth2%2Fcode%2Fciy")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(403);
    }

    @Test
    void tokenRequest_invalidCode_returnError() {
        given()
            .auth()
            .preemptive().basic("api-gateway", "crm")
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", "myCustomCode")
            .param("redirect_uri", "http%3A%2F%2F127.0.0.1%3A8888%2Flogin%2Foauth2%2Fcode%2Fciy")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(403);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'', NULL",
        "'openid, profile', Dawid"
    }, nullValues = "NULLL")
    void tokenRequest_authenticatedAndValidRequest_returnTokens(Set<String> scopes, String expectedNickname) throws ParseException {
        signUp("dawid@d.c", "password", "Dawid");
        var signInResponse = signIn("dawid@d.c", "password");
        var code = authorize(signInResponse, scopes);

        var response = given()
            .auth().preemptive().basic("api-gateway", "crm")
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", code)
            .param("redirect_uri", "http%3A%2F%2F127.0.0.1%3A8888%2Flogin%2Foauth2%2Fcode%2Fciy")
        .when()
            .post("/oauth2/token");

        response.then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("refresh_token", notNullValue());

        if (expectedNickname != null) {
            var idToken = response.body().jsonPath().getString("id_token");
            var actualNickname = JWSObject.parse(idToken)
                .getPayload().toJSONObject()
                .get("nickname");

            assertThat(actualNickname)
                .isEqualTo(expectedNickname);
        }
    }
}
