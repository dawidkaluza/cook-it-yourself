package pl.dkaluza.domaincore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FactoriesComposite<T> extends AbstractFactory<T> {
    private final List<Factory<?>> factories;

    public FactoriesComposite(Supplier<T> objectSupplier, Factory<?>... factories) {
        super(objectSupplier);
        this.factories = List.of(factories);
    }

    @Override
    List<FieldError> collectErrors() {
        var errors = new ArrayList<FieldError>();
        for (Factory<?> factory : factories) {
            errors.addAll(factory.validate());
        }
        return errors;
    }
}
