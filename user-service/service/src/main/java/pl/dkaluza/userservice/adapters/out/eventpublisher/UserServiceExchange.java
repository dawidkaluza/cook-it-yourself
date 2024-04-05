package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.amqp.core.TopicExchange;

class UserServiceExchange extends TopicExchange {
    public UserServiceExchange() {
        super("userExchange");
    }
}
