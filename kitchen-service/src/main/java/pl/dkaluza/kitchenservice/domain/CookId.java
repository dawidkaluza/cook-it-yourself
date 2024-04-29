package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

public class CookId extends LongIndex {
    private CookId(Long id) {
        super(id);
    }

    public static Factory<CookId> of(Long id) {
        return new CookIdFactory(id);
    }

    static class CookIdFactory extends LongIndexFactory<CookId> {
        CookIdFactory(Long id) {
            super(id, () -> new CookId(id));
        }

        @Override
        protected CookId assemble() {
            return super.assemble();
        }
    }
}
