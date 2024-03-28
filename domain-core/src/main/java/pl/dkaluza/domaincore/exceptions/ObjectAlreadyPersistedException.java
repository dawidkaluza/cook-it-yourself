package pl.dkaluza.domaincore.exceptions;

import pl.dkaluza.domaincore.Persistable;

public class ObjectAlreadyPersistedException extends DomainException {
    public ObjectAlreadyPersistedException(Persistable<?> persistable) {
        super(persistable.getClass().getSimpleName() + " with id=" + persistable.getId() + " has already been persisted");
    }

    public static void throwIfPersisted(Persistable<?> persistable) throws ObjectAlreadyPersistedException {
        if (persistable.isPersisted()) {
            throw new ObjectAlreadyPersistedException(persistable);
        }
    }
}
