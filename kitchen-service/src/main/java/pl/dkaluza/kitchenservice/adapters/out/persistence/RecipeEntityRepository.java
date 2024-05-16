package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;


interface RecipeEntityRepository extends CrudRepository<RecipeEntity, Long>, PagingAndSortingRepository<RecipeEntity, Long> {
    @Query(
        "SELECT * FROM recipe AS r " +
        "WHERE (:name IS NULL OR r.name LIKE '%' || :name || '%') " +
        "AND (:cookId IS NULL OR r.cook_id = :cookId) " +
        "ORDER BY id DESC " +
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
