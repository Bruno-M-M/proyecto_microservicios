package Metodo_de_pago.Controller;

import com.Pago.Metodo_de_pago.Controller.BoletaController;
import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoletaControllerTest {

    @Mock
    private BoletaService boletaService;

    @InjectMocks
    private BoletaController boletaController;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    // ─── Helper ────────────────────────────────────────────────────────────────

    private BoletaResponseDTO crearBoletaResponseFake(String estado, String metodoPago) {
        double neto = faker.number().randomDouble(2, 5000, 200000);
        double iva  = Math.round(neto * 0.19 * 100.0) / 100.0;

        BoletaResponseDTO dto = new BoletaResponseDTO();
        dto.setBoletaId(faker.number().numberBetween(1L, 1000L));
        dto.setClienteId((long) faker.number().numberBetween(1, 50));
        dto.setClienteNombre(faker.name().fullName());
        dto.setClienteRun(faker.numerify("########-#"));
        dto.setClienteCorreo(faker.internet().emailAddress());
        dto.setClienteDireccion(faker.address().fullAddress());
        dto.setClienteTelefono(faker.number().numberBetween(900000000, 999999999));
        dto.setMetodoPago(metodoPago);
        dto.setTotalNeto(neto);
        dto.setIva(iva);
        dto.setTotalConIva(Math.round((neto + iva) * 100.0) / 100.0);
        dto.setFechaEmision(LocalDateTime.now());
        dto.setEstado(estado);
        dto.setItems(List.of());
        return dto;
    }

    // ─── emitir ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("emitir: debe retornar 201 con la boleta creada")
    void emitir_debeRetornar201ConBoleta() {
        BoletaResponseDTO boleta = crearBoletaResponseFake("EMITIDA", "EFECTIVO");
        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(boleta.getClienteId());
        request.setMetodoPago("EFECTIVO");

        when(boletaService.emitirBoleta(any())).thenReturn(boleta);

        ResponseEntity<BoletaResponseDTO> respuesta = boletaController.emitir(request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("EMITIDA", respuesta.getBody().getEstado());
        assertEquals("EFECTIVO", respuesta.getBody().getMetodoPago());
        verify(boletaService, times(1)).emitirBoleta(any());
    }

    @Test
    @DisplayName("emitir: debe propagar excepción si el service falla")
    void emitir_debePropagar_ExcepcionDelService() {
        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(1L);
        request.setMetodoPago("EFECTIVO");

        when(boletaService.emitirBoleta(any())).thenThrow(new RuntimeException("Cliente no encontrado"));

        assertThrows(RuntimeException.class, () -> boletaController.emitir(request));
    }

    @ParameterizedTest
    @DisplayName("emitir: debe funcionar con todos los tipos de pago válidos")
    @ValueSource(strings = {"EFECTIVO", "DEBITO", "CREDITO", "TRANSFERENCIA"})
    void emitir_todosLosTiposDePagoValidos(String tipoPago) {
        BoletaResponseDTO boleta = crearBoletaResponseFake("EMITIDA", tipoPago);
        BoletaRequestDTO request = new BoletaRequestDTO();
        request.setClienteId(1L);
        request.setMetodoPago(tipoPago);

        when(boletaService.emitirBoleta(any())).thenReturn(boleta);

        ResponseEntity<BoletaResponseDTO> respuesta = boletaController.emitir(request);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(tipoPago, respuesta.getBody().getMetodoPago());
    }

    // ─── getAll ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAll: debe retornar 200 con lista de boletas")
    void getAll_debeRetornarListaDeBoletas() {
        BoletaResponseDTO b1 = crearBoletaResponseFake("EMITIDA", "EFECTIVO");
        BoletaResponseDTO b2 = crearBoletaResponseFake("ANULADA", "DEBITO");

        when(boletaService.getBoletas()).thenReturn(List.of(b1, b2));

        ResponseEntity<List<BoletaResponseDTO>> respuesta = boletaController.getAll();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(2, respuesta.getBody().size());
        verify(boletaService, times(1)).getBoletas();
    }

    @Test
    @DisplayName("getAll: debe retornar 200 con lista vacía si no hay boletas")
    void getAll_debeRetornarListaVacia() {
        when(boletaService.getBoletas()).thenReturn(List.of());

        ResponseEntity<List<BoletaResponseDTO>> respuesta = boletaController.getAll();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
    }

    // ─── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById: debe retornar 200 con la boleta encontrada")
    void getById_debeRetornarBoleta() {
        BoletaResponseDTO boleta = crearBoletaResponseFake("EMITIDA", "CREDITO");

        when(boletaService.getBoletaById(boleta.getBoletaId())).thenReturn(boleta);

        ResponseEntity<BoletaResponseDTO> respuesta = boletaController.getById(boleta.getBoletaId());

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(boleta.getBoletaId(), respuesta.getBody().getBoletaId());
    }

    @Test
    @DisplayName("getById: debe propagar excepción si no existe")
    void getById_debePropagar_ExcepcionSiNoExiste() {
        when(boletaService.getBoletaById(999L)).thenThrow(new RuntimeException("Boleta no encontrada"));

        assertThrows(RuntimeException.class, () -> boletaController.getById(999L));
    }

    // ─── getByCliente ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("getByCliente: debe retornar 200 con boletas del cliente")
    void getByCliente_debeRetornarBoletasDelCliente() {
        Long clienteId = 5L;
        BoletaResponseDTO boleta = crearBoletaResponseFake("EMITIDA", "TRANSFERENCIA");
        boleta.setClienteId(clienteId);

        when(boletaService.getBoletasByCliente(clienteId)).thenReturn(List.of(boleta));

        ResponseEntity<List<BoletaResponseDTO>> respuesta = boletaController.getByCliente(clienteId);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(clienteId, respuesta.getBody().get(0).getClienteId());
        verify(boletaService, times(1)).getBoletasByCliente(clienteId);
    }

    @Test
    @DisplayName("getByCliente: debe retornar lista vacía si el cliente no tiene boletas")
    void getByCliente_sinBoletas_debeRetornarListaVacia() {
        when(boletaService.getBoletasByCliente(99L)).thenReturn(List.of());

        ResponseEntity<List<BoletaResponseDTO>> respuesta = boletaController.getByCliente(99L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
    }

    // ─── anular ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("anular: debe retornar 200 con estado ANULADA")
    void anular_debeRetornarBoletaAnulada() {
        BoletaResponseDTO anulada = crearBoletaResponseFake("ANULADA", "EFECTIVO");

        when(boletaService.anular(anulada.getBoletaId())).thenReturn(anulada);

        ResponseEntity<BoletaResponseDTO> respuesta = boletaController.anular(anulada.getBoletaId());

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("ANULADA", respuesta.getBody().getEstado());
        verify(boletaService, times(1)).anular(anulada.getBoletaId());
    }

    @Test
    @DisplayName("anular: debe propagar excepción si la boleta ya está anulada")
    void anular_debePropagar_ExcepcionSiYaAnulada() {
        when(boletaService.anular(999L)).thenThrow(new RuntimeException("ya está anulada"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> boletaController.anular(999L));

        assertTrue(ex.getMessage().contains("ya está anulada"));
    }
}