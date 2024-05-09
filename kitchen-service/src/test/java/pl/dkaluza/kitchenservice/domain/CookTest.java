package pl.dkaluza.kitchenservice.domain;

import org.junit.jupiter.api.Test;
import pl.dkaluza.domaincore.FieldError;
import pl.dkaluza.domaincore.exceptions.ValidationException;

import static org.assertj.core.api.Assertions.*;

class CookTest {

    @Test
    void newCook_invalidId_returnExpectedFieldErrors() {
        // Given, when
        var e = catchThrowableOfType(
            () -> Cook.of(0L).produce(),
            ValidationException.class
        );

        // Then
        assertThat(e.getErrors())
            .extracting(FieldError::name)
            .containsExactly("id");
    }

    @Test
    void newCook_validId_returnCreatedObject() {
        // Given
        var id = CookId.of(1L).produce();

        // When
        var cook = Cook.of(id).produce();

        // Then
        assertThat(cook)
            .isNotNull();

        assertThat(cook.getId())
            .isEqualTo(id);
    }
}