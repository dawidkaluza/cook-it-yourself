package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.AbstractPersistable;
import pl.dkaluza.domaincore.FactoriesComposite;
import pl.dkaluza.domaincore.Factory;

import static pl.dkaluza.kitchenservice.domain.CookId.CookIdFactory;

public class Cook extends AbstractPersistable<CookId> {
    private final boolean isPersisted;

    private Cook(CookId id, boolean isPersisted) {
        super(id);
        this.isPersisted = isPersisted;
    }

    @Override
    public boolean isPersisted() {
        return isPersisted;
    }

    public static Factory<Cook> newCook(Long id) {
        return of(id, false);
    }

    public static Factory<Cook> fromPersistence(Long id) {
        return of(id, true);
    }

    private static Factory<Cook> of(Long id, boolean isPersisted) {
        var cookIdFactory = new CookIdFactory(id);
        return new FactoriesComposite<>(
            () -> new Cook(cookIdFactory.assemble(), isPersisted),
            cookIdFactory
        );
    }
}
