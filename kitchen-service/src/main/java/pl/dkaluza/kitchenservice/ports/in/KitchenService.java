package pl.dkaluza.kitchenservice.ports.in;

import pl.dkaluza.kitchenservice.domain.Recipe;

public interface KitchenService {
    Recipe addRecipe(Recipe recipe);
}
