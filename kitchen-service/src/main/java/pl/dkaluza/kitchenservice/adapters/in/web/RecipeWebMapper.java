package pl.dkaluza.kitchenservice.adapters.in.web;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.Authentication;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.Ingredient;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.Step;

import java.time.Duration;
import java.util.List;

@Mapper
abstract class RecipeWebMapper {
    Recipe toRecipe(Authentication euth, AddRecipeRequest reqBody) throws ValidationException {
        var builder = Recipe.newRecipeBuilder()
            .name(reqBody.name())
            .description(reqBody.description());

        var ingredients = reqBody.ingredients();
        for (var ingredient : ingredients) {
            builder.ingredient(ingredient.name(), ingredient.amount(), ingredient.measure());
        }

        var methodSteps = reqBody.methodSteps();
        for (var methodStep : methodSteps) {
            builder.methodStep(methodStep.text());
        }

        builder
            .cookingTime(Duration.ofSeconds(reqBody.cookingTime()))
            .portionSize(reqBody.portionSize().amount(), reqBody.portionSize().measure());

        builder
            .cookId(1L); // TODO figure out how to retrieve cookId from auth

        return builder.build().produce();
    }

    @Mapping(target = "id", source = "recipe.id.id")
    @Mapping(target = "cookingTime", source = "recipe.cookingTime.seconds")
    @Mapping(target = "portionSize.amount", source = "recipe.portionSize.value")
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
}
