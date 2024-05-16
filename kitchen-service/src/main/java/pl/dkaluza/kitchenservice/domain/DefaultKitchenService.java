package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
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
        Assertions.assertArgument(recipe != null, "recipe is null");
        return recipeRepository.insertRecipe(recipe);
    }

    @Override
    public Page<Recipe> browseRecipes(RecipeFilters filters, PageRequest pageReq) {
        Assertions.assertArgument(filters != null, "filters is null");
        Assertions.assertArgument(pageReq != null, "pageReq is null");

        return recipeRepository.findRecipes(filters, pageReq);
    }

    @Override
    public Cook registerCook(Cook cook) throws ObjectAlreadyPersistedException {
        Assertions.assertArgument(cook != null, "cook is null");
        ObjectAlreadyPersistedException.throwIfPersisted(cook);
        return cookRepository.saveCook(cook);
    }
}
