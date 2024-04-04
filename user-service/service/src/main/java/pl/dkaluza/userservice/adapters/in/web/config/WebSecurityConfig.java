package pl.dkaluza.userservice.adapters.in.web.config;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.web.OAuth2AuthorizationEndpointFilter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Field;
import java.util.UUID;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, WebAppSettings webAppSettings, SignInUrlAuthenticationEntryPoint authEntryPoint, ExtendedRedirectStrategy redirectStrategy) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .authorizationEndpoint(authorization ->
                authorization.consentPage(webAppSettings.getConsentUri())
            )
            .oidc(Customizer.withDefaults());	// Enable OpenID Connect 1.0
        http
            .cors(Customizer.withDefaults())
            // Redirect to the login page when not authenticated from the
            // authorization endpoint
            .exceptionHandling(handler -> handler
                .defaultAuthenticationEntryPointFor(
                    authEntryPoint,
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
            )
            // Accept access tokens for User Info and/or Client Registration
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        // Trick to use custom redirectStrategy
        var filterChain = http.build();
        var authorizationEndpointFilter = (OAuth2AuthorizationEndpointFilter) filterChain.getFilters().stream()
            .filter(filter -> filter instanceof OAuth2AuthorizationEndpointFilter)
            .findAny().orElseThrow();

        Field field = OAuth2AuthorizationEndpointFilter.class.getDeclaredField("redirectStrategy");
        field.setAccessible(true);
        field.set(authorizationEndpointFilter, redirectStrategy);

        return filterChain;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain userAuthSecurityFilterChain(HttpSecurity http, WebAppSettings webAppSettings, CookieCsrfTokenRepository tokenRepository, CsrfTokenRequestAttributeHandler tokenReqAttrHandler, CsrfGenerateCookieFilter generateCookieFilter, RestfulRedirectStrategy redirectStrategy) throws Exception {
        http
            .securityMatcher("/sign-in", "/sign-out")
            .cors(Customizer.withDefaults())
            .csrf((csrf) -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(tokenReqAttrHandler)
            )
            .addFilterAfter(generateCookieFilter, BasicAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage(webAppSettings.getSignInUri())
                .loginProcessingUrl("/sign-in")
                .successHandler((req, res, auth) -> {
                    var savedReq = new HttpSessionRequestCache().getRequest(req, res);
                    redirectStrategy.sendRedirect(req, res, savedReq == null ? "" : savedReq.getRedirectUrl());
                })
                .failureHandler((req, res, ex) ->
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )
            .logout(logout -> logout
                .logoutUrl("/sign-out")
                .logoutSuccessUrl(webAppSettings.getSignOutUri())
            )
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)
                )
                .accessDeniedHandler((req, res, ex) -> {
                    logger.debug("Access denied handler caught exception", ex);
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                })
            )
            .authorizeHttpRequests(authorize ->
                authorize
                    .anyRequest().authenticated()
            );

        return http.build();
    }


    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, SignInUrlAuthenticationEntryPoint signInUrlAuthEntryPoint) throws Exception {
        //noinspection Convert2MethodRef
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(handler -> handler
                .defaultAuthenticationEntryPointFor(
                    signInUrlAuthEntryPoint,
                    new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                )
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.FORBIDDEN),
                    new MediaTypeRequestMatcher(MediaType.ALL)
                )
            )
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/user/sign-up").permitAll()
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(WebAppSettings webAppSettings) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin(webAppSettings.getBaseUri());
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .build()
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    CsrfTokenRequestAttributeHandler csrfTokenReqAttrHandler() {
        return new CsrfTokenRequestAttributeHandler();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient webappClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("ciy-web")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://webapp/sign-in")
            .postLogoutRedirectUri("http://webapp/sign-in?sign-out")
            .scope("openid").scope("profile")
            .clientSettings(
                ClientSettings.builder()
                    .requireProofKey(true)
                    .requireAuthorizationConsent(false)
                    .build()
            )
            .build();

        return new InMemoryRegisteredClientRepository(webappClient);
    }
}
