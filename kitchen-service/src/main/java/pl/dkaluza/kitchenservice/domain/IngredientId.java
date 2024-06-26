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
            this(id, "");
        }

        IngredientIdFactory(Long id, String prefix) {
            super(id, () -> new IngredientId(id), prefix);
        }

        @Override
        protected IngredientId assemble() {
            return super.assemble();
        }
    }
}
