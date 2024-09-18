package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.FactoriesList;
import pl.dkaluza.domaincore.LongIndex;

import java.util.List;

public class IngredientId extends LongIndex {
    private IngredientId(Long id) {
        super(id);
    }

    public static pl.dkaluza.domaincore.Factory<IngredientId> of(Long id) {
        return new Factory(id);
    }

    static class Factory extends LongIndexFactory<IngredientId> {
        Factory(Long id) {
            this(id, "");
        }

        Factory(Long id, String prefix) {
            super(id, () -> new IngredientId(id), prefix);
        }

        @Override
        protected IngredientId assemble() {
            return super.assemble();
        }
    }

    static class ListFactory extends FactoriesList<IngredientId> {
        ListFactory(List<Long> ids, String fieldName) {
            super(ids.stream().map(id -> new Factory(id, fieldName + ".")).toList());
        }

        @Override
        protected List<IngredientId> assemble() {
            return super.assemble();
        }
    }
}
