package pl.dkaluza.userservice.adapters.out.eventpublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import pl.dkaluza.userservice.domain.events.SignUpEvent;

@Component
class SignUpEventMapper {
    private final ObjectMapper objectMapper;
    private final UserServiceExchange userServiceExchange;

    public SignUpEventMapper(ObjectMapper objectMapper, UserServiceExchange userServiceExchange) {
        this.objectMapper = objectMapper;
        this.userServiceExchange = userServiceExchange;
    }

    MessageEntity toEntity(SignUpEvent event) {
        try {
            var userId = event.id();
            var signUpMessage = new SignUpMessage(userId.getId());
            var signUpMessageAsString = objectMapper.writeValueAsString(signUpMessage);

            return new MessageEntity(
                null, userServiceExchange.getName(), "user.signUp", signUpMessageAsString
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Object mapper thrown exception", e);
        }

    }
}
