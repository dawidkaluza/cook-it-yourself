package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

public class StepId extends LongIndex {
    private StepId(Long id) {
        super(id);
    }

    public static Factory<StepId> of(Long id) {
        return new StepIdFactory(id);
    }

    static class StepIdFactory extends LongIndexFactory<StepId> {
        StepIdFactory(Long id) {
            super(id, () -> new StepId(id));
        }

        @Override
        protected StepId assemble() {
            return super.assemble();
        }
    }
}
