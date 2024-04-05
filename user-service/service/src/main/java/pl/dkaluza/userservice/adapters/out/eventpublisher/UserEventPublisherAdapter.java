package pl.dkaluza.userservice.adapters.out.eventpublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.userservice.domain.events.SignUpEvent;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;

@Component
class UserEventPublisherAdapter implements UserEventPublisher {
    private final MessageEntityRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public UserEventPublisherAdapter(MessageEntityRepository messageRepository, ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(SignUpEvent event) {
        Assertions.assertArgument(event != null, "Event is null");

        var userId = event.id();
        var message = new SignUpMessage(userId.getId());
        MessageEntity messageEntity;
        try {
            messageEntity = new MessageEntity(
                null,
                "userService",
                "user.signUp",
                objectMapper.writeValueAsString(message)
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }

        messageEntity = messageRepository.save(messageEntity);
        eventPublisher.publishEvent(new OnMessagePublishedEvent(messageEntity.id()));
    }
}
