package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.AbstractPersistable;
import pl.dkaluza.domaincore.Assembler;
import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.ValidationExecutor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Recipe extends AbstractPersistable<RecipeId> {
    private final String name;
    private final String description;
    private final List<Ingredient> ingredients;
    private final List<Step> methodSteps;
    private final Duration cookingTime;
    private final Amount portionSize;

    private Recipe(RecipeId id, String name, String description, List<Ingredient> ingredients, List<Step> methodSteps, Duration cookingTime, Amount portionSize) {
        super(id);
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.methodSteps = methodSteps;
        this.cookingTime = cookingTime;
        this.portionSize = portionSize;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public List<Step> getMethodSteps() {
        return new ArrayList<>(methodSteps);
    }

    public Duration getCookingTime() {
        return cookingTime;
    }

    public Amount getPortionSize() {
        return portionSize;
    }

    public static class Builder {
        private static boolean isNameValid(String name) {
            if (name == null || name.isBlank()) {
                return false;
            }

            int length = name.trim().length();
            return !(length < 3 || length > 256);
        }

        private static boolean isDescriptionValid(String description) {
            if (description == null) {
                return false;
            }

            return description.trim().length() <= 16384;
        }

        // TODO impl
    }
}
