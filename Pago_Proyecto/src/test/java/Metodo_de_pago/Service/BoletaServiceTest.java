package Metodo_de_pago.Service;

import com.Pago.Metodo_de_pago.DTO.*;
import com.Pago.Metodo_de_pago.Model.MetodoPago;
import com.Pago.Metodo_de_pago.Repostory.BoletaRepository;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import com.Pago.Metodo_de_pago.Client.CarritoFeingClient;
import com.Pago.Metodo_de_pago.Client.ClienteFeingClient;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = com.Pago.Metodo_de_pago.MetodoDePagoApplication.class)
public class BoletaServiceTest {

    @Autowired
    private BoletaService boletaService;

    @MockitoBean
    private BoletaRepository boletaRepository;

    @MockitoBean
    private CarritoFeingClient carritoClient;

    @MockitoBean
    private ClienteFeingClient clienteClient;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    private MetodoPago crearBoletaFake() {
        double neto = faker.number().randomDouble(2, 5000, 200000);
        double iva  = Math.round(neto * 0.19 * 100.0) / 100.0;

        MetodoPago boleta = new MetodoPago();
        boleta.setId(faker.number().randomNumber());
        boleta.setClienteId((long) faker.number().numberBetween(1, 50));
        boleta.setClienteNombre(faker.name().fullName());
        boleta.setClienteRun(faker.numerify("########-#"));
        boleta.setClienteCorreo(faker.internet().emailAddress());
        boleta.setClienteDireccion(faker.address().fullAddress());
        boleta.setClienteTelefono(faker.number().numberBetween(900000000, 999999999));
        boleta.setTipoPago(MetodoPago.TipoPago.EFECTIVO);
        boleta.setTotalNeto(neto);
        boleta.setIva(iva);
        boleta.setTotalConIva(Math.round((neto + iva) * 100.0) / 100.0);
        boleta.setFechaEmision(LocalDateTime.now());
        boleta.setEstado(MetodoPago.EstadoBoleta.EMITIDA);
        boleta.setPedidosIds("1");
        return boleta;
    }

    // ─── Helper: crea un CarritoDetalleDTO con estado CONFIRMADO ─────────────
    private CarritoDetalleDTO crearCarritoConfirmadoFake(Long clienteId) {
        CarritoItemDetalleDTO item = new CarritoItemDetalleDTO();
        item.setProductoId(1L);
        item.setProductoNombre(faker.commerce().productName());
        item.setProductoCategoria(faker.commerce().department());
        item.setProductoPrecio(faker.number().numberBetween(1000, 50000));
        item.setCantidad(faker.number().numberBetween(1, 5));
        item.setSubtotal((double) item.getProductoPrecio() * item.getCantidad());

        CarritoDetalleDTO carrito = new CarritoDetalleDTO();
        carrito.setPedidoId(1L);
        carrito.setEstadoPedido("CONFIRMADO");
        carrito.setClienteId(clienteId);
        carrito.setClienteNombre(faker.name().fullName());
        carrito.setClienteRun(faker.numerify("########-#"));
        carrito.setClienteCorreo(faker.internet().emailAddress());
        carrito.setClienteDireccion(faker.address().fullAddress());
        carrito.setClienteTelefono(faker.number().numberBetween(900000000, 999999999));
        carrito.setItems(List.of(item));
        return carrito;
    }

    // ─── Test 1: getBoletas retorna lista correctamente ───────────────────────
    @Test
    void getBoletas_debeRetornarListaDeBoletas() {
        MetodoPago boleta1 = crearBoletaFake();
        MetodoPago boleta2 = crearBoletaFake();

        when(boletaRepository.findAll()).thenReturn(List.of(boleta1, boleta2));
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(CollectionModel.of(List.of()));

        List<BoletaResponseDTO> resultado = boletaService.getBoletas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(boletaRepository, times(1)).findAll();
    }

