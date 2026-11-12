package com.demo.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
public class GatewayRoutes {

    @Bean
    public RouterFunction<ServerResponse> authRoot(@Value("${auth.service.url}") String target) {
        return forward("/api/auth", target);
    }

    @Bean
    public RouterFunction<ServerResponse> authNested(@Value("${auth.service.url}") String target) {
        return forward("/api/auth/**", target);
    }

    @Bean
    public RouterFunction<ServerResponse> resourceRoot(@Value("${resource.service.url}") String target) {
        return forward("/api/resources", target);
    }

    @Bean
    public RouterFunction<ServerResponse> resourceNested(@Value("${resource.service.url}") String target) {
        return forward("/api/resources/**", target);
    }

    @Bean
    public RouterFunction<ServerResponse> reservationRoot(@Value("${reservation.service.url}") String target) {
        return forward("/api/reservations", target);
    }

    @Bean
    public RouterFunction<ServerResponse> reservationNested(@Value("${reservation.service.url}") String target) {
        return forward("/api/reservations/**", target);
    }

    @Bean
    public RouterFunction<ServerResponse> authApiDocs(@Value("${auth.service.url}") String target) {
        return forward("/v3/api-docs/auth", target);
    }

    @Bean
    public RouterFunction<ServerResponse> resourceApiDocs(@Value("${resource.service.url}") String target) {
        return forward("/v3/api-docs/resources", target);
    }

    @Bean
    public RouterFunction<ServerResponse> reservationApiDocs(@Value("${reservation.service.url}") String target) {
        return forward("/v3/api-docs/reservations", target);
    }

    private RouterFunction<ServerResponse> forward(String pattern, String target) {
        return route().route(path(pattern), http()).before(uri(target)).build();
    }
}
