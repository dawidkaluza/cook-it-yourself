package pl.dkaluza.userservice;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;


@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRestApiTest {
    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        baseURI = "http://localhost:" + port;
    }

    @Test
    void healthCheck() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200);
    }
}
