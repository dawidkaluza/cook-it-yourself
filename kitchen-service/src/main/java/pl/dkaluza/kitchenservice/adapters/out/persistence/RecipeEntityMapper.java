package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dkaluza.domaincore.exceptions.ValidationException;
import pl.dkaluza.kitchenservice.domain.CookId;
import pl.dkaluza.kitchenservice.domain.Recipe;
import pl.dkaluza.kitchenservice.domain.RecipeUpdate;

import java.time.Duration;
import java.util.List;

@Mapper
interface RecipeEntityMapper {
    @Mapping(target = "id", source = "recipe.id.id")
    @Mapping(target = "cookingTime", source = "recipe.cookingTime.seconds")
    @Mapping(target = "portionSizeAmount", source = "recipe.portionSize.value")
    @Mapping(target = "portionSizeMeasure", source = "recipe.portionSize.measure")
    @Mapping(target = "cookId", source = "recipe.cookId.id")
    RecipeEntity toEntity(Recipe recipe);

    @Mapping(target = "id", source = "recipe.id.id")
    @Mapping(target = "name", source = "basicInformation.name")
    @Mapping(target = "description", source = "basicInformation.description")
    @Mapping(target = "cookingTime", source = "basicInformation.cookingTime.seconds")
    @Mapping(target = "portionSizeAmount", source = "basicInformation.portionSize.value")
    @Mapping(target = "portionSizeMeasure", source = "basicInformation.portionSize.measure")
    @Mapping(target = "cookId", source = "recipe.cookId.id")
    RecipeEntity toEntity(Recipe recipe, RecipeUpdate.BasicInformation basicInformation);

    default Recipe toDomain(RecipeEntity recipeEntity, List<IngredientEntity> ingredientEntities, List<StepEntity> stepEntities) throws ValidationException {
        var builder = Recipe.fromPersistenceRecipeBuilder()
            .id(recipeEntity.id())
            .name(recipeEntity.name())
            .description(recipeEntity.description());

        for (var ingredientEntity : ingredientEntities) {
            builder.ingredient(
                ingredientEntity.id(),
                ingredientEntity.name(), ingredientEntity.amount(), ingredientEntity.measure()
            );
        }

        for (var stepEntity : stepEntities) {
            builder.methodStep(
                stepEntity.id(), stepEntity.text()
            );
        }

        return builder
            .cookingTime(Duration.ofSeconds(recipeEntity.cookingTime()))
            .portionSize(recipeEntity.portionSizeAmount(), recipeEntity.portionSizeMeasure())
            .cookId(recipeEntity.cookId())
            .build().produce();
    }
}
