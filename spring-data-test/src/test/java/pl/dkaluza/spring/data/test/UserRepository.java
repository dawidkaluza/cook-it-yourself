package pl.dkaluza.spring.data.test;

class UserRepository extends InMemoryRepository<User, Long> {
    public UserRepository() {
        super(User.class, new LongIdGenerator());
    }

    @Override
    protected User newEntity(User base, Long newId) {
        return new User(newId, base.name());
    }

    @Override
    protected Long getEntityId(User entity) {
        return entity.id();
    }
}
