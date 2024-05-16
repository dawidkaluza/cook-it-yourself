package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface IngredientEntityRepository extends CrudRepository<IngredientEntity, Long> {
    List<IngredientEntity> findAllByRecipeId(Long recipeId);
}
