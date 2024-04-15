package pl.dkaluza.userservice;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.dkaluza.userservice.config.EnableTestcontainers;

import java.util.HashMap;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRestApiTest {
    @LocalServerPort
    private Integer port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void beforeEach() {
        baseURI = "http://localhost:" + port;
        //noinspection SqlWithoutWhere
        jdbcTemplate.execute("DELETE FROM users");
    }

    @Test
    void signUp_invalidRequestBodyFields_returnUnprocessableEntity() {
        var requestBody = new HashMap<String, Object>();
        requestBody.put("user", "dawid@d.c");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/user/sign-up")
        .then()
            .statusCode(422)
            .contentType(ContentType.JSON)
            .body("message", not(empty()))
            .body("timestamp", not(empty()))
            .body("fields", not(empty()))
            .body("fields.name", hasItems("email", "password", "name"));
    }

    @Test
    void signUp_invalidRequestBodyData_returnUnprocessableEntity() {
        var requestBody = new HashMap<String, Object>();
        requestBody.put("email", "dawid");
        requestBody.put("password", "124");
        requestBody.put("name", "Dawid");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/user/sign-up")
        .then()
            .statusCode(422)
            .contentType(ContentType.JSON)
            .body("fields.name", hasItems("email", "password"));
    }

    @Test
    void signUp_emailAlreadyExists_returnConflict() {
        jdbcTemplate.update("INSERT INTO users (email, encoded_password, name) VALUES (?, ?, ?)", "dawid@d.c", "123456", "Dawid");

        var requestBody = new HashMap<String, Object>();
        requestBody.put("email", "dawid@d.c");
        requestBody.put("password", "123456");
        requestBody.put("name", "Dawid");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/user/sign-up")
        .then()
            .statusCode(409)
            .contentType(ContentType.JSON)
            .body("message", not(empty()))
            .body("timestamp", not(empty()));
    }

    @Test
    void signUp_validRequest_returnCreatedUser() {
        var requestBody = new HashMap<String, Object>();
        requestBody.put("email", "dawid@d.c");
        requestBody.put("password", "123456");
        requestBody.put("name", "Dawid");

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/user/sign-up")
        .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("email", equalTo("dawid@d.c"))
            .body("name", equalTo("Dawid"));
    }
}
