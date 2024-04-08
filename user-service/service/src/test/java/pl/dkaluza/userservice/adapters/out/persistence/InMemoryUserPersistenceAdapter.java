package pl.dkaluza.userservice.adapters.out.persistence;

import org.mapstruct.factory.Mappers;

public class InMemoryUserPersistenceAdapter extends UserPersistenceAdapter {
    public InMemoryUserPersistenceAdapter(boolean empty) {
        super(Mappers.getMapper(UserEntityMapper.class), inMemoryUserRepository(empty));
    }

    public InMemoryUserPersistenceAdapter() {
        this(true);
    }

    private static InMemoryUserEntityRepository inMemoryUserRepository(boolean empty) {
        var repository = new InMemoryUserEntityRepository();
        if (empty) {
            repository.deleteAll();
        }

        return repository;
    }
}
