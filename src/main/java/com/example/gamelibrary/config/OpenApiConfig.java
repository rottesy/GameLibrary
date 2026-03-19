package com.example.gamelibrary.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI gameLibraryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("GameLibrary API")
                .version("1.0.0")
                .description("REST API for managing games, users, developers, genres, reviews and collections."));
    }
}
