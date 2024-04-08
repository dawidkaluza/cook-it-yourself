package pl.dkaluza.userservice.adapters.out.eventpublisher;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class EventPublisherService {
    private final MessageEntityRepository messageRepository;
    private final RabbitTemplate rabbitTemplate;

    public EventPublisherService(MessageEntityRepository messageRepository, RabbitTemplate rabbitTemplate) {
        this.messageRepository = messageRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void sendById(Long id) {
        // Could have been removed by scheduler, so message not being there is not a bug
        messageRepository
            .findById(id)
            .ifPresent(this::sendAndRemove);
    }

    @Transactional
    public void sendRemainingMessages(int amount) {
        var messages = messageRepository.findAll(
            PageRequest.of(0, amount, Sort.by(Sort.Direction.ASC, "id"))
        );

        for (var message : messages) {
            sendAndRemove(message);
        }
    }

    private void sendAndRemove(MessageEntity message) {
        rabbitTemplate.send(
            message.exchange(),
            message.routingKey(),
            new Message(message.message().getBytes())
        );
        messageRepository.delete(message);
    }
}
