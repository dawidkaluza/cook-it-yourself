package pl.dkaluza.kitchenservice.domain.exceptions;

public class StepNotFoundException extends KitchenException {
    public StepNotFoundException(String message) {
        super(message);
    }

    public StepNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
