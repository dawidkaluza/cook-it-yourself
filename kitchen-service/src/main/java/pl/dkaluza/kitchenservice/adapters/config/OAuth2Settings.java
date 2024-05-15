package pl.dkaluza.kitchenservice.adapters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ciy.oauth2")
public class OAuth2Settings {
    /**
     * Enables/disables the mock auth server.
     * Useful in development, where you don't need to have real auth server and prefer to keep it simple.
     */
    private String mockAuthServerEnabled;

    /**
     * Port of the mock auth server.
     */
    private int mockAuthServerPort;

    public String getMockAuthServerEnabled() {
        return mockAuthServerEnabled;
    }

    public void setMockAuthServerEnabled(String mockAuthServerEnabled) {
        this.mockAuthServerEnabled = mockAuthServerEnabled;
    }

    public int getMockAuthServerPort() {
        return mockAuthServerPort;
    }

    public void setMockAuthServerPort(int mockAuthServerPort) {
        this.mockAuthServerPort = mockAuthServerPort;
    }
}
