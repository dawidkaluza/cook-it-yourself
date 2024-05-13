package pl.dkaluza.kitchenservice.adapters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("ciy.web")
public class WebSettings {
    /**
     * List of origins allowed to make requests to API.
     * Configured via CORS policy.
     */
    private List<String> corsAllowedOrigins;

    /**
     * URL to user service.
     * Used to configure resource server.
     */
    private String userServiceUrl;

    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public void setUserServiceUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }
}
