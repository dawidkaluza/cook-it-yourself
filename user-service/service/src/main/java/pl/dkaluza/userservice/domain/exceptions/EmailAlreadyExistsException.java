package pl.dkaluza.userservice.domain.exceptions;

import pl.dkaluza.domaincore.exceptions.DomainException;
import pl.dkaluza.userservice.domain.EmailAddress;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(EmailAddress email) {
        super("E-mail " + email + " already exists");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
