package pl.dkaluza.domaincore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class LongIndexTest {
    @ParameterizedTest
    @ValueSource(longs = { 0L })
    @NullSource
    void produce_invalidId_throwException(Long id) {
        // Given
        var factory = factory(id);

        // When
        ValidationException e = catchThrowableOfType(
            factory::produce,
            ValidationException.class
        );

        // Then
        assertThat(e)
            .isNotNull();

        assertThat(e.getErrors())
            .extracting(FieldError::getName)
            .containsExactly("id");
    }

    @ParameterizedTest
    @ValueSource(longs = { 1L, Long.MAX_VALUE })
    void produce_validId_returnCreatedObject(Long id) {
        // Given
        var factory = factory(id);

        // When
        var index = factory.produce();

        // Then
        assertThat(index)
            .isNotNull()
            .extracting(Index::getId)
            .isEqualTo(id);
    }

    private static Factory<LongIndex> factory(Long id) {
        return LongIndex.factory(id, () -> new UserId(id));
    }


}