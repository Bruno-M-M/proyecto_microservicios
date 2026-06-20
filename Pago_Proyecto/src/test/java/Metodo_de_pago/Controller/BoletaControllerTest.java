package Metodo_de_pago.Controller;

import com.Pago.Metodo_de_pago.DTO.*;
import com.Pago.Metodo_de_pago.Model.MetodoPago;
import com.Pago.Metodo_de_pago.Repostory.BoletaRepository;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import com.Pago.Metodo_de_pago.Client.CarritoFeingClient;
import com.Pago.Metodo_de_pago.Client.ClienteFeingClient;
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

@SpringBootTest(classes = com.Pago.Metodo_de_pago.MetodoDePagoApplication.class)
public class BoletaControllerTest {
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



    @Test
    void getBoletas_debeRetornarListaDeBoletas() {
        MetodoPago boleta1 = crearBoletaFake();
        MetodoPago boleta2 = crearBoletaFake();

        when(boletaRepository.findAll()).thenReturn(List.of(boleta1, boleta2));
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(List.of());

        List<BoletaResponseDTO> resultado = boletaService.getBoletas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(boletaRepository, times(1)).findAll();
    }

    @Test
    void getBoletas_sinDatos_debeRetornarListaVacia() {
        when(boletaRepository.findAll()).thenReturn(List.of());

        List<BoletaResponseDTO> resultado = boletaService.getBoletas();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void getBoletaById_existente_debeRetornarBoleta() {
        MetodoPago boleta = crearBoletaFake();

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(List.of());

        BoletaResponseDTO resultado = boletaService.getBoletaById(boleta.getId());

        assertNotNull(resultado);
        assertEquals(boleta.getClienteNombre(), resultado.getClienteNombre());
        assertEquals(boleta.getTotalNeto(), resultado.getTotalNeto());
    }

    @Test
    void getBoletaById_noExistente_debeLanzarExcepcion() {
        Long idInexistente = faker.number().randomNumber();

        when(boletaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.getBoletaById(idInexistente));

        assertTrue(ex.getMessage().contains("Boleta no encontrada"));
    }

    @Test
    void getBoletasByCliente_debeRetornarBoletasDelCliente() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        MetodoPago boleta = crearBoletaFake();
        boleta.setClienteId(clienteId);

        when(boletaRepository.findByClienteId(clienteId)).thenReturn(List.of(boleta));
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(List.of());

        List<BoletaResponseDTO> resultado = boletaService.getBoletasByCliente(clienteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(boletaRepository, times(1)).findByClienteId(clienteId);
    }

    @Test
    void getBoletasByCliente_sinBoletas_debeRetornarListaVacia() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);

        when(boletaRepository.findByClienteId(clienteId)).thenReturn(List.of());

        List<BoletaResponseDTO> resultado = boletaService.getBoletasByCliente(clienteId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(boletaRepository, times(1)).findByClienteId(clienteId);
    }



