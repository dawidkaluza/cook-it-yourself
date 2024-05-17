package pl.dkaluza.kitchenservice.domain.exceptions;

public class RecipeNotOwnedException extends KitchenException {
    public RecipeNotOwnedException(String message) {
        super(message);
    }

    public RecipeNotOwnedException(String message, Throwable cause) {
        super(message, cause);
    }
}
