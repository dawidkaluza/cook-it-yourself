package pl.dkaluza.domaincore;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class IndexTest {
    @ParameterizedTest
    @MethodSource("equalArgumentsProvider")
    void equal_variousObjects_returnExpectedResult(Index<?> first, Object second, boolean expectedResult) {
        Assertions.assertThat(first.equals(second))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> equalArgumentsProvider() {
        return Stream.of(
            Arguments.of(new UserId(1L), null, false),
            Arguments.of(new UserId(1L), new Object(), false),
            Arguments.of(new UserId(1L), new UserId(2L), false),
            Arguments.of(new UserId(1L), new UserId(1L), true)
        );
    }
}
