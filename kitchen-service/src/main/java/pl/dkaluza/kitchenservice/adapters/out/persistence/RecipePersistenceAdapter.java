package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.stereotype.Component;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

@Component
class RecipePersistenceAdapter implements RecipeRepository  {
    @Override
    public Recipe insertRecipe(Recipe recipe) {
        return null;
    }
}
