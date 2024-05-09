package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class AmountTest {
    @ParameterizedTest
    @MethodSource("newAmountInvalidParamsProvider")
    void of_invalidParams_throwException(BigDecimal value, String measure, String[] expectedErrorFields) {
        // Given
        var factory = Amount.of(value, measure);

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e)
            .isNotNull();

        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly(expectedErrorFields);
    }

    private static Stream<Arguments> newAmountInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                null, null,
                new String[] { "value", "measure" }
            ),
            Arguments.of(
                BigDecimal.ZERO, "c".repeat(33),
                new String[] { "measure" }
            ),
            Arguments.of(
                new BigDecimal(-1), "",
                new String[] { "value" }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("newAmountValidParamsProvider")
    void of_validParams_returnProducedAmount(String value, String measure) {
        // Given, when
        var amount = Amount.of(new BigDecimal(value), measure).produce();

        // When
        assertThat(amount)
            .isNotNull()
            .extracting(Amount::getValue, Amount::getMeasure)
            .containsExactly(new BigDecimal(value), measure.trim());
    }

    private static Stream<Arguments> newAmountValidParamsProvider() {
        return Stream.of(
            Arguments.of("0.1", ""),
            Arguments.of("1.0", "kg"),
            Arguments.of("1213213.333", "g".repeat(32)),
            Arguments.of("2.0", " " + "g".repeat(32) + " ")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "0, ''",
        "0.0, 'kg'"
    })
    void of_paramsBeingZero_returnZero(String value, String measure) {
        // Given, when
        var amount = Amount.of(new BigDecimal(value), measure).produce();

        // Then
        assertThat(amount)
            .isNotNull()
            .extracting(Amount::getValue, Amount::getMeasure)
            .containsExactly(BigDecimal.ZERO, "");
    }

    @ParameterizedTest
    @MethodSource("equalsParamsProvider")
    void equals_variousObjects_returnExpectedResult(Amount firstAmount, Object secondAmount, boolean expectedResult) {
        assertThat(firstAmount.equals(secondAmount))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> equalsParamsProvider() {
        var amount = Amount.of(new BigDecimal(2), "kg").produce();
        return Stream.of(
            Arguments.of(amount, null, false),
            Arguments.of(amount, new Object(), false),
            Arguments.of(amount, Amount.of(new BigDecimal(2), "g").produce(), false),
            Arguments.of(amount, Amount.of(new BigDecimal(2000), "g").produce(), false),
            Arguments.of(amount, Amount.of(new BigDecimal("1"), "kg").produce(), false),
            Arguments.of(amount, Amount.of(new BigDecimal("3"), "kg").produce(), false),
            Arguments.of(amount, amount, true),
            Arguments.of(amount, Amount.of(new BigDecimal("2"), "kg").produce(), true),
            Arguments.of(amount, Amount.of(new BigDecimal("2.0"), "kg").produce(), true)
        );
    }
}