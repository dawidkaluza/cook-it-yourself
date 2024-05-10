package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;
import pl.dkaluza.kitchenservice.ports.out.RecipeRepository;

class DefaultKitchenService implements KitchenService {
    private final RecipeRepository recipeRepository;

    public DefaultKitchenService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Recipe addRecipe(Recipe recipe) throws ObjectAlreadyPersistedException, CookNotFoundException {
        Assertions.assertArgument(recipe != null, "Recipe is null");
        return recipeRepository.insertRecipe(recipe);
    }
}
