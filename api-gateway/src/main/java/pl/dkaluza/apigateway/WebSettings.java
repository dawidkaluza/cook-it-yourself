package pl.dkaluza.apigateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("ciy.web")
class WebSettings {
    private String apiGatewayUrl;

    private String userServiceServerUrl;

    private String userServiceClientUrl;

    private String userServiceClientSecret;

    private String kitchenServiceUrl;

    private String webAppUrl;

    private String webAppSignInPage;

    private List<String> corsAllowedOrigins;

    public String getApiGatewayUrl() {
        return apiGatewayUrl;
    }

    public void setApiGatewayUrl(String apiGatewayUrl) {
        this.apiGatewayUrl = apiGatewayUrl;
    }

    public String getUserServiceServerUrl() {
        return userServiceServerUrl;
    }

    public void setUserServiceServerUrl(String userServiceServerUrl) {
        this.userServiceServerUrl = userServiceServerUrl;
    }

    public String getUserServiceClientUrl() {
        return userServiceClientUrl;
    }

    public void setUserServiceClientUrl(String userServiceClientUrl) {
        this.userServiceClientUrl = userServiceClientUrl;
    }

    public String getUserServiceClientSecret() {
        return userServiceClientSecret;
    }

    public void setUserServiceClientSecret(String userServiceClientSecret) {
        this.userServiceClientSecret = userServiceClientSecret;
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

    public String getWebAppSignInPage() {
        return webAppSignInPage;
    }

    public String getWebAppSignInUrl() {
        return webAppUrl + webAppSignInPage;
    }

    public void setWebAppSignInPage(String webAppSignInPage) {
        this.webAppSignInPage = webAppSignInPage;
    }

    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(List<String> corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }
}
