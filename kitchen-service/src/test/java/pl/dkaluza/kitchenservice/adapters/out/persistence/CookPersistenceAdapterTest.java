package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.kitchenservice.domain.Cook;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CookPersistenceAdapterTest {
    private InMemoryCookPersistenceAdapter cookPersistenceAdapter;

    @BeforeEach
    void beforeEach() {
        cookPersistenceAdapter = new InMemoryCookPersistenceAdapter();
    }

    @Test
    void saveCook_nullCook_throwException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> cookPersistenceAdapter.saveCook(null));
    }

    @ParameterizedTest
    @MethodSource("saveCookVariousCooksProvider")
    void saveCook_newOrPersistedCook_returnPersistedCook(Cook givenCook) {
        // Given
        // When
        var persistedCook = cookPersistenceAdapter.saveCook(givenCook);

        // Then
        assertThat(persistedCook)
            .isNotNull()
            .extracting(Cook::getId, Cook::isPersisted)
            .containsExactly(givenCook.getId(), true);
    }

    private static Stream<Cook> saveCookVariousCooksProvider() {
        return Stream.of(
            Cook.newCook(1L).produce(),
            Cook.fromPersistence(1L).produce()
        );
    }
}
