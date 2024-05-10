package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("step")
record StepEntity(@Id Long id, String text, Integer position, Long recipeId) {
}
