package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.*;

import java.math.BigDecimal;

import static pl.dkaluza.domaincore.Validator.*;
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

    public String getName() {
        return name;
    }

    public Amount getAmount() {
        return amount;
    }

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

        static IngredientFactory newIngredient(Long id, String name, BigDecimal value, String measure) {
            var idFactory = new IngredientIdFactory(id);

            var nameFactory = DefaultFactory.newWithObject(
                ValidationExecutor.of(validator(isNameValid(name), "name", "Name must have from 3 to 256 chars")),
                name
            );
            var amountFactory = new AmountFactory(value, measure);

            return new IngredientFactory(
                () -> new Ingredient(idFactory.assemble(), name, amountFactory.assemble()),
                idFactory, nameFactory, amountFactory
            );
        }

        @Override
        protected Ingredient assemble() {
            return super.assemble();
        }
    }
}
