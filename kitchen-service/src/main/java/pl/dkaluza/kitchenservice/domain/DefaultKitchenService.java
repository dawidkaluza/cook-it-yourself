package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
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
        Assertions.assertArgument(recipe != null, "Recipe is null");
        return recipeRepository.insertRecipe(recipe);
    }

    @Override
    public Cook registerCook(Cook cook) throws ObjectAlreadyPersistedException {
        Assertions.assertArgument(cook != null, "Cook is null");
        ObjectAlreadyPersistedException.throwIfPersisted(cook);
        return cookRepository.saveCook(cook);
    }
}
