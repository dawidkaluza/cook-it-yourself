package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Duration;

@Table("recipe")
record RecipeEntity(@Id Long id, String name, String description, Duration cookingTime, BigDecimal portionSizeAmount, String portionSizeMeasure, Long cookId) {
}
