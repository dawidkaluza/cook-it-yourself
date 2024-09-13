package pl.dkaluza.kitchenservice.ports.out;

import pl.dkaluza.domaincore.Page;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.kitchenservice.domain.RecipeFilters;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.RecipeId;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;

import java.util.Optional;

public interface RecipeRepository {
    Recipe insertRecipe(Recipe recipe) throws ObjectAlreadyPersistedException, CookNotFoundException;

    Optional<Recipe> findRecipeById(RecipeId id);

    Page<Recipe> findAllRecipes(PageRequest pageReq);

    Page<Recipe> findRecipes(RecipeFilters filters, PageRequest pageReq);

    void deleteRecipe(Recipe recipe);
}
