package pl.dkaluza.kitchenservice;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pl.dkaluza.kitchenservice.config.EnableTestcontainers;
import pl.dkaluza.kitchenservice.config.JdbiFacade;
import pl.dkaluza.kitchenservice.domain.Recipe;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeRestApiTest {
    private JdbiFacade jdbiFacade;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() {
        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();

        var handle = jdbiFacade.getHandle();
        handle.execute("DELETE FROM step");
        handle.execute("DELETE FROM ingredient");
        handle.execute("DELETE FROM recipe");
        handle.execute("DELETE FROM cook");

        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void afterEach() {
        jdbiFacade.stop();
    }

    @Test
    void addRecipe_noJwt_returnError() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(addRecipeReqBody())
        .when()
            .post("/recipe")
        .then()
            .statusCode(403);

        assertThatPersistenceIsEmpty();
    }

    @ParameterizedTest
    @MethodSource("addRecipeInvalidParamsProvider")
    void addRecipe_invalidParams_returnError(String reqBody, String[] expectedFieldErrors) throws Exception {
        // Given
        var req =  given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody);

        // When
        var res = req.post("recipe");

        // Then
        res.then()
            .statusCode(422)
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("fields.name", hasItems(expectedFieldErrors));

        assertThatPersistenceIsEmpty();
    }

    private static Stream<Arguments> addRecipeInvalidParamsProvider() throws Exception {
        return Stream.of(
            Arguments.of(
                "{ }",
                new String[] {
                    "name", "description",
                    "ingredients", "methodSteps",
                    "cookingTime",
                    "portionSize.value", "portionSize.measure"
                }
            ),
            Arguments.of(
                addRecipeReqBody(
                    "A", "B",
                    "B", "", "",
                    "",
                    300,
                    "0", ""
                ),
                new String[] {
                    "name",
                    "ingredients.name", "ingredients.value",
                    "methodSteps.text",
                    "portionSize.value"
                }
            )
        );
    }

    @Test
    void addRecipe_invalidCook_returnError() throws Exception {
        insertCook(2L);

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(addRecipeReqBody())
        .when()
            .post("/recipe")
        .then()
            .statusCode(404)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());

        assertThatPersistenceIsEmpty();
    }

    @Test
    void addRecipe_validParams_returnNewRecipe() throws Exception {
        // Given
        insertCook(1L);

        var reqBody = addRecipeReqBody();
        var reqBodyJsonPath = new JsonPath(reqBody);
        var req = given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody);

        // When
        var res = req.post("/recipe");

        // Then
        res.then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", is(reqBodyJsonPath.getString("name")))
            .body("description", is(reqBodyJsonPath.getString("description")))
            .body("ingredients[0].id", notNullValue())
            .body("ingredients[0].name", is(reqBodyJsonPath.getString("ingredients[0].name")))
            .body("ingredients[0].value", is(reqBodyJsonPath.getString("ingredients[0].value")))
            .body("ingredients[0].measure", is(reqBodyJsonPath.getString("ingredients[0].measure")))
            .body("methodSteps[0].id", notNullValue())
            .body("methodSteps[0].text", is(reqBodyJsonPath.getString("methodSteps[0].text")))
            .body("cookingTime", is(reqBodyJsonPath.getInt("cookingTime")))
            .body("portionSize.value", is(reqBodyJsonPath.getString("portionSize.value")))
            .body("portionSize.measure", is(reqBodyJsonPath.getString("portionSize.measure")))
            .body("cookId", is(1));

        var resBodyAsJsonPath = new JsonPath(res.getBody().asString());
        assertThatPersisted(
            resBodyAsJsonPath.getInt("id"),
            "SELECT COUNT(id) FROM recipe WHERE id = ?"
        );
        assertThatPersisted(
            resBodyAsJsonPath.getInt("ingredients[0].id"),
            "SELECT COUNT(id) FROM ingredient WHERE id = ?"
        );
        assertThatPersisted(
            resBodyAsJsonPath.getInt("methodSteps[0].id"),
            "SELECT COUNT(id) FROM step WHERE id = ?"
        );

        // TODO assert that all objects are associated with each other through foreign keys
    }

    @Test
    void browseRecipes_noJwt_returnError() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe")
        .then()
            .statusCode(403);
    }

    @Test
    void browseRecipes_invalidPageParams_returnError() {
        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .param("page", "0")
            .param("pageSize", "0")
        .when()
            .get("/recipe")
        .then()
            .statusCode(422)
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("fields.name", hasItems("page", "pageSize"));
    }

    @Test
    void browseRecipes_noMatchingRecipes_returnNoRecipes() throws Exception {
        insertCook(1L);

        addRecipe(addRecipeReqBody(
            "Boiled sausages", "",
            "sausage", "3", "pc",
            "Diy",
            180,
            "3", "pc"
        ));

        addRecipe(addRecipeReqBody(
            "Iced coffee", "",
            "coffee", "50", "g",
            "Diy",
            180,
            "250", "ml"
        ));

        addRecipe(addRecipeReqBody(
            "Toasts", "",
            "Bread", "4", "pc",
            "Diy",
            180,
            "4", ""
        ));

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .param("name", "Sth veeeery specific")
        .when()
            .get("/recipe")
        .then()
            .statusCode(200)
            .body("items", emptyIterable())
            .body("totalPages", is(1));
    }

    @Test
    void browseRecipes_noParams_returnAllUserRecipes() throws Exception {
        insertCook(1L);

        addRecipe(addRecipeReqBody(
            "Boiled sausages", "",
            "sausage", "3", "pc",
            "Diy",
            180,
            "3", "pc"
        ));

        addRecipe(addRecipeReqBody(
            "Iced coffee", "",
            "coffee", "50", "g",
            "Diy",
            180,
            "250", "ml"
        ));

        addRecipe(addRecipeReqBody(
            "Toasts", "",
            "Bread", "4", "pc",
            "Diy",
            180,
            "4", ""
        ));

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe")
        .then()
            .statusCode(200)
            .body("items.name", hasItems("Boiled sausages", "Iced coffee", "Toasts"))
            .body("totalPages", is(1));
    }

    @Test
    void browseRecipes_variousParams_returnExpectedRecipes() {

    }

    // TODO reimpl to use AMQP API
    private void insertCook(Long id) {
        var handle = jdbiFacade.getHandle();
        handle.execute("INSERT INTO cook VALUES (?)", id);
    }

    private Long addRecipe(String reqBody) {
        var res = given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody)
            .post("/recipe");

        return new JsonPath(res.getBody().asString()).getLong("id");
    }

    private void assertThatPersistenceIsEmpty() {
        var handle = jdbiFacade.getHandle();

        var queries = List.of(
            "SELECT COUNT(id) FROM ingredient",
            "SELECT COUNT(id) FROM step",
            "SELECT COUNT(id) FROM recipe"
        );

        for (var query : queries) {
            var count = handle.select(query)
                .mapTo(Integer.class)
                .first();

            assertThat(count)
                .isEqualTo(0);
        }
    }

    private <T> void assertThatPersisted(T id, String countQuery) {
        var handle = jdbiFacade.getHandle();
        var count = handle
            .select(countQuery, id)
            .mapTo(Integer.class)
            .first();

        assertThat(count)
            .isEqualTo(1);
    }

    private static String addRecipeReqBody() throws JSONException {
        return addRecipeReqBody(
            "Boiled sausages", "",
            "Sausage", "3", "pc",
            "Boil sausages for about 3 mins",
            180,
            "3", "pc"
        );
    }

    private static String addRecipeReqBody(
        String name, String description,
        String ingredientName, String ingredientValue, String ingredientMeasure,
        String stepText,
        int cookingTime,
        String portionSizeValue, String portionSizeMeasure
    ) throws JSONException {
        var reqBody = new JSONObject();

        reqBody.put("name", name);
        reqBody.put("description", description);

        var ingredient = new JSONObject();
        ingredient.put("name", ingredientName);
        ingredient.put("value", ingredientValue);
        ingredient.put("measure", ingredientMeasure);

        var ingredients = new JSONArray();
        ingredients.put(ingredient);

        reqBody.put("ingredients", ingredients);

        var step = new JSONObject();
        step.put("text", stepText);

        var steps = new JSONArray();
        steps.put(step);

        reqBody.put("methodSteps", steps);
        reqBody.put("cookingTime", cookingTime);

        var portionSize = new JSONObject();
        portionSize.put("value", portionSizeValue);
        portionSize.put("measure", portionSizeMeasure);

        reqBody.put("portionSize", portionSize);

        return reqBody.toString();
    }

    private static class JwtFilter implements Filter {
        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            var res = given().get("http://localhost:8081/jwt"); //cook (aka user/resource owner) id encoded in jwt is 1
            requestSpec.header("Authorization", "Bearer " + res.getBody().asString());
            return ctx.next(requestSpec, responseSpec);
        }
    }
}
