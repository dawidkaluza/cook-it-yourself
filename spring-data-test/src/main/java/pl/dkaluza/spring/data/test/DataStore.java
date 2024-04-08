package pl.dkaluza.spring.data.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class DataStore {
    private static final DataStore instance;
    private final Map<Class<?>, Set<?>> entitiesByClassMap;

    static {
        instance = new DataStore();
    }

    public DataStore() {
        this.entitiesByClassMap = new HashMap<>();
    }

    public static DataStore getInstance() {
        return instance;
    }

    <T> Set<T> initAndGet(Class<T> entityClass) {
        //noinspection unchecked
        return (Set<T>) entitiesByClassMap.computeIfAbsent(entityClass, k -> new HashSet<>());
    }
}