    @Test
    void anular_boletaEmitida_debeAnularla() {
        MetodoPago boleta = crearBoletaFake();
        boleta.setEstado(MetodoPago.EstadoBoleta.EMITIDA);

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));
        when(boletaRepository.save(any())).thenReturn(boleta);
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(List.of());

        BoletaResponseDTO resultado = boletaService.anular(boleta.getId());

        assertEquals("ANULADA", resultado.getEstado());
        verify(boletaRepository, times(1)).save(boleta);
    }

    @Test
    void anular_boletaYaAnulada_debeLanzarExcepcion() {
        MetodoPago boleta = crearBoletaFake();
        boleta.setEstado(MetodoPago.EstadoBoleta.ANULADA);

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.anular(boleta.getId()));

        assertTrue(ex.getMessage().contains("ya está anulada"));
    }

    @Test
    void anular_debeGuardarExactamenteUnaVez() {
        MetodoPago boleta = crearBoletaFake();
        boleta.setEstado(MetodoPago.EstadoBoleta.EMITIDA);

        when(boletaRepository.findById(boleta.getId())).thenReturn(Optional.of(boleta));
        when(boletaRepository.save(any())).thenReturn(boleta);
        when(carritoClient.getPedidosDelCliente(any())).thenReturn(List.of());

        boletaService.anular(boleta.getId());

        verify(boletaRepository, times(1)).save(boleta); // exactamente 1 vez
        verify(boletaRepository, never()).findAll();      // nunca llamó findAll
    }



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
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(List.of(pedidoPendiente));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.emitirBoleta(request));

        assertTrue(ex.getMessage().contains("CONFIRMADOS"));
    }

    @Test
    void emitirBoleta_metodoPagoInvalido_debeLanzarExcepcion() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        CarritoDetalleDTO carrito = crearCarritoConfirmadoFake(clienteId);

        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(clienteId);
        request.setMetodoPago("BITCOIN");

        when(clienteClient.existsById(clienteId)).thenReturn(true);
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(List.of(carrito));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaService.emitirBoleta(request));

        assertTrue(ex.getMessage().contains("Método de pago inválido"));
    }

    @Test
    void emitirBoleta_exitosa_debeGuardarYRetornarBoleta() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        CarritoDetalleDTO carrito = crearCarritoConfirmadoFake(clienteId);

        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(clienteId);
        request.setMetodoPago("EFECTIVO");

        when(clienteClient.existsById(clienteId)).thenReturn(true);
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(List.of(carrito));
        when(boletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletaResponseDTO resultado = boletaService.emitirBoleta(request);

        assertNotNull(resultado);
        assertEquals("EMITIDA", resultado.getEstado());
        assertEquals("EFECTIVO", resultado.getMetodoPago());
        assertTrue(resultado.getTotalConIva() > resultado.getTotalNeto());
        verify(boletaRepository, times(1)).save(any());
    }

    @Test
    void emitirBoleta_exitosa_ivaDebeSer19Porciento() {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        CarritoDetalleDTO carrito = crearCarritoConfirmadoFake(clienteId);

        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(clienteId);
        request.setMetodoPago("TRANSFERENCIA");

        when(clienteClient.existsById(clienteId)).thenReturn(true);
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(List.of(carrito));
        when(boletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletaResponseDTO resultado = boletaService.emitirBoleta(request);

        double ivaEsperado = Math.round(resultado.getTotalNeto() * 0.19 * 100.0) / 100.0;
        assertEquals(ivaEsperado, resultado.getIva(), 0.01,
                "El IVA debe ser exactamente el 19% del total neto");
    }



    @ParameterizedTest
    @ValueSource(strings = {"EFECTIVO", "DEBITO", "CREDITO", "TRANSFERENCIA"})
    void emitirBoleta_todosLosTiposDePagoValidos_debenFuncionar(String tipoPago) {
        Long clienteId = (long) faker.number().numberBetween(1, 100);
        CarritoDetalleDTO carrito = crearCarritoConfirmadoFake(clienteId);

        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(clienteId);
        request.setMetodoPago(tipoPago);

        when(clienteClient.existsById(clienteId)).thenReturn(true);
        when(carritoClient.getPedidosDelCliente(clienteId)).thenReturn(List.of(carrito));
        when(boletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoletaResponseDTO resultado = boletaService.emitirBoleta(request);

        assertNotNull(resultado);
        assertEquals(tipoPago, resultado.getMetodoPago());
    }



    @Test
    void boletaFake_totalConIva_debeSerMayorQueNeto() {
        MetodoPago boleta = crearBoletaFake();

        assertTrue(boleta.getTotalConIva() > boleta.getTotalNeto(),
                "El total con IVA siempre debe ser mayor al neto");
        assertTrue(boleta.getIva() > 0,
                "El IVA no puede ser cero");
    }

    @Test
    void boletaFake_correo_debeContenerArroba() {
        MetodoPago boleta = crearBoletaFake();

        assertTrue(boleta.getClienteCorreo().contains("@"),
                "El correo generado debe tener @");
    }

    @Test
    void boletaFake_telefono_debeEstarEnRangoCorrecto() {
        MetodoPago boleta = crearBoletaFake();

        assertTrue(boleta.getClienteTelefono() >= 900000000,
                "El teléfono debe comenzar con 9");
        assertTrue(boleta.getClienteTelefono() <= 999999999,
                "El teléfono debe tener 9 dígitos");
    }

    @Test
    void boletaFake_estadoInicial_debeSerEmitida() {
        MetodoPago boleta = crearBoletaFake();

        assertEquals(MetodoPago.EstadoBoleta.EMITIDA, boleta.getEstado(),
                "El estado inicial debe ser EMITIDA");
        assertNotEquals(MetodoPago.EstadoBoleta.ANULADA, boleta.getEstado(),
                "No debe estar ANULADA al crearse");
    }

    @Test
    void boletaFake_clienteId_debeSerPositivo() {
        MetodoPago boleta = crearBoletaFake();

        assertTrue(boleta.getClienteId() > 0,
                "El ID del cliente debe ser positivo");
    }
}
