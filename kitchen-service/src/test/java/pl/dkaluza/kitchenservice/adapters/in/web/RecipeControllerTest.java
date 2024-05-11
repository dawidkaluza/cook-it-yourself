package pl.dkaluza.kitchenservice.adapters.in.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

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
            kitchenService
        );
        recipeController = new RecipeController(facade);
    }

    @Test
    void addRecipe_invalidData_returnErrorResponse() {
        // Given
        mockAddRecipe();

        var auth = authentication(3L);
        var reqBody = new AddRecipeRequest(
            "", "",
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
            .contains("name");
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

            assertThat(respIngredient.amount())
                .isEqualTo(reqIngredient.amount());

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
            .extracting(RecipeResponse.PortionSize::amount, RecipeResponse.PortionSize::measure)
            .containsExactly(reqBody.portionSize().amount(), reqBody.portionSize().measure());

        assertThat(respBody.cookId())
            .isEqualTo(userId);
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

    private Authentication authentication(Long id) {
        return new JwtAuthenticationToken(
            new Jwt("xyz", Instant.now(), Instant.now(), Map.of("x", "y"), Map.of("id", id.toString()))
        );
    }
}