package pl.dkaluza.domaincore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

class AssertionsTest {
    @ParameterizedTest
    @CsvSource({
        "'', 'Name is blank'",
        "'   ', 'Name is blank'"
    })
    void assertState_variousConditions_throwException(String name, String message) {
        IllegalStateException e = catchThrowableOfType(
            () -> pl.dkaluza.domaincore.Assertions.assertState(!name.isBlank(), message),
            IllegalStateException.class
        );

        assertThat(e)
            .isNotNull()
            .hasMessageContaining(message);
    }

    @ParameterizedTest
    @CsvSource({
        "Dawid', 'Name is blank'",
        "Gabriel, 'Name is blank'"
    })
    void assertState_variousConditions_doNotThrowException(String name, String message) {
        assertThatCode(
            () -> pl.dkaluza.domaincore.Assertions.assertState(!name.isBlank(), message)
        ).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({
        "'', 'Name is blank'",
        "'   ', 'Name is blank'"
    })
    void assertArgument_variousConditions_throwException(String name, String message) {
        IllegalArgumentException e = catchThrowableOfType(
            () -> pl.dkaluza.domaincore.Assertions.assertArgument(!name.isBlank(), message),
            IllegalArgumentException.class
        );

        assertThat(e)
            .isNotNull()
            .hasMessageContaining(message);
    }

    @ParameterizedTest
    @CsvSource({
        "Dawid', 'Name is blank'",
        "Gabriel, 'Name is blank'"
    })
    void assertArgument_variousConditions_doNotThrowException(String name, String message) {
        assertThatCode(
            () -> pl.dkaluza.domaincore.Assertions.assertArgument(!name.isBlank(), message)
        ).doesNotThrowAnyException();
    }
}