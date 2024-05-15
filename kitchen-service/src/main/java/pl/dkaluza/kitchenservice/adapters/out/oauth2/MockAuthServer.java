package pl.dkaluza.kitchenservice.adapters.out.oauth2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.dkaluza.kitchenservice.adapters.config.OAuth2Settings;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Component
@ConditionalOnProperty("ciy.oauth2.mock-auth-server-enabled")
class MockAuthServer implements InitializingBean, DisposableBean {
    private static final String JWT_PAYLOAD_TEMPLATE =
        """
        {
            "sub": "1",
            "aud": "ciy-web",
            "name": "Dawid",
            "iat": "ISSUED_AT",
            "exp": "EXPIRES_AT"
        }
        """;

    private final WireMockServer server;

    public MockAuthServer(OAuth2Settings oAuth2Settings) {
        server = new WireMockServer(options().port(oAuth2Settings.getMockAuthServerPort()));
    }

    @Override
    public void afterPropertiesSet() throws JOSEException {
        server.start();
        configure();
    }

    @Override
    public void destroy() {
        server.stop();
    }

    private void configure() throws JOSEException {
        var client = new WireMock(server.port());
        var rsaKey = new RSAKeyGenerator(2048).generate();

        mockOauthJwksEndpoint(client, new JWKSet(rsaKey));

        var unixTimeNow = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        var payload = new Payload(
            JWT_PAYLOAD_TEMPLATE
                .replace("ISSUED_AT", Long.toString(unixTimeNow))
                .replace("EXPIRES_AT", Long.toString(unixTimeNow + 604800)) // +1 week
        );
        mockJwtEndpoint(client, new JWSHeader.Builder(JWSAlgorithm.RS256).build(), payload, new RSASSASigner(rsaKey));
    }

    private void mockOauthJwksEndpoint(WireMock client, JWKSet jwkSet) {
        client.register(
            get("/oauth2/jwks").willReturn(
                aResponse().withBody(jwkSet.toString())
            )
        );
    }

    private void mockJwtEndpoint(WireMock client, JWSHeader header, Payload payload, JWSSigner signer) throws JOSEException {
        var jws = new JWSObject(header, payload);
        jws.sign(signer);

        client.register(
            get("/jwt").willReturn(
                aResponse().withBody(jws.serialize())
            )
        );
    }
}
