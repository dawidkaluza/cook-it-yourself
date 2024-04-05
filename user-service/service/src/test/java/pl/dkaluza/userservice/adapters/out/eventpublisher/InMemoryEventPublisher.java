package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.LinkedList;
import java.util.Queue;

class InMemoryEventPublisher implements ApplicationEventPublisher {
    private final Queue<Object> events;

    public InMemoryEventPublisher() {
        this.events = new LinkedList<>();
    }

    @Override
    public void publishEvent(Object event) {
        events.add(event);
    }

    <T> T dequeue() {
        //noinspection unchecked
        return (T) events.poll();
    }
}
