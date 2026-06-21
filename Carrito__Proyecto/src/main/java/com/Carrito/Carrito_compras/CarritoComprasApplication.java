package com.Carrito.Carrito_compras;

import org.springdoc.core.configuration.SpringDocHateoasConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {SpringDocHateoasConfiguration.class})
@EnableFeignClients
public class CarritoComprasApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarritoComprasApplication.class, args);
	}

}
