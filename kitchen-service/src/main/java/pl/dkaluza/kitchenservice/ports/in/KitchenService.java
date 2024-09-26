package pl.dkaluza.kitchenservice.ports.in;

import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.*;
import pl.dkaluza.kitchenservice.domain.exceptions.*;

public interface KitchenService {
    /**
     * Creates a new recipe.
     * @param recipe recipe to create
     * @return a created recipe
     * @throws ObjectAlreadyPersistedException if any of given objects is already persisted
     * @throws CookNotFoundException if given cook id can't be found
     */
    Recipe addRecipe(Recipe recipe) throws ObjectAlreadyPersistedException, CookNotFoundException;

    /**
     * Returns page with recipes matching provided criteria.
     * @param filters filters for browsed recipes
     * @param pageReq details about page to be shown
     * @return a page of recipes
     */
    Page<Recipe> browseRecipes(RecipeFilters filters, PageRequest pageReq);

    /**
     * Finds recipe by given id and returns it.
     * @param recipeId id to look for.
     * @param cookId id of cook who wants to view the recipe.
     * @return found recipe.
     * @throws RecipeNotFoundException if the recipe can't be found.
     * @throws RecipeNotOwnedException if the recipe does not belong to given cook.
     */
    Recipe viewRecipe(RecipeId recipeId, CookId cookId) throws RecipeNotFoundException, RecipeNotOwnedException;

    /**
     * Updates recipe.
     * @param recipeId id of recipe to be updated.
     * @param recipeUpdate object containing updates to be performed.
     * @param cookId id of cook who wants to update the recipe.
     * @return updated recipe.
     * @throws RecipeNotFoundException if the recipe can't be found.
     * @throws RecipeNotOwnedException if the recipe does not belong to given cook.
     * @throws IngredientNotFoundException if any ingredient in the update object does not exist or is not part of the recipe.
     * @throws StepNotFoundException if any step in the update object does not exist or is not part of the recipe.
     */
    Recipe updateRecipe(RecipeId recipeId, RecipeUpdate recipeUpdate, CookId cookId) throws RecipeNotFoundException, RecipeNotOwnedException, IngredientNotFoundException, StepNotFoundException;

    /**
     * Deletes recipe by id.
     * @param recipeId id of recipe to be deleted
     * @param cookId id of cook wha want to delete the recipe.
     * @throws RecipeNotFoundException if the recipe can't be found.
     * @throws RecipeNotOwnedException if the recipe does not belong to given cook.
     */
    void deleteRecipe(RecipeId recipeId, CookId cookId) throws RecipeNotFoundException, RecipeNotOwnedException;

    /**
     * Registers a new cook.
     * To does not matter whether cook is already saved in persistence layer - as long as object is created via
     * "newCook" static method, it can be registered in the system whether it has already been there or not.
     * @param cook a cook to be registered
     * @return a registered cook
     * @throws ObjectAlreadyPersistedException if given cook is already persisted
     */
    Cook registerCook(Cook cook) throws ObjectAlreadyPersistedException;
}
