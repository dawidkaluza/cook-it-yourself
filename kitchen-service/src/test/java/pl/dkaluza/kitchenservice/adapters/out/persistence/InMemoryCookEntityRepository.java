package pl.dkaluza.kitchenservice.adapters.out.persistence;

import pl.dkaluza.spring.data.test.InMemoryRepository;

class InMemoryCookEntityRepository extends InMemoryRepository<CookEntity, Long> implements CookEntityRepository {
    public InMemoryCookEntityRepository() {
        super(CookEntity.class, () -> null);
    }

    @Override
    protected CookEntity newEntity(CookEntity cookEntity, Long id) {
        return new CookEntity(id);
    }

    @Override
    protected Long getEntityId(CookEntity cookEntity) {
        return cookEntity.id();
    }
}
