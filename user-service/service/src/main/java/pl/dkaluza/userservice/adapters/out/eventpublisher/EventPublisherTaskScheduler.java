package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
class EventPublisherTaskScheduler extends ThreadPoolTaskScheduler {
}
