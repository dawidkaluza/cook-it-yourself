package pl.dkaluza.kitchenservice.adapters.out.oauth2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
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
    private static final String JWKS_TEMPLATE =
        """
        {
            "keys": [ {
                "kty": "RSA",
                "e": "AQAB",
                "n": "RSA_KEY"
            } ]
       }
        """;
    private static final String JWT_PAYLOAD_TEMPLATE =
        """
        {
            "sub": "1",
            "aud": "ciy-web",
            "name": "Dawid",
            "exp": "EXPIRES_AT",
            "iat": "ISSUED_AT"
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
        var rsaKey = generateRsaKey();

        mockOauthJwksEndpoint(client, rsaKey);
        mockJwtEndpoint(client, rsaKey);
    }

    private RSAKey generateRsaKey() throws JOSEException {
        var rsaKey = new RSAKeyGenerator(2048).generate();
        return rsaKey;
    }

    private void mockOauthJwksEndpoint(WireMock client, RSAKey rsaKey) {
        var jwkSet = new JWKSet(rsaKey);
        client.register(
            get("/oauth2/jwks").willReturn(
                aResponse().withBody(jwkSet.toString())
            )
        );
    }

    private void mockJwtEndpoint(WireMock client, RSAKey rsaKey) throws JOSEException {
        var signer = new RSASSASigner(rsaKey);

        var unixTimeNow = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        var jws = new JWSObject(
            new JWSHeader.Builder(JWSAlgorithm.RS256).build(),
            new Payload(
                JWT_PAYLOAD_TEMPLATE
                    .replace("ISSUED_AT", Long.toString(unixTimeNow))
                    .replace("EXPIRES_AT", Long.toString(unixTimeNow + 86400)))
        );
        jws.sign(signer);

        var jwt = jws.serialize();
        client.register(
            get("/jwt").willReturn(
                aResponse().withBody(jwt)
            )
        );
    }
}
