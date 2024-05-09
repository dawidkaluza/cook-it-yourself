package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.ValidationExecutor;
import pl.dkaluza.domaincore.Validator;

import java.math.BigDecimal;

import static pl.dkaluza.domaincore.Validator.validator;

public class Amount {
    private static final Amount ZERO = new Amount(BigDecimal.ZERO, "");

    private final BigDecimal value;
    private final String measure;

    private Amount(BigDecimal value, String measure) {
        this.value = value;
        this.measure = measure;
    }

    public static Amount zero() {
        return ZERO;
    }

    public static Factory<Amount> of(BigDecimal value, String measure) {
        return new AmountFactory(value, measure);
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getMeasure() {
        return measure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Amount amount)) {
            return false;
        }

        return value.compareTo(amount.value) == 0 && measure.equals(amount.measure);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    static class AmountFactory extends DefaultFactory<Amount> {
        AmountFactory(BigDecimal value, String measure) {
            this(value, measure, "", new Validator[0]);
        }

        AmountFactory(BigDecimal value, String measure, String prefix) {
            this(value, measure, prefix, new Validator[0]);
        }

        AmountFactory(BigDecimal value, String measure, Validator... validators) {
            this(value, measure, "", validators);
        }

        AmountFactory(BigDecimal value, String measure, String prefix, Validator... validators) {
            super(
                getValidationExecutor(value, measure, prefix, validators),
                () -> {
                    if (value.signum() == 0) {
                        return ZERO;
                    }

                    return new Amount(value, measure);
                }
            );
        }

        private static ValidationExecutor getValidationExecutor(BigDecimal value, String measure, String prefix, Validator... validators) {
            var builder = ValidationExecutor.builder()
                .withValidator(validator(!(value == null || value.signum() < 0), prefix + "value", "Value must not be a negative number"))
                .withValidator(validator(!(measure == null || measure.trim().length() > 32), prefix + "measure", "Measure must have from 0 to 32 chars"));

            for (Validator validator : validators) {
                builder.withValidator(validator);
            }

            return builder.build();
        }

        @Override
        protected Amount assemble() {
            return super.assemble();
        }
    }
}
