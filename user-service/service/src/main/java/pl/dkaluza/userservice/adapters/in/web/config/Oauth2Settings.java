package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ciy.oauth2")
class Oauth2Settings {
    /**
     * Client ID, used to identify client in OAuth2 protocol.
     */
    private String clientId;

    /**
     * Client secret, used to authenticate client in OAuth2 protocol.
     * NOTE: Given secret should be already encoded with algorithm used defined as a prefix (see DelegatedPasswordEncoder).
     * E.g. {bcrypt}#encoded#secret#
     */
    private String clientSecret;

    /**
     * Client redirect URI, it's where users will be redirected with authorization code generated for the client.
     */
    private String redirectUri;

    /**
     * Client sign out URI, it's where users will be redirected after signing out at authorization server.
     */
    private String signOutUri;

    /**
     * Client origins - allowed origins for CORS policy.
     */
    private List<String> clientOrigins;

    private List<String> clientsOrigins;

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

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getSignOutUri() {
        return signOutUri;
    }

    public void setSignOutUri(String signOutUri) {
        this.signOutUri = signOutUri;
    }

    public List<String> getClientOrigins() {
        return clientOrigins;
    }

    public void setClientOrigins(List<String> clientOrigins) {
        this.clientOrigins = clientOrigins;
    }

    @Deprecated
    public void setClientsOrigins(List<String> clientsOrigins) {
        this.clientsOrigins = clientsOrigins;
    }
}
