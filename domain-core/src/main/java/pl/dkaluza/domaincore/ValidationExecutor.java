package pl.dkaluza.domaincore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ValidationExecutor {
    private final List<Validator> validators;

    private ValidationExecutor() {
        this.validators = new ArrayList<>();
    }

    public static ValidationExecutor create() {
        return new ValidationExecutor();
    }

    public ValidationExecutor withValidation(boolean condition, String name, String message) {
        Validator validator = () -> condition ? null : new FieldError(name, message);
        validators.add(validator);
        return this;
    }

    public ValidationExecutor withValidation(BooleanSupplier conditionSupplier, String name, String message) {
        Validator validator = () -> conditionSupplier.getAsBoolean() ? null : new FieldError(name, message);
        validators.add(validator);
        return this;
    }

    public List<FieldError> validate() {
        var errors = new ArrayList<FieldError>();
        for (Validator validator : validators) {
            var error = validator.validate();
            if (error != null) {
                errors.add(error);
            }
        }

        return errors;
    }
}
