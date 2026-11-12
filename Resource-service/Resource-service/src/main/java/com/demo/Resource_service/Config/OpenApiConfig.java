package com.demo.Resource_service.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI resourceOpenAPI(@Value("${openapi.server-url}") String serverUrl) {
        return new OpenAPI()
                .info(new Info()
                        .title("Resources")
                        .version("1.0")
                        .description("Browse rooms, desks, and vehicles. Creating or deleting a resource requires an admin token."))
                .servers(List.of(new Server().url(serverUrl)))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth", bearerScheme()));
    }

    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Paste the token from login. Do not include the word Bearer.");
    }
}
