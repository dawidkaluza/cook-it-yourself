package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static pl.dkaluza.domaincore.Validator.validator;
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

        private static Amount.AmountFactory amountFactory(BigDecimal value, String measure, String prefix) {
            return new Amount.AmountFactory(
                value, measure,
                prefix,
                Validator.validator(value != null && value.signum() > 0, prefix + "value", "Value must be a positive number")
            );
        }

        static IngredientFactory newIngredient(String name, BigDecimal value, String measure) {
            return newIngredient(name, value, measure, "");
        }

        static IngredientFactory newIngredient(String name, BigDecimal value, String measure, String prefix) {
            var nameFactory = new NameFactory(name, prefix);
            var amountFactory = amountFactory(value, measure, prefix);

            return new IngredientFactory(
                () -> new Ingredient(null, nameFactory.assemble(), amountFactory.assemble()),
                nameFactory, amountFactory
            );
        }

        static IngredientFactory fromPersistence(Long id, String name, BigDecimal value, String measure) {
            return fromPersistence(id, name, value, measure, "");
        }

        static IngredientFactory fromPersistence(Long id, String name, BigDecimal value, String measure, String prefix) {
            var idFactory = new IngredientIdFactory(id, prefix);
            var nameFactory = new NameFactory(name, prefix);
            var amountFactory = amountFactory(value, measure, prefix);

            return new IngredientFactory(
                () -> new Ingredient(idFactory.assemble(), nameFactory.assemble(), amountFactory.assemble()),
                idFactory, nameFactory, amountFactory
            );
        }

        @Override
        protected Ingredient assemble() {
            return super.assemble();
        }
    }

    static class NameFactory extends DefaultFactory<String> {
        NameFactory(String name) {
            this(name, "");
        }

        NameFactory(String name, String prefix) {
            //noinspection Convert2MethodRef
            super(
                () -> name.trim(),
                ValidationExecutor.of(validator(isNameValid(name), prefix + "name", "Name must have from 3 to 256 chars"))
            );
        }

        private static boolean isNameValid(String name) {
            if (name == null) {
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

    static class IngredientsFactory extends FactoriesList<Ingredient> {
        private IngredientsFactory(List<? extends Factory<Ingredient>> factories, List<Validator> validators) {
            super(factories, validators);
        }

        private static IngredientsFactory of(List<FactoryDto> dtos, Function<FactoryDto, IngredientFactory> mapper, boolean allowEmpty, String fieldName) {
            var factories = dtos.stream()
                .map(mapper)
                .toList();

            List<Validator> validators = allowEmpty
                ? List.of()
                : List.of(validator(!dtos.isEmpty(), fieldName, "List must not be empty."));

            return new IngredientsFactory(factories, validators);
        }

        static IngredientsFactory newIngredients(List<FactoryDto> dtos, boolean allowEmpty, String fieldName) {
            return of(
                dtos,
                (dto) -> IngredientFactory.newIngredient(dto.name(), dto.value(), dto.measure(), fieldName + "."),
                allowEmpty,
                fieldName
            );
        }

        static IngredientsFactory fromPersistence(List<FactoryDto> dtos, boolean allowEmpty, String fieldName) {
            return of(
                dtos,
                (dto) -> IngredientFactory.fromPersistence(dto.id(), dto.name(), dto.value(), dto.measure(), fieldName + "."),
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
