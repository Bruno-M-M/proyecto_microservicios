package com.Pago.Metodo_de_pago.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Servicio de Pago API")
                        .version("1.2")
                        .description("API REST para la gestión de pagos. Procesa transacciones vinculadas al carrito de compras.")
                        .contact(new Contact()
                                .name("Tu Nombre")
                                .email("tuemail@duoc.cl")));
    }
}
