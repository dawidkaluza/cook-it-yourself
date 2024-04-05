package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
class EventPublisherRemainingMessagesSettings {
    /**
     * Rate of sending remaining messages cycle.
     */
    private final Duration rate;

    /**
     * Maximum amount of messages being sent on one cycle.
     */
    private final int amount;

    public EventPublisherRemainingMessagesSettings(
        @Value("${ciy.event-publisher.rem-msgs.rate:2s}") Duration rate,
        @Value("${ciy.event-publisher.rem-msgs.amount:100}") int amount
    ) {
        this.rate = rate;
        this.amount = amount;
    }

    public Duration getRate() {
        return rate;
    }

    public int getAmount() {
        return amount;
    }
}
