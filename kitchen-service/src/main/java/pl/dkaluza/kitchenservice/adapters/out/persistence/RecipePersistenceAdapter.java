package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.RecipeFilters;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

import java.util.ArrayList;
import java.util.List;

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
            return recipeMapper.toDomain(recipeEntity, ingredientEntities, stepEntities);
        } catch (ValidationException e) {
            throw new IllegalStateException(
                "Caught unexpected validation exception. " +
                "Make sure that data retrieved from persistence layer is valid.",
                e
            );
        }
    }

    public Page<Recipe> findAllRecipes(PageRequest pageReq) {
        Assertions.assertArgument(pageReq != null, "pageReq is null");

        var recipeEntitiesPage = recipeRepository.findAll(
            org.springframework.data.domain.PageRequest.of(
                pageReq.getPageNumber() - 1, pageReq.getPageSize(), Sort.by(Sort.Direction.DESC, "id")
            )
        );
        var recipes = fetchAndMap(recipeEntitiesPage.getContent());
        return toPage(recipes, pageReq.getPageNumber(), recipeEntitiesPage.getTotalPages());
    }

    @Override
    public Page<Recipe> findRecipes(RecipeFilters filters, PageRequest pageReq) {
        Assertions.assertArgument(filters != null, "filters is null");
        Assertions.assertArgument(pageReq != null, "pageReq is null");

        var cookId = filters.getCookId() == null ? null : filters.getCookId().getId();
        var pageNo = pageReq.getPageNumber();
        var pageSize = pageReq.getPageSize();
        var recipeEntities = recipeRepository.findByFilters(filters.getName(), cookId, (pageNo - 1) * pageSize, pageSize);
        var recipes = fetchAndMap(recipeEntities);
        var totalRecipes = recipeRepository.countByFilters(filters.getName(), cookId);
        var totalPages = totalRecipes > 0 ? (int) Math.ceil((double) totalRecipes / pageSize) : 1;
        return toPage(recipes, pageNo, totalPages);
    }

    private List<Recipe> fetchAndMap(List<RecipeEntity> recipeEntities) {
        try {
            var recipes = new ArrayList<Recipe>();
            for (var recipeEntity : recipeEntities) {
                var recipeId = recipeEntity.id();
                var ingredientEntities = ingredientRepository.findAllByRecipeId(recipeId);
                var stepEntities = stepRepository.findAllByRecipeId(recipeId);

                var recipe = recipeMapper.toDomain(recipeEntity, ingredientEntities, stepEntities);
                recipes.add(recipe);
            }
            return recipes;
        } catch (ValidationException e) {
            throw new IllegalStateException(e);
        }
    }

    private <T> Page<T> toPage(List<T> items, int pageNumber, int pageSize) {
        try {
            return Page.of(items, pageNumber, pageSize).produce();
        } catch (ValidationException e) {
            throw new IllegalStateException(
                "Caught unexpected validation exception. " +
                "Make sure that data retrieved from persistence layer is valid.",
                e
            );
        }
    }
}
