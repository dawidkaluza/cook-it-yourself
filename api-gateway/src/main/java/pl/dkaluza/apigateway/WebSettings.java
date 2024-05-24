package pl.dkaluza.apigateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("ciy.web")
class WebSettings {
    private String userServiceUrl;

    private String userServiceClientUrl;

    private String kitchenServiceUrl;

    private String webAppUrl;

    private List<String> corsAllowedOrigins;

    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public void setUserServiceUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public String getUserServiceClientUrl() {
        return userServiceClientUrl;
    }

    public void setUserServiceClientUrl(String userServiceClientUrl) {
        this.userServiceClientUrl = userServiceClientUrl;
    }

    public String getKitchenServiceUrl() {
        return kitchenServiceUrl;
    }

    public void setKitchenServiceUrl(String kitchenServiceUrl) {
        this.kitchenServiceUrl = kitchenServiceUrl;
    }

    public String getWebAppUrl() {
        return webAppUrl;
    }

    public void setWebAppUrl(String webAppUrl) {
        this.webAppUrl = webAppUrl;
    }

    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }
}
