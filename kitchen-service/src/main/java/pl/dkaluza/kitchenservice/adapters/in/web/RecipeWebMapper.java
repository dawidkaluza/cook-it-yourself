package pl.dkaluza.kitchenservice.adapters.in.web;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.CookId;
import pl.dkaluza.kitchenservice.domain.Ingredient;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.Step;

import java.time.Duration;
import java.util.List;

@Mapper
abstract class RecipeWebMapper {
    Recipe toRecipe(Authentication auth, AddRecipeRequest reqBody) throws ValidationException {
        var builder = Recipe.newRecipeBuilder()
            .name(reqBody.name())
            .description(reqBody.description());

        var ingredients = reqBody.ingredients();
        if (ingredients != null) {
            for (var ingredient : ingredients) {
                builder.ingredient(ingredient.name(), ingredient.value(), ingredient.measure());
            }
        }

        var methodSteps = reqBody.methodSteps();
        if (methodSteps != null) {
            for (var methodStep : methodSteps) {
                builder.methodStep(methodStep.text());
            }
        }

        if (reqBody.cookingTime() != null) {
            builder.cookingTime(Duration.ofSeconds(reqBody.cookingTime()));
        }

        if (reqBody.portionSize() != null) {
            builder.portionSize(reqBody.portionSize().value(), reqBody.portionSize().measure());
        }

        builder.cookId(toCookIdValue(auth));

        return builder.build().produce();
    }

    @Mapping(target = "id", source = "recipe.id.id")
    @Mapping(target = "cookingTime", source = "recipe.cookingTime.seconds")
    @Mapping(target = "portionSize.value", source = "recipe.portionSize.value")
    @Mapping(target = "portionSize.measure", source = "recipe.portionSize.measure")
    @Mapping(target = "cookId", source = "recipe.cookId.id")
    abstract RecipeResponse toResponse(Recipe recipe);

    List<RecipeResponse.Ingredient> toResponseIngredients(List<Ingredient> ingredients) {
        return ingredients.stream()
            .map(ingredient -> {
                var id = ingredient.getId();
                var amt = ingredient.getAmount();
                return new RecipeResponse.Ingredient(id.getId(), ingredient.getName(), amt.getValue(), amt.getMeasure());
            }).toList();
    }

    List<RecipeResponse.Step> toResponseSteps(List<Step> steps) {
        return steps.stream()
            .map(step -> new RecipeResponse.Step(step.getId().getId(), step.getText()))
            .toList();
    }

    @Mapping(target = "id", source = "recipe.id.id")
    abstract ShortRecipeResponse toShortResponse(Recipe recipe);

    CookId toCookId(Authentication auth) {
        try {
            return CookId.of(toCookIdValue(auth)).produce();
        } catch (ValidationException e) {
            throw new IllegalStateException(
                "Couldn't acquire required cookId from authentication object." +
                "The object seems to not be valid.",
                e
            );
        }
    }

    private Long toCookIdValue(Authentication auth) {
        if (auth == null) {
            return null;
        }

        var userId = ((Jwt) auth.getPrincipal()).getClaimAsString("sub");
        return Long.valueOf(userId);
    }
}
