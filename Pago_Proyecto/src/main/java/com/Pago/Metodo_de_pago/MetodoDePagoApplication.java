package com.Pago.Metodo_de_pago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MetodoDePagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetodoDePagoApplication.class, args);
	}

}
