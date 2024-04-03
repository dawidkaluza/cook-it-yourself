package pl.dkaluza.userservice.ports.out;

import pl.dkaluza.userservice.domain.events.SignUpEvent;

public interface UserEventPublisher {
    void publish(SignUpEvent event);
}
