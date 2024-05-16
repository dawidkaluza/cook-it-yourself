package pl.dkaluza.kitchenservice.adapters.out.persistence;

import pl.dkaluza.spring.data.test.InMemoryRepository;
import pl.dkaluza.spring.data.test.LongIdGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    @Override
    public List<RecipeEntity> findByFilters(String name, Long cookId, int pageOffset, int pageSize) {
        var recipes = findAll().stream()
            .filter(entity -> name == null || entity.name().contains(name))
            .filter(entity -> cookId == null || entity.cookId().equals(cookId))
            .sorted(Comparator.comparing(RecipeEntity::id))
            .toList();

        var pageOffsetEnd = Math.min(recipes.size(), pageOffset + pageSize);
        if (pageOffset >= pageOffsetEnd) {
            return Collections.emptyList();
        }

        return recipes.subList(pageOffset, pageOffsetEnd);
    }

    @Override
    public int countByFilters(String name, Long cookId) {
        return findAll().stream()
            .filter(entity -> name == null || entity.name().contains(name))
            .filter(entity -> cookId == null || entity.cookId().equals(cookId))
            .toList()
            .size();
    }
}
