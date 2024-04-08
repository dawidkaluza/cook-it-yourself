package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EventPublisherServiceTest {
    private InMemoryMessageRepository messageRepository;
    private RabbitTemplate rabbitTemplate;
    private EventPublisherService eventPublisherService;

    @BeforeEach
    void beforeEach() {
        messageRepository = new InMemoryMessageRepository();
        messageRepository.deleteAll();
        rabbitTemplate = mock();
        eventPublisherService = new EventPublisherService(messageRepository, rabbitTemplate);
    }

    @ParameterizedTest
    @NullSource
    void sendById_invalidParams_throwException(Long id) {
        assertThatThrownBy(() -> eventPublisherService.sendById(id))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sendById_nonExistingMessage_ignore() {
        // Given
        // When
        eventPublisherService.sendById(1L);

        // Then
        var messages = messageRepository.findAll();
        assertThat(messages)
            .isEmpty();

        verify(rabbitTemplate, times(0)).send(any(), any(), any(Message.class));
    }

    @Test
    void sendById_existingMessage_sendAndRemove() {
        // Given
        var message = new MessageEntity(1L, "userExchange", "user.ping", "PING");
        messageRepository.save(message);

        // When
        eventPublisherService.sendById(1L);

        // Then
        var sentMessage = messageRepository.findById(1L).orElse(null);
        assertThat(sentMessage)
            .isNull();

        verify(rabbitTemplate, times(1)).send(eq(message.exchange()), eq(message.routingKey()), any());
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1 })
    void sendRemainingMessages_invalidParams_throwException(int amount) {
        assertThatThrownBy(() -> eventPublisherService.sendRemainingMessages(amount))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void sendRemainingMessages_messagesAwaiting_sendAndRemove() {
        // Given
        var message1 = new MessageEntity(1L, "userExchange", "user.ping", "PING");
        var message2 = new MessageEntity(2L, "userExchange", "user.ping", "PING");
        var message3 = new MessageEntity(3L, "userExchange", "user.ping", "PING");
        messageRepository.saveAll(List.of(message1, message2, message3));

        // When
        eventPublisherService.sendRemainingMessages(2);

        // Then
        var messages = messageRepository.findAll();
        assertThat(messages)
            .singleElement()
            .extracting(MessageEntity::id, MessageEntity::exchange, MessageEntity::routingKey, MessageEntity::message)
            .containsExactly(message3.id(), message3.exchange(), message3.routingKey(), message3.message());

        verify(rabbitTemplate, times(2)).send(eq("userExchange"), eq("user.ping"), any(Message.class));
    }
}