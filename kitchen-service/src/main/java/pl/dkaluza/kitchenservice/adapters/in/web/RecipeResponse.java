package pl.dkaluza.kitchenservice.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.List;

record RecipeResponse(
    Long id, String name, String description,
    List<Ingredient> ingredients, List<Step> methodSteps,
    Long cookingTime, PortionSize portionSize,
    Long cookId
) {
    record Ingredient(
        Long id,
        String name,
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal value,
        String measure
    ) {}

    record Step(Long id, String text) {}

    record PortionSize(
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal value,
        String measure
    ) {}
}
