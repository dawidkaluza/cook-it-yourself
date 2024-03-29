package pl.dkaluza.domaincore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultFactoryTest {
    @ParameterizedTest
    @CsvSource(value = {
        "NULL, true",
        "'   ', true",
        "Dawid, false",
    }, nullValues = { "NULL" })
    void validate_differentComponents_returnExpectedErrors(String name, boolean hasErrors) {
        // Given
        var factory = new DefaultFactory<>(
            ValidationExecutor.builder()
                .withValidation(name != null && !name.isBlank(), "name", "Name must not be blank")
                .build(),
            () -> "*** " + name + " ***"
        );

        // When
        var errors = factory.validate();

        // Then
        if (hasErrors) {
            assertThat(errors)
                .extracting(FieldError::getName)
                .containsExactly("name");
        } else {
            assertThat(errors)
                .isEmpty();
        }
    }
}