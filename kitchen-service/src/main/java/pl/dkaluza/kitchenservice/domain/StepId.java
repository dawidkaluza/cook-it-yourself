package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.FactoriesList;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

import java.util.List;

public class StepId extends LongIndex {
    private StepId(Long id) {
        super(id);
    }

    public static Factory<StepId> of(Long id) {
        return new StepIdFactory(id);
    }

    static class StepIdFactory extends LongIndexFactory<StepId> {
        StepIdFactory(Long id) {
            this(id, "");
        }

        StepIdFactory(Long id, String prefix) {
            super(id, () -> new StepId(id), prefix);
        }

        @Override
        protected StepId assemble() {
            return super.assemble();
        }
    }

    static class StepIdsFactory extends FactoriesList<StepId> {
        StepIdsFactory(List<Long> ids, String fieldName) {
            super(ids.stream().map(id -> new StepId.StepIdFactory(id, fieldName + ".")).toList());
        }

        @Override
        protected List<StepId> assemble() {
            return super.assemble();
        }
    }
}
