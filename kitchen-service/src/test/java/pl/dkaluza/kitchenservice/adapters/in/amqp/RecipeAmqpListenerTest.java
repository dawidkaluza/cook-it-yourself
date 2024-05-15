package pl.dkaluza.kitchenservice.adapters.in.amqp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

class RecipeAmqpListenerTest {
    private RecipeAmqpListener recipeAmqpListener;

    @BeforeEach
    void beforeEach() {
        var kitchenService = mock(KitchenService.class);
        var recipeAmqpFacade = new RecipeAmqpFacade(kitchenService);
        recipeAmqpListener = new RecipeAmqpListener(recipeAmqpFacade);
    }

    @Test
    void onUserSignedUp_invalidId_throwException() {
        // Given
        var msg = new OnUserSignedUp(0L);

        // When, then
        assertThatExceptionOfType(ValidationException.class)
            .isThrownBy(() -> recipeAmqpListener.onUserSignedUp(msg));
    }

    @Test
    void onUserSignedUp_validId_registerCook() {
        // Given
        var msg = new OnUserSignedUp(1L);

        // When, then
        recipeAmqpListener.onUserSignedUp(msg);
    }

}