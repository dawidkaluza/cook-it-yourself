package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static pl.dkaluza.domaincore.Validator.validator;
import static pl.dkaluza.kitchenservice.domain.Amount.AmountFactory;
import static pl.dkaluza.kitchenservice.domain.IngredientId.IngredientIdFactory;

public class Ingredient extends AbstractPersistable<IngredientId> {
    private final String name;
    private final Amount amount;

    private Ingredient(IngredientId id, String name, Amount amount) {
        super(id);
        this.name = name;
        this.amount = amount;
    }

    public static Factory<Ingredient> newIngredient(String name, BigDecimal value, String measure) {
        return IngredientFactory.newIngredient(name, value, measure);
    }

    public static Factory<Ingredient> fromPersistence(Long id, String name, BigDecimal value, String measure) {
        return IngredientFactory.fromPersistence(id, name, value, measure);
    }

    public String getName() {
        return name;
    }

    public Amount getAmount() {
        return amount;
    }

    record FactoryDto(Long id, String name, BigDecimal value, String measure) {}

    static class IngredientFactory extends FactoriesComposite<Ingredient> {
        private IngredientFactory(Assembler<Ingredient> assembler, Factory<?>... factories) {
            super(assembler, factories);
        }

        private static boolean isNameValid(String name) {
            if (name == null) {
                return false;
            }

            int length = name.trim().length();
            return !(length < 3 || length > 256);
        }

        private static Assembler<String> nameAssembler(String name) {
            return name == null ? () -> null : name::trim;
        }

        private static Factory<String> nameFactory(String name, String prefix) {
            return DefaultFactory.newWithAssembler(
                ValidationExecutor.of(validator(isNameValid(name), prefix + "name", "Name must have from 3 to 256 chars")),
                nameAssembler(name)
            );
        }

        private static AmountFactory amountFactory(BigDecimal value, String measure, String prefix) {
            return new AmountFactory(
                value, measure,
                prefix,
                Validator.validator(value != null && value.signum() > 0, prefix + "value", "Value must be a positive number")
            );
        }

        static IngredientFactory newIngredient(String name, BigDecimal value, String measure) {
            return newIngredient(name, value, measure, "");
        }

        static IngredientFactory newIngredient(String name, BigDecimal value, String measure, String prefix) {
            var nameFactory = nameFactory(name, prefix);
            var amountFactory = amountFactory(value, measure, prefix);

            return new IngredientFactory(
                () -> new Ingredient(null, nameAssembler(name).assemble(), amountFactory.assemble()),
                nameFactory, amountFactory
            );
        }

        static IngredientFactory fromPersistence(Long id, String name, BigDecimal value, String measure) {
            return fromPersistence(id, name, value, measure, "");
        }

        static IngredientFactory fromPersistence(Long id, String name, BigDecimal value, String measure, String prefix) {
            var idFactory = new IngredientIdFactory(id, prefix);
            var nameFactory = nameFactory(name, prefix);
            var amountFactory = amountFactory(value, measure, prefix);

            return new IngredientFactory(
                () -> new Ingredient(idFactory.assemble(), nameAssembler(name).assemble(), amountFactory.assemble()),
                idFactory, nameFactory, amountFactory
            );
        }

        @Override
        protected Ingredient assemble() {
            return super.assemble();
        }
    }
    
    static class IngredientsFactory extends FactoriesComposite<List<Ingredient>> {
        private IngredientsFactory(Assembler<List<Ingredient>> assembler, List<Factory<?>> allFactories) {
            super(assembler, allFactories);
        }

        private static List<Ingredient> assemble(List<IngredientFactory> ingredients) {
            return ingredients.stream()
                .map(IngredientFactory::assemble)
                .toList();
        }

        private static IngredientsFactory of(List<FactoryDto> ingredients, Function<FactoryDto, IngredientFactory> mapper, boolean allowEmpty, String fieldName) {
            var ingredientsFactories = ingredients.stream()
                .map(mapper)
                .toList();

            var allFactories = new ArrayList<Factory<?>>(ingredientsFactories);
            if (!allowEmpty) {
                var listFactory = DefaultFactory.newWithObject(
                    ValidationExecutor.of(validator(!ingredients.isEmpty(), fieldName, "Ingredients must not be empty.")),
                    ingredients
                );
                allFactories.add(listFactory);
            }

            return new IngredientsFactory(() -> assemble(ingredientsFactories), allFactories);
        }

        static IngredientsFactory newIngredients(List<FactoryDto> ingredients, boolean allowEmpty, String fieldName) {
            return of(
                ingredients,
                ingredient -> IngredientFactory.newIngredient(ingredient.name(), ingredient.value(), ingredient.measure(), fieldName + "."),
                allowEmpty,
                fieldName
            );
        }

        static IngredientsFactory fromPersistence(List<FactoryDto> ingredients, boolean allowEmpty, String fieldName) {
            return of(
                ingredients,
                ingredient -> IngredientFactory.fromPersistence(ingredient.id(), ingredient.name(), ingredient.value(), ingredient.measure(), fieldName + "."),
                allowEmpty,
                fieldName
            );
        }

        @Override
        protected List<Ingredient> assemble() {
            return super.assemble();
        }
    } 
}
