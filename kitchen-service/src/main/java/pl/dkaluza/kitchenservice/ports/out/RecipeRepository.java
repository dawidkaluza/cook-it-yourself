package pl.dkaluza.kitchenservice.ports.out;

import pl.dkaluza.kitchenservice.domain.Recipe;

public interface RecipeRepository {
    Recipe insertRecipe(Recipe recipe);
}
