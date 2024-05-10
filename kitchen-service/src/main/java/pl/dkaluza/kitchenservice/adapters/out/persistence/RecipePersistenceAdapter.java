package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

import java.util.ArrayList;

@Component
class RecipePersistenceAdapter implements RecipeRepository  {
    private final RecipeEntityRepository recipeRepository;
    private final RecipeEntityMapper recipeMapper;
    private final IngredientEntityRepository ingredientRepository;
    private final IngredientEntityMapper ingredientMapper;
    private final StepEntityRepository stepRepository;
    private final StepEntityMapper stepMapper;
    private final CookEntityRepository cookRepository;

    public RecipePersistenceAdapter(RecipeEntityRepository recipeRepository, RecipeEntityMapper recipeMapper, IngredientEntityRepository ingredientRepository, IngredientEntityMapper ingredientMapper, StepEntityRepository stepRepository, StepEntityMapper stepMapper, CookEntityRepository cookRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.cookRepository = cookRepository;
    }

    @Override
    public Recipe insertRecipe(Recipe recipe) throws ObjectAlreadyPersistedException, CookNotFoundException {
        Assertions.assertArgument(recipe != null, "Recipe is null");
        ObjectAlreadyPersistedException.throwIfPersisted(recipe);

        var cookId = recipe.getCookId();
        if (!cookRepository.existsById(cookId.getId())) {
            throw new CookNotFoundException("Cook with id = " + cookId + " could not be found");
        }

        var recipeEntity = recipeMapper.toEntity(recipe, cookId);
        recipeEntity = recipeRepository.save(recipeEntity);

        var ingredients = recipe.getIngredients();
        var ingredientsSize = ingredients.size();
        var ingredientEntities = new ArrayList<IngredientEntity>();
        for (int i = 0; i < ingredientsSize; i++) {
            var ingredient = ingredients.get(i);

            ObjectAlreadyPersistedException.throwIfPersisted(ingredient);

            var ingredientEntity = ingredientMapper.toEntity(ingredient, i + 1, recipeEntity.id());
            ingredientEntity = ingredientRepository.save(ingredientEntity);
            ingredientEntities.add(ingredientEntity);
        }

        var steps = recipe.getMethodSteps();
        var stepsSize = steps.size();
        var stepEntities = new ArrayList<StepEntity>();
        for (int i = 0; i < stepsSize; i++) {
            var step = steps.get(i);

            ObjectAlreadyPersistedException.throwIfPersisted(step);

            var stepEntity = stepMapper.toEntity(step, i + 1, recipeEntity.id());
            stepEntity = stepRepository.save(stepEntity);
            stepEntities.add(stepEntity);
        }

        try {
            return recipeMapper.toDomain(recipeEntity, ingredientEntities, stepEntities, cookId.getId());
        } catch (ValidationException e) {
            throw new IllegalStateException(
                "Caught unexpected validation exception. " +
                "Make sure that data retrieved from persistence layer is valid.",
                e
            );
        }
    }
}
