package pl.dkaluza.domaincore;

import java.util.ArrayList;
import java.util.List;

public class FactoriesList<T> extends FactoriesComposite<List<T>> {
    public FactoriesList(List<? extends Factory<T>> factories) {
        this(factories, List.of());
    }

    public FactoriesList(List<? extends Factory<T>> factories, List<Validator> validators) {
        super(
            () -> factories.stream().map(Factory::assemble).toList(),
            buildValidatingFactories(factories, validators)
        );
    }

    private static List<Factory<?>> buildValidatingFactories(List<? extends Factory<?>> factories, List<Validator> validators) {
        var allFactories = new ArrayList<Factory<?>>(factories);
        allFactories.add(DefaultFactory.validatingFactory(ValidationExecutor.of(validators)));
        return allFactories;
    }
}
