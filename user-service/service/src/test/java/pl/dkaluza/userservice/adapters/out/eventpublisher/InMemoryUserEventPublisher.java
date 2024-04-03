package pl.dkaluza.userservice.adapters.out.eventpublisher;

import pl.dkaluza.userservice.domain.events.SignUpEvent;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;

import java.util.LinkedList;
import java.util.Queue;

public class InMemoryUserEventPublisher implements UserEventPublisher {
    private final Queue<Object> events;

    public InMemoryUserEventPublisher() {
        this.events = new LinkedList<>();
    }

    @Override
    public void publish(SignUpEvent event) {
        events.add(event);
    }

    public <T> T dequeue() {
        //noinspection unchecked
        return (T) events.poll();
    }
}
