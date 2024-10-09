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
import pl.dkaluza.kitchenservice.config.RabbitFacade;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecipeRestApiTest {
    private JdbiFacade jdbiFacade;
    private RabbitFacade rabbitFacade;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() throws Exception {
        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();
        var handle = jdbiFacade.getHandle();
        handle.execute("DELETE FROM step");
        handle.execute("DELETE FROM ingredient");
        handle.execute("DELETE FROM recipe");
        handle.execute("DELETE FROM cook");

        rabbitFacade = new RabbitFacade();
        rabbitFacade.start();

        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void afterEach() throws Exception {
        jdbiFacade.stop();
        rabbitFacade.stop();
    }

    @Test
    void addRecipe_noJwt_returnError() {
        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newBoiledSausagesRecipeReqBody())
        .when()
            .post("/recipe")
        .then()
            .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("addRecipeInvalidParamsProvider")
    void addRecipe_invalidParams_returnError(Map<String, Object> reqBody, String[] expectedFieldErrors) {
        // Given
        var req = given()
            .filter(new JwtFilter(1))
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
    }

    private static Stream<Arguments> addRecipeInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                Map.of(),
                new String[] {
                    "name", "description",
                    "ingredients", "methodSteps",
                    "cookingTime",
                    "portionSize.value", "portionSize.measure"
                }
            ),
            Arguments.of(
                Map.of(
                    "name", "A",
                    "description", "B",
                    "ingredients", List.of(
                        Map.of(
                            "name", "B",
                            "value", "",
                            "measure", ""
                        )
                    ),
                    "methodSteps", List.of(
                        Map.of(
                            "text", ""
                        )
                    ),
                    "cookingTime", 300,
                    "portionSize", Map.of(
                        "value", "0",
                        "measure", ""
                    )
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
    void addRecipe_invalidCook_returnError() {
        signUpCook(2L);

        given()
            .filter(new JwtFilter(1))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(newBoiledSausagesRecipeReqBody())
        .when()
            .post("/recipe")
        .then()
            .statusCode(404)
            .body("message", notNullValue())
            .body("timestamp", notNullValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    void addRecipe_validParams_returnNewRecipe() {
        // Given
        signUpCook(1L);

        var reqBody = newBoiledSausagesRecipeReqBody();
        var ingredientReqBody = ((List<Map<String, Object>>) reqBody.get("ingredients")).get(0);
        var stepReqBody = ((List<Map<String, Object>>) reqBody.get("methodSteps")).get(0);
        var portionSizeReqBody = (Map<String, Object>) reqBody.get("portionSize");
        
        var req = given()
            .filter(new JwtFilter(1))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody);

        // When
        var res = req.post("/recipe");

        // Then
        res.then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", is(reqBody.get("name")))
            .body("description", is(reqBody.get("description")))
            .body("ingredients[0].id", notNullValue())
            .body("ingredients[0].name", is(ingredientReqBody.get("name")))
            .body("ingredients[0].value", numericEqualTo(ingredientReqBody.get("value").toString()))
            .body("ingredients[0].measure", is(ingredientReqBody.get("measure")))
            .body("methodSteps[0].id", notNullValue())
            .body("methodSteps[0].text", is(stepReqBody.get("text")))
            .body("cookingTime", is(reqBody.get("cookingTime")))
            .body("portionSize.value", numericEqualTo(portionSizeReqBody.get("value").toString()))
            .body("portionSize.measure", is(portionSizeReqBody.get("measure")))
            .body("cookId", is(1));

        var resBodyAsJsonPath = new JsonPath(res.getBody().asString());
        var recipeId = resBodyAsJsonPath.getInt("id");

        given()
            .filter(new JwtFilter(1))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe/{id}", recipeId)
        .then()
            .statusCode(200);
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
            .filter(new JwtFilter(1))
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
    void browseRecipes_noMatchingRecipes_returnNoRecipes() {
        signUpCook(1L);

        addRecipe(Map.of(
            "name", "Boiled sausages",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "sausage",
                    "value", "3",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "3",
                "measure", "pc"
            )
        ));

        addRecipe(Map.of(
            "name", "Iced coffee",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "coffee",
                    "value", "50",
                    "measure", "g"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "4",
                "measure", ""
            )
        ));

        addRecipe(Map.of(
            "name", "Toasts",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "Bread",
                    "value", "4",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "4",
                "measure", ""
            )
        ));

        given()
            .filter(new JwtFilter(1))
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
    void browseRecipes_noParams_returnAllUserRecipes() {
        signUpCook(1L);

        addRecipe(Map.of(
            "name", "Boiled sausages",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "sausage",
                    "value", "3",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "3",
                "measure", "pc"
            )
        ));

        addRecipe(Map.of(
            "name", "Iced coffee",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "coffee",
                    "value", "50",
                    "measure", "g"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "4",
                "measure", ""
            )
        ));

        addRecipe(Map.of(
            "name", "Toasts",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "Bread",
                    "value", "4",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "4",
                "measure", ""
            )
        ));

        given()
            .filter(new JwtFilter(1))
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
    void browseRecipes_nameFilterApplied_returnExpectedRecipes() {
        signUpCook(1L);

        addRecipe(Map.of(
            "name", "Boiled sausages",
            "description", "a",
            "ingredients", List.of(
                Map.of(
                    "name", "sausage",
                    "value", "3",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "3",
                "measure", "pc"
            )
        ));

        addRecipe(Map.of(
            "name", "Iced coffee",
            "description", "b",
            "ingredients", List.of(
                Map.of(
                    "name", "coffee",
                    "value", "50",
                    "measure", "g"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "250",
                "measure", "ml"
            )
        ));

        addRecipe(Map.of(
            "name", "Toasts",
            "description", "c",
            "ingredients", List.of(
                Map.of(
                    "name", "Bread",
                    "value", "4",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Diy"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "4",
                "measure", ""
            )
        ));

        given()
            .filter(new JwtFilter(1))
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
    void viewRecipe_noJwt_returnError() {
        signUpCook(1L);
        addRecipe(newBoiledSausagesRecipeReqBody());

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe/1")
        .then()
            .statusCode(401);
    }

    @Test
    void viewRecipe_invalidId_returnError() {
        signUpCook(1L);
        addRecipe(newBoiledSausagesRecipeReqBody());

        given()
            .filter(new JwtFilter(1))
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
    void viewRecipe_recipeNotFound_returnError() {
        signUpCook(1L);
        var addRecipeResp = addRecipe(newBoiledSausagesRecipeReqBody());
        var id = new JsonPath(addRecipeResp.getBody().asString()).getInt("id");

        given()
            .filter(new JwtFilter(1))
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
    void viewRecipe_recipeNotOwned_returnError() {
        signUpCook(1L);
        var addRecipeResp = addRecipe(newBoiledSausagesRecipeReqBody());
        var id = new JsonPath(addRecipeResp.getBody().asString()).getInt("id");

        given()
            .filter(new JwtFilter(2L))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
        .when()
            .get("/recipe/{id}", id)
        .then()
            .statusCode(403);
    }

    @Test
    @SuppressWarnings("unchecked")
    void viewRecipe_validRequest_returnRecipe() {
        // Given
        signUpCook(1L);
        var reqBody = newBoiledSausagesRecipeReqBody();
        var ingredientReqBody = ((List<Map<String, Object>>) reqBody.get("ingredients")).get(0);
        var stepReqBody = ((List<Map<String, Object>>) reqBody.get("methodSteps")).get(0);
        var portionSizeReqBody = (Map<String, Object>) reqBody.get("portionSize");
        var addRecipeRespBody = addRecipe(reqBody);
        var recipeId = new JsonPath(addRecipeRespBody.getBody().asString()).getInt("id");

        var req = given()
            .filter(new JwtFilter(1))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);

        // When
        var res = req.get("/recipe/{id}", recipeId);

        // Then
        res.then()
            .statusCode(200)
            .body("id", is(recipeId))
            .body("name", is(reqBody.get("name")))
            .body("description", is(reqBody.get("description")))
            .body("ingredients[0].id", notNullValue())
            .body("ingredients[0].name", is(ingredientReqBody.get("name")))
            .body("ingredients[0].value", numericEqualTo(ingredientReqBody.get("value").toString()))
            .body("ingredients[0].measure", is(ingredientReqBody.get("measure")))
            .body("methodSteps[0].id", notNullValue())
            .body("methodSteps[0].text", is(stepReqBody.get("text")))
            .body("cookingTime", is(reqBody.get("cookingTime")))
            .body("portionSize.value", numericEqualTo(portionSizeReqBody.get("value").toString()))
            .body("portionSize.measure", is(portionSizeReqBody.get("measure")))
            .body("cookId", is(1));
    }

    @Test
    void updateRecipe_noJwt_returnError() {
        signUpCook(1L);
        addRecipe(newBoiledSausagesRecipeReqBody());

        given()
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Map.of())
        .when()
            .put("/recipe/1")
        .then()
            .statusCode(401);
    }

    @Test
    void updateRecipe_invalidId_returnError() {
        signUpCook(1L);
        addRecipe(newBoiledSausagesRecipeReqBody());

        given()
            .filter(new JwtFilter(1))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Map.of())
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
    void updateRecipe_invalidReqBody_returnError(Map<String, Object> reqBody, String[] expectedFieldErrors) {
        signUpCook(1L);
        var addRecipeResponse = addRecipe(newBoiledSausagesRecipeReqBody());
        var recipeId = new JsonPath(addRecipeResponse.getBody().asString()).getLong("id");

        given()
            .filter(new JwtFilter(1))
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
    void updateRecipe_recipeNotFound_returnError() {
        signUpCook(1L);
        var addRecipeResponse = addRecipe(newBoiledSausagesRecipeReqBody());
        var recipeId = new JsonPath(addRecipeResponse.getBody().asString()).getLong("id");

        given()
            .filter(new JwtFilter(1))
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
    void updateRecipe_recipeNotOwned_returnError() {
        signUpCook(1L);
        var addRecipeResponse = addRecipe(newBoiledSausagesRecipeReqBody());
        var recipeId = new JsonPath(addRecipeResponse.getBody().asString()).getLong("id");

        given()
            .filter(new JwtFilter(2L))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(Map.of())
        .when()
            .put("/recipe/{id}", recipeId)
        .then()
            .statusCode(403);
    }

    @Test
    void updateRecipe_ingredientNotFound_returnError() {
        signUpCook(1L);
        var addRecipeResponse = addRecipe(newBoiledSausagesRecipeReqBody());
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        var recipeId = addRecipeRespBody.getLong("id");
        var ingredientId = addRecipeRespBody.getLong("ingredients[0].id");

        given()
            .filter(new JwtFilter(1))
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
    void updateRecipe_stepNotFound_returnError() {
        signUpCook(1L);
        var addRecipeResponse = addRecipe(newBoiledSausagesRecipeReqBody());
        var addRecipeRespBody = new JsonPath(addRecipeResponse.getBody().asString());
        var recipeId = addRecipeRespBody.getLong("id");
        var stepId = addRecipeRespBody.getLong("methodSteps[0].id");

        given()
            .filter(new JwtFilter(1))
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
        signUpCook(1L);
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
        var recipeId = addRecipeRespBody.getInt("id");

        var updatedName = "Hot oats with milk";
        var updatedDescription = "";
        var updatedCookingTime = 60 * 10;
        var updatedPortionSizeValue = "500";
        var updatedPortionSizeMeasure = "g";

        var newIngredientName = "almond milk";
        var newIngredientValue = "300";
        var newIngredientMeasure = "ml";

        var updatedIngredientId = addRecipeRespBody.getInt("ingredients[1].id");
        var updatedIngredientName = "oats";
        var updatedIngredientValue = "150";
        var updatedIngredientMeasure = "g";

        var deletedIngredientId = addRecipeRespBody.getInt("ingredients[0].id");

        var newStepText = "Once the oats become thick, take them out of the pot.";

        var updatedStepId = addRecipeRespBody.getInt("methodSteps[1].id");
        var updatedStepText = "Heat the milk, add the oats once the milk is hot enough.";

        var firstDeletedStepId = addRecipeRespBody.getInt("methodSteps[0].id");
        var secondDeletedStepId = addRecipeRespBody.getInt("methodSteps[2].id");

        given()
            .filter(new JwtFilter(1))
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
            .body("id", is(recipeId))
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

    private void signUpCook(Long id) {
        try {
            var channel = rabbitFacade.getChannel();
            channel.exchangeDeclare("userService", "topic", true);

            var msg = String.format("{\"id\": %d}", id);
            channel.basicPublish(
                "userService", "user.signUp", null,
                msg.getBytes(StandardCharsets.UTF_8)
            );

            await()
                .atMost(Duration.ofSeconds(15))
                .until(() -> {
                    var handle = jdbiFacade.getHandle();
                    var count = handle.select("SELECT COUNT(id) FROM cook WHERE id = ?", id)
                        .mapTo(Integer.class)
                        .first();

                    return count > 0;
                });
        } catch (Exception e) {
            throw new IllegalStateException("Sign up thrown unexpected exception", e);
        }
    }
    
    private Response addRecipe(Map<String, Object> reqBody) {
        return given()
            .filter(new JwtFilter(1))
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(reqBody)
            .post("/recipe");
    }

    private static Map<String, Object> newBoiledSausagesRecipeReqBody() {
        return Map.of(
            "name", "Boiled sausages",
            "description", "",
            "ingredients", List.of(
                Map.of(
                    "name", "Sausage",
                    "value", "3",
                    "measure", "pc"
                )
            ),
            "methodSteps", List.of(
                Map.of(
                    "text", "Boil sausages for about 3 mins"
                )
            ),
            "cookingTime", 180,
            "portionSize", Map.of(
                "value", "3",
                "measure", "pc"
            )
        );
    }

    private static Matcher<?> numericEqualTo(String expectedValue) {
        return new NumericMatcher(expectedValue);
    }

    private static class JwtFilter implements Filter {
        private final long id;

        JwtFilter(long id) {
            if (id < 1 || id > 2) {
                throw new IllegalArgumentException("id must be between 1 and 2");
            }

            this.id = id;
        }

        @Override
        public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
            var res = given()
                .queryParam("subject", id)
                .get("http://localhost:8081/jwt");
            requestSpec.header("Authorization", "Bearer " + res.getBody().asString());
            return ctx.next(requestSpec, responseSpec);
        }
    }
}
