package pl.dkaluza.domaincore;

import pl.dkaluza.domaincore.exceptions.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

abstract class AbstractFactory<T> implements Factory<T> {
    private final Supplier<T> objectSupplier;

    private List<FieldError> errors;
    private boolean isValidated;

    public AbstractFactory(Supplier<T> objectSupplier) {
        this.objectSupplier = objectSupplier;

        errors = Collections.emptyList();
        isValidated = false;
    }

    abstract List<FieldError> collectErrors();

    protected Supplier<T> getObjectSupplier() {
        return objectSupplier;
    }

    @Override
    public List<FieldError> validate() {
        if (isValidated) {
            return errors;
        }

        this.errors = collectErrors();
        isValidated = true;
        return Collections.unmodifiableList(errors);
    }

    @Override
    public T create() throws ValidationException {
        if (!isValidated) {
            validate();
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return objectSupplier.get();
    }
}
