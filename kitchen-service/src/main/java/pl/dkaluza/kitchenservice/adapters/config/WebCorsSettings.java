package pl.dkaluza.kitchenservice.adapters.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("ciy.web.cors")
public class WebCorsSettings {
    /**
     * List of origins allowed to make requests to API.
     * Configured via CORS policy.
     */
    private List<String> allowedOrigins;

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
}
