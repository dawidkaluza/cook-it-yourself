package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class StepTest {
    @ParameterizedTest
    @MethodSource("newStepInvalidParamsProvider")
    void newStep_invalidParams_throwException(String text, String[] expectedFieldErrors) {
        // Given
        var factory = Step.newStep(text);

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly(expectedFieldErrors);
    }

    private static Stream<Arguments> newStepInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                null,
                new String[] { "text" }
            ),
            Arguments.of(
                "de",
                new String[] { "text" }
            ),
            Arguments.of(
                "   pl ",
                new String[] { "text" }
            ),
            Arguments.of(
                "   " + "c".repeat(16385) + "   ",
                new String[] { "text" }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("newStepValidParamsProvider")
    void newStep_validParams_returnCreatedObject(String text) {
        // Given, when
        var step = Step.newStep(text).produce();

        // When
        assertThat(step)
            .isNotNull()
            .extracting(Step::getText, Step::isPersisted)
            .contains(text.trim(), false);
    }

    private static Stream<String> newStepValidParamsProvider() {
        return Stream.of(
            "    Run   ",
            "Run",
            "   " + "d".repeat(16384) + " "
        );
    }

    @ParameterizedTest
    @MethodSource("fromPersistenceInvalidParamsProvider")
    void fromPersistence_invalidParams_throwException(Long id, String text, String[] expectedFieldErrors) {
        // Given
        var factory = Step.fromPersistence(id, text);

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly(expectedFieldErrors);
    }

    private static Stream<Arguments> fromPersistenceInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(null, "", new String[] { "id", "text" })
        );
    }

    @ParameterizedTest
    @MethodSource("fromPersistenceValidParamsProvider")
    void fromPersistence_validParams_returnCreatedObject(Long id, String text) {
        // Given, when
        var step = Step.fromPersistence(id, text).produce();

        // When
        assertThat(step)
            .isNotNull()
            .extracting(
                (obj) -> obj.getId().getId(),
                Step::getText,
                Step::isPersisted
            )
            .contains(
                id, text.trim(), true
            );
    }

    private static Stream<Arguments> fromPersistenceValidParamsProvider() {
        return Stream.of(
            Arguments.of(1L, "Run")
        );
    }
}