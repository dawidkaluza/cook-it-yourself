package pl.dkaluza.domaincore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class PersistableTest {
    @ParameterizedTest
    @MethodSource("equalDataProvider")
    void equal_variousObjects_returnExpectedResult(Persistable<?> first, Object second, boolean expectedResult) {
        assertThat(first.equals(second))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> equalDataProvider() {
        var userId1 = new UserId(1L);
        var userId2 = new UserId(2L);
        return Stream.of(
            Arguments.of(new User(userId1), null, false),
            Arguments.of(new User(userId1), new Object(), false),
            Arguments.of(new User(userId1), new User(userId2), false),
            Arguments.of(new User(userId1), new User(userId1), true)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
        "1, true",
        "NULL, false",
    }, nullValues = { "NULL" })
    void isPersistable_variousIds_returnExpectedResult(Long id, boolean expectedResult) {
        // Given
        var persistable = new User(id == null ? null : new UserId(id));

        // When, then
        assertThat(persistable.isPersisted())
            .isEqualTo(expectedResult);

    }
}