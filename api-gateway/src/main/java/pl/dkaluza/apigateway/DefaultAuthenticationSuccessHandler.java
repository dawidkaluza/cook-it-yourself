package pl.dkaluza.apigateway;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
class DefaultAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final WebSettings webSettings;

    public DefaultAuthenticationSuccessHandler(WebSettings webSettings) {
        this.webSettings = webSettings;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        var user = (OidcUser) authentication.getPrincipal();
        return UriComponentsBuilder
            .fromHttpUrl(webSettings.getWebAppSignInUrl())
            .queryParam("nickname", user.getNickName())
            .build().toString();
    }
}
