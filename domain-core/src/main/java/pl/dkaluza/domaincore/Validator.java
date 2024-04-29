package pl.dkaluza.domaincore;

import java.util.function.BooleanSupplier;

public interface Validator {
    FieldError validate();

    static Validator validator(boolean condition, String name, String description) {
        return () -> condition ? new FieldError(name, description) : null;
    }

    static Validator validator(BooleanSupplier condition, String name, String description) {
        return () -> condition.getAsBoolean() ? new FieldError(name, description) : null;
    }
}
