package pl.dkaluza.domaincore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ValidationExecutor {
    private final List<Validator> validators;

    private ValidationExecutor(List<Validator> validators) {
        this.validators = validators;
    }

    public static ValidationExecutor of(List<Validator> validators) {
        return new ValidationExecutor(validators);
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {
        private final List<Validator> validators;

        private Builder() {
            validators = new ArrayList<>();
        }

        public Builder withValidation(boolean condition, String name, String message) {
            Validator validator = () -> condition ? null : new FieldError(name, message);
            validators.add(validator);
            return this;
        }

        public Builder withValidation(BooleanSupplier conditionSupplier, String name, String message) {
            Validator validator = () -> conditionSupplier.getAsBoolean() ? null : new FieldError(name, message);
            validators.add(validator);
            return this;
        }

        public ValidationExecutor build() {
            return new ValidationExecutor(validators);
        }
    }
}
