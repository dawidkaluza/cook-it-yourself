package pl.dkaluza.domaincore;

public interface Persistable <T extends Index>{
    T getId();

    default boolean isPersisted() {
        return getId() != null;
    }
}
