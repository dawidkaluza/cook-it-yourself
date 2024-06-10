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
     * URL where client is accessed by resource owners.
     */
    private String clientUrl;

    /**
     * Path where resource owners will be redirected with code during authorization code flow.
     */
    private String redirectPath;

    /**
     * Path where resource owners will be redirected after signing out.
     */
    private String signOutPath;

    /**
     * Client origins - allowed origins for CORS policy.
     */
    private List<String> clientOrigins;

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

    public String getClientUrl() {
        return clientUrl;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }

    public String getRedirectPath() {
        return redirectPath;
    }

    public void setRedirectPath(String redirectPath) {
        this.redirectPath = redirectPath;
    }

    public String getRedirectUri() {
        return clientUrl + redirectPath;
    }

    public String getSignOutPath() {
        return signOutPath;
    }

    public void setSignOutPath(String signOutPath) {
        this.signOutPath = signOutPath;
    }

    public String getSignOutUri() {
        return clientUrl + signOutPath;
    }

    public List<String> getClientOrigins() {
        return clientOrigins;
    }

    public void setClientOrigins(List<String> clientOrigins) {
        this.clientOrigins = clientOrigins;
    }
}
