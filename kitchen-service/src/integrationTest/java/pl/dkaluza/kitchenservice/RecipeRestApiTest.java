package pl.dkaluza.kitchenservice;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.hamcrest.Matcher;
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

import java.util.List;
import java.util.Map;
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
            .statusCode(401);

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
            .body("ingredients[0].value", numericEqualTo(reqBodyJsonPath.getString("ingredients[0].value")))
            .body("ingredients[0].measure", is(reqBodyJsonPath.getString("ingredients[0].measure")))
            .body("methodSteps[0].id", notNullValue())
            .body("methodSteps[0].text", is(reqBodyJsonPath.getString("methodSteps[0].text")))
            .body("cookingTime", is(reqBodyJsonPath.getInt("cookingTime")))
            .body("portionSize.value", numericEqualTo(reqBodyJsonPath.getString("portionSize.value")))
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
            .statusCode(401);
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
    void browseRecipes_nameFilterApplied_returnExpectedRecipes() throws Exception {
        insertCook(1L);

        addRecipe(addRecipeReqBody(
            "Boiled sausages", "a",
            "sausage", "3", "pc",
            "Diy",
            180,
            "3", "pc"
        ));

        addRecipe(addRecipeReqBody(
            "Iced coffee", "b",
            "coffee", "50", "g",
            "Diy",
            180,
            "250", "ml"
        ));

        addRecipe(addRecipeReqBody(
            "Toasts", "c",
            "Bread", "4", "pc",
            "Diy",
            180,
            "4", ""
        ));

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .param("name", "a")
        .when()
            .get("/recipe")
        .then()
            .statusCode(200)
            .body("items.id", hasSize(2))
            .body("items.name", hasItems("Boiled sausages", "Toasts"))
            .body("items.description", hasItems("a", "c"))
            .body("totalPages", is(1));
    }
    
    @Test
    void viewRecipe_noJwt_returnError() throws Exception {
        insertCook(1L);
        addRecipe(addRecipeReqBody());

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe/1")
        .then()
            .statusCode(401);
    }

    @Test
    void viewRecipe_invalidId_returnError() throws Exception {
        insertCook(1L);
        addRecipe(addRecipeReqBody());

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe/0")
        .then()
            .statusCode(422)
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("fields.name", hasItems("id"));
    }

    @Test
    void viewRecipe_recipeNotFound_returnError() throws Exception {
        insertCook(1L);
        var id = addRecipe(addRecipeReqBody());

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe/{id}", id + 1) //+1 to look for a recipe that definitely does not exist
        .then()
            .statusCode(404)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    void viewRecipe_validRequest_returnRecipe() throws Exception {
        // Given
        insertCook(1L);
        var reqBody = addRecipeReqBody();
        var id = addRecipe(reqBody);

        var req = given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);

        // When
        var res = req.get("/recipe/{id}", id);

        // Then
        var reqBodyJsonPath = new JsonPath(reqBody);
        res.then()
            .statusCode(200)
            .body("id", is(id.intValue()))
            .body("name", is(reqBodyJsonPath.getString("name")))
            .body("description", is(reqBodyJsonPath.getString("description")))
            .body("ingredients[0].id", notNullValue())
            .body("ingredients[0].name", is(reqBodyJsonPath.getString("ingredients[0].name")))
            .body("ingredients[0].value", numericEqualTo(reqBodyJsonPath.getString("ingredients[0].value")))
            .body("ingredients[0].measure", is(reqBodyJsonPath.getString("ingredients[0].measure")))
            .body("methodSteps[0].id", notNullValue())
            .body("methodSteps[0].text", is(reqBodyJsonPath.getString("methodSteps[0].text")))
            .body("cookingTime", is(reqBodyJsonPath.getInt("cookingTime")))
            .body("portionSize.value", numericEqualTo(reqBodyJsonPath.getString("portionSize.value")))
            .body("portionSize.measure", is(reqBodyJsonPath.getString("portionSize.measure")))
            .body("cookId", is(1));
    }

    @Test
    void updateRecipe_noJwt_returnError() throws Exception {
        insertCook(1L);
        addRecipe(addRecipeReqBody());

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                updateRecipeReqBody(null, null, null).toString()
            )
        .when()
            .put("/recipe/1")
        .then()
            .statusCode(401);
    }

    @Test
    void updateRecipe_invalidId_returnError() throws Exception {
        insertCook(1L);
        addRecipe(addRecipeReqBody());

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                updateRecipeReqBody(null, null, null).toString()
            )
        .when()
            .put("/recipe/0")
        .then()
            .statusCode(422)
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("fields.name", hasItems("id"));
    }

    @ParameterizedTest
    @MethodSource("updateRecipeInvalidReqBodyParams")
    void updateRecipe_invalidReqBody_returnError(Map<String, Object> reqBody, String[] expectedFieldErrors) throws Exception {
        insertCook(1L);
        var addRecipeResponse = addRecipeAndReturnResponse(addRecipeReqBody());
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        var recipeId = addRecipeRespBody.getLong("id");

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody)
        .when()
            .put("/recipe/{id}", recipeId)
        .then()
            .statusCode(422)
            .body("message", notNullValue())
            .body("timestamp", notNullValue())
            .body("fields.name", hasItems(expectedFieldErrors));
    }

    static Stream<Arguments> updateRecipeInvalidReqBodyParams() {
        return Stream.of(
            Arguments.of(
                Map.of(
                    "basicInformation",
                    Map.of()
                ),
                new String[] {
                    "basicInformation.name",
                    "basicInformation.description",
                    "basicInformation.cookingTime",
                    "basicInformation.portionSize.value",
                    "basicInformation.portionSize.measure",
                }
            ),
            Arguments.of(
                Map.of(
                    "ingredients", Map.of(
                        "ingredientsToAdd", List.of(Map.of(
                            "name", "",
                            "value", "",
                            "measure", ""
                        )),
                        "ingredientsToUpdate", List.of(Map.of())
                    )
                ),
                new String[] {
                    "ingredients.ingredientsToAdd.name",
                    "ingredients.ingredientsToAdd.value",
                    "ingredients.ingredientsToUpdate.id",
                    "ingredients.ingredientsToUpdate.name",
                    "ingredients.ingredientsToUpdate.value",
                    "ingredients.ingredientsToUpdate.measure",
                }
            ),
            Arguments.of(
                Map.of(
                    "steps", Map.of(
                        "stepsToAdd", List.of(Map.of(
                            "text", ""
                        )),
                        "stepsToUpdate", List.of(Map.of())
                    )
                ),
                new String[] {
                    "steps.stepsToAdd.text",
                    "steps.stepsToUpdate.id",
                    "steps.stepsToUpdate.text",
                }
            )
        );
    }

    @Test
    void updateRecipe_recipeNotFound_returnError() throws Exception {
        insertCook(1L);
        var addRecipeResponse = addRecipeAndReturnResponse(addRecipeReqBody());
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        var recipeId = addRecipeRespBody.getLong("id");

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Map.of())
        .when()
            .put("/recipe/{id}", recipeId + 1) //+1 to look for a recipe that definitely does not exist
        .then()
            .statusCode(404)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    void updateRecipe_recipeNotOwned_returnError() throws Exception {
        // TODO now there is no way of adding recipes by two different cooks.
        //  Add this possibility and test the scenario
    }

    @Test
    void updateRecipe_ingredientNotFound_returnError() throws Exception {
        insertCook(1L);
        var addRecipeResponse = addRecipeAndReturnResponse(addRecipeReqBody());
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        var recipeId = addRecipeRespBody.getLong("id");
        var ingredientId = addRecipeRespBody.getLong("ingredients[0].id");

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                Map.of(
                    "ingredients", Map.of(
                        "ingredientsToUpdate", List.of(
                            Map.of(
                                "id", ingredientId + 1, // id+1 does not exist
                                "name", "Sausages",
                                "value", "1",
                                "measure", ""
                            )
                        )
                    )
                )
            )
        .when()
            .put("/recipe/{id}", recipeId)
        .then()
            .statusCode(404)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    void updateRecipe_stepNotFound_returnError() throws Exception {
        insertCook(1L);
        var addRecipeResponse = addRecipeAndReturnResponse(addRecipeReqBody());
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        var recipeId = addRecipeRespBody.getLong("id");
        var stepId = addRecipeRespBody.getLong("methodSteps[0].id");

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                Map.of(
                    "steps", Map.of(
                        "stepsToDelete", List.of(stepId + 1) // id+1 does not exist
                    )
                )
            )
        .when()
            .put("/recipe/{id}", recipeId)
        .then()
            .statusCode(404)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    void updateRecipe_validRequest_returnUpdatedRecipe() {
        // Given
        insertCook(1L);
        var addRecipeResponse = addRecipe(Map.of(
            "name", "Oats with milk",
            "description", "How to prepare delicious hot oats on milk",
            "ingredients", List.of(
                Map.of(
                    "name", "milk",
                    "value", "300",
                    "measure", "ml"
                ),
                Map.of(
                    "name", "oats",
                    "value", "150",
                    "measure", "g"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Heat the milk."
                ),
                Map.of(
                    "text", "Add the oats once the milk is hot."
                ),
                Map.of(
                    "text", "Cook for about 3 mins on medium heat. Stir from time to time."
                )
            ),
            "cookingTime", 60 * 10,
            "portionSize", Map.of(
                "value", "450",
                "measure", "g"
            )
        ));
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        Long recipeId = addRecipeRespBody.getLong("id");

        var updatedName = "Hot oats with milk";
        var updatedDescription = "";
        var updatedCookingTime = 60 * 10;
        var updatedPortionSizeValue = "500";
        var updatedPortionSizeMeasure = "g";

        var newIngredientName = "almond milk";
        var newIngredientValue = "300";
        var newIngredientMeasure = "ml";

        var updatedIngredientId = addRecipeRespBody.getLong("ingredients[1].id");
        var updatedIngredientName = "oats";
        var updatedIngredientValue = "150";
        var updatedIngredientMeasure = "g";

        var deletedIngredientId = addRecipeRespBody.getLong("ingredients[0].id");

        var newStepText = "Once the oats become thick, take them out of the pot.";

        var updatedStepId = addRecipeRespBody.getLong("methodSteps[1].id");
        var updatedStepText = "Heat the milk, add the oats once the milk is hot enough.";

        var firstDeletedStepId = addRecipeRespBody.getLong("methodSteps[0].id");
        var secondDeletedStepId = addRecipeRespBody.getLong("methodSteps[2].id");

        given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(
                Map.of(
                    "basicInformation", Map.of(
                        "name", updatedName,
                        "description", updatedDescription,
                        "cookingTime", updatedCookingTime,
                        "portionSize", Map.of(
                            "value", updatedPortionSizeValue,
                            "measure", updatedPortionSizeMeasure
                        )
                    ),
                    "ingredients", Map.of(
                        "ingredientsToAdd", List.of(Map.of(
                            "name", newIngredientName,
                            "value", newIngredientValue,
                            "measure", newIngredientMeasure
                        )),
                        "ingredientsToUpdate", List.of(Map.of(
                            "id", updatedIngredientId,
                            "name", updatedIngredientName,
                            "value", updatedIngredientValue,
                            "measure", updatedIngredientMeasure
                        )),
                        "ingredientsToDelete", List.of(deletedIngredientId)
                    ),
                    "steps", Map.of(
                        "stepsToAdd", List.of(Map.of(
                            "text", newStepText
                        )),
                        "stepsToUpdate", List.of(Map.of(
                            "id", updatedStepId,
                            "text", updatedStepText
                        )),
                        "stepsToDelete", List.of(firstDeletedStepId, secondDeletedStepId)
                    )
                )
            )
        .when()
            .put("/recipe/{id}", recipeId)
        .then()
            .statusCode(200)
            .body("id", is(recipeId.intValue()))
            .body("name", is(updatedName))
            .body("description", is(updatedDescription))
            .body("cookingTime", is(updatedCookingTime))
            .body("portionSize.value", numericEqualTo(updatedPortionSizeValue))
            .body("portionSize.measure", is(updatedPortionSizeMeasure))
            .body("ingredients[0].id", notNullValue())
            .body("ingredients[0].name", is(updatedIngredientName))
            .body("ingredients[0].value", is(updatedIngredientValue))
            .body("ingredients[0].measure", is(updatedIngredientMeasure))
            .body("ingredients[1].id", notNullValue())
            .body("ingredients[1].name", is(newIngredientName))
            .body("ingredients[1].value", is(newIngredientValue))
            .body("ingredients[1].measure", is(newIngredientMeasure))
            .body("ingredients.size()", is(2))
            .body("methodSteps[0].id", notNullValue())
            .body("methodSteps[0].text", is(updatedStepText))
            .body("methodSteps[1].id", notNullValue())
            .body("methodSteps[1].text", is(newStepText))
            .body("methodSteps.size()", is(2));
    }

    // TODO reimpl to use AMQP API
    private void insertCook(Long id) {
        var handle = jdbiFacade.getHandle();
        handle.execute("INSERT INTO cook VALUES (?)", id);
    }

    // TODO refactor this method and the whole test class, some methods are too suited to specific use cases
    private Long addRecipe(String reqBody) {
        var response = addRecipeAndReturnResponse(reqBody);
        return new JsonPath(response.getBody().asString()).getLong("id");
    }

    private Response addRecipeAndReturnResponse(String reqBody) {
        return given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody)
            .post("/recipe");
    }
    
    private Response addRecipe(Map<String, Object> reqBody) {
        return given()
            .filter(new JwtFilter())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody)
            .post("/recipe");
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

    @Deprecated
    private static JSONObject updateRecipeReqBody(JSONObject basicInfo, JSONObject ingredients, JSONObject steps) throws JSONException {
        var reqBody = new JSONObject();
        reqBody.put("basicInformation", basicInfo);
        reqBody.put("ingredients", ingredients);
        reqBody.put("steps", steps);
        return reqBody;
    }

    private static Matcher<?> numericEqualTo(String expectedValue) {
        return new NumericMatcher(expectedValue);
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
