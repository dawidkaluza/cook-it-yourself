package pl.dkaluza.kitchenservice.adapters.in.web;

import java.math.BigDecimal;
import java.util.List;


// TODO verify that when some fields are not present in JSON input, they will be null
record UpdateRecipeRequest(BasicInformation basicInformation, Ingredients ingredients, Steps steps) {
    record BasicInformation(String name, String description, Long cookingTime, PortionSize portionSize) {}

    record Ingredients(
        List<New> ingredientsToAdd,
        List<Update> ingredientsToUpdate,
        List<Long> ingredientsToDelete
    ) {
        record New(String name, BigDecimal value, String measure) { }

        record Update(Long id, String name, BigDecimal value, String measure) { }
    }

    record Steps(
        List<New> stepsToAdd,
        List<Update> stepsToUpdate,
        List<Long> stepsToDelete
    ) {
        record New(String text) { }

        record Update(Long id, String text) { }
    }

    record PortionSize(BigDecimal value, String measure) { }
}
