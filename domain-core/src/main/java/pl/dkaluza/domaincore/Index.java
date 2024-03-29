package pl.dkaluza.domaincore;

public abstract class Index<T> {
    private final T id;

    protected Index(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Index<?> index = (Index<?>) o;
        return id.equals(index.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
