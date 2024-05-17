package pl.dkaluza.kitchenservice.ports.in;

import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.*;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotOwnedException;

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
     * Registers a new cook in the application.
     * To does not matter whether cook is already saved in persistence layer - as long as object is created via
     * "newCook" static method, it can be registered in the system whether it has already been there or not.
     * @param cook a cook to be registered
     * @return a registered cook
     * @throws ObjectAlreadyPersistedException if given cook is already persisted
     */
    Cook registerCook(Cook cook) throws ObjectAlreadyPersistedException;
}
