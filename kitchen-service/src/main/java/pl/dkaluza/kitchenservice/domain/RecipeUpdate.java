package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.Assembler;
import pl.dkaluza.domaincore.FactoriesComposite;
import pl.dkaluza.domaincore.Factory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static pl.dkaluza.domaincore.DefaultFactory.validatingFactory;
import static pl.dkaluza.domaincore.ValidationExecutor.of;
import static pl.dkaluza.domaincore.Validator.validator;

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
                var deletionsOverlapUpdates = false;
                for (var ingredient : ingredientsToUpdate) {
                    if (ingredientsToDelete.contains(ingredient.id())) {
                        deletionsOverlapUpdates = true;
                        break;
                    }
                }

                var ingredientsToAddFactory = Ingredient.ListFactory.newIngredients(ingredientsToAdd, true, "ingredients.ingredientsToAdd");
                var ingredientsToUpdateFactory = Ingredient.ListFactory.fromPersistence(ingredientsToUpdate, true, "ingredients.ingredientsToUpdate");
                var ingredientsToDeleteFactory = new IngredientId.ListFactory(ingredientsToDelete, "ingredients.ingredientsToDelete");
                return Factory.of(
                    ingredientsToAddFactory,
                    ingredientsToUpdateFactory,
                    ingredientsToDeleteFactory,
                    List.of(
                        validatingFactory(of(validator(!deletionsOverlapUpdates, "ingredients.ingredientsToDelete", "Ingredients to update and delete must not overlap.")))
                    )
                );
            }
        }
        
        static class Factory extends FactoriesComposite<Ingredients> {
            private Factory(Assembler<Ingredients> assembler, List<pl.dkaluza.domaincore.Factory<?>> factories) {
                super(assembler, factories);
            }

            static Factory of() {
                return new Factory(() -> null, List.of());
            }

            static Factory of(
                Ingredient.ListFactory ingredientsToAddFactory,
                Ingredient.ListFactory ingredientsToUpdateFactory,
                IngredientId.ListFactory ingredientsToDeleteFactory,
                List<? extends pl.dkaluza.domaincore.Factory<?>> validatingFactories
            ) {
                List<pl.dkaluza.domaincore.Factory<?>> allFactories = new ArrayList<>(validatingFactories);
                allFactories.add(ingredientsToAddFactory);
                allFactories.add(ingredientsToUpdateFactory);
                allFactories.add(ingredientsToDeleteFactory);

                return new Factory(
                    () -> new Ingredients(
                        ingredientsToAddFactory.assemble(),
                        ingredientsToUpdateFactory.assemble(),
                        ingredientsToDeleteFactory.assemble()
                    ),
                    allFactories
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
                var deletionOverlapUpdates = false;
                for (var step : stepsToUpdate) {
                    if (stepsToDelete.contains(step.id())) {
                        deletionOverlapUpdates = true;
                        break;
                    }
                }

                var stepsToAddFactory = Step.ListFactory.newSteps(stepsToAdd, true, "steps.stepsToAdd");
                var stepsToUpdateFactory = Step.ListFactory.fromPersistence(stepsToUpdate, true, "steps.stepsToUpdate");
                var stepsToDeleteFactory = new StepId.ListFactory(stepsToDelete, "steps.stepsToDelete");
                return Factory.of(
                    stepsToAddFactory,
                    stepsToUpdateFactory,
                    stepsToDeleteFactory,
                    List.of(
                        validatingFactory(of(validator(!deletionOverlapUpdates, "steps.stepsToDelete", "Steps to update and delete must not overlap.")))
                    )
                );
            }
        }

        static class Factory extends FactoriesComposite<Steps> {
            private Factory(Assembler<Steps> assembler, List<pl.dkaluza.domaincore.Factory<?>> factories) {
                super(assembler, factories);
            }

            static Factory of() {
                return new Factory(() -> null, List.of());
            }

            static Factory of(
                Step.ListFactory stepsToAddFactory,
                Step.ListFactory stepsToUpdateFactory,
                StepId.ListFactory stepsToDeleteFactory,
                List<? extends pl.dkaluza.domaincore.Factory<?>> validatingFactories
            ) {
                List<pl.dkaluza.domaincore.Factory<?>> allFactories = new ArrayList<>(validatingFactories);
                allFactories.add(stepsToAddFactory);
                allFactories.add(stepsToUpdateFactory);
                allFactories.add(stepsToDeleteFactory);

                return new Factory(
                    () -> new Steps(
                        stepsToAddFactory.assemble(),
                        stepsToUpdateFactory.assemble(),
                        stepsToDeleteFactory.assemble()
                    ),
                    allFactories
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
            var ingredientsFactory = ingredientsBuilder == null ? Ingredients.Factory.of() : ingredientsBuilder.build();
            var stepsFactory = stepsBuilder == null ? Steps.Factory.of() : stepsBuilder.build();

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
