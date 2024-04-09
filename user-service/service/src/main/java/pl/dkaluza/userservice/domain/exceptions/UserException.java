package pl.dkaluza.userservice.domain.exceptions;

import pl.dkaluza.domaincore.exceptions.DomainException;

public abstract class UserException extends DomainException {
    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
