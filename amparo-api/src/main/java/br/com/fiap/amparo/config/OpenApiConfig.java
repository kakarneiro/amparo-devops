package br.com.fiap.amparo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI amparoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Amparo API")
                        .version("1.0")
                        .description("""
                                Rede de cuidado para idosos e pessoas com deficiencia.
                                Um dispositivo IoT (ESP32) registra eventos de emergencia; a API aciona os
                                cuidadores cadastrados em ordem de prioridade e expoe tudo para o app Mobile.

                                Global Solution 2026/2 - CitizenTech - FIAP 2TDSPB.
                                Clique em Authorize e informe o JWT obtido em POST /auth/login.
                                """)
                        .contact(new Contact().name("Equipe Amparo")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT obtido em /auth/login"))
                        .addSecuritySchemes("deviceToken", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Device-Token")
                                .description("Token fixo do dispositivo ESP32")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
