package pl.dkaluza.domaincore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationExecutorTest {

    @ParameterizedTest
    @CsvSource({
        "1, false",
        "0, true",
    })
    void validate_variousValidators_returnFailures(int length, boolean hasErrors) {
        // Given
        Validator validator = () -> {
            if (length > 0) {
                return null;
            }

            return new FieldError("length", "Number must be positive");
        };

        var validationExecutor = ValidationExecutor.of(List.of(validator));

        // When
        List<FieldError> errors = validationExecutor.validate();

        // Then
        if (hasErrors) {
            assertThat(errors)
                .extracting(FieldError::getName)
                .containsExactly("length");
        } else {
            assertThat(errors)
                .isEmpty();
        }
    }

    @ParameterizedTest
    @CsvSource({
        "1, '', 'name'",
        "0, 'Dawid', 'number'",
        "1, 'Dawid', ''",
    })
    void buildAndValidate_variousValidations_returnFailures(int number, String name, String expectedErrorsNamesJoint) {
        // Given
        var expectedErrorsNames = expectedErrorsNamesJoint.isBlank()
            ? new String[] {}
            : expectedErrorsNamesJoint.split(" ");

        // When
        var validationExecutor = ValidationExecutor.builder()
            .withValidation(number > 0, "number", "Number must be positive")
            .withValidation(() -> !name.isBlank(), "name", "Name must not be empty")
            .build();

        var errors = validationExecutor.validate();

        // Then
        assertThat(errors)
            .extracting(FieldError::getName)
            .containsExactly(expectedErrorsNames);
    }
}