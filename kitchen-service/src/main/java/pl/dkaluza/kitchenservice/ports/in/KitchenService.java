package pl.dkaluza.kitchenservice.ports.in;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.*;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;

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
     * @param pageReq details about page to be shown
     * @param filters filters for browsed recipes
     * @return a page of recipes
     */
    Page<Recipe> browseRecipes(PageRequest pageReq, BrowseRecipesFilters filters);

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
