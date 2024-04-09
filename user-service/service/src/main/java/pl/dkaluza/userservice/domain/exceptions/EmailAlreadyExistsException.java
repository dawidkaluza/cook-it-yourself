package pl.dkaluza.userservice.domain.exceptions;

import pl.dkaluza.userservice.domain.EmailAddress;

public class EmailAlreadyExistsException extends UserException {
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
