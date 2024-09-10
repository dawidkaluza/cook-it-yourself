package pl.dkaluza.userservice;

import com.nimbusds.jose.JWSObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;
import pl.dkaluza.userservice.config.JdbiFacade;

import java.text.ParseException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static pl.dkaluza.userservice.RestAssuredUtils.*;

@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Oauth2Test {
    private final String redirectUrl = "http://api-gateway/login/oauth2/code/ciy";

    private JdbiFacade jdbiFacade;
    private String baseUrl;
    private Response signUpResponse;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();

        baseUrl = "http://localhost:" + port;
        RestAssured.baseURI = baseUrl;

        var handle = jdbiFacade.getHandle();
        handle.execute("DELETE FROM users");
        signUpResponse = signUp("dawid@d.c", "password", "Dawid");
    }

    @AfterEach
    void afterAll() {
        jdbiFacade.stop();
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
        var oauthAuthorizeUrl = "/oauth2/authorize?response_type=code&client_id=api-gateway&redirect_uri=http://webapp/login";

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
        "'/oauth2/authorize?response_type=code&client_id=api-gateway&redirect_uri=http://api-gateway/login/oauth2/code/ciy&scope=openid&state=123xyz', 123xyz"
    }, nullValues = "NULL")
    void authorizationRequest_authenticatedAndValidRequest_redirectWithCode(String oauthAuthorizeUrl, String expectedStateValue) {
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
            .header("Location", containsString("code="));

        if (expectedStateValue != null) {
            response.header("Location", containsString("state=" + expectedStateValue));
        }
    }

    @Test
    void tokenRequest_unauthenticatedClient_returnError() {
        var signInResponse = signIn("dawid@d.c", "password");
        var code = authorize(signInResponse);

        given()
            .accept(ContentType.JSON)
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", code)
            .param("redirect_uri", redirectUrl)
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(401);
    }

    @Test
    void tokenRequest_invalidRedirectUri_returnError() {
        var signInResponse = signIn("dawid@d.c", "password");
        var code = authorize(signInResponse);

        given()
            .accept(ContentType.JSON)
            .auth().preemptive().basic("api-gateway", "crm")
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", code)
            .param("redirect_uri", "http://webapp/login")
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(400);
    }

    @Test
    void tokenRequest_invalidCode_returnError() {
        given()
            .accept(ContentType.JSON)
            .auth().preemptive().basic("api-gateway", "crm")
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", "cooode")
            .param("redirect_uri", redirectUrl)
        .when()
            .post("/oauth2/token")
        .then()
            .statusCode(400);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'', ''",
        "'openid profile', Dawid"
    })
    void tokenRequest_authenticatedAndValidRequest_returnTokens(String scopes, String expectedNickname) throws ParseException {
        var signInResponse = signIn("dawid@d.c", "password");
        var code = authorize(signInResponse, scopes);

        var response = given()
            .accept(ContentType.JSON)
            .auth().preemptive().basic("api-gateway", "crm")
            .param("grant_type", "authorization_code")
            .param("client_id", "api-gateway")
            .param("code", code)
            .param("redirect_uri", redirectUrl)
            .post("/oauth2/token");

        response.then()
            .statusCode(200)
            .body("access_token", notNullValue())
            .body("refresh_token", notNullValue());

        var actualUserId = signUpResponse.body().jsonPath().getString("id");
        var accessToken = response.body().jsonPath().getString("access_token");
        var expectedUserId = JWSObject.parse(accessToken).getPayload().toJSONObject().get("sub");
        assertThat(actualUserId)
            .isEqualTo(expectedUserId);

        if (!expectedNickname.isBlank()) {
            var idToken = response.body().jsonPath().getString("id_token");
            var actualNickname = JWSObject.parse(idToken)
                .getPayload().toJSONObject()
                .get("nickname");

            assertThat(actualNickname)
                .isEqualTo(expectedNickname);
        }
    }
}
