package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.repository.CrudRepository;

interface IngredientEntityRepository extends CrudRepository<IngredientEntity, Long> {
}
