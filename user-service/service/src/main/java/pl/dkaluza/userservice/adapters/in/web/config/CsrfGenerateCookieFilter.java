package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
class CsrfGenerateCookieFilter extends OncePerRequestFilter {
    private final CookieCsrfTokenRepository tokenRepository;

    CsrfGenerateCookieFilter(CookieCsrfTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = tokenRepository.generateToken(request);
        tokenRepository.saveToken(token, request, response);

        filterChain.doFilter(request, response);
    }
}
