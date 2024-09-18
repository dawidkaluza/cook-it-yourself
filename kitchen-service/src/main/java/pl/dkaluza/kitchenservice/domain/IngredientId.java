package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.FactoriesList;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

import java.util.List;

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

    static class IngredientIdsFactory extends FactoriesList<IngredientId> {
        IngredientIdsFactory(List<Long> ids, String fieldName) {
            super(ids.stream().map(id -> new IngredientId.IngredientIdFactory(id, fieldName + ".")).toList());
        }

        @Override
        protected List<IngredientId> assemble() {
            return super.assemble();
        }
    }
}
