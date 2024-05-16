package pl.dkaluza.kitchenservice.adapters.out.persistence;

import pl.dkaluza.spring.data.test.InMemoryRepository;
import pl.dkaluza.spring.data.test.LongIdGenerator;

import java.util.List;

class InMemoryIngredientEntityRepository extends InMemoryRepository<IngredientEntity, Long> implements IngredientEntityRepository {
    public InMemoryIngredientEntityRepository() {
        super(IngredientEntity.class, new LongIdGenerator());
    }

    @Override
    protected IngredientEntity newEntity(IngredientEntity ingredientEntity, Long id) {
        return new IngredientEntity(
            id, ingredientEntity.name(), ingredientEntity.amount(), ingredientEntity.measure(),
            ingredientEntity.position(), ingredientEntity.recipeId()
        );
    }

    @Override
    protected Long getEntityId(IngredientEntity ingredientEntity) {
        return ingredientEntity.id();
    }

    @Override
    public List<IngredientEntity> findAllByRecipeId(Long recipeId) {
        return findAll().stream()
            .filter(ingredient -> ingredient.recipeId().equals(recipeId))
            .toList();
    }
}
