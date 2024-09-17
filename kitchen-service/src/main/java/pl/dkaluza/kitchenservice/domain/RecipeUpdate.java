package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.FactoriesComposite;
import pl.dkaluza.domaincore.FactoriesList;
import pl.dkaluza.domaincore.Factory;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class RecipeUpdate {
    private final RecipeId recipeId;
    private final BasicInfo basicInfo;
    private final Ingredients ingredients;
    private final Steps steps;

    private RecipeUpdate(RecipeId recipeId, BasicInfo basicInfo, Ingredients ingredients, Steps steps) {
        this.recipeId = recipeId;
        this.basicInfo = basicInfo;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public static Builder builder() {
        return new Builder();
    }

    RecipeId getRecipeId() {
        return recipeId;
    }

    Optional<BasicInfo> getBasicInfo() {
        return Optional.ofNullable(basicInfo);
    }

    Optional<Ingredients> getIngredients() {
        return Optional.ofNullable(ingredients);
    }

    Optional<Steps> getSteps() {
        return Optional.ofNullable(steps);
    }

    public static class BasicInfo {
        private final String name;
        private final String description;
        private final Duration cookingTime;
        private final Amount portionSize;

        private BasicInfo(String name, String description, Duration cookingTime, Amount portionSize) {
            this.name = name;
            this.description = description;
            this.cookingTime = cookingTime;
            this.portionSize = portionSize;
        }

        String getName() {
            return name;
        }

        String getDescription() {
            return description;
        }

        Duration getCookingTime() {
            return cookingTime;
        }

        Amount getPortionSize() {
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

            BasicInfo.Factory build() {
                return new BasicInfo.Factory(
                    new Recipe.NameFactory(name, "basicInfo."),
                    new Recipe.DescriptionFactory(description, "basicInfo."),
                    new Recipe.CookingTimeFactory(cookingTime, "basicInfo."),
                    new Recipe.PortionSizeFactory(portionSizeValue, portionSizeMeasure, "basicInfo.portionSize.")
                );
            }
        }

        static class Factory extends FactoriesComposite<BasicInfo> {
            Factory() {
                super(() -> null);
            }

            Factory(
                Recipe.NameFactory nameFactory, Recipe.DescriptionFactory descriptionFactory,
                Recipe.CookingTimeFactory cookingTimeFactory, Recipe.PortionSizeFactory portionSizeFactory
            ) {
                super(
                    () -> new BasicInfo(
                        nameFactory.assemble(),
                        descriptionFactory.assemble(),
                        cookingTimeFactory.assemble(),
                        portionSizeFactory.assemble()
                    ),
                    nameFactory, descriptionFactory, cookingTimeFactory, portionSizeFactory
                );
            }

            @Override
            protected BasicInfo assemble() {
                return super.assemble();
            }
        }
    }

    static class Ingredients {
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
            private final List<Ingredient.FactoryDto> ingredientsToAdd;
            private final List<Ingredient.FactoryDto> ingredientsToUpdate;
            private final List<Long> ingredientsToDelete;

            Builder() {
                this.ingredientsToAdd = new ArrayList<>();
                this.ingredientsToUpdate = new ArrayList<>();
                this.ingredientsToDelete = new ArrayList<>();
            }

            public Builder ingredientToAdd(String name, BigDecimal value, String measure) {
                ingredientsToAdd.add(new Ingredient.FactoryDto(null, name, value, measure));
                return this;
            }

            public Builder ingredientToUpdate(Long id, String name, BigDecimal value, String measure) {
                ingredientsToUpdate.add(new Ingredient.FactoryDto(id, name, value, measure));
                return this;
            }

            public Builder ingredientToDelete(Long id) {
                ingredientsToDelete.add(id);
                return this;
            }

            Factory build() {
                return new Factory(
                    Ingredient.IngredientsFactory.newIngredients(ingredientsToAdd, true, "ingredients.ingredientsToAdd"),
                    Ingredient.IngredientsFactory.fromPersistence(ingredientsToUpdate, true, "ingredients.ingredientsToUpdate"),
                    new IngredientIdsFactory(ingredientsToDelete, "ingredients.ingredientsToDelete")
                );
            }
        }
        
        static class Factory extends FactoriesComposite<Ingredients> {
            Factory() {
                super(() -> null);
            }

            Factory(
                Ingredient.IngredientsFactory ingredientsToAddFactory,
                Ingredient.IngredientsFactory ingredientsToUpdateFactory,
                IngredientIdsFactory ingredientsToDeleteFactory
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
            private final List<Step.FactoryDto> stepsToAdd;
            private final List<Step.FactoryDto> stepsToUpdate;
            private final List<Long> stepsToDelete;
            
            Builder() {
                stepsToAdd = new ArrayList<>();
                stepsToUpdate = new ArrayList<>();
                stepsToDelete = new ArrayList<>();
            }

            public Builder stepToAdd(String text) {
                stepsToAdd.add(new Step.FactoryDto(null, text));
                return this;
            }
    
            public Builder stepToUpdate(Long id, String text) {
                stepsToUpdate.add(new Step.FactoryDto(id, text));
                return this;
            }
    
            public Builder stepToDelete(Long id) {
                stepsToDelete.add(id);
                return this;
            }
            
            Factory build() {
                return new Factory(
                    Step.StepsFactory.newSteps(stepsToAdd, true, "steps.stepsToAdd"),
                    Step.StepsFactory.fromPersistence(stepsToUpdate, true, "steps.stepsToUpdate"),
                    new StepIdsFactory(stepsToDelete, "steps.stepsToDelete")
                );
            }
        }

        static class Factory extends FactoriesComposite<Steps> {
            private Factory() {
                super(() -> null);
            }

            private Factory(
                Step.StepsFactory stepsToAddFactory,
                Step.StepsFactory stepsToUpdateFactory,
                StepIdsFactory stepsToDeleteFactory
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
        private Long id;
        private BasicInfo.Builder basicInfoBuilder;
        private Ingredients.Builder ingredientsBuilder;
        private Steps.Builder stepsBuilder;

        private Builder() { }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder basicInfo(Consumer<BasicInfo.Builder> consumer) {
            var builder = new BasicInfo.Builder();
            consumer.accept(builder);
            basicInfoBuilder = builder;
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
            var recipeIdFactory = new RecipeId.RecipeIdFactory(id);
            var basicInfoFactory = basicInfoBuilder == null ? new BasicInfo.Factory() : basicInfoBuilder.build();
            var ingredientsFactory = ingredientsBuilder == null ? new Ingredients.Factory() : ingredientsBuilder.build();
            var stepsFactory = stepsBuilder == null ? new Steps.Factory() : stepsBuilder.build();

            return new FactoriesComposite<>(
                () -> new RecipeUpdate(
                    recipeIdFactory.assemble(),
                    basicInfoFactory.assemble(),
                    ingredientsFactory.assemble(),
                    stepsFactory.assemble()
                ),
                List.of(recipeIdFactory, basicInfoFactory, ingredientsFactory, stepsFactory)
            );
        }
    }

    private static class IngredientIdsFactory extends FactoriesList<IngredientId> {
        private IngredientIdsFactory(List<Long> ids, String fieldName) {
            super(ids.stream().map(id -> new IngredientId.IngredientIdFactory(id, fieldName + ".")).toList());
        }

        @Override
        protected List<IngredientId> assemble() {
            return super.assemble();
        }
    }

    private static class StepIdsFactory extends FactoriesList<StepId> {
        private StepIdsFactory(List<Long> ids, String fieldName) {
            super(ids.stream().map(id -> new StepId.StepIdFactory(id, fieldName + ".")).toList());
        }

        @Override
        protected List<StepId> assemble() {
            return super.assemble();
        }
    }
}
