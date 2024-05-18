package pl.dkaluza.kitchenservice.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.PageRequest;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.RecipeFilters;
import pl.dkaluza.kitchenservice.domain.RecipeId;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.RecipeNotOwnedException;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
class RecipeWebFacade {
    private static final Map<String, String> BROWSE_RECIPES_ERROR_FIELDS_MAP;

    static {
        BROWSE_RECIPES_ERROR_FIELDS_MAP = new HashMap<>();
        BROWSE_RECIPES_ERROR_FIELDS_MAP.put("pageNumber", "page");
    }

    private final RecipeWebMapper recipeWebMapper;
    private final PageWebMapper pageWebMapper;
    private final KitchenService kitchenService;

    public RecipeWebFacade(RecipeWebMapper recipeWebMapper, PageWebMapper pageWebMapper, KitchenService kitchenService) {
        this.recipeWebMapper = recipeWebMapper;
        this.pageWebMapper = pageWebMapper;
        this.kitchenService = kitchenService;
    }

    ResponseEntity<?> addRecipe(Authentication auth, AddRecipeRequest reqBody) throws ObjectAlreadyPersistedException {
        try {
            var recipe = recipeWebMapper.toRecipe(auth, reqBody);
            recipe = kitchenService.addRecipe(recipe);
            var respBody = recipeWebMapper.toResponse(recipe);
            return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
        } catch (ValidationException e) {
            var errorRespBodyFields = e.getErrors().stream()
                .map(fieldError -> new ErrorResponse.Field(fieldError.name(), fieldError.message()))
                .toList();

            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                    new ErrorResponse(
                        "Invalid fields values", ZonedDateTime.now(ZoneOffset.UTC), errorRespBodyFields
                    )
                );
        } catch (CookNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    new ErrorResponse(
                        "Cook with given id could not be found", ZonedDateTime.now(ZoneOffset.UTC)
                    )
                );
        }
    }

    ResponseEntity<?> browseRecipes(Authentication auth, int page, int pageSize, String name) {
        try {
            var cookId = recipeWebMapper.toRequiredCookId(auth);
            var filters = RecipeFilters.of(name, cookId);
            var pageReq = PageRequest.of(page, pageSize).produce();
            var recipesPage = kitchenService.browseRecipes(filters, pageReq);
            var responseRecipes = recipesPage.getItems().stream()
                .map(recipeWebMapper::toShortResponse)
                .toList();
            var responseRecipesPage = pageWebMapper.toResponse(responseRecipes, recipesPage.getTotalPages());
            return ResponseEntity.ok(responseRecipesPage);
        } catch (ValidationException e) {
            var errors = e.getErrors().stream()
                .map(fieldError ->
                    new ErrorResponse.Field(
                        BROWSE_RECIPES_ERROR_FIELDS_MAP.getOrDefault(fieldError.name(), fieldError.name()),
                        fieldError.message()
                    )
                )
                .toList();

            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                    new ErrorResponse(
                        "Invalid fields values", ZonedDateTime.now(ZoneOffset.UTC), errors
                    )
                );
        }
    }

    ResponseEntity<?> viewRecipe(Authentication auth, Long id) {
        try {
            var recipeId = RecipeId.of(id).produce();
            var cookId = recipeWebMapper.toRequiredCookId(auth);
            var recipe = kitchenService.viewRecipe(recipeId, cookId);
            var respBody = recipeWebMapper.toResponse(recipe);
            return ResponseEntity.ok(respBody);
        } catch (ValidationException e) {
            var errors = e.getErrors().stream()
                .map(error -> new ErrorResponse.Field(error.name(), error.message()))
                .toList();

            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                    new ErrorResponse(
                        "Invalid fields values", ZonedDateTime.now(ZoneOffset.UTC), errors
                    )
                );
        } catch (RecipeNotFoundException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    new ErrorResponse(
                        "Recipe with given id could not be found", ZonedDateTime.now(ZoneOffset.UTC)
                    )
                );
        } catch (RecipeNotOwnedException e) {
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
        }
    }
}
