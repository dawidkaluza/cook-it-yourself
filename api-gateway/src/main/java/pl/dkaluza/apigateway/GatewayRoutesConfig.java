package pl.dkaluza.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions.*;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
class GatewayRoutesConfig {
    @Bean
    RouterFunction<ServerResponse> kitchenServiceRoute(WebSettings webSettings) {
        return route("kitchen-service")
            .route(path("/kitchen/**"), http(webSettings.getKitchenServiceUrl()))
            .before(rewritePath("/kitchen/(?<segment>.*)", "/${segment}"))
            .filter(tokenRelay())
            .after(dedupeResponseHeader("Access-Control-Allow-Credentials Access-Control-Allow-Origin", DedupeStrategy.RETAIN_UNIQUE))
            .build();
    }
}
