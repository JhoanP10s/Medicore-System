package com.medicore.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI medicoreOpenApi() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("medicore-system")
                        .version("1.0.0")
                        .description("""
                                API REST para gestionar la operacion basica de una clinica.
                                Permite administrar pacientes, medicos, especialidades y citas medicas,
                                evitando conflictos de agenda, citas con pacientes inactivos y eliminaciones
                                fisicas de registros criticos con historial asociado.
                                """)
                        .contact(new Contact()
                                .name("Medicore System")
                                .email("soporte@medicore.com")));
    }
}
