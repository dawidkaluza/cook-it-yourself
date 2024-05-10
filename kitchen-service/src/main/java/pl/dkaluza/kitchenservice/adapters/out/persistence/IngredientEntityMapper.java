package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dkaluza.kitchenservice.domain.Ingredient;

@Mapper
interface IngredientEntityMapper {
    @Mapping(target = "id", source = "ingredient.id.id")
    @Mapping(target = "amount", source = "ingredient.amount.value")
    @Mapping(target = "measure", source = "ingredient.amount.measure")
    IngredientEntity toEntity(Ingredient ingredient, Integer position, Long recipeId);
}
