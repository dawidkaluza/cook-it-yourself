package pl.dkaluza.userservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class UserTest {
    @ParameterizedTest
    @MethodSource("newUserInvalidParamsProvider")
    void newUser_invalidParams_throwException(String email, char[] password, Function<char[], char[]> passwordEncoder, String name, String[] expectedErrorsNames) {
        // Given
        var factory = User.builder()
            .email(email)
            .password(password)
            .passwordEncoder(passwordEncoder)
            .name(name)
            .newUserFactory();

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
            .containsExactly(expectedErrorsNames);
    }

    private static Stream<Arguments> newUserInvalidParamsProvider() {
        Function<char[], char[]> passwordEncoder =  (pwd) -> pwd;
        return Stream.of(
            Arguments.of(
                "d@p", "password".toCharArray(), passwordEncoder, "D",
                new String[] { "email", "name" }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("newUserValidParamsProvider")
    void newUser_validParams_returnObject(String email, char[] password, Function<char[], char[]> passwordEncoder, String name) {
        // Given
        var factory = User.builder()
            .email(email)
            .password(password)
            .passwordEncoder(passwordEncoder)
            .name(name)
            .newUserFactory();

        // When
        var user = factory.produce();

        // Then
        assertThat(user)
            .isNotNull();

        assertThat(user.getId())
            .isNull();

        assertThat(user.getEmail())
            .isNotNull()
            .extracting(EmailAddress::getValue)
            .isEqualTo(email);

        assertThat(user.getPassword())
            .isNotNull();

        assertThat(user.getName())
            .isNotNull()
            .extracting(UserName::getValue)
            .isEqualTo(name);

        assertThat(user.isPersisted())
            .isEqualTo(false);
    }

    private static Stream<Arguments> newUserValidParamsProvider() {
        Function<char[], char[]> passwordEncoder =  (pwd) -> pwd;
        return Stream.of(
            Arguments.of(
                "daiwd@d.c", "paswd".toCharArray(), passwordEncoder, "Dawid"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("fromPersistenceInvalidParamsProvider")
    void fromPersistence_invalidParams_throwException(Long id, String email, char[] encodedPassword, String name, String[] expectedErrorsNames) {
        // Given
        var factory = User.builder()
            .id(id)
            .email(email)
            .encodedPassword(encodedPassword)
            .name(name)
            .fromPersistenceFactory();

        // When
        var e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        assertThat(e)
            .isNotNull();

        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly(expectedErrorsNames);
    }

    private static Stream<Arguments> fromPersistenceInvalidParamsProvider() {
        return Stream.of(
            Arguments.of(
                0L, "d@d.c", "encded!@#&^*#".toCharArray(), "   ",
                new String[] { "id", "name" }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("fromPersistenceValidParamsProvider")
    void fromPersistence_validParams_returnObject(Long id, String email, char[] encodedPassword, String name) {
        // Given
        var factory = User.builder()
            .id(id)
            .email(email)
            .encodedPassword(encodedPassword)
            .name(name)
            .fromPersistenceFactory();

        // When
        var user = factory.produce();

        // Then
        assertThat(user)
            .isNotNull();

        assertThat(user.getId())
            .isNotNull()
            .extracting(UserId::getId)
            .isEqualTo(id);

        assertThat(user.getEmail())
            .isNotNull()
            .extracting(EmailAddress::getValue)
            .isEqualTo(email);

        assertThat(user.getPassword())
            .isNotNull()
            .extracting(Password::getValue)
            .isEqualTo(encodedPassword);

        assertThat(user.getName())
            .isNotNull()
            .extracting(UserName::getValue)
            .isEqualTo(name);

        assertThat(user.isPersisted())
            .isEqualTo(true);
    }

    private static Stream<Arguments> fromPersistenceValidParamsProvider() {
        return Stream.of(
            Arguments.of(
                1L, "dawid@d.c", "encdd@!#@!*(#@!".toCharArray(), "Dawid"
            )
        );
    }
}