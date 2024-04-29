package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

public class RecipeId extends LongIndex {
    private RecipeId(Long id) {
        super(id);
    }

    public static Factory<RecipeId> of(Long id) {
        return new RecipeIdFactory(id);
    }

    static class RecipeIdFactory extends LongIndexFactory<RecipeId> {
        RecipeIdFactory(Long id) {
            super(id, () -> new RecipeId(id));
        }

        @Override
        protected RecipeId assemble() {
            return super.assemble();
        }
    }
}
