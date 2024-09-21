package pl.dkaluza.kitchenservice.domain.exceptions;

public class IngredientNotFoundException extends KitchenException {
    public IngredientNotFoundException(String message) {
        super(message);
    }

    public IngredientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
