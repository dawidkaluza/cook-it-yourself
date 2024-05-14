package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pl.dkaluza.kitchenservice.domain.Cook;

import java.util.function.Function;
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
    void saveCook_newOrPersistedCook_returnPersistedCook(Function<Long, Cook> cookCreator) {
        // Given
        var id = 1L;
        var givenCook = cookCreator.apply(id);

        // WHen
        var persistedCook = cookPersistenceAdapter.saveCook(givenCook);

        // Then
        assertThat(persistedCook)
            .isNotNull()
            .extracting(cook -> cook.getId().getId(), Cook::isPersisted)
            .containsExactly(id, true);
    }

    private static Stream<Function<Long, Cook>> saveCookVariousCooksProvider() {
        return Stream.of(
            (id) -> Cook.newCook(id).produce(),
            (id) -> Cook.fromPersistence(id).produce()
        );
    }
}
