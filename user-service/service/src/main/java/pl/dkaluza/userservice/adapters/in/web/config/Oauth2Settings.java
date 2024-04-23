package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ciy.oauth2")
class Oauth2Settings {
    private List<String> clientsOrigins;

    public List<String> getClientsOrigins() {
        return clientsOrigins;
    }

    public void setClientsOrigins(List<String> clientsOrigins) {
        this.clientsOrigins = clientsOrigins;
    }
}
