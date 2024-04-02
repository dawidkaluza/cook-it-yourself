package pl.dkaluza.userservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class UserNameTest {
    @ParameterizedTest
    @MethodSource("invalidNameParamsProvider")
    void of_invalidParams_throwException(String name) {
        // Given
        var factory = UserName.of(name);

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
            .containsExactly("name");
    }

    private static Stream<String> invalidNameParamsProvider() {
        return Stream.of(
            null,
            "dd",
            "d".repeat(129),
            "   D    "
        );
    }

    @ParameterizedTest
    @MethodSource("validNameParamsProvider")
    void of_validParams_returnObject(String name) {
        // Given
        var factory = UserName.of(name);

        // When
        var nameObject = factory.produce();

        // Then
        assertThat(nameObject)
            .isNotNull()
            .extracting(UserName::getValue)
            .isEqualTo(name);

    }

    private static Stream<String> validNameParamsProvider() {
        return Stream.of(
            "Daw",
            "D D",
            "D".repeat(128)
        );
    }

    @ParameterizedTest
    @MethodSource("equalParamsProvider")
    void equal_variousObjects_returnExpectedResult(UserName first, Object second, boolean expectedResult) {
        assertThat(first.equals(second))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> equalParamsProvider() {
        var userName1 = UserName.of("Dawid").produce();
        var userName1Clone = UserName.of("Dawid").produce();
        var username2 = UserName.of("Gabriel").produce();
        return Stream.of(
            Arguments.of(userName1, null, false),
            Arguments.of(userName1, new Object(), false),
            Arguments.of(userName1, username2, false),
            Arguments.of(userName1, userName1, true),
            Arguments.of(userName1, userName1Clone, true)
        );
    }
}
