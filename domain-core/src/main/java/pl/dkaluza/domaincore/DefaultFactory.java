package pl.dkaluza.domaincore;

import java.util.List;
import java.util.function.Supplier;

public class DefaultFactory<T> extends AbstractFactory<T> {
    private final ValidationExecutor validationExecutor;

    protected DefaultFactory(ValidationExecutor validationExecutor, Supplier<T> objectSupplier) {
        super(objectSupplier);
        this.validationExecutor = validationExecutor;
    }

    @Override
    List<FieldError> collectErrors() {
        return validationExecutor.validate();
    }
}
