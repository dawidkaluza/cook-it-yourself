package pl.dkaluza.userservice.adapters.out.eventpublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.data.domain.Sort;
import pl.dkaluza.userservice.domain.UserId;
import pl.dkaluza.userservice.domain.events.SignUpEvent;

import static org.assertj.core.api.Assertions.*;

class UserEventPublisherAdapterTest {
    private UserEventPublisherAdapter userEventPublisherAdapter;
    private InMemoryMessageRepository messageRepository;
    private InMemoryEventPublisher eventPublisher;

    @BeforeEach
    void beforeEach() {
        messageRepository = new InMemoryMessageRepository();
        eventPublisher = new InMemoryEventPublisher();
        userEventPublisherAdapter = new UserEventPublisherAdapter(
            new SignUpEventMapper(
                new ObjectMapper(),
                new UserServiceExchange()
            ),
            messageRepository,
            eventPublisher
        );
    }

    @ParameterizedTest
    @NullSource
    void publishSignUpMessage_invalidParams_throwException(SignUpEvent event) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> userEventPublisherAdapter.publish(event));
    }

    @Test
    void publishSignUpMessage_validEvent_saveAndPublishMessage() {
        // Given
        var userId = UserId.of(10L).produce();
        var event = new SignUpEvent(userId);

        // When
        userEventPublisherAdapter.publish(event);

        // Then
        var messages = messageRepository.findAll(Sort.by("id"));
        assertThat(messages)
            .isNotNull()
            .hasSize(1);

        var message = messages.get(0);
        assertThat(message)
            .extracting(MessageEntity::exchange, MessageEntity::routingKey)
            .containsExactly("userService", "user.signUp");

        OnMessagePublishedEvent onMsgPublishedEvent = eventPublisher.dequeue();
        assertThat(onMsgPublishedEvent)
            .isNotNull()
            .extracting(OnMessagePublishedEvent::id)
            .isEqualTo(message.id());
    }

}