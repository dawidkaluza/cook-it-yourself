package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.adapters.out.persistence.InMemoryCookPersistenceAdapter;
import pl.dkaluza.kitchenservice.adapters.out.persistence.InMemoryRecipePersistenceAdapter;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultKitchenServiceTest {
    private DefaultKitchenService kitchenService;

    @BeforeEach
    void beforeEach() {
        var recipeRepository = new InMemoryRecipePersistenceAdapter();
        var cookRepository = new InMemoryCookPersistenceAdapter();
        kitchenService = new DefaultKitchenService(recipeRepository, cookRepository);
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
        kitchenService.registerCook(Cook.newCook(1L).produce());

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

    @Test
    void registerCook_nullCook_throwException() {
        assertThatThrownBy(
            () -> kitchenService.registerCook(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = { 1L, 2L })
    void registerCook_persistedCook_throwException(Long id) {
        // Given
        kitchenService.registerCook(Cook.newCook(1L).produce());

        // When
        // NOTE: it does not matter if cook is already registered or not - only object's state is verified.
        assertThatThrownBy(
            () -> kitchenService.registerCook(Cook.fromPersistence(id).produce())
        ).isInstanceOf(ObjectAlreadyPersistedException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = { 1L, 2L })
    void registerCook_newCook_returnPersisted(Long id) {
        // Given
        kitchenService.registerCook(Cook.newCook(1L).produce());

        // When
        var registeredCook = kitchenService.registerCook(Cook.newCook(id).produce());

        // Then
        assertThat(registeredCook)
            .isNotNull()
            .extracting(cook -> cook.getId().getId(), Cook::isPersisted)
            .containsExactly(id, true);
    }
}