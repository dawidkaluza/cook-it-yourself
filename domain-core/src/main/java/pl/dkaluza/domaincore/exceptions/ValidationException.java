package pl.dkaluza.domaincore.exceptions;

import pl.dkaluza.domaincore.FieldError;

import java.util.List;

public class ValidationException extends DomainException {
    private final List<FieldError> errors;

    public ValidationException(List<FieldError> errors) {
        super(buildMessage(errors));
        this.errors = errors;
    }

    private static String buildMessage(List<FieldError> errors) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Exception thrown because of the following errors: [");

        for (FieldError error : errors) {
            stringBuilder
                .append(error.name())
                .append("=")
                .append(error.message())
                .append("; ");
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public List<FieldError> getErrors() {
        return errors;
    }
}
