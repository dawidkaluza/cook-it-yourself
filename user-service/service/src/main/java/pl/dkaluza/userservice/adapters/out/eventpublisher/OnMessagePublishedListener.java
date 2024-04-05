package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
class OnMessagePublishedListener {
    private final EventPublisherService eventPublisherService;

    public OnMessagePublishedListener(EventPublisherService eventPublisherService) {
        this.eventPublisherService = eventPublisherService;
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMessagePublished(OnMessagePublishedEvent event) {
        eventPublisherService.sendById(event.id());
    }
}
