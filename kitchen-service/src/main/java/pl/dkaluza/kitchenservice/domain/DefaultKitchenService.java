package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.exceptions.*;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;
import pl.dkaluza.kitchenservice.ports.out.CookRepository;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

class DefaultKitchenService implements KitchenService {
    private final RecipeRepository recipeRepository;
    private final CookRepository cookRepository;

    public DefaultKitchenService(RecipeRepository recipeRepository, CookRepository cookRepository) {
        this.recipeRepository = recipeRepository;
        this.cookRepository = cookRepository;
    }

    @Override
    public Recipe addRecipe(Recipe recipe) throws ObjectAlreadyPersistedException, CookNotFoundException {
        Assertions.assertArgument(recipe != null, "recipe is null");
        return recipeRepository.insertRecipe(recipe);
    }

    @Override
    public Page<Recipe> browseRecipes(RecipeFilters filters, PageRequest pageReq) {
        Assertions.assertArgument(filters != null, "filters is null");
        Assertions.assertArgument(pageReq != null, "pageReq is null");

        if (filters.isEmpty()) {
            return recipeRepository.findAllRecipes(pageReq);
        }

        return recipeRepository.findRecipes(filters, pageReq);
    }

    @Override
    public Recipe viewRecipe(RecipeId recipeId, CookId cookId) throws RecipeNotFoundException, RecipeNotOwnedException {
        Assertions.assertArgument(recipeId != null, "recipeId is null");
        Assertions.assertArgument(cookId != null, "cookId is null");

        var recipe = recipeRepository.findRecipeById(recipeId)
            .orElseThrow(() -> new RecipeNotFoundException("Recipe with id = " + recipeId + " could not be found"));

        if (!recipe.isOwnedBy(cookId)) {
            throw new RecipeNotOwnedException("Recipe with id = " + recipeId + " is not owned by " + cookId);
        }

        return recipe;
    }

    @Override
    public Recipe updateRecipe(RecipeId recipeId, RecipeUpdate recipeUpdate, CookId cookId) throws RecipeNotFoundException, RecipeNotOwnedException, IngredientNotFoundException, StepNotFoundException {
        Assertions.assertArgument(recipeId != null, "recipeId is null");
        Assertions.assertArgument(recipeUpdate != null, "recipeUpdate is null");
        Assertions.assertArgument(cookId != null, "cookId is null");

        var recipe = recipeRepository.findRecipeById(recipeId)
            .orElseThrow(() -> new RecipeNotFoundException("Recipe with id = " + recipeId + " could not be found"));

        if (!recipe.isOwnedBy(cookId)) {
            throw new RecipeNotOwnedException("Recipe with id = " + recipeId + " is not owned by " + cookId);
        }

        recipe.validate(recipeUpdate);
        return recipeRepository.updateRecipe(recipe, recipeUpdate);
    }

    @Override
    public void deleteRecipe(RecipeId recipeId, CookId cookId) throws RecipeNotFoundException, RecipeNotOwnedException {
        Assertions.assertArgument(recipeId != null, "recipeId is null");
        Assertions.assertArgument(cookId != null, "cookId is null");

        var recipe = recipeRepository.findRecipeById(recipeId)
            .orElseThrow(() -> new RecipeNotFoundException("Recipe with id = " + recipeId + " could not be found"));

        if (!recipe.isOwnedBy(cookId)) {
            throw new RecipeNotOwnedException("Recipe with id = " + recipeId + " is not owned by " + cookId);
        }

        recipeRepository.deleteRecipe(recipe);
    }

    @Override
    public Cook registerCook(Cook cook) throws ObjectAlreadyPersistedException {
        Assertions.assertArgument(cook != null, "cook is null");
        ObjectAlreadyPersistedException.throwIfPersisted(cook);
        return cookRepository.saveCook(cook);
    }
}
