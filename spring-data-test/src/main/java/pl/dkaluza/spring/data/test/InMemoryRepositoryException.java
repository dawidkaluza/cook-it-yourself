package pl.dkaluza.spring.data.test;

public class InMemoryRepositoryException extends RuntimeException {
    public InMemoryRepositoryException(String message) {
        super(message);
    }

    public InMemoryRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
