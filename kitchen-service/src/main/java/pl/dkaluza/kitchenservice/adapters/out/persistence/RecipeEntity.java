package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("recipe")
record RecipeEntity(@Id Long id, String name, String description, long cookingTime, BigDecimal portionSizeAmount, String portionSizeMeasure, Long cookId) {
}
