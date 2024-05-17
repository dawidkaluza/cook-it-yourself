package pl.dkaluza.kitchenservice.domain.exceptions;

public class RecipeNotFoundException extends KitchenException {
    public RecipeNotFoundException(String message) {
        super(message);
    }

    public RecipeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
