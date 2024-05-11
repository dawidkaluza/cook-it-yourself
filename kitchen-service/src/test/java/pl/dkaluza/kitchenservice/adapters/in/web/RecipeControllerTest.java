package pl.dkaluza.kitchenservice.adapters.in.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
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

        var auth = new JwtAuthenticationToken(
            new Jwt("xyz", Instant.now(), Instant.now(), Map.of("x", "y"), Map.of("id", "3"))
        );

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

    void addRecipe_alreadyPersistedData_returnErrorResponse() {

    }

    void addRecipe_notExistingCookId_returnErrorResponse() {

    }

    void addRecipe_validRequest_returnCreatedRecipe() {

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