    // ─── Test 2: getBoletas lista vacía ───────────────────────────────────────
    @Test
    void getBoletas_sinDatos_debeRetornarListaVacia() {
        when(boletaRepository.findAll()).thenReturn(List.of());

        List<BoletaResponseDTO> resultado = boletaService.getBoletas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ─── Test 3: getBoletaById retorna boleta existente ───────────────────────
    @Test
    void getBoletaById_existente_debeRetornarBoleta() {
        MetodoPago boleta = crearBoletaFake();

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(CollectionModel.of(List.of()));

        BoletaResponseDTO resultado = boletaService.getBoletaById(boleta.getId());

        assertNotNull(resultado);
        assertEquals(boleta.getClienteNombre(), resultado.getClienteNombre());
        assertEquals(boleta.getTotalNeto(), resultado.getTotalNeto());
    }

    // ─── Test 4: getBoletaById lanza excepción si no existe ──────────────────
    @Test
    void getBoletaById_noExistente_debeLanzarExcepcion() {
        Long idInexistente = faker.number().randomNumber();

        when(boletaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.getBoletaById(idInexistente));

        assertTrue(ex.getMessage().contains("Boleta no encontrada"));
    }

    // ─── Test 5: anular boleta EMITIDA la cambia a ANULADA ───────────────────
    @Test
    void anular_boletaEmitida_debeAnularla() {
        MetodoPago boleta = crearBoletaFake();
        boleta.setEstado(MetodoPago.EstadoBoleta.EMITIDA);

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));
        when(boletaRepository.save(any())).thenReturn(boleta);
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(CollectionModel.of(List.of()));

        BoletaResponseDTO resultado = boletaService.anular(boleta.getId());

        assertEquals("ANULADA", resultado.getEstado());
        verify(boletaRepository, times(1)).save(boleta);
    }

    // ─── Test 6: anular boleta ya ANULADA lanza excepción ────────────────────
    @Test
    void anular_boletaYaAnulada_debeLanzarExcepcion() {
        MetodoPago boleta = crearBoletaFake();
        boleta.setEstado(MetodoPago.EstadoBoleta.ANULADA);

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.anular(boleta.getId()));

        assertTrue(ex.getMessage().contains("ya está anulada"));
    }

    // ─── Test 7: emitirBoleta con cliente inexistente lanza excepción ─────────
    @Test
    void emitirBoleta_clienteNoExiste_debeLanzarExcepcion() {
        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(faker.number().randomNumber());
        request.setMetodoPago("EFECTIVO");

        when(clienteClient.existsById(request.getClienteId())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.emitirBoleta(request));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }

    // ─── Test 8: emitirBoleta sin pedidos CONFIRMADOS lanza excepción ─────────
    @Test
    void emitirBoleta_sinPedidosConfirmados_debeLanzarExcepcion() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);

        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(clienteId);
        request.setMetodoPago("DEBITO");

        CarritoDetalleDTO pedidoPendiente = new CarritoDetalleDTO();
        pedidoPendiente.setPedidoId(1L);
        pedidoPendiente.setEstadoPedido("PENDIENTE");
        pedidoPendiente.setClienteId(clienteId);

        when(clienteClient.existsById(clienteId)).thenReturn(true);
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(CollectionModel.of(List.of(pedidoPendiente)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.emitirBoleta(request));

        assertTrue(ex.getMessage().contains("CONFIRMADOS"));
    }

    // ─── Test 9: emitirBoleta con método de pago inválido lanza excepción ─────
    @Test
    void emitirBoleta_metodoPagoInvalido_debeLanzarExcepcion() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        CarritoDetalleDTO carrito = crearCarritoConfirmadoFake(clienteId);

        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(clienteId);
        request.setMetodoPago("BITCOIN");

        when(clienteClient.existsById(clienteId)).thenReturn(true);
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(CollectionModel.of(List.of(carrito)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.emitirBoleta(request));

        assertTrue(ex.getMessage().contains("Método de pago inválido"));
    }

    // ─── Test 10: getBoletasByCliente retorna boletas del cliente ────────────
    @Test
    void getBoletasByCliente_debeRetornarBoletasDelCliente() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        MetodoPago boleta = crearBoletaFake();
        boleta.setClienteId(clienteId);

        when(boletaRepository.findByClienteId(clienteId)).thenReturn(List.of(boleta));
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(CollectionModel.of(List.of()));

        List<BoletaResponseDTO> resultado = boletaService.getBoletasByCliente(clienteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(boletaRepository, times(1)).findByClienteId(clienteId);
    }




}