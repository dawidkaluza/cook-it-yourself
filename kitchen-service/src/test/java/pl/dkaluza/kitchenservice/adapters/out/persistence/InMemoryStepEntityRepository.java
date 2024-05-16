package pl.dkaluza.kitchenservice.adapters.out.persistence;

import pl.dkaluza.spring.data.test.InMemoryRepository;
import pl.dkaluza.spring.data.test.LongIdGenerator;

import java.util.List;

class InMemoryStepEntityRepository extends InMemoryRepository<StepEntity, Long> implements StepEntityRepository {
    public InMemoryStepEntityRepository() {
        super(StepEntity.class, new LongIdGenerator());
    }

    @Override
    protected StepEntity newEntity(StepEntity stepEntity, Long id) {
        return new StepEntity(id, stepEntity.text(), stepEntity.position(), stepEntity.recipeId());
    }

    @Override
    protected Long getEntityId(StepEntity stepEntity) {
        return stepEntity.id();
    }

    @Override
    public List<StepEntity> findAllByRecipeId(Long recipeId) {
        return findAll().stream()
            .filter(stepEntity -> stepEntity.recipeId().equals(recipeId))
            .toList();
    }
}
