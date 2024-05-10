package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.dkaluza.kitchenservice.adapters.out.persistence.InMemoryRecipePersistenceAdapter;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

class DefaultKitchenServiceTest {
    private RecipeRepository recipeRepository;
    private DefaultKitchenService kitchenService;

    @BeforeEach
    void beforeEach() {
        recipeRepository = new InMemoryRecipePersistenceAdapter();
        kitchenService = new DefaultKitchenService(recipeRepository);
    }

    @Test
    void addRecipe_nullParam_throwException() {
        assertThatThrownBy(
            () -> kitchenService.addRecipe(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addRecipe_validRecipe_returnNewRecipe() {
        // Given
        recipeRepository.insertCook(Cook.of(1L).produce());

        var newRecipe = Recipe.newRecipeBuilder()
            .name("xyz")
            .description("")
            .ingredient("sausage", new BigDecimal(2), "pc")
            .methodStep( "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(1L)
            .build().produce();

        // When
        var createdRecipe = kitchenService.addRecipe(newRecipe);

        // Then
        assertThat(createdRecipe)
            .isNotNull();

        assertThat(createdRecipe.isPersisted())
            .isTrue();

        for (var ingredient : createdRecipe.getIngredients()) {
            assertThat(ingredient.isPersisted())
                .isTrue();
        }

        for (var step : createdRecipe.getMethodSteps()) {
            assertThat(step.isPersisted())
                .isTrue();
        }
    }

}