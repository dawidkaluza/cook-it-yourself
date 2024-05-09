package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static pl.dkaluza.domaincore.Validator.validator;
import static pl.dkaluza.kitchenservice.domain.Amount.AmountFactory;
import static pl.dkaluza.kitchenservice.domain.Ingredient.IngredientFactory;
import static pl.dkaluza.kitchenservice.domain.RecipeId.RecipeIdFactory;
import static pl.dkaluza.kitchenservice.domain.Step.StepFactory;

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

    private static abstract class RecipeBuilder<T extends RecipeBuilder<T>> {
        private String name;
        private String description;
        private Duration cookingTime;
        private BigDecimal portionSizeValue;
        private String portionSizeMeasure;

        private RecipeBuilder() { }

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

        static DefaultFactory<String> newNameFactory(String name) {
            return DefaultFactory.newWithObject(
                ValidationExecutor.of(validator(isNameValid(name), "name", "Name must have from 3 to 256 chars")),
                name
            );
        }

        static DefaultFactory<String> newDescriptionFactory(String description) {
            return DefaultFactory.newWithObject(
                ValidationExecutor.of(validator(isDescriptionValid(description), "description", "Description must have 16384 chars at most")),
                description
            );
        }

        static DefaultFactory<Duration> newCookingTimeFactory(Duration cookingTime) {
            return DefaultFactory.newWithObject(
                ValidationExecutor.of(
                    validator(
                        !(cookingTime == null || cookingTime.isZero() || cookingTime.isNegative() || cookingTime.getNano() != 0),
                        "cookingTime", "Cooking time must be a positive number with resolution to a second"
                    )
                ),
                cookingTime
            );
        }

        static AmountFactory newPortionSizeFactory(BigDecimal value, String measure) {
            final var prefix = "portionSize.";
            return new AmountFactory(
                value, measure,
                prefix,
                Validator.validator(value != null && value.signum() > 0, prefix + "value", "Value must be a positive number")
            );
        }
    }

    public final static class NewRecipeBuilder extends RecipeBuilder<NewRecipeBuilder> {
        private final List<IngredientBuilderDto> ingredients;
        private final List<MethodStepBuilderDto> methodSteps;

        private NewRecipeBuilder() {
            ingredients = new ArrayList<>();
            methodSteps = new ArrayList<>();
        }

        @Override
        NewRecipeBuilder getThis() {
            return this;
        }

        public NewRecipeBuilder ingredient(String name, BigDecimal value, String measure) {
            ingredients.add(new IngredientBuilderDto(null, name, value, measure));
            return this;
        }

        public NewRecipeBuilder methodStep(String text) {
            methodSteps.add(new MethodStepBuilderDto(null, text));
            return this;
        }

        @Override
        public Factory<Recipe> build() {
            var portionSizeFactory = newPortionSizeFactory(portionSizeValue(), portionSizeMeasure());
            var ingredientFactories = ingredients.stream()
                .map(dto -> IngredientFactory.newIngredient(dto.name(), dto.value(), dto.measure(), "ingredients[]."))
                .toList();
            var methodStepFactories = methodSteps.stream()
                .map(dto -> StepFactory.newStep(dto.text(), "methodSteps[]."))
                .toList();

            var factories = new ArrayList<Factory<?>>();
            factories.add(newNameFactory(name()));
            factories.add(newDescriptionFactory(description()));
            factories.addAll(ingredientFactories);
            factories.addAll(methodStepFactories);
            factories.add(newCookingTimeFactory(cookingTime()));
            factories.add(portionSizeFactory);
            return new FactoriesComposite<>(
                () -> new Recipe(
                    null,
                    name(), description(),
                    ingredientFactories.stream().map(IngredientFactory::assemble).toList(),
                    methodStepFactories.stream().map(StepFactory::assemble).toList(),
                    cookingTime(),
                    portionSizeFactory.assemble()
                ),
                factories
            );
        }
    }

    public final static class FromPersistenceRecipeBuilder extends RecipeBuilder<FromPersistenceRecipeBuilder> {
        private Long id;
        private final List<IngredientBuilderDto> ingredients;
        private final List<MethodStepBuilderDto> methodSteps;

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
            ingredients.add(new IngredientBuilderDto(id, name, value, measure));
            return this;
        }

        public FromPersistenceRecipeBuilder methodStep(Long id, String text) {
            methodSteps.add(new MethodStepBuilderDto(id, text));
            return this;
        }

        @Override
        public Factory<Recipe> build() {
            var idFactory = new RecipeIdFactory(id);
            var portionSizeFactory = newPortionSizeFactory(portionSizeValue(), portionSizeMeasure());
            var ingredientFactories = ingredients.stream()
                .map(dto -> IngredientFactory.fromPersistence(dto.id(), dto.name(), dto.value(), dto.measure(), "ingredients[]."))
                .toList();
            var methodStepFactories = methodSteps.stream()
                .map(dto -> StepFactory.fromPersistence(dto.id(), dto.text(), "methodSteps[]."))
                .toList();

            var factories = new ArrayList<Factory<?>>();
            factories.add(idFactory);
            factories.add(newNameFactory(name()));
            factories.add(newDescriptionFactory(description()));
            factories.addAll(ingredientFactories);
            factories.addAll(methodStepFactories);
            factories.add(newCookingTimeFactory(cookingTime()));
            factories.add(portionSizeFactory);
            return new FactoriesComposite<>(
                () -> new Recipe(
                    idFactory.assemble(),
                    name(), description(),
                    ingredientFactories.stream().map(IngredientFactory::assemble).toList(),
                    methodStepFactories.stream().map(StepFactory::assemble).toList(),
                    cookingTime(),
                    portionSizeFactory.assemble()
                ),
                factories
            );
        }
    }

    private record IngredientBuilderDto(Long id, String name, BigDecimal value, String measure) {}

    private record MethodStepBuilderDto(Long id, String text) {}
}
