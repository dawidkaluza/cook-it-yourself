package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class IngredientTest {
    @ParameterizedTest
    @MethodSource("newIngredientInvalidParamsProvider")
    void newIngredient_invalidParams_throwException(String name, BigDecimal value, String measure, String[] expectedErrorFields) {
        // Given
        var factory = Ingredient.newIngredient(name, value, measure);

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

    private static Stream<Arguments> newIngredientInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                null, null, null,
                new String[] { "name", "value", "measure"}
            ),
            Arguments.of(
                " in ", BigDecimal.ZERO, null,
                new String[] { "name", "measure"}
            ),
            Arguments.of(
                "d".repeat(257), null, "",
                new String[] { "name", "value"}
            )
        );
    }

    @ParameterizedTest
    @MethodSource("newIngredientValidParamsProvider")
    void newIngredient_validParams_returnProducedObject(String name, BigDecimal value, String measure) {
        // Given, when
        var ingredient = Ingredient.newIngredient(name, value, measure).produce();

        // Then
        assertThat(ingredient)
            .isNotNull()
            .extracting(
                Ingredient::getId, Ingredient::getName,
                (obj) -> obj.getAmount().getValue(),
                (obj) -> obj.getAmount().getMeasure()
            ).containsExactly(
                null, name.trim(),
                value, measure
            );

        assertThat(ingredient.isPersisted())
            .isFalse();
    }

    private static Stream<Arguments> newIngredientValidParamsProvider() {
        return Stream.of(
            Arguments.of("Onion", new BigDecimal("1"), "pc"),
            Arguments.of(" Ham   ", new BigDecimal("1"), "pc"),
            Arguments.of(" " + "H".repeat(256) + "    ", new BigDecimal("0.1"), "kg")
        );
    }

    @ParameterizedTest
    @MethodSource("fromPersistenceInvalidParams")
    void fromPersistence_invalidParams_throwException(Long id, String name, BigDecimal value, String measure, String[] expectedFieldErrors) {
        // Given
        var factory = Ingredient.fromPersistence(id, name, value, measure);

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .contains(expectedFieldErrors);
    }

    private static Stream<Arguments> fromPersistenceInvalidParams() {
        return Stream.of(
            Arguments.of(
                null, "Onion", new BigDecimal(1), "pc",
                new String[] { "id" }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("fromPersistenceValidParamsProvider")
    void fromPersistence_validParams_returnProducedObject(Long id, String name, BigDecimal value, String measure) {
        // Given, when
        var ingredient = Ingredient.fromPersistence(id, name, value, measure).produce();

        // Then
        assertThat(ingredient)
            .isNotNull()
            .extracting(
                (obj) -> obj.getId().getId(), Ingredient::getName,
                (obj) -> obj.getAmount().getValue(),
                (obj) -> obj.getAmount().getMeasure()
            )
            .containsExactly(
                id, name.trim(),
                value, measure
            );

        assertThat(ingredient.isPersisted())
            .isTrue();
    }

    private static Stream<Arguments> fromPersistenceValidParamsProvider() {
        return Stream.of(
            Arguments.of(3L, "Onion", new BigDecimal("1"), "pc"),
            Arguments.of(3L, " Onion   ", new BigDecimal("1"), "pc")
        );
    }
}