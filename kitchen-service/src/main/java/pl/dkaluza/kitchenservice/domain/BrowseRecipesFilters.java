package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.ValidationExecutor;

public class BrowseRecipesFilters {
    private final String name;

    private BrowseRecipesFilters(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static class BrowseRecipesFilterFactory extends DefaultFactory<BrowseRecipesFilters> {
        private BrowseRecipesFilterFactory(String name) {
            super(
                ValidationExecutor.of(),
                () -> new BrowseRecipesFilters(name)
            );
        }
    }
}
