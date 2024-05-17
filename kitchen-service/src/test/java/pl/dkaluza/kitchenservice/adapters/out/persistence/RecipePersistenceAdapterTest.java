package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.*;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.Stream;

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
            .methodStep("Diy")
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
            .methodStep("Diy")
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

    @Test
    void findRecipeById_nullParams_throwException() {
        assertThatThrownBy(
            () -> recipePersistenceAdapter.findRecipeById(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findRecipeById_nonExistingRecipe_returnEmptyOptional() {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());
        var recipe = recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("xyz")
                .description("")
                .ingredient("sausage", new BigDecimal(2), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(2), "pc")
                .cookId(1L)
                .build().produce()
        );

        var id = recipe.getId().getId() + 1;

        // When
        var optionalRecipe = recipePersistenceAdapter.findRecipeById(RecipeId.of(id).produce());

        // Then
        assertThat(optionalRecipe)
            .isEmpty();
    }

    @Test
    void findRecipeById_existingRecipe_returnRecipe() {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());
        var recipe = recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(2), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(2), "pc")
                .cookId(1L)
                .build().produce()
        );
        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Iced coffee")
                .description("")
                .ingredient("coffee", new BigDecimal(50), "g")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(50), "g")
                .cookId(1L)
                .build().produce()
        );

        var recipeId = recipe.getId();
        var id = recipeId.getId();

        // When
        var optionalRecipe = recipePersistenceAdapter.findRecipeById(RecipeId.of(id).produce());

        // Then
        assertThat(optionalRecipe)
            .hasValue(recipe);
    }

    @Test
    void findAllRecipes_nullParams_throwException() {
        assertThatThrownBy(
            () -> recipePersistenceAdapter.findAllRecipes(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("findAllRecipesParamsProvider")
    void findAllRecipes_variousParams_returnExpectedResult(
        PageRequest pageReq,
        String[] expectedRecipes,
        int expectedPageNo, int expectedTotalPages
    ) {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());
        cookPersistenceAdapter.saveCook(Cook.newCook(2L).produce());

        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(1L)
                .build().produce()
        );

        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Iced coffee")
                .description("")
                .ingredient("coffee", new BigDecimal(50), "g")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(50), "g")
                .cookId(1L)
                .build().produce()
        );

        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Toasts")
                .description("")
                .ingredient("Bread", new BigDecimal(4), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(2L)
                .build().produce()
        );

        // When
        var actualPage = recipePersistenceAdapter.findAllRecipes(pageReq);

        // Then
        assertThat(actualPage)
            .isNotNull();

        var actualItems = actualPage.getItems();
        assertThat(actualItems)
            .isNotNull()
            .extracting(Recipe::getName)
            .containsExactly(expectedRecipes);

        assertThat(actualPage.getPageNumber())
            .isEqualTo(expectedPageNo);

        assertThat(actualPage.getTotalPages())
            .isEqualTo(expectedTotalPages);
    }


    private static Stream<Arguments> findAllRecipesParamsProvider() {
        return Stream.of(
            Arguments.of(
                PageRequest.of(1, 10).produce(),
                new String[] { "Boiled sausages", "Iced coffee", "Toasts" },
                1,
                1
            ),
            Arguments.of(
                PageRequest.of(2, 1).produce(),
                new String[] { "Iced coffee" },
                2,
                3
            ),
            Arguments.of(
                PageRequest.of(5, 5).produce(),
                new String[] { },
                5,
                1
            )
        );
    }

    @Test
    void findRecipes_nullParams_throwException() {
        assertThatThrownBy(
            () -> recipePersistenceAdapter.findRecipes(null, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("findRecipesParamsProvider")
    void findRecipes_variousParams_returnExpectedResult(
        RecipeFilters filers, PageRequest pageReq,
        String[] expectedRecipes, int expectedPageNo, int expectedTotalPages
    ) {
        // Given
        cookPersistenceAdapter.saveCook(Cook.newCook(1L).produce());
        cookPersistenceAdapter.saveCook(Cook.newCook(2L).produce());

        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(1L)
                .build().produce()
        );

        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Iced coffee")
                .description("")
                .ingredient("coffee", new BigDecimal(50), "g")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(50), "g")
                .cookId(1L)
                .build().produce()
        );

        recipePersistenceAdapter.insertRecipe(
            Recipe.newRecipeBuilder()
                .name("Toasts")
                .description("")
                .ingredient("Bread", new BigDecimal(4), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(2L)
                .build().produce()
        );

        // When
        var actualPage = recipePersistenceAdapter.findRecipes(filers, pageReq);

        // Then
        assertThat(actualPage)
            .isNotNull();

        var actualItems = actualPage.getItems();
        assertThat(actualItems)
            .isNotNull()
            .extracting(Recipe::getName)
            .containsExactly(expectedRecipes);

        assertThat(actualPage.getPageNumber())
            .isEqualTo(expectedPageNo);

        assertThat(actualPage.getTotalPages())
            .isEqualTo(expectedTotalPages);
    }

    private static Stream<Arguments> findRecipesParamsProvider() {
        return Stream.of(
            Arguments.of(
                RecipeFilters.of(null, null),
                PageRequest.of(1, 10).produce(),
                new String[] { "Boiled sausages", "Iced coffee", "Toasts" },
                1,
                1
            ),
            Arguments.of(
                RecipeFilters.of("a", null),
                PageRequest.of(1, 10).produce(),
                new String[] { "Boiled sausages", "Toasts" },
                1,
                1
            ),
            Arguments.of(
                RecipeFilters.of(null, CookId.of(2L).produce()),
                PageRequest.of(1, 10).produce(),
                new String[] { "Toasts" },
                1,
                1
            ),
            Arguments.of(
                RecipeFilters.of(null, null),
                PageRequest.of(2, 1).produce(),
                new String[] { "Iced coffee" },
                2,
                3
            ),
            Arguments.of(
                RecipeFilters.of(null, null),
                PageRequest.of(5, 5).produce(),
                new String[] { },
                5,
                1
            )
            // TODO test scenario when no recipes matches given filters AND returned page is empty
        );
    }
}