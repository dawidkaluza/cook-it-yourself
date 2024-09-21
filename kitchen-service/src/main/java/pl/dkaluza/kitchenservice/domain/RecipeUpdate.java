package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.FactoriesComposite;
import pl.dkaluza.domaincore.Factory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RecipeUpdate {
    private final BasicInformation basicInformation;
    private final Ingredients ingredients;
    private final Steps steps;

    private RecipeUpdate(BasicInformation basicInformation, Ingredients ingredients, Steps steps) {
        this.basicInformation = basicInformation;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<BasicInformation> getBasicInformation() {
        return Optional.ofNullable(basicInformation);
    }

    public Optional<Ingredients> getIngredients() {
        return Optional.ofNullable(ingredients);
    }

    public Optional<Steps> getSteps() {
        return Optional.ofNullable(steps);
    }

    public static class BasicInformation {
        private final String name;
        private final String description;
        private final Duration cookingTime;
        private final Amount portionSize;

        private BasicInformation(String name, String description, Duration cookingTime, Amount portionSize) {
            this.name = name;
            this.description = description;
            this.cookingTime = cookingTime;
            this.portionSize = portionSize;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Duration getCookingTime() {
            return cookingTime;
        }

        public Amount getPortionSize() {
            return portionSize;
        }

        public static class Builder {
            private String name;
            private String description;
            private Duration cookingTime;
            private BigDecimal portionSizeValue;
            private String portionSizeMeasure;

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder cookingTime(Duration cookingTime) {
                this.cookingTime = cookingTime;
                return this;
            }

            public Builder portionSize(BigDecimal value, String measure) {
                this.portionSizeValue = value;
                this.portionSizeMeasure = measure;
                return this;
            }

            BasicInformation.Factory build() {
                return new BasicInformation.Factory(
                    new Recipe.NameFactory(name, "basicInformation."),
                    new Recipe.DescriptionFactory(description, "basicInformation."),
                    new Recipe.CookingTimeFactory(cookingTime, "basicInformation."),
                    new Recipe.PortionSizeFactory(portionSizeValue, portionSizeMeasure, "basicInformation.portionSize.")
                );
            }
        }

        static class Factory extends FactoriesComposite<BasicInformation> {
            Factory() {
                super(() -> null);
            }

            Factory(
                Recipe.NameFactory nameFactory, Recipe.DescriptionFactory descriptionFactory,
                Recipe.CookingTimeFactory cookingTimeFactory, Recipe.PortionSizeFactory portionSizeFactory
            ) {
                super(
                    () -> new BasicInformation(
                        nameFactory.assemble(),
                        descriptionFactory.assemble(),
                        cookingTimeFactory.assemble(),
                        portionSizeFactory.assemble()
                    ),
                    nameFactory, descriptionFactory, cookingTimeFactory, portionSizeFactory
                );
            }

            @Override
            protected BasicInformation assemble() {
                return super.assemble();
            }
        }
    }

    public static class Ingredients {
        private final List<Ingredient> ingredientsToAdd;
        private final List<Ingredient> ingredientsToUpdate;
        private final List<IngredientId> ingredientsToDelete;

        private Ingredients(List<Ingredient> ingredientsToAdd, List<Ingredient> ingredientsToUpdate, List<IngredientId> ingredientsToDelete) {
            this.ingredientsToAdd = ingredientsToAdd;
            this.ingredientsToUpdate = ingredientsToUpdate;
            this.ingredientsToDelete = ingredientsToDelete;
        }

        public List<Ingredient> getIngredientsToAdd() {
            return ingredientsToAdd;
        }

        public List<Ingredient> getIngredientsToUpdate() {
            return ingredientsToUpdate;
        }

        public List<IngredientId> getIngredientsToDelete() {
            return ingredientsToDelete;
        }

        public static class Builder {
            private final List<Ingredient.Dto> ingredientsToAdd;
            private final List<Ingredient.Dto> ingredientsToUpdate;
            private final List<Long> ingredientsToDelete;

            Builder() {
                this.ingredientsToAdd = new ArrayList<>();
                this.ingredientsToUpdate = new ArrayList<>();
                this.ingredientsToDelete = new ArrayList<>();
            }

            public Builder ingredientToAdd(String name, BigDecimal value, String measure) {
                ingredientsToAdd.add(new Ingredient.Dto(null, name, value, measure));
                return this;
            }

            public Builder ingredientToUpdate(Long id, String name, BigDecimal value, String measure) {
                ingredientsToUpdate.add(new Ingredient.Dto(id, name, value, measure));
                return this;
            }

            public Builder ingredientToDelete(Long id) {
                ingredientsToDelete.add(id);
                return this;
            }

            Factory build() {
                return new Factory(
                    Ingredient.ListFactory.newIngredients(ingredientsToAdd, true, "ingredients.ingredientsToAdd"),
                    Ingredient.ListFactory.fromPersistence(ingredientsToUpdate, true, "ingredients.ingredientsToUpdate"),
                    new IngredientId.ListFactory(ingredientsToDelete, "ingredients.ingredientsToDelete")
                );
            }
        }
        
        static class Factory extends FactoriesComposite<Ingredients> {
            Factory() {
                super(() -> null);
            }

            Factory(
                Ingredient.ListFactory ingredientsToAddFactory,
                Ingredient.ListFactory ingredientsToUpdateFactory,
                IngredientId.ListFactory ingredientsToDeleteFactory
            ) {
                super(
                    () -> new Ingredients(
                        ingredientsToAddFactory.assemble(),
                        ingredientsToUpdateFactory.assemble(),
                        ingredientsToDeleteFactory.assemble()
                    ),
                    ingredientsToAddFactory, ingredientsToUpdateFactory, ingredientsToDeleteFactory
                );
            }

            @Override
            protected Ingredients assemble() {
                return super.assemble();
            }
        }
    }

    public static class Steps {
        private final List<Step> stepsToAdd;
        private final List<Step> stepsToUpdate;
        private final List<StepId> stepsToDelete;

        private Steps(List<Step> stepsToAdd, List<Step> stepsToUpdate, List<StepId> stepsToDelete) {
            this.stepsToAdd = stepsToAdd;
            this.stepsToUpdate = stepsToUpdate;
            this.stepsToDelete = stepsToDelete;
        }

        public List<Step> getStepsToAdd() {
            return stepsToAdd;
        }

        public List<Step> getStepsToUpdate() {
            return stepsToUpdate;
        }

        public List<StepId> getStepsToDelete() {
            return stepsToDelete;
        }
        
        public static class Builder {
            private final List<Step.Dto> stepsToAdd;
            private final List<Step.Dto> stepsToUpdate;
            private final List<Long> stepsToDelete;
            
            Builder() {
                stepsToAdd = new ArrayList<>();
                stepsToUpdate = new ArrayList<>();
                stepsToDelete = new ArrayList<>();
            }

            public Builder stepToAdd(String text) {
                stepsToAdd.add(new Step.Dto(null, text));
                return this;
            }
    
            public Builder stepToUpdate(Long id, String text) {
                stepsToUpdate.add(new Step.Dto(id, text));
                return this;
            }
    
            public Builder stepToDelete(Long id) {
                stepsToDelete.add(id);
                return this;
            }
            
            Factory build() {
                return new Factory(
                    Step.ListFactory.newSteps(stepsToAdd, true, "steps.stepsToAdd"),
                    Step.ListFactory.fromPersistence(stepsToUpdate, true, "steps.stepsToUpdate"),
                    new StepId.ListFactory(stepsToDelete, "steps.stepsToDelete")
                );
            }
        }

        static class Factory extends FactoriesComposite<Steps> {
            private Factory() {
                super(() -> null);
            }

            private Factory(
                Step.ListFactory stepsToAddFactory,
                Step.ListFactory stepsToUpdateFactory,
                StepId.ListFactory stepsToDeleteFactory
            ) {
                super(
                    () -> new Steps(
                        stepsToAddFactory.assemble(),
                        stepsToUpdateFactory.assemble(),
                        stepsToDeleteFactory.assemble()
                    ),
                    stepsToAddFactory, stepsToUpdateFactory, stepsToDeleteFactory
                );
            }

            @Override
            protected Steps assemble() {
                return super.assemble();
            }
        }
    }

    public static class Builder {
        private BasicInformation.Builder basicInformationBuilder;
        private Ingredients.Builder ingredientsBuilder;
        private Steps.Builder stepsBuilder;

        private Builder() { }

        public Builder basicInformation(Consumer<BasicInformation.Builder> consumer) {
            var builder = new BasicInformation.Builder();
            consumer.accept(builder);
            basicInformationBuilder = builder;
            return this;
        }

        public Builder ingredients(Consumer<Ingredients.Builder> consumer) {
            var builder = new Ingredients.Builder();
            consumer.accept(builder);
            ingredientsBuilder = builder;
            return this;
        }

        public Builder steps(Consumer<Steps.Builder> consumer) {
            var builder = new Steps.Builder();
            consumer.accept(builder);
            stepsBuilder = builder;
            return this;
        }

        public Factory<RecipeUpdate> build() {
            var basicInfoFactory = basicInformationBuilder == null ? new BasicInformation.Factory() : basicInformationBuilder.build();
            var ingredientsFactory = ingredientsBuilder == null ? new Ingredients.Factory() : ingredientsBuilder.build();
            var stepsFactory = stepsBuilder == null ? new Steps.Factory() : stepsBuilder.build();

            return new FactoriesComposite<>(
                () -> new RecipeUpdate(
                    basicInfoFactory.assemble(),
                    ingredientsFactory.assemble(),
                    stepsFactory.assemble()
                ),
                List.of(basicInfoFactory, ingredientsFactory, stepsFactory)
            );
        }
    }
}
