package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.LongIndex;

import java.util.function.Supplier;

public class UserId extends LongIndex {
    private UserId(Long id) {
        super(id);
    }

    public static Factory<UserId> of(Long id) {
        return factory(id, () -> new UserId(id));
    }

    static class UserIdFactory extends LongIndexFactory<UserId> {
        UserIdFactory(Long id) {
            super(id, () -> new UserId(id));
        }

        @Override
        protected Supplier<UserId> getObjectSupplier() {
            return super.getObjectSupplier();
        }
    }
}
