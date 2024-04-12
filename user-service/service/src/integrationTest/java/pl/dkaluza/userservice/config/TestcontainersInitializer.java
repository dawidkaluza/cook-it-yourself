package pl.dkaluza.userservice.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final ComposeContainer compose;

    static {
        var resource = TestcontainersInitializer.class.getResource("/compose.common.yml");
        if (resource == null) {
            throw new IllegalStateException("compose.common.yml file not found. Make sure it's in the resources.");
        }

        compose = new ComposeContainer(new File(resource.getFile()))
            .withExposedService("postgres", 5432, Wait.forHealthcheck())
            .withExposedService("rabbitmq", 5672, Wait.forHealthcheck())
            .withExposedService("redis", 6379, Wait.forHealthcheck())
            .withLocalCompose(true);
        compose.start();
    }

    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var postgresHost = compose.getServiceHost("postgres", 5432);
        var postgresPort = compose.getServicePort("postgres", 5432);
        var rabbitHost = compose.getServiceHost("rabbitmq", 5672);
        var rabbitPort = compose.getServicePort("rabbitmq", 5672);
        var redisHost = compose.getServiceHost("redis", 6379);
        var redisPort = compose.getServicePort("redis", 6379);
        TestPropertyValues.of(
            "spring.datasource.url=jdbc:postgresql://" + postgresHost + ":" + postgresPort + "/user",
            "spring.datasource.username=developer",
            "spring.datasource.password=developer",
            "spring.rabbitmq.host=" + rabbitHost,
            "spring.rabbitmq.port=" + rabbitPort,
            "spring.rabbitmq.username=developer",
            "spring.rabbitmq.password=developer",
            "spring.data.redis.host=" + redisHost,
            "spring.data.redis.port=" + redisPort,
            "spring.data.redis.password=developer"
        ).applyTo(ctx.getEnvironment());
    }
}
