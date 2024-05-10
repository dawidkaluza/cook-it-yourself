package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("ingredient")
record IngredientEntity(@Id Long id, String name, BigDecimal amount, String measure, Integer position, Long recipeId) {
}
