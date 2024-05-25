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
            .oauth2Login(Customizer.withDefaults())
            .oauth2Client(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(Oauth2Settings oauth2Settings) {
        return new InMemoryClientRegistrationRepository(
            ClientRegistration.withRegistrationId(oauth2Settings.getRegistrationId())
                .clientId(oauth2Settings.getClientId())
                .clientSecret(oauth2Settings.getClientSecret())
                .clientName(oauth2Settings.getClientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope(OidcScopes.OPENID)
                .issuerUri(oauth2Settings.getAuthServerUrl())
                .authorizationUri(oauth2Settings.getAuthServerClientUrl() + "/oauth2/authorize")
                .tokenUri(oauth2Settings.getAuthServerUrl() + "/oauth2/token")
                .jwkSetUri(oauth2Settings.getAuthServerUrl() + "/oauth2/jwks")
                .userInfoUri(oauth2Settings.getAuthServerUrl() + "/userinfo")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .build()
        );
    }
}
