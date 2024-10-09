package pl.dkaluza.kitchenservice.adapters.out.oauth2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
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
            "sub": "SUBJECT",
            "aud": "ciy-web",
            "name": "Dawid",
            "iat": ISSUED_AT,
            "exp": EXPIRES_AT
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
        var rsaKeyGenerator = new RSAKeyGenerator(2048).generate();

        mockOauthJwksEndpoint(client, new JWKSet(rsaKeyGenerator));
        mockJwtEndpoint(client, new JWSHeader.Builder(JWSAlgorithm.RS256).build(), new RSASSASigner(rsaKeyGenerator));
    }

    private void mockOauthJwksEndpoint(WireMock client, JWKSet jwkSet) {
        client.register(
            get("/oauth2/jwks").willReturn(
                aResponse().withBody(jwkSet.toString())
            )
        );
    }

    private void mockJwtEndpoint(WireMock client, JWSHeader header, JWSSigner signer) throws JOSEException {
        var unixTimeNow = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
        var payloadTemplate = JWT_PAYLOAD_TEMPLATE
                .replace("ISSUED_AT", Long.toString(unixTimeNow))
                .replace("EXPIRES_AT", Long.toString(unixTimeNow + 604800)); // +1 week

        var firstPayload = new Payload(payloadTemplate.replace("SUBJECT", "1"));
        var firstJws = new JWSObject(header, firstPayload);
        firstJws.sign(signer);
        client.register(
            get("/jwt")
                .withQueryParam("subject", or(absent(), equalTo("1")))
                .willReturn(
                    aResponse().withBody(firstJws.serialize())
                )
        );

        var secondPayload = new Payload(payloadTemplate.replace("SUBJECT", "2"));
        var secondJws = new JWSObject(header, secondPayload);
        secondJws.sign(signer);
        client.register(
            get("/jwt")
                .withQueryParam("subject", equalTo("2"))
                .willReturn(
                    aResponse().withBody(secondJws.serialize())
                )
        );
    }
}
