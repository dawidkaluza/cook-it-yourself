package pl.dkaluza.userservice.adapters.in.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class WebAppSettings {
    /**
     * A path where web application will be served.
     * Leave it empty if web app is not served inside the service. (e.g. it's served independently in dev env)
     */
    private final String publicPath;

    /**
     * Base URI of the web app.
     */
    private final String baseUri;

    /**
     * A path where users will be redirected to perform sign in.
     */
    private final String signInPath;

    /**
     * A path where users will be redirected after sign out.
     */
    private final String signOutPath;

    /**
     * A path where users will be redirected to consent access to clients.
     */
    private final String consentPath;

    public WebAppSettings(
        @Value("${ciy.web-app.public-path:}") String publicPath,
        @Value("${ciy.web-app.base-uri:http://localhost:9090/}") String baseUri,
        @Value("${ciy.web-app.sign-in-path:/sign-in}") String signInPath,
        @Value("${ciy.web-app.sign-out-path:/sign-in?logout}") String signOutPath,
        @Value("${ciy.web-app.consent-path:/consent}") String consentPath
    ) {
        this.publicPath = publicPath;
        this.baseUri = baseUri;
        this.signInPath = signInPath;
        this.signOutPath = signOutPath;
        this.consentPath = consentPath;
    }

    public String getPublicPath() {
        return publicPath;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public String getSignInPath() {
        return signInPath;
    }

    public String getSignInUri() {
        return baseUri + signInPath;
    }

    public String getSignOutPath() {
        return signOutPath;
    }

    public String getSignOutUri() {
        return baseUri + signOutPath;
    }

    public String getConsentPath() {
        return consentPath;
    }

    public String getConsentUri() {
        return baseUri + consentPath;
    }

    public boolean isWebAppEmbedded() {
        return !publicPath.isEmpty();
    }
}
