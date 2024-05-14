package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class CookTest {
    @ParameterizedTest
    @MethodSource("ofInvalidParamsProvider")
    void of_invalidId_throwException(Factory<Cook> factory) {
        // Given, when
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly("id");
    }

    private static Stream<Factory<Cook>> ofInvalidParamsProvider() {
        return Stream.of(
            Cook.newCook(0L),
            Cook.fromPersistence(0L)
        );
    }

    @ParameterizedTest
    @MethodSource("ofValidParamsProvider")
    void of_validId_returnCreatedObject(Factory<Cook> factory, Long id, boolean isPersisted) {
        // Given
        // When
        var cook = factory.produce();

        // Then
        assertThat(cook)
            .isNotNull();

        assertThat(cook.getId())
            .isEqualTo(CookId.of(id).produce());

        assertThat(cook.isPersisted())
            .isEqualTo(isPersisted);
    }

    private static Stream<Arguments> ofValidParamsProvider() {
        return Stream.of(
            Arguments.of(Cook.newCook(1L), 1L, false),
            Arguments.of(Cook.fromPersistence(1L), 1L, true)
        );
    }
}