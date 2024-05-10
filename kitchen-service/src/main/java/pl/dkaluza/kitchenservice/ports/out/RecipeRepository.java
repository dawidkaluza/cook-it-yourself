package pl.dkaluza.kitchenservice.ports.out;

import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.Cook;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;

public interface RecipeRepository {
    Recipe insertRecipe(Recipe recipe) throws ObjectAlreadyPersistedException, CookNotFoundException;

    Cook insertCook(Cook cook);
}
