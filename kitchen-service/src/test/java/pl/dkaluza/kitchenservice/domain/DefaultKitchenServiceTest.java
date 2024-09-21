package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.adapters.out.persistence.InMemoryCookPersistenceAdapter;
import pl.dkaluza.kitchenservice.adapters.out.persistence.InMemoryRecipePersistenceAdapter;
import pl.dkaluza.kitchenservice.domain.exceptions.IngredientNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotOwnedException;
import pl.dkaluza.kitchenservice.domain.exceptions.StepNotFoundException;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultKitchenServiceTest {
    private RecipeRepository recipeRepository;
    private DefaultKitchenService kitchenService;

    @BeforeEach
    void beforeEach() {
        recipeRepository = new InMemoryRecipePersistenceAdapter();
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
    void browseRecipes_nullParams_throwException() {
        assertThatThrownBy(
            () -> kitchenService.browseRecipes(null, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("browseRecipesParamsProvider")
    void browseRecipes_validRecipes_returnExpectedRecipes(
        RecipeFilters filters, PageRequest pageReq,
        String[] expectedRecipes, int expectedPageNo, int expectedTotalPages
    ) {
        kitchenService.registerCook(Cook.newCook(1L).produce());
        kitchenService.registerCook(Cook.newCook(2L).produce());

        kitchenService.addRecipe(
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

        kitchenService.addRecipe(
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

        kitchenService.addRecipe(
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
        var page = kitchenService.browseRecipes(filters, pageReq);

        // Then
        assertThat(page)
            .isNotNull();

        assertThat(page.getItems())
            .extracting(Recipe::getName)
            .containsExactly(expectedRecipes);

        assertThat(page.getPageNumber())
            .isEqualTo(expectedPageNo);

        assertThat(page.getTotalPages())
            .isEqualTo(expectedTotalPages);
    }


    private static Stream<Arguments> browseRecipesParamsProvider() {
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
                PageRequest.of(2, 1).produce(),
                new String[] { "Toasts" },
                2,
                2
            ),
            Arguments.of(
                RecipeFilters.of(null, CookId.of(2L).produce()),
                PageRequest.of(1, 10).produce(),
                new String[] { "Toasts" },
                1,
                1
            )
        );
    }

    @Test
    void viewRecipe_nullParams_throwException() {
        assertThatThrownBy(
            () -> kitchenService.viewRecipe(null, null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void viewRecipe_recipeNotFound_throwException() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());

        // When, then
        assertThatThrownBy(
            () -> kitchenService.viewRecipe(RecipeId.of(1L).produce(), cook.getId())
        ).isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void viewRecipe_recipeNotOwned_throwException() {
        // Given
        var firstCook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var secondCook = kitchenService.registerCook(Cook.newCook(2L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(firstCook.getId().getId())
                .build().produce()
        );

        // When, then
        assertThatThrownBy(
            () -> kitchenService.viewRecipe(recipe.getId(), secondCook.getId())
        ).isInstanceOf(RecipeNotOwnedException.class);
    }

    @Test
    void viewRecipe_validParams_returnRecipe() {
        // Given
        var firstCook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(firstCook.getId().getId())
                .build().produce()
        );

        // When
        var viewedRecipe = kitchenService.viewRecipe(recipe.getId(), firstCook.getId());

        // Then
        assertThat(viewedRecipe)
            .isEqualTo(recipe);
    }

    @ParameterizedTest
    @MethodSource("updateRecipeNullParamsProvider")
    void updateRecipe_nullParams_throwException(RecipeId recipeId, RecipeUpdate recipeUpdate, CookId cookId) {
        assertThatThrownBy(() -> kitchenService.updateRecipe(recipeId, recipeUpdate, cookId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> updateRecipeNullParamsProvider() {
        return Stream.of(
            Arguments.of(
                null, null, null
            ),
            Arguments.of(
                RecipeId.of(1L).produce(), RecipeUpdate.builder().build().produce(), null
            ),
            Arguments.of(
                RecipeId.of(1L).produce(), null, CookId.of(1L).produce()
            ),
            Arguments.of(
                null, RecipeUpdate.builder().build().produce(), CookId.of(1L).produce()
            )
        );
    }

    @Test
    void updateRecipe_recipeNotFound_throwException() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(cook.getId().getId())
                .build().produce()
        );

        var recipeId = RecipeId.of(2L).produce();
        var recipeUpdate = RecipeUpdate.builder().build().produce();
        var cookId = cook.getId();

        // When, then
        assertThatThrownBy(() -> kitchenService.updateRecipe(recipeId, recipeUpdate, cookId))
            .isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void updateRecipe_recipeNotOwned_throwException() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(cook.getId().getId())
                .build().produce()
        );

        var recipeId = recipe.getId();
        var recipeUpdate = RecipeUpdate.builder().build().produce();
        var cookId = CookId.of(2L).produce();

        // When, then
        assertThatThrownBy(() -> kitchenService.updateRecipe(recipeId, recipeUpdate, cookId))
            .isInstanceOf(RecipeNotOwnedException.class);
    }

    @Test
    void updateRecipe_ingredientNotFound_throwException() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(cook.getId().getId())
                .build().produce()
        );

        var recipeId = recipe.getId();
        var recipeUpdate = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToUpdate(6L, "Diy", new BigDecimal(1), "")
                .ingredientToDelete(5L)
            )
            .build().produce();
        var cookId = cook.getId();

        // When, then
        assertThatThrownBy(() -> kitchenService.updateRecipe(recipeId, recipeUpdate, cookId))
            .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    void updateRecipe_stepNotFound_throwException() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(cook.getId().getId())
                .build().produce()
        );

        var recipeId = recipe.getId();
        var recipeUpdate = RecipeUpdate.builder()
            .steps(steps -> steps
                .stepToUpdate(6L, "Boil for about a minute")
                .stepToDelete(5L)
            )
            .build().produce();
        var cookId = cook.getId();

        // When, then
        assertThatThrownBy(() -> kitchenService.updateRecipe(recipeId, recipeUpdate, cookId))
            .isInstanceOf(StepNotFoundException.class);
    }

    @Test
    void updateRecipe_validParams_returnUpdatedRecipe() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .ingredient("water", new BigDecimal(500), "ml")
                .methodStep("Pour water into a pot and make it boil")
                .methodStep("Add sausages and boil for about a minute")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(cook.getId().getId())
                .build().produce()
        );

        var recipeId = recipe.getId();
        var firstIngredientId = recipe.getIngredients().get(0).getId().getId();
        var secondIngredientId = recipe.getIngredients().get(1).getId().getId();
        var firstStepId = recipe.getMethodSteps().get(0).getId().getId();
        var secondStepId = recipe.getMethodSteps().get(1).getId().getId();
        var recipeUpdate = RecipeUpdate.builder()
            .basicInformation(info -> info
                .name("Sausages")
                .description("How to boil sausages properly")
                .cookingTime(Duration.ofMinutes(5))
                .portionSize(new BigDecimal("2"), "")
            )
            .ingredients(ingredients -> ingredients
                .ingredientToDelete(secondIngredientId)
                .ingredientToUpdate(firstIngredientId, "sausage", new BigDecimal("2"), "")
                .ingredientToAdd("water", new BigDecimal(700), "ml")
            )
            .steps(steps -> steps
                .stepToDelete(firstStepId)
                .stepToUpdate(secondStepId, "Boil for about a minute")
                .stepToAdd("After boiling, take them out onto a plate. Bon appetit!")
            )
            .build().produce();
        var cookId = cook.getId();

        // When
        var upgrededRecipe = kitchenService.updateRecipe(recipeId, recipeUpdate, cookId);

        // Then
        assertThat(upgrededRecipe)
            .isNotNull();
    }

    @ParameterizedTest
    @CsvSource(value = {
        "NULL, NULL",
        "1, NULL",
        "NULL, 1",
    }, nullValues = "NULL")
    void deleteRecipe_nullParams_throwException(Long recipeId, Long cookId) {
        assertThatThrownBy(
            () -> kitchenService.deleteRecipe(
                recipeId == null ? null : RecipeId.of(recipeId).produce(),
                cookId == null ? null : CookId.of(cookId).produce()
            )
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteRecipe_recipeNotFound_throwException() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipeId = RecipeId.of(1L).produce();

        // When, then
        assertThatThrownBy(
            () -> kitchenService.deleteRecipe(recipeId, cook.getId())
        ).isInstanceOf(RecipeNotFoundException.class);
    }

    @Test
    void deleteRecipe_recipeNotOwned_throwException() {
        // Given
        var firstCook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var secondCook = kitchenService.registerCook(Cook.newCook(2L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(firstCook.getId().getId())
                .build().produce()
        );

        // When, then
        assertThatThrownBy(
            () -> kitchenService.deleteRecipe(recipe.getId(), secondCook.getId())
        ).isInstanceOf(RecipeNotOwnedException.class);
    }

    @Test
    void deleteRecipe_validParams_recipeProperlyDeleted() {
        // Given
        var cook = kitchenService.registerCook(Cook.newCook(1L).produce());
        var recipe = kitchenService.addRecipe(
            Recipe.newRecipeBuilder()
                .name("Boiled sausages")
                .description("")
                .ingredient("sausage", new BigDecimal(3), "pc")
                .methodStep("Diy")
                .cookingTime(Duration.ofMinutes(3))
                .portionSize(new BigDecimal(3), "pc")
                .cookId(cook.getId().getId())
                .build().produce()
        );

        // When
        kitchenService.deleteRecipe(recipe.getId(), cook.getId());

        // Then
        var optionalRecipe = recipeRepository.findRecipeById(recipe.getId());
        assertThat(optionalRecipe)
            .isEmpty();
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