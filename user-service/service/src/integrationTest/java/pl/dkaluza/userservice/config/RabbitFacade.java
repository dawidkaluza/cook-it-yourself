package pl.dkaluza.userservice.config;

import com.rabbitmq.client.*;
import pl.dkaluza.userservice.config.TestcontainersInitializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class RabbitFacade {
    private static final ConnectionFactory connectionFactory;

    static {
        var factory = new ConnectionFactory();
        var compose = TestcontainersInitializer.getComposeContainer();
        factory.setHost(compose.getServiceHost("rabbitmq", 5672));
        factory.setPort(compose.getServicePort("rabbitmq", 5672));
        factory.setUsername("developer");
        factory.setPassword("developer");
        connectionFactory = factory;
    }

    private Connection connection;
    private Channel channel;

    public void start() throws IOException, TimeoutException {
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
    }

    public void stop() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void subscribe(String exchange, String routingKey, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException {
        channel.exchangeDeclare(exchange, "topic", true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, routingKey);
        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);
    }
}
