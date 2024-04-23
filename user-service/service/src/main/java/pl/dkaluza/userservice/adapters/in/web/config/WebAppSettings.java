package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ciy.web-app")
class WebAppSettings {
    /**
     * Origins where web app can be located.
     * Used in CORS policy.
     */
    private List<String> origins;

    /**
     * A path where web application will be served.
     * Leave it empty if web app is not served inside the service. (e.g. it's served independently in dev env)
     */
    private String publicPath;

    /**
     * Base URI of the web app.
     * Can have origin, if web app is served independently.
     * Note that this is not concatenated with public path.
     */
    private String baseUri;

    /**
     * A path where users will be redirected to perform sign in.
     */
    private String signInPath;

    /**
     * A path where users will be redirected after sign out.
     */
    private String signOutPath;

    /**
     * A path where users will be redirected to consent access to clients.
     */
    private String consentPath;

    public List<String> getOrigins() {
        return origins;
    }

    public void setOrigins(List<String> origins) {
        this.origins = origins;
    }

    public String getPublicPath() {
        return publicPath;
    }

    public void setPublicPath(String publicPath) {
        this.publicPath = publicPath;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getSignInPath() {
        return signInPath;
    }

    public void setSignInPath(String signInPath) {
        this.signInPath = signInPath;
    }

    public String getSignInUri() {
        return baseUri + signInPath;
    }

    public String getSignOutPath() {
        return signOutPath;
    }

    public void setSignOutPath(String signOutPath) {
        this.signOutPath = signOutPath;
    }

    public String getSignOutUri() {
        return baseUri + signOutPath;
    }

    public String getConsentPath() {
        return consentPath;
    }

    public void setConsentPath(String consentPath) {
        this.consentPath = consentPath;
    }

    public String getConsentUri() {
        return baseUri + consentPath;
    }

    public boolean isWebAppEmbedded() {
        return !publicPath.isEmpty();
    }
}
