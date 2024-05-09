package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.AbstractPersistable;
import pl.dkaluza.domaincore.FactoriesComposite;
import pl.dkaluza.domaincore.Factory;

import static pl.dkaluza.kitchenservice.domain.CookId.CookIdFactory;

public class Cook extends AbstractPersistable<CookId> {
    private Cook(CookId id) {
        super(id);
    }

    public static Factory<Cook> of(CookId id) {
        return of(id.getId());
    }

    public static Factory<Cook> of(Long id) {
        var cookIdFactory = new CookIdFactory(id);
        return new FactoriesComposite<>(
            () -> new Cook(cookIdFactory.assemble()),
            cookIdFactory
        );
    }
}
