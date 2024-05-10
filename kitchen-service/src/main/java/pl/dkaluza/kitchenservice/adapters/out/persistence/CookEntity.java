package pl.dkaluza.kitchenservice.adapters.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("cook")
record CookEntity(@Id Long id) {
}
