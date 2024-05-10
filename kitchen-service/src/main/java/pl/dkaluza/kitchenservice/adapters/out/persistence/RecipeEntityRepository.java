package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.repository.CrudRepository;

interface RecipeEntityRepository extends CrudRepository<RecipeEntity, Long> {
}
