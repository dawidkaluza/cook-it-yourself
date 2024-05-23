package pl.dkaluza.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(CorsConfigurer::disable)
            .csrf(CsrfConfigurer::disable)
            .oauth2Login(Customizer.withDefaults())
            .oauth2Client(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(Oauth2Settings oauth2Settings) {
        return new InMemoryClientRegistrationRepository(
            ClientRegistration.withRegistrationId("ciy")
                .clientId("api-gateway")
                .clientSecret(oauth2Settings.getClientSecret())
                .clientName("API Gateway")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .issuerUri(oauth2Settings.getUserServiceUrl())
                .authorizationUri(oauth2Settings.getUserServiceUrl() + "/oauth/authorize")
                .tokenUri(oauth2Settings.getUserServiceUrl() + "/oauth/token")
                .jwkSetUri(oauth2Settings.getUserServiceUrl() + "/oauth2/jwks")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .build()
        );
    }
}
