package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class SignInUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    private final WebAppSettings webAppSettings;
    private final CookieCsrfTokenRepository tokenRepository;

    public SignInUrlAuthenticationEntryPoint(WebAppSettings webAppSettings, CookieCsrfTokenRepository tokenRepository) {
        super(webAppSettings.getSignInUri());
        this.webAppSettings = webAppSettings;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (!webAppSettings.isWebAppEmbedded()) {
            var token = tokenRepository.generateToken(request);
            tokenRepository.saveToken(token, request, response);
        }

        super.commence(request, response, authException);
    }
}
