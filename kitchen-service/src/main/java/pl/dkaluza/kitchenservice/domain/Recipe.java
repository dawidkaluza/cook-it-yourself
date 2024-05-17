package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    private static abstract class RecipeBuilder<T extends RecipeBuilder<T>> {
        private String name;
        private String description;
        private Duration cookingTime;
        private BigDecimal portionSizeValue;
        private String portionSizeMeasure;
        private Long cookId;

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

        public T cookId(Long cookId) {
            this.cookId = cookId;
            return getThis();
        }

        Long cookId() {
            return cookId;
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

        static CookId.CookIdFactory newCookIdFactory(Long id) {
            return new CookId.CookIdFactory(id, "cookId");
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
            var nameFactory = new NameFactory(name());
            var descriptionFactory = new DescriptionFactory(description());
            var ingredientsFactory = IngredientsFactory.newIngredients(ingredients);
            var methodStepsFactory = StepsFactory.newSteps(methodSteps);
            var portionSizeFactory = newPortionSizeFactory(portionSizeValue(), portionSizeMeasure());
            var cookIdFactory = newCookIdFactory(cookId());

            return new FactoriesComposite<>(
                () -> new Recipe(
                    null,
                    nameFactory.assemble(), descriptionFactory.assemble(),
                    ingredientsFactory.assemble(),
                    methodStepsFactory.assemble(),
                    cookingTime(),
                    portionSizeFactory.assemble(),
                    cookIdFactory.assemble()
                ),
                nameFactory, descriptionFactory,
                ingredientsFactory, methodStepsFactory,
                newCookingTimeFactory(cookingTime()),
                portionSizeFactory,
                cookIdFactory
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
            var nameFactory = new NameFactory(name());
            var descriptionFactory = new DescriptionFactory(description());
            var ingredientsFactory = IngredientsFactory.fromPersistence(ingredients);
            var methodStepsFactory = StepsFactory.fromPersistence(methodSteps);
            var portionSizeFactory = newPortionSizeFactory(portionSizeValue(), portionSizeMeasure());
            var cookIdFactory = newCookIdFactory(cookId());

            return new FactoriesComposite<>(
                () -> new Recipe(
                    idFactory.assemble(),
                    nameFactory.assemble(), descriptionFactory.assemble(),
                    ingredientsFactory.assemble(),
                    methodStepsFactory.assemble(),
                    cookingTime(),
                    portionSizeFactory.assemble(),
                    cookIdFactory.assemble()
                ),
                idFactory, nameFactory, descriptionFactory,
                ingredientsFactory, methodStepsFactory,
                newCookingTimeFactory(cookingTime()),
                portionSizeFactory,
                cookIdFactory
            );
        }
    }

    private static class NameFactory extends DefaultFactory<String> {
        NameFactory(String name) {
            super(
                ValidationExecutor.of(validator(isNameValid(name), "name", "Name must have from 3 to 256 chars")),
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

    private static class DescriptionFactory extends DefaultFactory<String> {
        protected DescriptionFactory(String description) {
            super(
                ValidationExecutor.of(validator(isDescriptionValid(description), "description", "Description must have 16384 chars at most")),
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


    private record IngredientBuilderDto(Long id, String name, BigDecimal value, String measure) {}

    private static class IngredientsFactory extends FactoriesComposite<List<Ingredient>> {
        private IngredientsFactory(Assembler<List<Ingredient>> assembler, List<Factory<?>> allFactories) {
            super(assembler, allFactories);
        }

        private static List<Ingredient> assemble(List<IngredientFactory> ingredients) {
            return ingredients.stream()
                .map(IngredientFactory::assemble)
                .toList();
        }

        private static IngredientsFactory of(List<IngredientBuilderDto> ingredients, Function<IngredientBuilderDto, IngredientFactory> mapper) {
            var factories = ingredients.stream()
                .map(mapper)
                .toList();

            var listFactory = DefaultFactory.newWithObject(
                ValidationExecutor.of(validator(!ingredients.isEmpty(), "ingredients", "Ingredients must not be empty.")),
                ingredients
            );

            var factoriesMerged = new ArrayList<Factory<?>>(factories);
            factoriesMerged.add(listFactory);

            return new IngredientsFactory(() -> assemble(factories), factoriesMerged);
        }

        public static IngredientsFactory newIngredients(List<IngredientBuilderDto> ingredients) {
            return of(
                ingredients,
                ingredient -> IngredientFactory.newIngredient(ingredient.name(), ingredient.value(), ingredient.measure(), "ingredients.")
            );
        }

        public static IngredientsFactory fromPersistence(List<IngredientBuilderDto> ingredients) {
            return of(
                ingredients,
                ingredient -> IngredientFactory.fromPersistence(ingredient.id(), ingredient.name(), ingredient.value(), ingredient.measure(), "ingredients.")
            );
        }

        @Override
        protected List<Ingredient> assemble() {
            return super.assemble();
        }
    }

    private record MethodStepBuilderDto(Long id, String text) {}

    private static class StepsFactory extends FactoriesComposite<List<Step>> {
        public StepsFactory(Assembler<List<Step>> assembler, List<? extends Factory<?>> factories) {
            super(assembler, factories);
        }

        private static List<Step> assemble(List<StepFactory> ingredients) {
            return ingredients.stream()
                .map(StepFactory::assemble)
                .toList();
        }

        private static StepsFactory of(List<MethodStepBuilderDto> steps, Function<MethodStepBuilderDto, StepFactory> mapper) {
            var factories = steps.stream()
                .map(mapper)
                .toList();

            var listFactory = DefaultFactory.newWithObject(
                ValidationExecutor.of(validator(!steps.isEmpty(), "methodSteps", "Steps must not be empty.")),
                steps
            );

            var factoriesMerged = new ArrayList<Factory<?>>(factories);
            factoriesMerged.add(listFactory);

            return new StepsFactory(() -> assemble(factories), factoriesMerged);
        }

        public static StepsFactory newSteps(List<MethodStepBuilderDto> steps) {
            return of(
                steps,
                step -> StepFactory.newStep(step.text(), "methodSteps.")
            );
        }

        public static StepsFactory fromPersistence(List<MethodStepBuilderDto> steps) {
            return of(
                steps,
                step -> StepFactory.fromPersistence(step.id(), step.text(), "methodSteps.")
            );
        }

        @Override
        protected List<Step> assemble() {
            return super.assemble();
        }
    }
}
