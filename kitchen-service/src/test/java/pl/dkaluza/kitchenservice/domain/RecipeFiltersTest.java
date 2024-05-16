package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RecipeFiltersTest {
    @ParameterizedTest
    @CsvSource(value = {
        "NULL, NULL, true",
        "NULL, 1, false",
        "T, NULL, false",
        "Xyz, 2, false",

    }, nullValues = { "NULL" })
    void isEmpty_variousParams_returnExpectedResult(String name, Long cookId, boolean expectedResult) {
        // Given
        var filters = RecipeFilters.of(name, cookId == null ? null : CookId.of(cookId).produce());

        // When, then
        assertThat(filters.isEmpty())
            .isEqualTo(expectedResult);
    }

}