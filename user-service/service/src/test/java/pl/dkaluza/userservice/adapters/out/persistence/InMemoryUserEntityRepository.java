package pl.dkaluza.userservice.adapters.out.persistence;

import pl.dkaluza.spring.data.test.InMemoryRepository;
import pl.dkaluza.spring.data.test.LongIdGenerator;

import java.util.Optional;

class InMemoryUserEntityRepository extends InMemoryRepository<UserEntity, Long> implements UserEntityRepository {
    public InMemoryUserEntityRepository() {
        super(UserEntity.class, new LongIdGenerator());
    }

    @Override
    protected UserEntity newEntity(UserEntity entity, Long newId) {
        return new UserEntity(newId, entity.email(), entity.encodedPassword(), entity.name());
    }

    @Override
    protected Long getEntityId(UserEntity entity) {
        return entity.id();
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return findAll().stream()
            .filter(user -> user.email().equals(email))
            .findAny();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream()
            .anyMatch(user -> user.email().equals(email));
    }
}
