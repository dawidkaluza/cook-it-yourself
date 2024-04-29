package pl.dkaluza.kitchenservice.domain;

import pl.dkaluza.domaincore.DefaultFactory;
import pl.dkaluza.domaincore.Factory;
import pl.dkaluza.domaincore.ValidationExecutor;

import java.math.BigDecimal;

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
            super(
                ValidationExecutor.builder()
                    .withValidation(!(value == null || value.signum() < 0), "value", "Value must not be a negative number")
                    .withValidation(!(measure == null || measure.trim().length() > 32), "measure", "Measure must have from 0 to 32 chars")
                    .build(),
                () -> {
                    //noinspection DataFlowIssue
                    if (value.signum() == 0) {
                        return ZERO;
                    }

                    return new Amount(value, measure);
                }
            );
        }

        @Override
        protected Amount assemble() {
            return super.assemble();
        }
    }
}
