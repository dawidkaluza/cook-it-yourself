package pl.dkaluza.domaincore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class DefaultFactoryTest {
    @ParameterizedTest
    @CsvSource(value = {
        "NULL, true",
        "'   ', true",
        "Dawid, false",
    }, nullValues = { "NULL" })
    void validate_differentComponents_returnExpectedErrors(String name, boolean hasErrors) {
        // Given
        var factory = fancyNameFactory(name);

        // When
        var errors = factory.validate();

        // Then
        if (hasErrors) {
            assertThat(errors)
                .extracting(FieldError::name)
                .containsExactly("name");
        } else {
            assertThat(errors)
                .isEmpty();
        }
    }

    @Test
    void assemble_validationFailed_returnObject() {
        // Given
        var factory = fancyNameFactory("");

        // When
        var errors = factory.validate();
        var object = factory.assemble();

        // Then
        assertThat(errors)
            .isNotEmpty();

        assertThat(object)
            .isEqualTo("***  ***");
    }

    @Test
    void assemble_validationSucceeded_returnObject() {
        // Given
        var factory = fancyNameFactory("Dawid");

        // When
        var errors = factory.validate();
        var object = factory.assemble();

        // Then
        assertThat(errors)
            .isEmpty();

        assertThat(object)
            .isEqualTo("*** Dawid ***");
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'', true",
        "Dawid, false",
    })
    void review_differentComponents_returnExpectedErrors(String name, boolean hasErrors) {
        // Given
        var factory = fancyNameFactory(name);

        // When
        var errors = factory.review();

        // Then
        if (hasErrors) {
            assertThat(errors)
                .extracting(FieldError::name)
                .containsExactly("name");
        } else {
            assertThat(errors)
                .isEmpty();
        }

    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void produce_invalidComponents_throwException(boolean reviewBefore) {
        // Given
        var factory = fancyNameFactory("");

        // When
        List<FieldError> reviewErrors = List.of();
        if (reviewBefore) {
            reviewErrors = factory.review();
        }

        ValidationException e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e)
            .isNotNull();

        if (reviewBefore) {
            assertThat(e.getErrors())
                .isEqualTo(reviewErrors);
        }

        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly("name");
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void produce_validComponents_returnProducedObject(boolean reviewBefore) {
        // Given
        var factory = fancyNameFactory("Dawid");

        // When
        if (reviewBefore) {
            factory.review();
        }
        var object = factory.produce();

        // Then
        assertThat(object)
            .isEqualTo("*** Dawid ***");
    }

    private Factory<String> fancyNameFactory(String name) {
        return new DefaultFactory<>(
            ValidationExecutor.builder()
                .withValidation(name != null && !name.isBlank(), "name", "Name must not be blank")
                .build(),
            () -> "*** " + name + " ***"
        );
    }
}