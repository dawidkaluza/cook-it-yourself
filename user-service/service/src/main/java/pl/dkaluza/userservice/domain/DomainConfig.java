package pl.dkaluza.userservice.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.dkaluza.userservice.ports.out.UserEventPublisher;
import pl.dkaluza.userservice.ports.out.UserRepository;

@Configuration
class DomainConfig {
    @Bean
    DefaultUserService defaultUserService(UserRepository userRepository, UserEventPublisher userEventPublisher) {
        return new DefaultUserService(userRepository, userEventPublisher);
    }
}
