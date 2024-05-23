package pl.dkaluza.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.TokenRelayFilterFunctions.tokenRelay;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
class GatewayRoutesConfig {
    @Bean
    RouterFunction<ServerResponse> userServiceRoute() {
        return route(path("/api/user/"), http("http://localhost:8081"))
            .filter(tokenRelay());
    }

    @Bean
    RouterFunction<ServerResponse> kitchenServiceRoute() {
        return route(path("/api/kitchen/"), http("http://localhost:8082"))
            .filter(tokenRelay());
    }
}
