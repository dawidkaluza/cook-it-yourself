package pl.dkaluza.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.dkaluza.apigateway.config.EnableTestcontainers;

@EnableTestcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayTest {
    @Test
    void contextLoads() {
    }
}
