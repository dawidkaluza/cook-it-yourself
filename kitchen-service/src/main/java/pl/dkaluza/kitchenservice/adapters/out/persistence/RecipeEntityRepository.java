package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface RecipeEntityRepository extends CrudRepository<RecipeEntity, Long> {
    @Query(
        "SELECT * FROM recipe AS r " +
        "WHERE (:name IS NULL OR r.name LIKE '%' || :name || '%') " +
        "AND (:cookId IS NULL OR r.cook_id = :cookId) " +
        "OFFSET :pageOffset LIMIT :pageSize"
    )
    List<RecipeEntity> findByFilters(String name, Long cookId, int pageOffset, int pageSize);

    @Query(
        "SELECT COUNT(id) FROM recipe AS r " +
        "WHERE (:name IS NULL OR r.name LIKE '%' || :name || '%') " +
        "AND (:cookId IS NULL OR r.cook_id = :cookId)"
    )
    int countByFilters(String name, Long cookId);
}
