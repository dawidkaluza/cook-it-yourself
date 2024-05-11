package pl.dkaluza.kitchenservice.adapters.in.web;

import java.math.BigDecimal;
import java.util.List;

record AddRecipeRequest(
    String name, String description,
    List<Ingredient> ingredients, List<Step> methodSteps,
    Long cookingTime,
    PortionSize portionSize
) {
    record Ingredient(String name, BigDecimal amount, String measure) { }

    record Step(String text) { }

    record PortionSize(BigDecimal amount, String measure) { }
}
