package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class RecipeUpdateTest {
    @ParameterizedTest
    @MethodSource("builderInvalidParamsProvider")
    void builder_invalidParams_throwException(RecipeUpdate.Builder builder, String[] expectedErrorFields, String[] unexpectedErrorFields) {
        // Given
        var factory = builder.build();

        // When
        var exception = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(exception.getErrors())
            .extracting(FieldError::name)
            .contains(expectedErrorFields)
            .doesNotContain(unexpectedErrorFields);
    }

    private static Stream<Arguments> builderInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                RecipeUpdate.builder()
                    .basicInformation(info -> info
                        .name("")
                        .description(null)
                        .cookingTime(Duration.ZERO)
                        .portionSize(BigDecimal.ZERO, "")
                    ),
                new String[] {
                    "basicInformation.name",
                    "basicInformation.description",
                    "basicInformation.cookingTime",
                    "basicInformation.portionSize.value",
                },
                new String[] {
                    "basicInformation.portionSize.measure",
                    "ingredients.ingredientsToAdd",
                    "steps.stepsToAdd",
                }
            ),
            Arguments.of(
                RecipeUpdate.builder()
                    .basicInformation(info -> info
                        .name("Boiled sausages")
                        .description("")
                        .cookingTime(Duration.ofMinutes(3))
                        .portionSize(new BigDecimal("3"), "pc")
                    )
                    .ingredients(ingredients -> ingredients
                        .ingredientToAdd("", BigDecimal.ZERO, null)
                        .ingredientToUpdate(null, "", BigDecimal.ZERO, null)
                        .ingredientToDelete(null)
                    )
                    .steps(steps -> steps
                        .stepToAdd("Diy")
                        .stepToUpdate(2L, "Diy")
                        .stepToDelete(3L)
                    ),
                new String[] {
                    "ingredients.ingredientsToAdd.name",
                    "ingredients.ingredientsToAdd.value",
                    "ingredients.ingredientsToAdd.measure",
                    "ingredients.ingredientsToUpdate.id",
                    "ingredients.ingredientsToUpdate.name",
                    "ingredients.ingredientsToUpdate.value",
                    "ingredients.ingredientsToUpdate.measure",
                    "ingredients.ingredientsToDelete.id",
                },
                new String[] {
                    "basicInformation.name",
                    "basicInformation.description",
                    "basicInformation.cookingTime",
                    "basicInformation.portionSize.value",
                    "basicInformation.portionSize.measure",
                    "steps.stepsToAdd.text",
                    "steps.stepsToUpdate.id",
                    "steps.stepsToUpdate.text",
                    "steps.stepsToDelete.id",
                }
            ),
            Arguments.of(
                RecipeUpdate.builder()
                    .basicInformation(info -> info
                        .name("Boiled sausages")
                        .description("")
                        .cookingTime(Duration.ofMinutes(3))
                        .portionSize(new BigDecimal("3"), "pc")
                    )
                    .ingredients(ingredients -> ingredients
                        .ingredientToAdd("sausage", new BigDecimal("3"), "pc")
                        .ingredientToUpdate(2L, "water", new BigDecimal("1"), "liter")
                        .ingredientToDelete(3L)
                    )
                    .steps(steps -> steps
                        .stepToAdd(null)
                        .stepToUpdate(null, "  ")
                        .stepToDelete(null)
                    ),
                new String[] {
                    "steps.stepsToAdd.text",
                    "steps.stepsToUpdate.id",
                    "steps.stepsToUpdate.text",
                    "steps.stepsToDelete.id",
                },
                new String[] {
                    "basicInformation.name",
                    "basicInformation.description",
                    "basicInformation.cookingTime",
                    "basicInformation.portionSize.value",
                    "basicInformation.portionSize.measure",
                    "ingredients.ingredientsToAdd.name",
                    "ingredients.ingredientsToAdd.value",
                    "ingredients.ingredientsToAdd.measure",
                    "ingredients.ingredientsToUpdate.id",
                    "ingredients.ingredientsToUpdate.name",
                    "ingredients.ingredientsToUpdate.value",
                    "ingredients.ingredientsToUpdate.measure",
                    "ingredients.ingredientsToDelete.id",
                }
            )
        );
    }

    @Test
    void builder_validBasicInfo_returnNewObject() {
        // Given
        var name = "Corn flakes";
        var description = "";
        var cookingTime = Duration.ofMinutes(3);
        var portionSizeValue = new BigDecimal("500");
        var portionSizeMeasure = "g";

        var factory = RecipeUpdate.builder()
            .basicInformation(info -> info
                .name(name)
                .description(description)
                .cookingTime(cookingTime)
                .portionSize(portionSizeValue, portionSizeMeasure)
            )
            .build();

        // When
        var recipeUpdate = factory.produce();

        // Then
        assertThat(recipeUpdate)
            .isNotNull();

        assertThat(recipeUpdate.getBasicInformation())
            .isPresent();

        var basicInformation = recipeUpdate.getBasicInformation().get();
        assertThat(basicInformation.getName())
            .isEqualTo(name);

        assertThat(basicInformation.getDescription())
            .isEqualTo(description);

        assertThat(basicInformation.getCookingTime())
            .isEqualTo(cookingTime);

        assertThat(basicInformation.getPortionSize())
            .extracting(Amount::getValue, Amount::getMeasure)
            .containsExactly(portionSizeValue, portionSizeMeasure);

        assertThat(recipeUpdate.getIngredients())
            .isEmpty();

        assertThat(recipeUpdate.getSteps())
            .isEmpty();
    }

    @Test
    void builder_validIngredients_returnNewObject() {
        // Given
        var ingredientToAddName = "Corn flakes";
        var ingredientToAddValue = new BigDecimal("250");
        var ingredientToAddMeasure = "g";
        var ingredientToUpdateId = 2L;
        var ingredientToUpdateName = "Milk";
        var ingredientToUpdateValue = new BigDecimal("250");
        var ingredientToUpdateMeasure = "ml";
        var ingredientToDeleteId = 3L;

        var factory = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToAdd(ingredientToAddName, ingredientToAddValue, ingredientToAddMeasure)
                .ingredientToUpdate(ingredientToUpdateId, ingredientToUpdateName, ingredientToUpdateValue, ingredientToUpdateMeasure)
                .ingredientToDelete(ingredientToDeleteId)
            )
            .build();

        // When
        var recipeUpdate = factory.produce();

        // Then
        assertThat(recipeUpdate)
            .isNotNull();

        assertThat(recipeUpdate.getBasicInformation())
            .isEmpty();

        assertThat(recipeUpdate.getIngredients())
            .isPresent();

        var ingredients = recipeUpdate.getIngredients().get();

        assertThat(ingredients.getIngredientsToAdd())
            .hasSize(1)
            .singleElement()
            .extracting(
                Ingredient::getName, Ingredient::getAmount, Ingredient::isPersisted
            )
            .containsExactly(
                ingredientToAddName, Amount.of(ingredientToAddValue, ingredientToAddMeasure).produce(), false
            );

        assertThat(ingredients.getIngredientsToUpdate())
            .hasSize(1)
            .singleElement()
            .extracting(
                ing -> ing.getId().getId(), Ingredient::getName,
                Ingredient::getAmount, Ingredient::isPersisted
            )
            .containsExactly(
                ingredientToUpdateId, ingredientToUpdateName,
                Amount.of(ingredientToUpdateValue, ingredientToUpdateMeasure).produce(), true
            );

        assertThat(ingredients.getIngredientsToDelete())
            .hasSize(1)
            .singleElement()
            .extracting(IngredientId::getId)
            .isEqualTo(ingredientToDeleteId);

        assertThat(recipeUpdate.getSteps())
            .isEmpty();
    }

    @Test
    void builder_validSteps_returnNewObject() {
        // Given
        var stepToAddText = "Diy";
        var stepToUpdateId = 2L;
        var stepToUpdateText = "Heat up the milk";
        var stepToDeleteId = 3L;

        var factory = RecipeUpdate.builder()
            .steps(steps -> steps
                .stepToAdd(stepToAddText)
                .stepToUpdate(stepToUpdateId, stepToUpdateText)
                .stepToDelete(stepToDeleteId)
            )
            .build();

        // When
        var recipeUpdate = factory.produce();

        // Then
        assertThat(recipeUpdate)
            .isNotNull();

        assertThat(recipeUpdate.getBasicInformation())
            .isEmpty();

        assertThat(recipeUpdate.getIngredients())
            .isEmpty();

        assertThat(recipeUpdate.getSteps())
            .isPresent();

        var steps = recipeUpdate.getSteps().get();

        assertThat(steps.getStepsToAdd())
            .hasSize(1)
            .singleElement()
            .extracting(Step::getText, Step::isPersisted)
            .containsExactly(stepToAddText, false);

        assertThat(steps.getStepsToUpdate())
            .hasSize(1)
            .singleElement()
            .extracting(step -> step.getId().getId(), Step::getText, Step::isPersisted)
            .containsExactly(stepToUpdateId, stepToUpdateText, true);

        assertThat(steps.getStepsToDelete())
            .hasSize(1)
            .singleElement()
            .extracting(StepId::getId)
            .isEqualTo(stepToDeleteId);
    }

    @Test
    void builder_validParamsMixed_returnNewObject() {
        // Given
        var factory = RecipeUpdate.builder()
            .ingredients(ingredients -> ingredients
                .ingredientToAdd("Potatoes", new BigDecimal("1"), "kg")
            )
            .steps(steps -> steps
                .stepToUpdate(2L, "Boil potatoes for about 30 mins")
            )
            .build();

        // When
        var recipeUpdate = factory.produce();

        // Then
        assertThat(recipeUpdate)
            .isNotNull();
    }
}