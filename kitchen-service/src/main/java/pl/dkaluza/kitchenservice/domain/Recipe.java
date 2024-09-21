package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;
import pl.dkaluza.kitchenservice.domain.exceptions.IngredientNotFoundException;
import pl.dkaluza.kitchenservice.domain.exceptions.StepNotFoundException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static pl.dkaluza.domaincore.Validator.validator;
import static pl.dkaluza.kitchenservice.domain.RecipeId.RecipeIdFactory;

public class Recipe extends AbstractPersistable<RecipeId> {
    private final String name;
    private final String description;
    private final List<Ingredient> ingredients;
    private final List<Step> methodSteps;
    private final Duration cookingTime;
    private final Amount portionSize;
    private final CookId cookId;

    private Recipe(RecipeId id, String name, String description, List<Ingredient> ingredients, List<Step> methodSteps, Duration cookingTime, Amount portionSize, CookId cookId) {
        super(id);
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.methodSteps = methodSteps;
        this.cookingTime = cookingTime;
        this.portionSize = portionSize;
        this.cookId = cookId;
    }

    public static NewRecipeBuilder newRecipeBuilder() {
        return new NewRecipeBuilder();
    }

    public static FromPersistenceRecipeBuilder fromPersistenceRecipeBuilder() {
        return new FromPersistenceRecipeBuilder();
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

    public CookId getCookId() {
        return cookId;
    }

    public boolean isOwnedBy(CookId cookId) {
        return this.cookId.equals(cookId);
    }

    public void validate(RecipeUpdate recipeUpdate) throws IngredientNotFoundException, StepNotFoundException {
        if (recipeUpdate.getIngredients().isPresent()) {
            var recipeUpdateIngredients = recipeUpdate.getIngredients().get();
            for (var ingredient : recipeUpdateIngredients.getIngredientsToUpdate()) {
                if (!ingredients.contains(ingredient)) {
                    throw new IngredientNotFoundException("Ingredient with id = " + ingredient.getId() + " not found");
                }
            }

            var ingredientsIds = ingredients.stream().map(Ingredient::getId).toList();
            for (var ingredientId : recipeUpdateIngredients.getIngredientsToDelete()) {
                if (!ingredientsIds.contains(ingredientId)) {
                    throw new IngredientNotFoundException("Ingredient with id = " + ingredientId + " not found");
                }
            }
        }

        if (recipeUpdate.getSteps().isPresent()) {
            var recipeUpdateSteps = recipeUpdate.getSteps().get();
            for (var step : recipeUpdateSteps.getStepsToUpdate()) {
                if (!methodSteps.contains(step)) {
                    throw new StepNotFoundException("Step with id = " + step.getId() + " not found");
                }
            }

            var stepsIds = methodSteps.stream().map(Step::getId).toList();
            for (var stepId : recipeUpdateSteps.getStepsToDelete()) {
                if (!stepsIds.contains(stepId)) {
                    throw new StepNotFoundException("Step with id = " + stepId + " not found");
                }
            }
        }
    }

    private static abstract class Builder<T extends Builder<T>> {
        private String name;
        private String description;
        private Duration cookingTime;
        private BigDecimal portionSizeValue;
        private String portionSizeMeasure;
        private Long cookId;

        private Builder() { }

        abstract T getThis();

        public abstract Factory<Recipe> build();

        public T name(String name) {
            this.name = name;
            return getThis();
        }

        String name() {
            return name;
        }

        public T description(String description) {
            this.description = description;
            return getThis();
        }

        String description() {
            return description;
        }

        public T cookingTime(Duration cookingTime) {
            this.cookingTime = cookingTime;
            return getThis();
        }

        Duration cookingTime() {
            return cookingTime;
        }

        public T portionSize(BigDecimal value, String measure) {
            this.portionSizeValue = value;
            this.portionSizeMeasure = measure;
            return getThis();
        }

        BigDecimal portionSizeValue() {
            return portionSizeValue;
        }

        String portionSizeMeasure() {
            return portionSizeMeasure;
        }

        public T cookId(Long cookId) {
            this.cookId = cookId;
            return getThis();
        }

        Long cookId() {
            return cookId;
        }
    }

    public final static class NewRecipeBuilder extends Builder<NewRecipeBuilder> {
        private final List<Ingredient.Dto> ingredients;
        private final List<Step.Dto> methodSteps;

        private NewRecipeBuilder() {
            ingredients = new ArrayList<>();
            methodSteps = new ArrayList<>();
        }

        @Override
        NewRecipeBuilder getThis() {
            return this;
        }

        public NewRecipeBuilder ingredient(String name, BigDecimal value, String measure) {
            ingredients.add(new Ingredient.Dto(null, name, value, measure));
            return this;
        }

        public NewRecipeBuilder methodStep(String text) {
            methodSteps.add(new Step.Dto(null, text));
            return this;
        }

        @Override
        public Factory<Recipe> build() {
            var nameFactory = new NameFactory(name());
            var descriptionFactory = new DescriptionFactory(description());
            var ingredientsFactory = Ingredient.ListFactory.newIngredients(ingredients, false, "ingredients");
            var methodStepsFactory = Step.ListFactory.newSteps(methodSteps, false, "methodSteps");
            var cookingTimeFactory = new CookingTimeFactory(cookingTime());
            var portionSizeFactory = new PortionSizeFactory(portionSizeValue(), portionSizeMeasure(), "portionSize.");
            var cookIdFactory = new CookId.Factory(cookId(), "cookId");

            return new FactoriesComposite<>(
                () -> new Recipe(
                    null,
                    nameFactory.assemble(), descriptionFactory.assemble(),
                    ingredientsFactory.assemble(),
                    methodStepsFactory.assemble(),
                    cookingTimeFactory.assemble(),
                    portionSizeFactory.assemble(),
                    cookIdFactory.assemble()
                ),
                nameFactory, descriptionFactory,
                ingredientsFactory, methodStepsFactory,
                cookingTimeFactory,
                portionSizeFactory,
                cookIdFactory
            );
        }
    }

    public final static class FromPersistenceRecipeBuilder extends Builder<FromPersistenceRecipeBuilder> {
        private Long id;
        private final List<Ingredient.Dto> ingredients;
        private final List<Step.Dto> methodSteps;

        private FromPersistenceRecipeBuilder() {
            ingredients = new ArrayList<>();
            methodSteps = new ArrayList<>();
        }

        @Override
        FromPersistenceRecipeBuilder getThis() {
            return this;
        }

        public FromPersistenceRecipeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FromPersistenceRecipeBuilder ingredient(Long id, String name, BigDecimal value, String measure) {
            ingredients.add(new Ingredient.Dto(id, name, value, measure));
            return this;
        }

        public FromPersistenceRecipeBuilder methodStep(Long id, String text) {
            methodSteps.add(new Step.Dto(id, text));
            return this;
        }

        @Override
        public Factory<Recipe> build() {
            var idFactory = new RecipeIdFactory(id);
            var nameFactory = new NameFactory(name());
            var descriptionFactory = new DescriptionFactory(description());
            var ingredientsFactory = Ingredient.ListFactory.fromPersistence(ingredients, false, "ingredients");
            var methodStepsFactory = Step.ListFactory.fromPersistence(methodSteps, false, "methodSteps");
            var cookingTimeFactory = new CookingTimeFactory(cookingTime());
            var portionSizeFactory = new PortionSizeFactory(portionSizeValue(), portionSizeMeasure(), "portionSize.");
            var cookIdFactory = new CookId.Factory(cookId(), "cookId");

            return new FactoriesComposite<>(
                () -> new Recipe(
                    idFactory.assemble(),
                    nameFactory.assemble(), descriptionFactory.assemble(),
                    ingredientsFactory.assemble(),
                    methodStepsFactory.assemble(),
                    cookingTimeFactory.assemble(),
                    portionSizeFactory.assemble(),
                    cookIdFactory.assemble()
                ),
                idFactory, nameFactory, descriptionFactory,
                ingredientsFactory, methodStepsFactory,
                cookingTimeFactory,
                portionSizeFactory,
                cookIdFactory
            );
        }
    }

    static class NameFactory extends DefaultFactory<String> {
        NameFactory(String name) {
            this(name, "");
        }

        NameFactory(String name, String prefix) {
            super(
                ValidationExecutor.of(validator(isNameValid(name), prefix + "name", "Name must have from 3 to 256 chars")),
                () -> name.trim()
            );
        }

        private static boolean isNameValid(String name) {
            if (name == null || name.isBlank()) {
                return false;
            }

            int length = name.trim().length();
            return !(length < 3 || length > 256);
        }

        @Override
        protected String assemble() {
            return super.assemble();
        }
    }

    static class DescriptionFactory extends DefaultFactory<String> {
        DescriptionFactory(String description) {
            this(description, "");
        }

        DescriptionFactory(String description, String prefix) {
            super(
                ValidationExecutor.of(validator(isDescriptionValid(description), prefix + "description", "Description must have 16384 chars at most")),
                () -> description.trim()
            );
        }

        private static boolean isDescriptionValid(String description) {
            if (description == null) {
                return false;
            }

            return description.trim().length() <= 16384;
        }

        @Override
        protected String assemble() {
            return super.assemble();
        }
    }

    static class CookingTimeFactory extends DefaultFactory<Duration> {
        CookingTimeFactory(Duration cookingTime) {
            this(cookingTime, "");
        }

        CookingTimeFactory(Duration cookingTime, String prefix) {
            super(
                ValidationExecutor.of(
                    validator(
                        !(cookingTime == null || cookingTime.isZero() || cookingTime.isNegative() || cookingTime.getNano() != 0),
                        prefix + "cookingTime", "Cooking time must be a positive number with an accuracy of one second"
                    )
                ),
                () -> cookingTime
            );
        }

        @Override
        protected Duration assemble() {
            return super.assemble();
        }
    }

    static class PortionSizeFactory extends Amount.Factory {
        PortionSizeFactory(BigDecimal value, String measure) {
            this(value, measure, "");
        }

        PortionSizeFactory(BigDecimal value, String measure, String prefix) {
            super(
                value, measure,
                prefix,
                Validator.validator(value != null && value.signum() > 0, prefix + "value", "Value must be a positive number")
            );
        }

        @Override
        protected Amount assemble() {
            return super.assemble();
        }
    }
}
