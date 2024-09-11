package pl.dkaluza.apigateway.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;

class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final ComposeContainer compose;

    static {
        var resource = TestcontainersInitializer.class.getResource("/compose.common.yml");
        if (resource == null) {
            throw new IllegalStateException("compose.common.yml file not found. Make sure it's in the resources.");
        }

        compose = new ComposeContainer(new File(resource.getFile()))
            .withExposedService("redis", 6379, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(90)))
            .withLocalCompose(true);
        compose.start();
    }

    static ComposeContainer getComposeContainer() {
        return compose;
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var redisHost = compose.getServiceHost("redis", 6379);
        var redisPort = compose.getServicePort("redis", 6379);
        TestPropertyValues.of(
            "spring.data.redis.host=" + redisHost,
            "spring.data.redis.port=" + redisPort,
            "spring.data.redis.password=developer"
        ).applyTo(ctx.getEnvironment());
    }
}
