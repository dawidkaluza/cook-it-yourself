package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.LongIndex;

public class CookId extends LongIndex {
    private CookId(Long id) {
        super(id);
    }

    public static pl.dkaluza.domaincore.Factory<CookId> of(Long id) {
        return new Factory(id);
    }

    static class Factory extends LongIndexFactory<CookId> {
        Factory(Long id) {
            super(id, () -> new CookId(id));
        }

        Factory(Long id, String fieldName) {
            super(id, () -> new CookId(id), "", fieldName);
        }

        @Override
        protected CookId assemble() {
            return super.assemble();
        }
    }
}
