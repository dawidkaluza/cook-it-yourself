package pl.dkaluza.kitchenservice.adapters.in.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import pl.dkaluza.kitchenservice.adapters.config.WebSettings;

@Configuration
class RecipeWebSecurityConfig {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, WebSettings webSettings) throws Exception {
        http
            .securityMatcher("/recipe/**")
            .cors(CorsConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(handler ->
                handler
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                    .accessDeniedHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
            )
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            ).oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwkSetUri(webSettings.getUserServiceUrl() + "/oauth2/jwks"))
            );

        return http.build();
    }

}
