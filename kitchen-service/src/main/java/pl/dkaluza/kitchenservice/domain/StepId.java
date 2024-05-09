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
}
