package com.danieldev87.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Configuración personalizada de SpringDoc OpenAPI / Swagger.
 * Define la información general de la API que se muestra en Swagger UI.
 */
@Configuration
public class SpringDocConfig {

    /**
     * Bean que configura la documentación OpenAPI con la información
     * general de la API: título, descripción, versión, contacto y licencia.
     *
     * @return Objeto OpenAPI configurado con los metadatos de la API
     */
    @Bean
    public OpenAPI yogurtMakerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API de Yogurt Maker")
                .description("API REST para la gestión completa de producción de yogurt artesanal. " +
                    "Incluye módulos para:\n" +
                    "- Gestión de recetas de yogurt\n" +
                    "- Control del ciclo de producción de lotes\n" +
                    "- Monitoreo de temperatura en tiempo real\n" +
                    "- Dashboard de métricas de producción")
                .version("1.0.0")
                .contact(new Contact()
                    .name("DanielDev87")
                    .email("soporte@yogurtmaker.com")
                    .url("https://github.com/danieldev87"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}