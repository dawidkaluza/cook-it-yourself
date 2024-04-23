package pl.dkaluza.userservice;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.userservice.config.EnableTestcontainers;
import pl.dkaluza.userservice.config.JdbiFacade;
import pl.dkaluza.userservice.config.RabbitFacade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;


@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserRestApiTest {
    private RabbitFacade rabbitFacade;
    private JdbiFacade jdbiFacade;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() throws IOException, TimeoutException {
        rabbitFacade = new RabbitFacade();
        rabbitFacade.start();

        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();
        var handle = jdbiFacade.getHandle();
        handle.execute("DELETE from users");

        baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void afterEach() throws IOException, TimeoutException {
        rabbitFacade.stop();
        jdbiFacade.stop();
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
        var handle = jdbiFacade.getHandle();
        handle.execute("INSERT INTO users (email, encoded_password, name) VALUES (?, ?, ?)", "dawid@d.c", "*(&@#*(@!BJCJAS123", "Dawid");

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
    void signUp_validRequest_returnCreatedUser() throws IOException {
        // Given
        var messageIdRef = new AtomicReference<Integer>();
        rabbitFacade.subscribe(
            "userService",
            "user.signUp",
            (tag, delivery) -> {
                var messageAsString = new String(delivery.getBody(), StandardCharsets.UTF_8);
                messageIdRef.set(new JsonPath(messageAsString).getInt("id"));
            },
            (tag) -> {}
        );

        var requestBody = new HashMap<String, Object>();
        requestBody.put("email", "dawid@d.c");
        requestBody.put("password", "123456");
        requestBody.put("name", "Dawid");

        var request = given()
            .contentType(ContentType.JSON)
            .body(requestBody);

        // When
        var response = request.when()
            .post("/user/sign-up");

        // Then
        response.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("email", equalTo("dawid@d.c"))
            .body("name", equalTo("Dawid"));

        var id = new JsonPath(response.getBody().asString()).getInt("id");

        var handle = jdbiFacade.getHandle();
        var users = handle.select("SELECT id, email, name FROM users WHERE id = ?", id).mapToMap().list();
        assertThat(users)
            .containsExactly(
                Map.of("id", id, "email", "dawid@d.c", "name", "Dawid")
            );

        await()
            .atMost(Duration.ofSeconds(15))
            .untilAtomic(messageIdRef, equalTo(id));
    }
}
