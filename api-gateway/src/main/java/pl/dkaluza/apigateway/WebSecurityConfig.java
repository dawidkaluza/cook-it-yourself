package pl.dkaluza.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, WebSettings webSettings) throws Exception {
        return http
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
            .csrf(CsrfConfigurer::disable)
            .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl(webSettings.getWebAppSignInUrl()))
            .oauth2Client(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/kitchen/**").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(WebSettings webSettings) {
        return new InMemoryClientRegistrationRepository(
            ClientRegistration.withRegistrationId("ciy")
                .clientId("api-gateway")
                .clientSecret(webSettings.getUserServiceClientSecret())
                .clientName("API Gateway")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope(OidcScopes.OPENID)
                .issuerUri(webSettings.getUserServiceServerUrl())
                .authorizationUri(webSettings.getUserServiceClientUrl() + "/oauth2/authorize")
                .tokenUri(webSettings.getUserServiceServerUrl() + "/oauth2/token")
                .jwkSetUri(webSettings.getUserServiceServerUrl() + "/oauth2/jwks")
                .userInfoUri(webSettings.getUserServiceServerUrl() + "/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .redirectUri(webSettings.getApiGatewayUrl() + "/login/oauth2/code/{registrationId}")
                .build()
        );
    }
}
