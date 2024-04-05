package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Callable;

//FIXME it's just for a POC for now, to be extended later
class InMemoryMessageRepository implements MessageEntityRepository {
    private final Set<MessageEntity> entities;
    private long idSeq;

    public InMemoryMessageRepository() {
        entities = new HashSet<>();
        idSeq = 0L;
    }

    @Override
    public List<MessageEntity> findAll(Sort sort) {
        try {
            var entities = new ArrayList<>(this.entities);
            var orders = sort.iterator();
            while (orders.hasNext()) {
                var order = orders.next();
                entities.sort((first, second) -> {
                    var prop = order.getProperty();
                    Object firstValueAsObject;
                    Object secondValueAsObject;
                    try {
                        firstValueAsObject = getFieldValue(first, MessageEntity.class.getField(prop));
                        secondValueAsObject = getFieldValue(first, MessageEntity.class.getField(prop));
                    } catch (Exception e) {
                        throw new RuntimeException("Reflection API thrown exception", e);
                    }

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
        } catch (Exception e) {
            throw new RuntimeException("Reflection API thrown exception", e);
        }
    }

    @Override
    public Page<MessageEntity> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public <S extends MessageEntity> S save(S entity) {
//        TODO check that S is a MessageEnttity (should be ok for unit testing)
        var entityWithId = new MessageEntity(++idSeq, entity.exchange(), entity.routingKey(), entity.message());
        entities.add(entityWithId);
        //noinspection unchecked
        return (S) entityWithId;
    }

    @Override
    public <S extends MessageEntity> Iterable<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<MessageEntity> findById(Long id) {
        return entities.stream()
            .filter((msg) -> msg.id().equals(id))
            .findAny();
    }

    @Override
    public boolean existsById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<MessageEntity> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<MessageEntity> findAllById(Iterable<Long> longs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteById(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(MessageEntity entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll(Iterable<? extends MessageEntity> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }


    private <T, ID> void setEntityId(T entity, ID id) {
        getFieldAnnotatedAsId(entity)
            .ifPresentOrElse(field -> setFieldValue(entity, field, id),
                () -> {
                    throw new RuntimeException("@Id field not found");
                });
    }

    private <T, ID> Optional<ID> getEntityId(T entity) {
        //noinspection unchecked
        return getFieldAnnotatedAsId(entity)
            .map(field -> (ID) getFieldValue(entity, field));
    }

    private Optional<Field> getFieldAnnotatedAsId(Object entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    private static Object getFieldValue(Object object, Field field) {
        try {
            return performActionOnSecuredFieldAndGetResult(object, field, () -> field.get(object));
        } catch (Exception e) {
            throw new RuntimeException("Reflection API thrown exception", e);
        }
    }

    private static void setFieldValue(Object object, Field field, Object value) {
        try {
            performActionOnSecuredFieldAndGetResult(object, field, () -> {
                field.set(object, value);
                return field.get(object);
            });
        } catch (Exception e) {
            throw new RuntimeException("Reflection API thrown exception", e);
        }
    }

    private static Object performActionOnSecuredFieldAndGetResult(Object object, Field field, Callable<?> action) throws Exception {
        final boolean fieldAccessibility = field.canAccess(object);
        field.setAccessible(true);
        Object fieldValue = action.call();
        field.setAccessible(fieldAccessibility);
        return fieldValue;
    }
}
