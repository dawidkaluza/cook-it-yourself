package pl.dkaluza.kitchenservice.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;

@RestController
@RequestMapping("/recipe")
class RecipeController {
    private final RecipeWebFacade recipeFacade;

    public RecipeController(RecipeWebFacade recipeFacade) {
        this.recipeFacade = recipeFacade;
    }

    @PostMapping
    ResponseEntity<?> addRecipe(Authentication auth, @RequestBody AddRecipeRequest reqBody) throws ObjectAlreadyPersistedException {
        return recipeFacade.addRecipe(auth, reqBody);
    }

    @GetMapping
    ResponseEntity<?> browseRecipes(
        Authentication auth,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize,
        @RequestParam(required = false, defaultValue = "") String name
    ) {
        return recipeFacade.browseRecipes(auth, page, pageSize, name);
    }

    @GetMapping("/{id}")
    ResponseEntity<?> viewRecipe(Authentication auth, @PathVariable Long id) {
        return recipeFacade.viewRecipe(auth, id);
    }
}
