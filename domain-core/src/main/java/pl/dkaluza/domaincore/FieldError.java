package pl.dkaluza.domaincore;

public class FieldError {
    private final String name;
    private final String message;

    FieldError(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
