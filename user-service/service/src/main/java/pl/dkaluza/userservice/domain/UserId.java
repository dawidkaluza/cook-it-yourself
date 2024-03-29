package pl.dkaluza.userservice.domain;

import pl.dkaluza.domaincore.LongIndex;

public class UserId extends LongIndex {
    private UserId(Long id) {
        super(id);
    }

    static class UserIdFactory extends LongIndexFactory<UserId> {
        UserIdFactory(Long id) {
            super(id, () -> new UserId(id));
        }

        @Override
        protected UserId assemble() {
            return super.assemble();
        }
    }
}
