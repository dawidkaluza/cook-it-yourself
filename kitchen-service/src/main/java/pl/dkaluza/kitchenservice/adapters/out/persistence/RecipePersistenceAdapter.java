package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ObjectNotPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.RecipeFilters;
import pl.dkaluza.kitchenservice.domain.RecipeId;
import pl.dkaluza.kitchenservice.domain.RecipeUpdate;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        var recipeEntity = recipeMapper.toEntity(recipe);
        recipeEntity = recipeRepository.save(recipeEntity);

        var ingredients = recipe.getIngredients();
        var ingredientsSize = ingredients.size();
        var ingredientEntities = new ArrayList<IngredientEntity>();
        for (int i = 0; i < ingredientsSize; i++) {
            var ingredient = ingredients.get(i);

            var ingredientEntity = ingredientMapper.toEntity(ingredient, i + 1, recipeEntity.id());
            ingredientEntity = ingredientRepository.save(ingredientEntity);
            ingredientEntities.add(ingredientEntity);
        }

        var steps = recipe.getMethodSteps();
        var stepsSize = steps.size();
        var stepEntities = new ArrayList<StepEntity>();
        for (int i = 0; i < stepsSize; i++) {
            var step = steps.get(i);

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

    @Override
    public Optional<Recipe> findRecipeById(RecipeId id) {
        Assertions.assertArgument(id != null, "id is null");
        return recipeRepository.findById(id.getId()).map(this::fetchAndMap);
    }

    public Page<Recipe> findAllRecipes(PageRequest pageReq) {
        Assertions.assertArgument(pageReq != null, "pageReq is null");

        var recipeEntitiesPage = recipeRepository.findAll(
            org.springframework.data.domain.PageRequest.of(
                pageReq.getPageNumber() - 1, pageReq.getPageSize(), Sort.by(Sort.Direction.DESC, "id")
            )
        );
        var recipes = recipeEntitiesPage.getContent().stream().map(this::fetchAndMap).toList();
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
        var recipes = recipeEntities.stream().map(this::fetchAndMap).toList();
        var totalRecipes = recipeRepository.countByFilters(filters.getName(), cookId);
        var totalPages = totalRecipes > 0 ? (int) Math.ceil((double) totalRecipes / pageSize) : 1;
        return toPage(recipes, pageNo, totalPages);
    }

    @Override
    public Recipe updateRecipe(Recipe recipe, RecipeUpdate recipeUpdate) {
        Assertions.assertArgument(recipe != null, "recipe is null");
        Assertions.assertArgument(recipeUpdate != null, "recipeUpdate is null");
        ObjectNotPersistedException.throwIfNotPersisted(recipe);

        var recipeEntity = updateBasicInformation(recipe, recipeUpdate);
        var ingredientEntities = updateIngredients(recipe, recipeUpdate);
        var stepEntities = updateSteps(recipe, recipeUpdate);
        return recipeMapper.toDomain(recipeEntity, ingredientEntities, stepEntities);
    }

    @Override
    public void deleteRecipe(Recipe recipe) throws ObjectNotPersistedException {
        Assertions.assertArgument(recipe != null, "recipe is null");
        ObjectNotPersistedException.throwIfNotPersisted(recipe);

        var ingredients = recipe.getIngredients();
        for (var ingredient : ingredients) {
            ingredientRepository.deleteById(ingredient.getId().getId());
        }

        var steps = recipe.getMethodSteps();
        for (var step : steps) {
            stepRepository.deleteById(step.getId().getId());
        }

        recipeRepository.deleteById(recipe.getId().getId());
    }

    private RecipeEntity updateBasicInformation(Recipe recipe, RecipeUpdate recipeUpdate) {
        RecipeEntity recipeEntity;
        if (recipeUpdate.getBasicInformation().isPresent()) {
            var basicInformation = recipeUpdate.getBasicInformation().get();
            recipeEntity = recipeMapper.toEntity(recipe, basicInformation);
            recipeEntity = recipeRepository.save(recipeEntity);
        } else {
            recipeEntity = recipeMapper.toEntity(recipe);
        }
        return recipeEntity;
    }

    private List<IngredientEntity> updateIngredients(Recipe recipe, RecipeUpdate recipeUpdate) {
        var recipeId = recipe.getId().getId();
        List<IngredientEntity> ingredientEntities = ingredientMapper.toEntities(recipe.getIngredients(), recipeId);
        if (recipeUpdate.getIngredients().isPresent()) {
            var recipeUpdateIngredients = recipeUpdate.getIngredients().get();

            var ingredientsToDelete = recipeUpdateIngredients.getIngredientsToDelete();
            var lowestIndexOfDeletedIngredients = ingredientEntities.size();
            for (var ingredientId : ingredientsToDelete) {
                var idValue = ingredientId.getId();
                var ingredientsSize = ingredientEntities.size();
                for (int i = 0; i < ingredientsSize; i++) {
                    var ingredientEntity = ingredientEntities.get(i);
                    if (ingredientEntity.id().equals(idValue)) {
                        ingredientEntities.remove(i);
                        ingredientRepository.deleteById(idValue);

                        if (i < lowestIndexOfDeletedIngredients) {
                            lowestIndexOfDeletedIngredients = i;
                        }

                        break;
                    }
                }
            }

            var ingredientsSize = ingredientEntities.size();
            for (int i = lowestIndexOfDeletedIngredients; i < ingredientsSize; i++) {
                var ingredientEntity = ingredientEntities.get(i);
                ingredientEntity = new IngredientEntity(
                    ingredientEntity.id(),
                    ingredientEntity.name(),
                    ingredientEntity.amount(),
                    ingredientEntity.measure(),
                    i + 1,
                    ingredientEntity.recipeId()
                );
                ingredientEntity = ingredientRepository.save(ingredientEntity);
                ingredientEntities.set(i, ingredientEntity);
            }

            var ingredientsToUpdate = recipeUpdateIngredients.getIngredientsToUpdate();
            for (var ingredient : ingredientsToUpdate) {
                var idValue = ingredient.getId().getId();
                int position = ingredientEntities.stream()
                    .filter(ingredientEntity -> ingredientEntity.id().equals(idValue))
                    .findFirst().orElseThrow()
                    .position();

                var ingredientEntity = ingredientMapper.toEntity(ingredient, position, recipeId);
                ingredientEntity = ingredientRepository.save(ingredientEntity);
                ingredientEntities.set(position - 1, ingredientEntity);
            }

            var ingredientsToAdd = recipeUpdateIngredients.getIngredientsToAdd();
            for (var ingredient : ingredientsToAdd) {
                var ingredientEntity = ingredientMapper.toEntity(ingredient, ingredientEntities.size() + 1, recipeId);
                ingredientEntity = ingredientRepository.save(ingredientEntity);
                ingredientEntities.add(ingredientEntity);
            }
        }
        return ingredientEntities;
    }

    private List<StepEntity> updateSteps(Recipe recipe, RecipeUpdate recipeUpdate) {
        var recipeId = recipe.getId().getId();
        List<StepEntity> stepEntities = stepMapper.toEntities(recipe.getMethodSteps(), recipeId);
        if (recipeUpdate.getSteps().isPresent()) {
            var recipeUpdateSteps = recipeUpdate.getSteps().get();

            var stepsToDelete = recipeUpdateSteps.getStepsToDelete();
            var lowestIndexOfDeletedSteps = stepEntities.size();
            for (var stepId : stepsToDelete) {
                var idValue = stepId.getId();
                var stepsSize = stepEntities.size();
                for (int i = 0; i < stepsSize; i++) {
                    var stepEntity = stepEntities.get(i);
                    if (stepEntity.id().equals(idValue)) {
                        stepEntities.remove(i);
                        stepRepository.deleteById(idValue);

                        if (i < lowestIndexOfDeletedSteps) {
                            lowestIndexOfDeletedSteps = i;
                        }

                        break;
                    }
                }
            }

            var stepsSize = stepEntities.size();
            for (int i = lowestIndexOfDeletedSteps; i < stepsSize; i++) {
                var stepEntity = stepEntities.get(i);
                stepEntity = new StepEntity(
                    stepEntity.id(),
                    stepEntity.text(),
                    i + 1,
                    stepEntity.recipeId()
                );
                stepEntity = stepRepository.save(stepEntity);
                stepEntities.set(i, stepEntity);
            }

            var stepsToUpdate = recipeUpdateSteps.getStepsToUpdate();
            for (var step : stepsToUpdate) {
                var idValue = step.getId().getId();
                int position = stepEntities.stream()
                    .filter(stepEntity -> stepEntity.id().equals(idValue))
                    .findFirst().orElseThrow()
                    .position();

                var stepEntity = stepMapper.toEntity(step, position, recipeId);
                stepEntity = stepRepository.save(stepEntity);
                stepEntities.set(position - 1, stepEntity);
            }

            var stepsToAdd = recipeUpdateSteps.getStepsToAdd();
            for (var step : stepsToAdd) {
                var stepEntity = stepMapper.toEntity(step, stepEntities.size() + 1, recipeId);
                stepEntity = stepRepository.save(stepEntity);
                stepEntities.add(stepEntity);
            }
        }
        return stepEntities;
    }

    private Recipe fetchAndMap(RecipeEntity recipeEntity) {
        try {
            var recipeId = recipeEntity.id();
            var ingredientEntities = ingredientRepository.findAllByRecipeId(recipeId);
            var stepEntities = stepRepository.findAllByRecipeId(recipeId);
            return recipeMapper.toDomain(recipeEntity, ingredientEntities, stepEntities);
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
