package pl.dkaluza.kitchenservice.adapters.in.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.dkaluza.kitchenservice.adapters.config.WebSettings;

@Configuration
class RecipeWebSecurityConfig {
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, WebSettings webSettings) throws Exception {
        //noinspection Convert2MethodRef
        http
            .securityMatcher("/recipe/**")
            .cors(cors -> {
                var config = new CorsConfiguration();
                config.addAllowedHeader("*");
                config.addAllowedMethod("*");
                config.setAllowedOrigins(webSettings.getCorsAllowedOrigins());
                config.setAllowCredentials(true);

                var source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                cors.configurationSource(source);
            })
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(handler -> handler.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().authenticated()
            ).oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> jwt.jwkSetUri(webSettings.getUserServiceUrl() + "/oauth2/jwks"))
            );

        return http.build();
    }

}
