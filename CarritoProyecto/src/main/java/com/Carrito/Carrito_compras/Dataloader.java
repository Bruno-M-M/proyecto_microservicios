package com.Carrito.Carrito_compras;

import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Model.CarritoItem;
import com.Carrito.Carrito_compras.Repository.CarritoRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Configuration
public class Dataloader {

    @Bean
    CommandLineRunner initData(CarritoRepository carritoRepository) {
        return args -> {
            Faker faker = new Faker(new Locale("es"));

            for (int i = 0; i < 5; i++) {
                Carrito carrito = new Carrito();
                carrito.setClienteId((long) faker.number().numberBetween(1, 100));
                carrito.setEstado(Carrito.EstadoPedido.PENDIENTE);
                carrito.setFechaCreacion(LocalDateTime.now());

                CarritoItem item1 = new CarritoItem();
                item1.setCarrito(carrito);
                item1.setProductoId((long) faker.number().numberBetween(1, 50));
                item1.setNombreProducto(faker.commerce().productName());
                item1.setCantidad(faker.number().numberBetween(1, 10));

                CarritoItem item2 = new CarritoItem();
                item2.setCarrito(carrito);
                item2.setProductoId((long) faker.number().numberBetween(1, 50));
                item2.setNombreProducto(faker.commerce().productName());
                item2.setCantidad(faker.number().numberBetween(1, 10));

                carrito.setItems(List.of(item1, item2));
                carritoRepository.save(carrito);
            }

            System.out.println("DataFaker: 5 carritos con items generados");
        };
    }
}