package com.bradesco.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
   @Bean
    public OpenAPI gestaoFinanceiraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Gestão Financeira")
                        .description("Documentação dos endpoints do backend.")
                        .version("v1"));
    }
}
