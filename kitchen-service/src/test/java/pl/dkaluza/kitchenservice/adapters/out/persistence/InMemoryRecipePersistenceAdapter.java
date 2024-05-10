package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.mapstruct.factory.Mappers;

public class InMemoryRecipePersistenceAdapter extends RecipePersistenceAdapter {
    public InMemoryRecipePersistenceAdapter() {
        this(true);
    }

    public InMemoryRecipePersistenceAdapter(boolean empty) {
        super(
            inMemoryRecipeRepository(empty),
            Mappers.getMapper(RecipeEntityMapper.class),
            inMemoryIngredientRepository(empty),
            Mappers.getMapper(IngredientEntityMapper.class),
            inMemoryStepEntityRepository(empty),
            Mappers.getMapper(StepEntityMapper.class),
            inMemoryCookEntityRepository(empty)
        );
    }

    private static InMemoryRecipeEntityRepository inMemoryRecipeRepository(boolean empty) {
        var repository = new InMemoryRecipeEntityRepository();
        if (empty) {
            repository.deleteAll();
        }

        return repository;
    }

    private static InMemoryIngredientEntityRepository inMemoryIngredientRepository(boolean empty) {
        var repository = new InMemoryIngredientEntityRepository();
        if (empty) {
            repository.deleteAll();
        }

        return repository;
    }

    private static InMemoryStepEntityRepository inMemoryStepEntityRepository(boolean empty) {
        var repository = new InMemoryStepEntityRepository();
        if (empty) {
            repository.deleteAll();
        }

        return repository;
    }

    private static InMemoryCookEntityRepository inMemoryCookEntityRepository(boolean empty) {
        var repository = new InMemoryCookEntityRepository();
        if (empty) {
            repository.deleteAll();
        }

        return repository;
    }
}
