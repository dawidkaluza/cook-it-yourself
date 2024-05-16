package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StepEntityRepository extends CrudRepository<StepEntity, Long> {
    List<StepEntity> findAllByRecipeId(Long recipeId);
}
