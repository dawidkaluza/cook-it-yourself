package pl.dkaluza.kitchenservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import pl.dkaluza.kitchenservice.config.EnableTestcontainers;
import pl.dkaluza.kitchenservice.config.JdbiFacade;
import pl.dkaluza.kitchenservice.config.RabbitFacade;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.awaitility.Awaitility.await;


@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CookAmqpTest {
    private RabbitFacade rabbitFacade;
    private JdbiFacade jdbiFacade;

    @BeforeEach
    void beforeEach() throws Exception {
        rabbitFacade = new RabbitFacade();
        rabbitFacade.start();

        jdbiFacade = new JdbiFacade();
        jdbiFacade.start();
        var handle = jdbiFacade.getHandle();
        handle.execute("DELETE FROM cook");
    }

    @ParameterizedTest
    @ValueSource(longs = { 1L, 2L })
    void onUserSignedUp_variousCooks_consumeSuccessfully(Long userId) throws Exception {
        // Given
        var handle = jdbiFacade.getHandle();
        handle.execute("INSERT INTO cook VALUES (1)");

        var channel = rabbitFacade.getChannel();
        channel.exchangeDeclare("userService", "topic", true);
        var msg = String.format("{\"id\": %d}", userId);

        // When
        channel.basicPublish(
            "userService", "user.signUp", null,
            msg.getBytes(StandardCharsets.UTF_8)
        );

        // Then
        await()
            .atMost(Duration.ofSeconds(15))
            .until(() -> {
                var count = handle.select("SELECT COUNT(id) FROM cook WHERE id = ?", userId)
                    .mapTo(Integer.class)
                    .first();

                return count > 0;
            });
    }
}
