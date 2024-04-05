package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
class EventPublisherTaskSchedulerSetup {
    private final EventPublisherTaskScheduler scheduler;
    private final EventPublisherRemainingMessagesSettings settings;
    private final EventPublisherService service;

    public EventPublisherTaskSchedulerSetup(EventPublisherTaskScheduler scheduler, EventPublisherRemainingMessagesSettings settings, EventPublisherService service) {
        this.scheduler = scheduler;
        this.settings = settings;
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onAppReady() {
        scheduler.scheduleAtFixedRate(() -> service.sendRemainingMessages(settings.getAmount()), settings.getRate());
    }
}
