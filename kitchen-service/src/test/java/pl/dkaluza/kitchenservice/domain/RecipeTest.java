package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.exceptions.IngredientNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.StepNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class RecipeTest {
    @ParameterizedTest
    @MethodSource("newRecipeBuilderInvalidParamsProvider")
    void newRecipeBuilder_invalidParams_throwException(Recipe.NewRecipeBuilder builder, String[] expectedErrorFields) {
        // Given
        var factory = builder.build();

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .contains(expectedErrorFields);
    }

    private static Stream<Arguments> newRecipeBuilderInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                Recipe.newRecipeBuilder()
                    .name(null)
                    .description(null)
                    .cookingTime(Duration.ZERO)
                    .portionSize(null, null)
                    .cookId(null),
                new String[] {
                    "name",
                    "description",
                    "ingredients",
                    "methodSteps",
                    "cookingTime",
                    "portionSize.value", "portionSize.measure",
                    "cookId"
                }
            ),
            Arguments.of(
                Recipe.newRecipeBuilder()
                    .name("aa")
                    .description("b".repeat(16385))
                    .ingredient(null, null, null)
                    .methodStep(null)
                    .cookingTime(Duration.ZERO)
                    .portionSize(null, null)
                    .cookId(null),
                new String[] {
                    "name",
                    "description",
                    "ingredients.name", "ingredients.value", "ingredients.measure",
                    "methodSteps.text",
                    "cookingTime",
                    "portionSize.value", "portionSize.measure",
                    "cookId"
                }
            ),
            Arguments.of(
                Recipe.newRecipeBuilder()
                    .name("   aa  ")
                    .description(" " + "b".repeat(16385) + "   ")
                    .cookingTime(Duration.ofMillis(1))
                    .portionSize(null, null)
                    .cookId(null),
                new String[] {
                    "name",
                    "description",
                    "ingredients",
                    "methodSteps",
                    "cookingTime",
                    "portionSize.value", "portionSize.measure",
                    "cookId"
                }
            ),
            Arguments.of(
                Recipe.newRecipeBuilder()
                    .name("   " + "a".repeat(257) + " ")
                    .description("")
                    .cookingTime(Duration.ofSeconds(1))
                    .portionSize(null, null)
                    .cookId(null),
                new String[] {
                    "name",
                    "ingredients",
                    "methodSteps",
                    "portionSize.value", "portionSize.measure",
                    "cookId"
                }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("newRecipeBuilderValidParamsProvider")
    void newRecipeBuilder_validParams_returnNewObjectWithGivenParams(
        String name, String description,
        String ingredientName, BigDecimal ingredientValue, String ingredientMeasure,
        String stepText,
        Duration cookingTime,
        BigDecimal portionSizeValue, String portionSizeMeasure,
        Long cookId
    ) {
        // When
        var recipe = Recipe.newRecipeBuilder()
            .name(name)
            .description(description)
            .ingredient(ingredientName, ingredientValue, ingredientMeasure)
            .methodStep(stepText)
            .cookingTime(cookingTime)
            .portionSize(portionSizeValue, portionSizeMeasure)
            .cookId(cookId)
            .build().produce();

        // Then
        assertThat(recipe)
            .isNotNull();

        assertThat(recipe.getName())
            .isEqualTo(name.trim());

        assertThat(recipe.getDescription())
            .isEqualTo(description.trim());

        assertThat(recipe.getIngredients())
            .singleElement()
            .extracting(
                Ingredient::getName, Ingredient::getAmount
            ).contains(
                ingredientName, Amount.of(ingredientValue, ingredientMeasure).produce()
            );

        assertThat(recipe.getMethodSteps())
            .singleElement()
            .extracting(Step::getText)
            .isEqualTo(stepText);

        assertThat(recipe.getCookingTime())
            .isEqualTo(cookingTime);

        assertThat(recipe.getPortionSize())
            .isEqualTo(Amount.of(portionSizeValue, portionSizeMeasure).produce());

        assertThat(recipe.getCookId().getId())
            .isEqualTo(cookId);
    }

    private static Stream<Arguments> newRecipeBuilderValidParamsProvider() {
        return Stream.of(
            Arguments.of(
                "Boiled sausages", "How to boil sausages",
                "sausage", new BigDecimal(2), "pc",
                "Boil sausages for about 3 mins",
                Duration.ofMinutes(3), new BigDecimal(2), "pc",
                1L
            ),
            Arguments.of(
                "   xyz ", "   " + "a".repeat(16384) + " ",
                "sausage", new BigDecimal(1), "pc",
                "diy",
                Duration.ofSeconds(1), new BigDecimal(1), "pc",
                2L
            ),
            Arguments.of(
                "a".repeat(256), "a".repeat(16384),
                "sausage", new BigDecimal(1), "",
                "diy",
                Duration.ofSeconds(1), new BigDecimal(1), "",
                3L
            )
        );
    }

    @Test
    void fromPersistenceBuilder_invalidParams_throwException() {
        // Given
        var factory = Recipe.fromPersistenceRecipeBuilder()
            .id(null)
            .name("xyz")
            .description("")
            .ingredient(null, "sausage", new BigDecimal(2), "pc")
            .methodStep(null, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(1L)
            .build();

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .contains("id", "ingredients.id", "methodSteps.id");
    }

    @Test
    void fromPersistenceBuilder_validParams_returnCreatedObject() {
        // Given
        var recipeId = 55L;
        var ingredientId = 66L;
        var methodStepId = 77L;

        var factory = Recipe.fromPersistenceRecipeBuilder()
            .id(recipeId)
            .name("xyz")
            .description("")
            .ingredient(ingredientId, "sausage", new BigDecimal(2), "pc")
            .methodStep(methodStepId, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(1L)
            .build();

        // When
        var recipe = factory.produce();

        // Then
        assertThat(recipe)
            .isNotNull();

        assertThat(recipe.getId().getId())
            .isEqualTo(recipeId);

        assertThat(recipe.getIngredients())
            .singleElement()
            .extracting(ingredient -> ingredient.getId().getId())
            .isEqualTo(ingredientId);

        assertThat(recipe.getMethodSteps())
            .singleElement()
            .extracting(step -> step.getId().getId())
            .isEqualTo(methodStepId);
    }

    @ParameterizedTest
    @CsvSource({
        "1, true",
        "2, false"
    })
    void iwOwnedBy_variousParams_returnExpectedResponse(Long cookIdValue, boolean expectedResult) {
        // Given
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

        var cookId = CookId.of(cookIdValue).produce();

        // When, then
        assertThat(recipe.isOwnedBy(cookId))
            .isEqualTo(expectedResult);

    }

    @Test
    void validate_invalidIngredientsToUpdate_throwException() {
        // Given
        var recipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(2L, "sausage", new BigDecimal(2), "pc")
            .methodStep(3L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(4L)
            .build().produce();

        var recipeUpdate = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToUpdate(1L, "sausages", new BigDecimal(2), "pc")
                .ingredientToDelete(2L)
            )
            .steps(steps -> steps
                .stepToUpdate(3L, "diy!")
            )
            .build().produce();

        // When, then
        assertThatThrownBy(() -> recipe.validate(recipeUpdate))
            .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    void validate_invalidIngredientsToDelete_throwException() {
        // Given
        var recipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(2L, "sausage", new BigDecimal(2), "pc")
            .methodStep(3L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(4L)
            .build().produce();

        var recipeUpdate = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToUpdate(2L, "sausages", new BigDecimal(2), "pc")
                .ingredientToDelete(1L)
            )
            .steps(steps -> steps
                .stepToUpdate(3L, "diy!")
            )
            .build().produce();

        // When, then
        assertThatThrownBy(() -> recipe.validate(recipeUpdate))
            .isInstanceOf(IngredientNotFoundException.class);
    }

    @Test
    void validate_invalidStepsToUpdate_throwException() {
        // Given
        var recipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(2L, "sausage", new BigDecimal(2), "pc")
            .methodStep(3L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(4L)
            .build().produce();

        var recipeUpdate = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToUpdate(2L, "sausages", new BigDecimal(2), "pc")
            )
            .steps(steps -> steps
                .stepToUpdate(1L, "diy!")
                .stepToDelete(3L)
            )
            .build().produce();

        // When, then
        assertThatThrownBy(() -> recipe.validate(recipeUpdate))
            .isInstanceOf(StepNotFoundException.class);
    }

    @Test
    void validate_invalidStepsToDelete_throwException() {
        // Given
        var recipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(2L, "sausage", new BigDecimal(2), "pc")
            .methodStep(3L, "Diy")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(4L)
            .build().produce();

        var recipeUpdate = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToUpdate(2L, "sausages", new BigDecimal(2), "pc")
            )
            .steps(steps -> steps
                .stepToUpdate(3L, "diy!")
                .stepToDelete(1L)
            )
            .build().produce();

        // When, then
        assertThatThrownBy(() -> recipe.validate(recipeUpdate))
            .isInstanceOf(StepNotFoundException.class);
    }

    @Test
    void validate_validUpdate_noException() {
        // Given
        var recipe = Recipe.fromPersistenceRecipeBuilder()
            .id(1L)
            .name("Boiled sausages")
            .description("")
            .ingredient(2L, "sausage", new BigDecimal(2), "pc")
            .ingredient(3L, "water", new BigDecimal(500), "ml")
            .methodStep(4L, "Put water into a pot and make it boil")
            .methodStep(5L, "Once it start boiling, add sausages and boil on medium power for about a minute")
            .cookingTime(Duration.ofMinutes(3))
            .portionSize(new BigDecimal(2), "pc")
            .cookId(6L)
            .build().produce();

        var recipeUpdate = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToUpdate(2L, "sausages", new BigDecimal(2), "pc")
                .ingredientToDelete(3L)
            )
            .steps(steps -> steps
                .stepToUpdate(4L, "Really you need a recipe for that?")
                .stepToDelete(5L)
            )
            .build().produce();

        // When, then
        recipe.validate(recipeUpdate);
    }
}