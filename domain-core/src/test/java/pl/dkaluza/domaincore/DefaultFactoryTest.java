package pl.dkaluza.domaincore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class DefaultFactoryTest {

    @ParameterizedTest
    @MethodSource("validateDataProvider")
    void validate_differentComponents_returnExpectedErrors(String name, List<String> expectedErrors) {
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
        assertThat(errors)
            .extracting(FieldError::getName)
            .containsExactlyElementsOf(expectedErrors);
    }

    private static Stream<Arguments> validateDataProvider() {
        return Stream.of(
            Arguments.of(null, List.of("name"))
        );
    }
}