package pl.dkaluza.userservice.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class PasswordTest {
    @ParameterizedTest
    @MethodSource("rawPasswordInvalidParamsProvider")
    void ofRawPassword_variousParams_throwException(char[] password, Function<char[], char[]> passwordEncoder, String[] expectedErrorsNames) {
        // Given, when
        ValidationException e = catchThrowableOfType(
            () -> Password.of(password, passwordEncoder).produce(),
            ValidationException.class
        );

        // Then
        assertThat(e)
            .isNotNull();

        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly(expectedErrorsNames);
    }

    private static Stream<Arguments> rawPasswordInvalidParamsProvider() {
        Function<char[], char[]> passwordEncoder = (pwd) -> pwd;
        return Stream.of(
            Arguments.of(null, null, new String[] { "password", "passwordEncoder" } ),
            Arguments.of("".toCharArray(), passwordEncoder, new String[] { "password" } ),
            Arguments.of("12".toCharArray(), passwordEncoder, new String[] { "password" } ),
            Arguments.of(".".repeat(33).toCharArray(), passwordEncoder, new String[] { "password" } ),
            Arguments.of(" ".repeat(4).toCharArray(), passwordEncoder, new String[] { "password" } ),
            Arguments.of("dd dd".toCharArray(), passwordEncoder, new String[] { "password" } )
        );
    }

    @ParameterizedTest
    @MethodSource("ofRawPasswordValidParamsProvider")
    void ofRawPassword_variousParams_returnObject(char[] password) {
        // Given
        Function<char[], char[]> passwordEncoder = (pwd) -> ("***" + String.valueOf(pwd) + "***").toCharArray();

        // When
        var passwordObject = Password.of(password, passwordEncoder).produce();

        // Then
        assertThat(passwordObject)
            .isNotNull()
            .extracting(Password::getValue)
            .isEqualTo(
                ("***" + String.valueOf(password) + "***").toCharArray()
            );
    }

    private static Stream<char[]> ofRawPasswordValidParamsProvider() {
        return Stream.of(
            "abcde".toCharArray(),
            "e".repeat(32).toCharArray(),
            "123#4.6)8HDSL!@#$%".toCharArray()
        );
    }

    @Test
    void ofEncodedPassword_variousParams_throwException() {
        // Given, when
        var e = catchThrowableOfType(
            () -> Password.of(null).produce(),
            ValidationException.class
        );

        assertThat(e)
            .isNotNull();

        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly("encodedPassword");
    }

    @ParameterizedTest
    @ValueSource(strings = { "a*(&&*(%JM675" })
    void ofEncodedPassword_variousParams_returnObject(String encodedPasswordAsString) {
        // Given
        var encodedPassword = encodedPasswordAsString.toCharArray();

        // Given, when
        var password = Password.of(encodedPassword).produce();

        // Then
        assertThat(password)
            .isNotNull()
            .extracting(Password::getValue)
            .isEqualTo(encodedPassword);
    }

    @ParameterizedTest
    @MethodSource("equalParamsProvider")
    void equal_variousObjects_returnExpectedResult(Password first, Object second, boolean expectedResult) {
        assertThat(first.equals(second))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> equalParamsProvider() {
        var password1 = Password.of("123".toCharArray()).produce();
        var password1Clone = Password.of("123".toCharArray()).produce();
        var password2 = Password.of("456".toCharArray()).produce();

        return Stream.of(
            Arguments.of(password1, null, false),
            Arguments.of(password1, new Object(), false),
            Arguments.of(password1, password2, false),
            Arguments.of(password1, password1, true),
            Arguments.of(password1, password1Clone, true)
        );
    }
}