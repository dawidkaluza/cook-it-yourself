package pl.dkaluza.kitchenservice.domain.exceptions;

public class CookNotFoundException extends KitchenException {
    public CookNotFoundException(String message) {
        super(message);
    }

    public CookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
