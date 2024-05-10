package pl.dkaluza.kitchenservice.adapters.out.persistence;

import pl.dkaluza.spring.data.test.InMemoryRepository;
import pl.dkaluza.spring.data.test.LongIdGenerator;

class InMemoryRecipeEntityRepository extends InMemoryRepository<RecipeEntity, Long> implements RecipeEntityRepository {
    public InMemoryRecipeEntityRepository() {
        super(RecipeEntity.class, new LongIdGenerator());
    }

    @Override
    protected RecipeEntity newEntity(RecipeEntity recipe, Long id) {
        return new RecipeEntity(id, recipe.name(), recipe.description(), recipe.cookingTime(), recipe.portionSizeAmount(), recipe.portionSizeMeasure(), recipe.cookId());
    }

    @Override
    protected Long getEntityId(RecipeEntity recipe) {
        return recipe.id();
    }
}
