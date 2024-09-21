package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dkaluza.kitchenservice.domain.Ingredient;

import java.util.ArrayList;
import java.util.List;

@Mapper
interface IngredientEntityMapper {
    @Mapping(target = "id", source = "ingredient.id.id")
    @Mapping(target = "amount", source = "ingredient.amount.value")
    @Mapping(target = "measure", source = "ingredient.amount.measure")
    IngredientEntity toEntity(Ingredient ingredient, Integer position, Long recipeId);

    default List<IngredientEntity> toEntities(List<Ingredient> ingredients, Long recipeId) {
        var entities = new ArrayList<IngredientEntity>();
        int ingredientsSize = ingredients.size();
        for (int i = 0; i < ingredientsSize; i++) {
            Ingredient ingredient = ingredients.get(i);
            entities.add(toEntity(ingredient, i + 1, recipeId));
        }
        return entities;
    }
}
