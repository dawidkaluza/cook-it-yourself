package pl.dkaluza.kitchenservice.adapters.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.exceptions.ObjectAlreadyPersistedException;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.exceptions.CookNotFoundException;
import pl.dkaluza.kitchenservice.ports.in.KitchenService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
class RecipeWebFacade {
    private final RecipeWebMapper recipeWebMapper;
    private final KitchenService kitchenService;

    public RecipeWebFacade(RecipeWebMapper recipeWebMapper, KitchenService kitchenService) {
        this.recipeWebMapper = recipeWebMapper;
        this.kitchenService = kitchenService;
    }

    ResponseEntity<?> addRecipe(Authentication auth, AddRecipeRequest reqBody) {
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
        } catch (ObjectAlreadyPersistedException e) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                    new ErrorResponse(
                        "One of given objects is already persisted", ZonedDateTime.now(ZoneOffset.UTC)
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
}
