package pl.dkaluza.kitchenservice.ports.in;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.Recipe;
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
}
