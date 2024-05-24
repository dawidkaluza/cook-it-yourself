package pl.dkaluza.apigateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ciy.oauth2")
class Oauth2Settings {
    private String registrationId;

    private String clientId;

    private String clientSecret;

    private String clientName;

    private String authServerUrl;

    private String authServerClientUrl;

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAuthServerUrl() {
        return authServerUrl;
    }

    public void setAuthServerUrl(String authServerUrl) {
        this.authServerUrl = authServerUrl;
    }

    public String getAuthServerClientUrl() {
        return authServerClientUrl;
    }

    public void setAuthServerClientUrl(String authServerClientUrl) {
        this.authServerClientUrl = authServerClientUrl;
    }
}
