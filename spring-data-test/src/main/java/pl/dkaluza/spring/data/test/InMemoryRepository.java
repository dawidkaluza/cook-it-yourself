package pl.dkaluza.spring.data.test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public abstract class InMemoryRepository<T, ID> implements ListCrudRepository<T, ID>, ListPagingAndSortingRepository<T, ID> {
    private final Set<T> entities;
    private final IdGenerator<ID> idGenerator;

    public InMemoryRepository(Class<T> entityClass, IdGenerator<ID> idGenerator) {
        this.entities = DataStore.getInstance().initAndGet(entityClass);
        this.idGenerator = idGenerator;
    }

    private static Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getField(fieldName);
            return performAction(object, field, () -> field.get(object));
        } catch (Exception e) {
            throw new InMemoryRepositoryException("Reflection API thrown exception", e);
        }
    }

    private static Object performAction(Object object, Field field, Callable<?> action) throws Exception {
        final boolean fieldAccessibility = field.canAccess(object);
        field.setAccessible(true);
        Object fieldValue = action.call();
        field.setAccessible(fieldAccessibility);
        return fieldValue;
    }

    protected abstract T newEntity(T base, ID newId);

    protected abstract ID getEntityId(T entity);

    @Override
    public <S extends T> S save(S entity) {
        var id = getEntityId(entity);
        if(id != null) {
            var persistedEntity = findById(id).orElse(null);
            if (persistedEntity != null) {
                if (persistedEntity == entity) {
                    return entity;
                }

                entities.remove(persistedEntity);
                entities.add(entity);
                return entity;
            } else {
                entities.add(entity);
                return entity;
            }
        }

        var newId = idGenerator.generate();
        var newEntity = newEntity(entity, newId);
        entities.add(newEntity);
        //noinspection unchecked
        return (S) newEntity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> savedEntities = new ArrayList<>();

        for (S entity : entities) {
            var savedEntity = save(entity);
            savedEntities.add(savedEntity);
        }

        return savedEntities;
    }

    @Override
    public Optional<T> findById(ID id) {
        return entities.stream()
            .filter(entity -> getEntityId(entity).equals(id))
            .findAny();
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities);
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        var idsSet = new HashSet<ID>();
        ids.forEach(idsSet::add);

        return entities.stream()
            .filter(entity -> idsSet.contains(getEntityId(entity)))
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return entities.size();
    }

    @Override
    public void deleteById(ID id) {
        entities.removeIf(entity -> getEntityId(entity).equals(id));
    }

    @Override
    public void delete(T entity) {
        entities.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        var idsSet = new HashSet<ID>();
        ids.forEach(idsSet::add);

        entities.removeIf(entity -> idsSet.contains(getEntityId(entity)));
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this.entities::remove);
    }

    @Override
    public void deleteAll() {
        entities.clear();
    }

    @Override
    public List<T> findAll(Sort sort) {
        var entities = new ArrayList<>(this.entities);
        for (Sort.Order order : sort) {
            entities.sort((first, second) -> {
                var prop = order.getProperty();
                Object firstValueAsObject = getFieldValue(first, prop);
                Object secondValueAsObject = getFieldValue(second, prop);

                if (firstValueAsObject instanceof Comparable<?>) {
                    //noinspection unchecked
                    var firstValue = (Comparable<Object>) firstValueAsObject;
                    //noinspection unchecked
                    var secondValue = (Comparable<Object>) secondValueAsObject;

                    return firstValue.compareTo(secondValue);
                }

                return 0;
            });
        }

        return entities;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        var sortedEntities = findAll(pageable.getSort());
        var pagedEntities = new ArrayList<T>();
        int start = (int) pageable.getOffset();
        int end = start + pageable.getPageSize();
        for (int i = start; i < end; i++) {
            pagedEntities.add(sortedEntities.get(i));
        }

        return new PageImpl<>(pagedEntities, pageable, count());
    }
}
