package pl.dkaluza.kitchenservice.config;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

public class JdbiFacade {
    private static final Jdbi jdbi;

    static {
        var compose = TestcontainersInitializer.getComposeContainer();
        var postgresHost = compose.getServiceHost("postgres", 5432);
        var postgresPort = compose.getServicePort("postgres", 5432);
        jdbi = Jdbi.create("jdbc:postgresql://" + postgresHost + ":" + postgresPort + "/kitchen", "developer", "developer");
    }

    private Handle handle;

    public void start() {
        handle = jdbi.open();
    }

    public void stop() {
        handle.close();
    }

    public Handle getHandle() {
        return handle;
    }
}
