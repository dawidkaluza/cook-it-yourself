package pl.dkaluza.kitchenservice;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.math.BigDecimal;

class NumericMatcher extends TypeSafeMatcher<String> {
    private final String expectedValue;

    NumericMatcher(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    protected boolean matchesSafely(String item) {
        var itemAsBigDecimal = new BigDecimal(item);
        return new BigDecimal(expectedValue).compareTo(itemAsBigDecimal) == 0;
    }

    @Override
    public void describeTo(Description description) {

    }
}
