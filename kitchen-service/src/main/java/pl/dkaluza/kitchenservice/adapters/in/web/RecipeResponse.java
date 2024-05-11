package pl.dkaluza.kitchenservice.adapters.in.web;

import java.math.BigDecimal;
import java.util.List;

record RecipeResponse(
    Long id, String name, String description,
    List<Ingredient> ingredients, List<Step> steps,
    Long cookingTime, PortionSize portionSize,
    Long cookId
) {
    record Ingredient(Long id, String name, BigDecimal amount, String measure) {}

    record Step(Long id, String text) {}

    record PortionSize(BigDecimal amount, String measure) {}
}
