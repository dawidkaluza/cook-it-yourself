package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;

class SignInUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
    private final CookieCsrfTokenRepository tokenRepository;

    public SignInUrlAuthenticationEntryPoint(CookieCsrfTokenRepository tokenRepository) {
        super("/sign-in");
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        var token = tokenRepository.generateToken(request);
        tokenRepository.saveToken(token, request, response);
        super.commence(request, response, authException);
    }
}
