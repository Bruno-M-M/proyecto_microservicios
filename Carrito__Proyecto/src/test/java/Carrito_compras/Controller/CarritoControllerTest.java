package Carrito_compras.Controller;

import com.Carrito.Carrito_compras.Client.ClienteFeingClient;
import com.Carrito.Carrito_compras.Client.InventarioFeingClient;
import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Model.CarritoItem;
import com.Carrito.Carrito_compras.Repository.CarritoRepository;
import com.Carrito.Carrito_compras.Service.CarritoService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = com.Carrito.Carrito_compras.CarritoComprasApplication.class)
public class CarritoControllerTest {

    @Autowired
    private CarritoService carritoService;

    @MockitoBean
    private CarritoRepository carritoRepository;

    @MockitoBean
    private ClienteFeingClient clienteFeingClient;

    @MockitoBean
    private InventarioFeingClient inventarioFeingClient;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    private Carrito crearCarritoFake() {
        Carrito carrito = new Carrito();
        carrito.setId(faker.number().randomNumber());
        carrito.setClienteId((long) faker.number().numberBetween(1, 100));
        carrito.setEstado(Carrito.EstadoPedido.PENDIENTE);
        carrito.setFechaCreacion(LocalDateTime.now());

        CarritoItem item = new CarritoItem();
        item.setId(faker.number().randomNumber());
        item.setCarrito(carrito);
        item.setProductoId((long) faker.number().numberBetween(1, 50));
        item.setNombreProducto(faker.commerce().productName());
        item.setCantidad(faker.number().numberBetween(1, 10));

        carrito.setItems(List.of(item));
        return carrito;
    }

    @Test
    void getTodo_debeRetornarListaDeCarritos() {
        Carrito c1 = crearCarritoFake();
        Carrito c2 = crearCarritoFake();

        when(carritoRepository.findAll()).thenReturn(List.of(c1, c2));

        var resultado = carritoService.getTodo();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(carritoRepository, times(1)).findAll();
    }

    @Test
    void getByCliente_debeRetornarCarritosPorCliente() {
        Carrito carrito = crearCarritoFake();
        Long clienteId = carrito.getClienteId();

        when(carritoRepository.findByClienteId(clienteId)).thenReturn(List.of(carrito));

        var resultado = carritoService.getByCliente(clienteId);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(carritoRepository, times(1)).findByClienteId(clienteId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE", "CONFIRMADO", "PAGADO", "CANCELADO"})
    void getByEstado_debeRetornarCarritosPorEstado(String estado) {
        Carrito carrito = crearCarritoFake();
        carrito.setEstado(Carrito.EstadoPedido.valueOf(estado));

        when(carritoRepository.findByEstado(Carrito.EstadoPedido.valueOf(estado)))
                .thenReturn(List.of(carrito));

        var resultado = carritoService.getByEstado(estado);

        assertNotNull(resultado);
        verify(carritoRepository, times(1))
                .findByEstado(Carrito.EstadoPedido.valueOf(estado));
    }

    @Test
    void confirmar_debeConfirmarCarrito() {
        Carrito carrito = crearCarritoFake();
        carrito.setEstado(Carrito.EstadoPedido.PENDIENTE);

        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any())).thenReturn(carrito);

        var resultado = carritoService.confirmar(carrito.getId());

        assertNotNull(resultado);
        verify(carritoRepository, times(1)).findById(carrito.getId());
    }

    @Test
    void eliminar_debeEliminarCarrito() {
        Carrito carrito = crearCarritoFake();

        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        carritoService.eliminar(carrito.getId());

        verify(carritoRepository, times(1)).deleteById(carrito.getId());
    }
}