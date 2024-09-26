package pl.dkaluza.kitchenservice.adapters.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import pl.dkaluza.domaincore.Page;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotOwnedException;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RecipeControllerTest {
    private KitchenService kitchenService;
    private RecipeController recipeController;

    @BeforeEach
    void beforeEach() {
        kitchenService = mock();
        var facade = new RecipeWebFacade(
            Mappers.getMapper(RecipeWebMapper.class),
            Mappers.getMapper(PageWebMapper.class),
            kitchenService
        );
        recipeController = new RecipeController(facade);
    }

    @ParameterizedTest
    @MethodSource("addRecipeInvalidDataProvider")
    void addRecipe_invalidData_returnErrorResponse(Authentication auth, AddRecipeRequest reqBody, String[] expectedFieldErrors) {
        // Given
        mockAddRecipe();

        // When
        var resp = recipeController.addRecipe(auth, reqBody);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();

        var respBodyErrorFields = respBody.fields();

        assertThat(respBodyErrorFields)
            .isNotNull()
            .extracting(ErrorResponse.Field::name)
            .contains(expectedFieldErrors);
    }

    // TODO test that every error field is mapped as expected!
    private static Stream<Arguments> addRecipeInvalidDataProvider() {
        return Stream.of(
            Arguments.of(
                authentication(3L),
                new AddRecipeRequest(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                ),
                new String[]{"name", "description", "ingredients", "methodSteps", "cookingTime", "portionSize.value", "portionSize.measure"}
            ),

            Arguments.of(
                authentication(3L),
                new AddRecipeRequest(
                    "", "",
                    List.of(
                        new AddRecipeRequest.Ingredient("sausage", new BigDecimal(2), "pc")
                    ),
                    List.of(
                        new AddRecipeRequest.Step("Boil sausages for about 3 mins")
                    ),
                    0L,
                    new AddRecipeRequest.PortionSize(new BigDecimal(2), "pc")
                ),
                new String[]{"name", "cookingTime"}
            )
        );
    }

    @Test
    void addRecipe_notExistingCookId_returnErrorResponse() {
        // Given
        when(kitchenService.addRecipe(any())).thenThrow(new CookNotFoundException("Cook not found"));

        var auth = authentication(1L);
        var reqBody = new AddRecipeRequest(
            "Boiled sausages", "",
            List.of(
                new AddRecipeRequest.Ingredient("sausage", new BigDecimal(2), "pc")
            ),
            List.of(
                new AddRecipeRequest.Step("Boil sausages for about 3 mins")
            ),
            180L,
            new AddRecipeRequest.PortionSize(new BigDecimal(2), "pc")
        );

        // When
        var resp = recipeController.addRecipe(auth, reqBody);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();
    }

    @Test
    void addRecipe_validRequest_returnCreatedRecipe() {
        // Given
        mockAddRecipe();

        var userId = 1L;
        var auth = authentication(userId);
        var reqBody = new AddRecipeRequest(
            "Boiled sausages", "",
            List.of(
                new AddRecipeRequest.Ingredient("sausage", new BigDecimal(2), "pc")
            ),
            List.of(
                new AddRecipeRequest.Step("Boil sausages for about 3 mins")
            ),
            180L,
            new AddRecipeRequest.PortionSize(new BigDecimal(2), "pc")
        );

        // When
        var resp = recipeController.addRecipe(auth, reqBody);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.CREATED);

        assertThat(resp.getBody())
            .isInstanceOf(RecipeResponse.class);

        var respBody = (RecipeResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.id())
            .isNotNull();

        assertThat(respBody.name())
            .isEqualTo(reqBody.name());

        assertThat(respBody.description())
            .isEqualTo(reqBody.description());

        var reqIngredients = reqBody.ingredients();
        var reqIngredientsSize = reqIngredients.size();
        var respIngredients = respBody.ingredients();
        var respIngredientsSize = respIngredients.size();

        assertThat(reqIngredientsSize)
            .isEqualTo(respIngredientsSize);

        for (var i = 0; i < respIngredientsSize; i++) {
            var reqIngredient = reqIngredients.get(i);
            var respIngredient = respIngredients.get(i);

            assertThat(respIngredient)
                .isNotNull();

            assertThat(respIngredient.id())
                .isNotNull();

            assertThat(respIngredient.name())
                .isEqualTo(reqIngredient.name());

            assertThat(respIngredient.value())
                .isEqualTo(reqIngredient.value());

            assertThat(respIngredient.measure())
                .isEqualTo(reqIngredient.measure());
        }

        var reqMethodSteps = reqBody.methodSteps();
        var reqMethodStepsSize = reqMethodSteps.size();
        var respMethodSteps = respBody.methodSteps();
        var respMethodStepsSize = respMethodSteps.size();

        assertThat(reqMethodStepsSize)
            .isEqualTo(respMethodStepsSize);

        for (var i = 0; i < respMethodStepsSize; i++) {
            var reqStep = reqMethodSteps.get(i);
            var respStep = respMethodSteps.get(i);

            assertThat(respStep)
                .isNotNull();

            assertThat(respStep.id())
                .isNotNull();

            assertThat(respStep.text())
                .isEqualTo(reqStep.text());
        }

        assertThat(respBody.cookingTime())
            .isEqualTo(reqBody.cookingTime());

        assertThat(respBody.portionSize())
            .isNotNull()
            .extracting(RecipeResponse.PortionSize::value, RecipeResponse.PortionSize::measure)
            .containsExactly(reqBody.portionSize().value(), reqBody.portionSize().measure());

        assertThat(respBody.cookId())
            .isEqualTo(userId);
    }

    @Test
    void browseRecipes_invalidPageParams_returnErrorResponse() {
        // Given
        var auth = authentication(1L);
        var page = 0;
        var pageSize = 0;
        var name = "";

        // When
        var resp = recipeController.browseRecipes(auth, page, pageSize, name);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();

        assertThat(respBody.fields())
            .isNotNull()
            .extracting(ErrorResponse.Field::name)
            .contains("page", "pageSize");
    }

    @Test
    void browseRecipes_validParams_returnExpectedRecipes() {
        // Given
        var mockRecipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(1L, "sausage", new BigDecimal(2), "pc")
            .methodStep( 1L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(1L)
            .build().produce();

        when(kitchenService.browseRecipes(any(), any())).thenReturn(
            Page.of(List.of(mockRecipe)).produce()
        );

        var auth = authentication(1L);
        var page = 1;
        var pageSize = 10;
        var name = "";

        // When
        var resp = recipeController.browseRecipes(auth, page, pageSize, name);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        assertThat(resp.getBody())
            .isInstanceOf(PageResponse.class);

        @SuppressWarnings("unchecked")
        var respBody = (PageResponse<ShortRecipeResponse>) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.totalPages())
            .isEqualTo(1);

        assertThat(respBody.items())
            .isNotNull()
            .singleElement()
            .extracting(ShortRecipeResponse::id, ShortRecipeResponse::name, ShortRecipeResponse::description)
            .containsExactly(1L, "Boiled sausages", "");
    }

    @Test
    void viewRecipe_invalidId_returnError() {
        // Given
        var auth = authentication(1L);
        var id = 0L;

        // When
        var resp = recipeController.viewRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();

        assertThat(respBody.fields())
            .isNotNull()
            .extracting(ErrorResponse.Field::name)
            .contains("id");
    }

    @Test
    void viewRecipe_recipeNotFound_returnError() {
        // Given
        when(kitchenService.viewRecipe(any(), any()))
            .thenThrow(new RecipeNotFoundException("Recipe not found"));

        var auth = authentication(1L);
        var id = 1L;

        // When
        var resp = recipeController.viewRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();
    }

    @Test
    void viewRecipe_recipeNotOwned_returnError() {
        // Given
        when(kitchenService.viewRecipe(any(), any()))
            .thenThrow(new RecipeNotOwnedException("Recipe not owned"));

        var auth = authentication(1L);
        var id = 1L;

        // When
        var resp = recipeController.viewRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void viewRecipe_validRequest_returnRecipe() {
        // Given
        var mockRecipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(1L, "sausage", new BigDecimal(2), "pc")
            .methodStep( 1L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(1L)
            .build().produce();

        when(kitchenService.viewRecipe(any(), any())).thenReturn(mockRecipe);

        var auth = authentication(1L);
        var id = 1L;

        // When
        var resp = recipeController.viewRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        assertThat(resp.getBody())
            .isInstanceOf(RecipeResponse.class);

        var respBody = (RecipeResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull()
            .extracting(RecipeResponse::id, RecipeResponse::name, RecipeResponse::description)
            .containsExactly(mockRecipe.getId().getId(), mockRecipe.getName(), mockRecipe.getDescription());
    }

    @ParameterizedTest
    @ValueSource(longs = { -1, 0 })
    void updateRecipe_invalidId_returnError(Long id) {
        // Given
        var auth = authentication(1L);
        var reqBody = new UpdateRecipeRequest(null, null, null);

        // When
        var resp = recipeController.updateRecipe(auth, id, reqBody);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();

        assertThat(respBody.fields())
            .isNotNull()
            .extracting(ErrorResponse.Field::name)
            .contains("id");
    }

    void updateRecipe_invalidRequestBody_returnError(UpdateRecipeRequest reqBody) {
        // Given
        var auth = authentication(1L);
        var id = 1L;

        // When
        var resp = recipeController.updateRecipe(auth, id, reqBody);
    }

    void updateRecipe_recipeNotFound_returnError() {}

    void updateRecipe_recipeNotOwned_returnError() {}

    void updateRecipe_ingredientNotFound_returnError() {}

    void updateRecipe_stepNotFound_returnError() {

    }



    @ParameterizedTest
    @ValueSource(longs = { -1L, 0L })
    void deleteRecipe_invalidId_returnError(Long id) {
        // Given
        var auth = authentication(1L);

        // When
        var resp = recipeController.deleteRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();

        assertThat(respBody.fields())
            .isNotNull()
            .extracting(ErrorResponse.Field::name)
            .contains("id");
    }

    @Test
    void deleteRecipe_recipeNotFound_returnError() {
        // Given
        var auth = authentication(1L);
        var id = 1L;

        doThrow(new RecipeNotFoundException("Recipe not found"))
            .when(kitchenService).deleteRecipe(any(), any());

        // When
        var resp = recipeController.deleteRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(resp.getBody())
            .isInstanceOf(ErrorResponse.class);

        var respBody = (ErrorResponse) resp.getBody();

        assertThat(respBody)
            .isNotNull();

        assertThat(respBody.message())
            .isNotNull();

        assertThat(respBody.timestamp())
            .isNotNull();
    }

    @Test
    void deleteRecipe_recipeNotOwned_returnError() {
        // Given
        var auth = authentication(1L);
        var id = 1L;

        doThrow(new RecipeNotOwnedException("Recipe not owned"))
            .when(kitchenService).deleteRecipe(any(), any());

        // When
        var resp = recipeController.deleteRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void deleteRecipe_validRequest_returnSuccess() {
        // Given
        var auth = authentication(1L);
        var id = 1L;

        // When
        var resp = recipeController.deleteRecipe(auth, id);

        // Then
        assertThat(resp)
            .isNotNull();

        assertThat(resp.getStatusCode())
            .isEqualTo(HttpStatus.NO_CONTENT);
    }

    private static Authentication authentication(Long id) {
        return new JwtAuthenticationToken(
            new Jwt("xyz", Instant.now(), Instant.now(), Map.of("x", "y"), Map.of("sub", id.toString()))
        );
    }

    private void mockAddRecipe() {
        when(kitchenService.addRecipe(any())).thenAnswer(inv -> {
            Recipe recipe = inv.getArgument(0);

            var builder = Recipe.fromPersistenceRecipeBuilder()
                .id(1L)
                .name(recipe.getName())
                .description(recipe.getDescription());

            var id = 1L;
            for (var ingredient : recipe.getIngredients()) {
                builder.ingredient(id++, ingredient.getName(), ingredient.getAmount().getValue(), ingredient.getAmount().getMeasure());
            }

            id = 1L;
            for (var methodStep : recipe.getMethodSteps()) {
                builder.methodStep(id++, methodStep.getText());
            }

            return builder
                .cookingTime(recipe.getCookingTime())
                .portionSize(recipe.getPortionSize().getValue(), recipe.getPortionSize().getMeasure())
                .cookId(recipe.getCookId().getId())
                .build().produce();
        });
    }
}