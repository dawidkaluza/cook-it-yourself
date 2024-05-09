package pl.dkaluza.domaincore;

import java.util.function.BooleanSupplier;

public interface Validator {
    FieldError validate();

    static Validator validator(boolean condition, String name, String description) {
        return () -> condition ? null : new FieldError(name, description);
    }

    static Validator validator(BooleanSupplier condition, String name, String description) {
        return () -> condition.getAsBoolean() ? null : new FieldError(name, description);
    }
}
