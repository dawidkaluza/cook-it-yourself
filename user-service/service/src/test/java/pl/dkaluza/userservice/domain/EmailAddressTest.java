package pl.dkaluza.userservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class EmailAddressTest {
    @ParameterizedTest
    @MethodSource("invalidEmailParamsProvider")
    void of_invalidParams_throwException(String email) {
        // Given
        var factory = EmailAddress.of(email);

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
            .containsExactly("email");
    }

    private static Stream<String> invalidEmailParamsProvider() {
        return Stream.of(
            null,
            "d@d",
            "d".repeat(125) + "@t.c",
            "web.pl",
            "dawid@local"
        );
    }

    @ParameterizedTest
    @MethodSource("validEmailParamsProvider")
    void of_validParams_returnObject(String email) {
        // Given
        var factory = EmailAddress.of(email);

        // When
        var emailObject = factory.produce();

        // Then
        assertThat(emailObject)
            .isNotNull()
            .extracting(EmailAddress::getValue)
            .isEqualTo(email);
    }

    private static Stream<String> validEmailParamsProvider() {
        return Stream.of(
            "d@d.c",
            "dawid@site.com",
            "d".repeat(124) + "@d.c"
        );
    }

    @ParameterizedTest
    @MethodSource("equalsParamsProvider")
    void equal_variousObjects_returnExpectedResult(EmailAddress first, Object second, boolean expectedResult) {
        assertThat(first.equals(second))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> equalsParamsProvider() {
        var email1 = EmailAddress.of("dawid@d.c").produce();
        var email1Clone = EmailAddress.of("dawid@d.c").produce();
        var email2 = EmailAddress.of("leo@d.c").produce();
        return Stream.of(
            Arguments.of(email1, null, false),
            Arguments.of(email1, new Object(), false),
            Arguments.of(email1, email2, false),
            Arguments.of(email1, email1Clone, true),
            Arguments.of(email1, email1, true)
        );
    }
}
