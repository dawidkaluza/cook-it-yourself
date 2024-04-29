package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

public class IngredientId extends LongIndex {
    private IngredientId(Long id) {
        super(id);
    }

    public static Factory<IngredientId> of(Long id) {
        return new IngredientIdFactory(id);
    }

    static class IngredientIdFactory extends LongIndexFactory<IngredientId> {
        IngredientIdFactory(Long id) {
            super(id, () -> new IngredientId(id));
        }

        @Override
        protected IngredientId assemble() {
            return super.assemble();
        }
    }
}
