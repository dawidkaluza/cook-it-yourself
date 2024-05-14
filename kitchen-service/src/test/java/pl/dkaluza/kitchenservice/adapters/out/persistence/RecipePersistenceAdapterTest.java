package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.*;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipePersistenceAdapterTest {
    private InMemoryRecipePersistenceAdapter recipePersistenceAdapter;
    private InMemoryCookPersistenceAdapter cookPersistenceAdapter;

    @BeforeEach
    void beforeEach() {
        recipePersistenceAdapter = new InMemoryRecipePersistenceAdapter();
        cookPersistenceAdapter = new InMemoryCookPersistenceAdapter();
    }

    @Test
    void insertRecipe_nullParam_throwException() {
        assertThatThrownBy(
            () -> recipePersistenceAdapter.insertRecipe(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertRecipe_alreadyPersistedObjects_throwException() {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());

        var recipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("xyz")
            .description("")
            .ingredient(1L, "sausage", new BigDecimal(2), "pc")
            .methodStep(1L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(1L)
            .build().produce();

        // When, then
        assertThatThrownBy(
            () -> recipePersistenceAdapter.insertRecipe(recipe)
        ).isInstanceOf(ObjectAlreadyPersistedException.class);
    }

    @Test
    void insertRecipe_notExistingCookId_throwException() {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());

        var recipe = Recipe.newRecipeBuilder()
            .name("xyz")
            .description("")
            .ingredient("sausage", new BigDecimal(2), "pc")
            .methodStep( "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(3L)
            .build().produce();

        // When, then
        assertThatThrownBy(
            () -> recipePersistenceAdapter.insertRecipe(recipe)
        ).isInstanceOf(CookNotFoundException.class);
    }

    @Test
    void insertRecipe_newRecipe_returnInsertedRecipe() {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());

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
        var insertedRecipe = recipePersistenceAdapter.insertRecipe(newRecipe);

        // Then
        assertThat(insertedRecipe)
            .isNotNull();

        assertThat(insertedRecipe.getName())
            .isEqualTo(newRecipe.getName());

        assertThat(insertedRecipe.getDescription())
            .isEqualTo(newRecipe.getDescription());

        assertThat(insertedRecipe.getIngredients())
            .hasSize(1);

        var newIngredient = newRecipe.getIngredients().get(0);
        var insertedIngredient = insertedRecipe.getIngredients().get(0);

        assertThat(insertedIngredient)
            .isNotNull();

        assertThat(insertedIngredient.getId())
            .isNotNull();

        assertThat(insertedIngredient.isPersisted())
            .isTrue();

        assertThat(insertedIngredient.getName())
            .isEqualTo(newIngredient.getName());

        assertThat(insertedIngredient.getAmount())
            .isEqualTo(newIngredient.getAmount());

        assertThat(insertedRecipe.getMethodSteps())
            .hasSize(1);

        var newStep = newRecipe.getMethodSteps().get(0);
        var insertedStep = insertedRecipe.getMethodSteps().get(0);

        assertThat(insertedStep.getId())
            .isNotNull();

        assertThat(insertedStep.isPersisted())
            .isTrue();

        assertThat(insertedStep.getText())
            .isEqualTo(newStep.getText());

        assertThat(insertedRecipe.getCookingTime())
            .isEqualTo(newRecipe.getCookingTime());

        assertThat(insertedRecipe.getPortionSize())
            .isEqualTo(newRecipe.getPortionSize());

        assertThat(insertedRecipe.getCookId())
            .isEqualTo(newRecipe.getCookId());
    }
}