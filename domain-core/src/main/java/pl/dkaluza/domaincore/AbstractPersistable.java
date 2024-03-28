package pl.dkaluza.domaincore;

public abstract class AbstractPersistable<T extends Index<?>> implements Persistable<T> {
    private final T id;

    public AbstractPersistable(T id) {
        this.id = id;
    }

    @Override
    public T getId() {
        return id;
    }

    /**
     * Determines equality between two objects.
     * <p></p>
     * To be equal, the objects must:
     * <ul>
     *     <li>not be null</li>
     *     <li>be instance of the same class</li>
     *     <li>have non-nullable equal ids</li>
     * </ul>
     *
     * @param object object to compare with
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Persistable<?> target = (Persistable<?>) object;

        T thisId = this.getId();
        Object targetId = target.getId();
        if (thisId == null || targetId == null) {
            return false;
        }

        return thisId.equals(targetId);
    }

    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }
}
