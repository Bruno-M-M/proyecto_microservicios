package com.Carrito.Carrito_compras.Config;

import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Model.CarritoItem;
import com.Carrito.Carrito_compras.Repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CarritoConifg implements CommandLineRunner {
    private final CarritoRepository carritoRepository;
    @Override
    public void run(String... args) {
        if (carritoRepository.count() > 0) {
            log.info(">>> Pedidos ya cargados. Se omite inicialización del carrito.");
            return;
        }

        log.info(">>> Cargando historial de pedidos iniciales...");

        // ── Bruno Mateluna (clienteId = 1) ────────────────────────────────

        // b1 – PAGADO hace 2 meses (Chocapic + Nescafé)
        Carrito b1 = new Carrito();
        b1.setClienteId(1L);
        b1.setEstado(Carrito.EstadoPedido.PAGADO);
        b1.setFechaCreacion(LocalDateTime.now().minusMonths(2).minusDays(3));
        b1.setFechaConfirmacion(LocalDateTime.now().minusMonths(2).minusDays(3));

        CarritoItem b1i1 = new CarritoItem();
        b1i1.setCarrito(b1);
        b1i1.setProductoId(1L);
        b1i1.setNombreProducto("Chocapic");
        b1i1.setCantidad(2);

        CarritoItem b1i2 = new CarritoItem();
        b1i2.setCarrito(b1);
        b1i2.setProductoId(2L);
        b1i2.setNombreProducto("Nescafe seleccion");
        b1i2.setCantidad(1);

        b1.getItems().add(b1i1);
        b1.getItems().add(b1i2);
        carritoRepository.save(b1);

        // b2 – PAGADO hace 1 mes (Leche + Pollo)
        Carrito b2 = new Carrito();
        b2.setClienteId(1L);
        b2.setEstado(Carrito.EstadoPedido.PAGADO);
        b2.setFechaCreacion(LocalDateTime.now().minusMonths(1).minusDays(8));
        b2.setFechaConfirmacion(LocalDateTime.now().minusMonths(1).minusDays(8));

        CarritoItem b2i1 = new CarritoItem();
        b2i1.setCarrito(b2);
        b2i1.setProductoId(4L);
        b2i1.setNombreProducto("Leche Colun entera");
        b2i1.setCantidad(6);

        CarritoItem b2i2 = new CarritoItem();
        b2i2.setCarrito(b2);
        b2i2.setProductoId(5L);
        b2i2.setNombreProducto("Filetitos de pollo");
        b2i2.setCantidad(2);

        b2.getItems().add(b2i1);
        b2.getItems().add(b2i2);
        carritoRepository.save(b2);

        // b3 – CANCELADO hace 15 días (Poett + Chocapic)
        Carrito b3 = new Carrito();
        b3.setClienteId(1L);
        b3.setEstado(Carrito.EstadoPedido.CANCELADO);
        b3.setFechaCreacion(LocalDateTime.now().minusDays(15));
        b3.setFechaConfirmacion(null);

        CarritoItem b3i1 = new CarritoItem();
        b3i1.setCarrito(b3);
        b3i1.setProductoId(3L);
        b3i1.setNombreProducto("Poett frescura lavanda");
        b3i1.setCantidad(1);

        CarritoItem b3i2 = new CarritoItem();
        b3i2.setCarrito(b3);
        b3i2.setProductoId(1L);
        b3i2.setNombreProducto("Chocapic");
        b3i2.setCantidad(1);

        b3.getItems().add(b3i1);
        b3.getItems().add(b3i2);
        carritoRepository.save(b3);

        // b4 – PENDIENTE hoy (Chocapic + Leche)
        Carrito b4 = new Carrito();
        b4.setClienteId(1L);
        b4.setEstado(Carrito.EstadoPedido.PENDIENTE);
        b4.setFechaCreacion(LocalDateTime.now());
        b4.setFechaConfirmacion(null);

        CarritoItem b4i1 = new CarritoItem();
        b4i1.setCarrito(b4);
        b4i1.setProductoId(1L);
        b4i1.setNombreProducto("Chocapic");
        b4i1.setCantidad(3);

        CarritoItem b4i2 = new CarritoItem();
        b4i2.setCarrito(b4);
        b4i2.setProductoId(4L);
        b4i2.setNombreProducto("Leche Colun entera");
        b4i2.setCantidad(2);

        b4.getItems().add(b4i1);
        b4.getItems().add(b4i2);
        carritoRepository.save(b4);

        // ── Claudio Bravo (clienteId = 2) ─────────────────────────────────

        // c1 – PAGADO hace 3 meses (Leche + Poett)
        Carrito c1 = new Carrito();
        c1.setClienteId(2L);
        c1.setEstado(Carrito.EstadoPedido.PAGADO);
        c1.setFechaCreacion(LocalDateTime.now().minusMonths(3));
        c1.setFechaConfirmacion(LocalDateTime.now().minusMonths(3));

        CarritoItem c1i1 = new CarritoItem();
        c1i1.setCarrito(c1);
        c1i1.setProductoId(4L);
        c1i1.setNombreProducto("Leche Colun entera");
        c1i1.setCantidad(4);

        CarritoItem c1i2 = new CarritoItem();
        c1i2.setCarrito(c1);
        c1i2.setProductoId(3L);
        c1i2.setNombreProducto("Poett frescura lavanda");
        c1i2.setCantidad(2);

        c1.getItems().add(c1i1);
        c1.getItems().add(c1i2);
        carritoRepository.save(c1);

        // c2 – PAGADO hace 4 días (Nescafé + Pollo)
        Carrito c2 = new Carrito();
        c2.setClienteId(2L);
        c2.setEstado(Carrito.EstadoPedido.PAGADO);
        c2.setFechaCreacion(LocalDateTime.now().minusDays(4));
        c2.setFechaConfirmacion(LocalDateTime.now().minusDays(4));

        CarritoItem c2i1 = new CarritoItem();
        c2i1.setCarrito(c2);
        c2i1.setProductoId(2L);
        c2i1.setNombreProducto("Nescafe seleccion");
        c2i1.setCantidad(2);

        CarritoItem c2i2 = new CarritoItem();
        c2i2.setCarrito(c2);
        c2i2.setProductoId(5L);
        c2i2.setNombreProducto("Filetitos de pollo");
        c2i2.setCantidad(1);

        c2.getItems().add(c2i1);
        c2.getItems().add(c2i2);
        carritoRepository.save(c2);

        // c3 – PENDIENTE hoy (Pollo + Chocapic)
        Carrito c3 = new Carrito();
        c3.setClienteId(2L);
        c3.setEstado(Carrito.EstadoPedido.PENDIENTE);
        c3.setFechaCreacion(LocalDateTime.now());
        c3.setFechaConfirmacion(null);

        CarritoItem c3i1 = new CarritoItem();
        c3i1.setCarrito(c3);
        c3i1.setProductoId(5L);
        c3i1.setNombreProducto("Filetitos de pollo");
        c3i1.setCantidad(1);

        CarritoItem c3i2 = new CarritoItem();
        c3i2.setCarrito(c3);
        c3i2.setProductoId(1L);
        c3i2.setNombreProducto("Chocapic");
        c3i2.setCantidad(2);

        c3.getItems().add(c3i1);
        c3.getItems().add(c3i2);
        carritoRepository.save(c3);

        log.info(">>> 7 pedidos cargados OK — Bruno Mateluna (4) | Claudio Bravo (3). Todos con 2+ ítems.");
    }
}
