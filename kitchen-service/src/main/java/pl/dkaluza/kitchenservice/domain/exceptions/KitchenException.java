package pl.dkaluza.kitchenservice.domain.exceptions;

import pl.dkaluza.domaincore.exceptions.DomainException;

public abstract class KitchenException extends DomainException {
    public KitchenException(String message) {
        super(message);
    }

    public KitchenException(String message, Throwable cause) {
        super(message, cause);
    }
}
