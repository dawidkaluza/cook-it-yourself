package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.stereotype.Component;
import pl.dkaluza.userservice.domain.events.SignUpEvent;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;

@Component
class UserEventPublisherAdapter implements UserEventPublisher {
    @Override
    public void publish(SignUpEvent event) {
        throw new UnsupportedOperationException();
    }
}
