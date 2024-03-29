package pl.dkaluza.domaincore;

public final class Assertions {
    private Assertions() {
    }

    public static void assertState(boolean condition, String message) throws IllegalStateException {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    public static void assertArgument(boolean condition, String message) throws IllegalArgumentException {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
