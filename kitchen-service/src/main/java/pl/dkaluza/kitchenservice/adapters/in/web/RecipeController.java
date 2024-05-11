package pl.dkaluza.kitchenservice.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipe")
class RecipeController {
    private final RecipeWebFacade recipeFacade;

    public RecipeController(RecipeWebFacade recipeFacade) {
        this.recipeFacade = recipeFacade;
    }

    @PostMapping
    ResponseEntity<?> addRecipe(Authentication auth, @RequestBody AddRecipeRequest reqBody) {
        return recipeFacade.addRecipe(auth, reqBody);
    }
}
