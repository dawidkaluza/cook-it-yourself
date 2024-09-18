package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.FactoriesList;
import pl.dkaluza.domaincore.LongIndex;

import java.util.List;

public class StepId extends LongIndex {
    private StepId(Long id) {
        super(id);
    }

    public static pl.dkaluza.domaincore.Factory<StepId> of(Long id) {
        return new Factory(id);
    }

    static class Factory extends LongIndexFactory<StepId> {
        Factory(Long id) {
            this(id, "");
        }

        Factory(Long id, String prefix) {
            super(id, () -> new StepId(id), prefix);
        }

        @Override
        protected StepId assemble() {
            return super.assemble();
        }
    }

    static class ListFactory extends FactoriesList<StepId> {
        ListFactory(List<Long> ids, String fieldName) {
            super(ids.stream().map(id -> new Factory(id, fieldName + ".")).toList());
        }

        @Override
        protected List<StepId> assemble() {
            return super.assemble();
        }
    }
}
