package pl.dkaluza.domaincore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Factory that can be a composite of other factories.
 */
public class FactoriesComposite<T> extends Factory<T> {
    private final List<Factory<?>> factories;

    public FactoriesComposite(Assembler<T> assembler, Factory<?>... factories) {
        this(assembler, List.of(factories));
    }

    public FactoriesComposite(Assembler<T> assembler, List<? extends Factory<?>> factories) {
        super(assembler);
        this.factories = new ArrayList<>(factories);
    }

    @Override
    protected List<FieldError> validate() {
        List<FieldError> errors = new ArrayList<>();
        for (Factory<?> factory : factories) {
            errors.addAll(factory.validate());
        }
        return errors;
    }
}
