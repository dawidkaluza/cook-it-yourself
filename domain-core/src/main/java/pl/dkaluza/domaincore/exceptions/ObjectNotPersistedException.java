package pl.dkaluza.domaincore.exceptions;

import pl.dkaluza.domaincore.Persistable;

public class ObjectNotPersistedException extends DomainException {
    public ObjectNotPersistedException(Persistable<?> persistable) {
        super(persistable.getClass().getSimpleName() + " has not been persisted");
    }

    public static void throwIfNotPersisted(Persistable<?> persistable) throws ObjectNotPersistedException {
        if (!persistable.isPersisted()) {
            throw new ObjectNotPersistedException(persistable);
        }
    }
}
