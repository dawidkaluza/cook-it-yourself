package pl.dkaluza.userservice.adapters.out.eventpublisher;

import pl.dkaluza.spring.data.test.InMemoryRepository;
import pl.dkaluza.spring.data.test.LongIdGenerator;

class InMemoryMessageRepository extends InMemoryRepository<MessageEntity, Long> implements MessageEntityRepository {
    public InMemoryMessageRepository() {
        super(MessageEntity.class, new LongIdGenerator());
    }

    @Override
    protected MessageEntity newEntity(MessageEntity base, Long newId) {
        return new MessageEntity(newId, base.exchange(), base.routingKey(), base.message());
    }

    @Override
    protected Long getEntityId(MessageEntity message) {
        return message.id();
    }
}
