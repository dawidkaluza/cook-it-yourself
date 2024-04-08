package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import pl.dkaluza.domaincore.Assertions;
import pl.dkaluza.userservice.domain.events.SignUpEvent;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;

@Component
class UserEventPublisherAdapter implements UserEventPublisher {
    private final SignUpEventMapper signUpEventMapper;
    private final MessageEntityRepository messageRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserEventPublisherAdapter(SignUpEventMapper signUpEventMapper, MessageEntityRepository messageRepository, ApplicationEventPublisher eventPublisher) {
        this.signUpEventMapper = signUpEventMapper;
        this.messageRepository = messageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(SignUpEvent event) {
        Assertions.assertArgument(event != null, "Event is null");

        var message = signUpEventMapper.toEntity(event);
        message = messageRepository.save(message);
        eventPublisher.publishEvent(new OnMessagePublishedEvent(message.id()));
    }
}
