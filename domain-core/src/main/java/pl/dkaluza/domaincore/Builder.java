package pl.dkaluza.domaincore;

import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class Builder<T> {
    private final ValidationExecutor validationExecutor;
    private List<FieldError> errors;
    private boolean isValidated;
    private final Supplier<T> objectSupplier;

    protected Builder(ValidationExecutor validationExecutor, Supplier<T> objectSupplier) {
        this.validationExecutor = validationExecutor;
        this.objectSupplier = objectSupplier;

        this.errors = Collections.emptyList();
        isValidated = false;
    }

    public List<FieldError> validate() {
        if (isValidated) {
            return errors;
        }

        this.errors = validationExecutor.validate();
        isValidated = true;
        return Collections.unmodifiableList(errors);
    }

    public T build() throws ValidationException {
        if (!isValidated) {
            validate();
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return objectSupplier.get();
    }
}
