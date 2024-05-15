package pl.dkaluza.kitchenservice.config;


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
            .withExposedService("postgres", 5432, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(90)))
            .withExposedService("rabbitmq", 5672, Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(90)))
            .withLocalCompose(true);
        compose.start();
    }

    static ComposeContainer getComposeContainer() {
        return compose;
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var postgresHost = compose.getServiceHost("postgres", 5432);
        var postgresPort = compose.getServicePort("postgres", 5432);
        var rabbitHost = compose.getServiceHost("rabbitmq", 5672);
        var rabbitPort = compose.getServicePort("rabbitmq", 5672);
        TestPropertyValues.of(
            "spring.datasource.url=jdbc:postgresql://" + postgresHost + ":" + postgresPort + "/kitchen",
            "spring.datasource.username=developer",
            "spring.datasource.password=developer",
            "spring.rabbitmq.host=" + rabbitHost,
            "spring.rabbitmq.port=" + rabbitPort,
            "spring.rabbitmq.username=developer",
            "spring.rabbitmq.password=developer",
            "ciy.web.user-service-url=http://localhost:8081",
            "ciy.oauth2.mock-auth-server-enabled=true",
            "ciy.oauth2.mock-auth-server-port=8081"
        ).applyTo(ctx.getEnvironment());
    }
}