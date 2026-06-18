package com.Pago.Metodo_de_pago;

import com.Pago.Metodo_de_pago.Model.MetodoPago;
import com.Pago.Metodo_de_pago.Repostory.BoletaRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

public class Dataloader {
    @Bean
    CommandLineRunner cargarBoletas(BoletaRepository boletaRepository) {
        return args -> {

            if (boletaRepository.count() > 0) {
                System.out.println("  Ya existen boletas en la BD, se omite la carga.");
                return;
            }

            Faker faker = new Faker();
            MetodoPago.TipoPago[] tiposPago = MetodoPago.TipoPago.values();

            for (int i = 0; i < 10; i++) {

                double totalNeto   = faker.number().randomDouble(2, 5000, 200000);
                double iva         = Math.round(totalNeto * 0.19 * 100.0) / 100.0;
                double totalConIva = Math.round((totalNeto + iva) * 100.0) / 100.0;

                MetodoPago boleta = new MetodoPago();
                boleta.setClienteId((long) faker.number().numberBetween(1, 20));
                boleta.setClienteNombre(faker.name().fullName());
                boleta.setClienteRun(faker.numerify("########-#"));
                boleta.setClienteCorreo(faker.internet().emailAddress());
                boleta.setClienteDireccion(faker.address().fullAddress());
                boleta.setClienteTelefono(faker.number().numberBetween(900000000, 999999999));
                boleta.setTipoPago(tiposPago[faker.number().numberBetween(0, tiposPago.length)]);
                boleta.setTotalNeto(totalNeto);
                boleta.setIva(iva);
                boleta.setTotalConIva(totalConIva);
                boleta.setFechaEmision(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30)));
                boleta.setEstado(MetodoPago.EstadoBoleta.EMITIDA);
                boleta.setPedidosIds(String.valueOf(faker.number().numberBetween(1, 50)));

                boletaRepository.save(boleta);
            }

            System.out.println("10 boletas de prueba cargadas con DataFaker.");
        };
    }
}
