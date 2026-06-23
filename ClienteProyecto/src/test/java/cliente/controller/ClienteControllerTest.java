package cliente.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import proyecto.cliente.assemblers.ClienteModelAssembler;
import proyecto.cliente.controller.ClienteController;
import proyecto.cliente.dto.ClienteRequestDTO;
import proyecto.cliente.dto.ClienteResponseDTO;
import proyecto.cliente.dto.LoginRequestDTO;
import proyecto.cliente.service.ClienteService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private ClienteModelAssembler assembler;

    @InjectMocks
    private ClienteController clienteController;

    private final Faker faker = new Faker();
    private ClienteResponseDTO responseDTO;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRun(faker.number().numberBetween(10000000, 25000000));
        responseDTO.setDv(String.valueOf(faker.number().digit()));
        responseDTO.setNombre(faker.name().fullName());
        responseDTO.setCorreo(faker.internet().emailAddress());
        responseDTO.setDireccion(faker.address().fullAddress());
        responseDTO.setTelefono(faker.number().numberBetween(900000000, 999999999));

        requestDTO = new ClienteRequestDTO();
        requestDTO.setRun(responseDTO.getRun());
        requestDTO.setDv(responseDTO.getDv());
        requestDTO.setNombre(responseDTO.getNombre());
        requestDTO.setCorreo(responseDTO.getCorreo());
        requestDTO.setDireccion(responseDTO.getDireccion());
        requestDTO.setTelefono(responseDTO.getTelefono());
        requestDTO.setContrasenia("clave123");
    }


    @Test
    @DisplayName("getClientes - Debe retornar CollectionModel con HATEOAS")
    void getClientes_ReturnCollectionWithHateoas() {
        when(clienteService.getClientes()).thenReturn(List.of(responseDTO));
        when(assembler.toModel(any(ClienteResponseDTO.class)))
                .thenReturn(EntityModel.of(responseDTO));

        CollectionModel<EntityModel<ClienteResponseDTO>> resultado = clienteController.getClientes();

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertTrue(resultado.getLink("self").isPresent());
    }


    @Test
    @DisplayName("getById - Debe retornar un cliente envuelto en EntityModel")
    void getById_ReturnCliente() {
        when(clienteService.getClienteById(1L)).thenReturn(responseDTO);
        when(assembler.toModel(responseDTO)).thenReturn(EntityModel.of(responseDTO));

        EntityModel<ClienteResponseDTO> resultado = clienteController.getById(1L);

        assertNotNull(resultado);
        assertEquals(responseDTO.getNombre(), resultado.getContent().getNombre());
    }

    @Test
    @DisplayName("getById - Cliente inexistente debe propagar la excepcion")
    void getById_WhenNotFound_ShouldThrow() {
        when(clienteService.getClienteById(99L))
                .thenThrow(new RuntimeException("Cliente no encontrado: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.getById(99L));

        assertEquals("Cliente no encontrado: 99", ex.getMessage());
    }


    @Test
    @DisplayName("getByRun - Debe retornar 200 con el cliente encontrado")
    void getByRun_ReturnOk() {
        when(clienteService.getByRun(responseDTO.getRun())).thenReturn(responseDTO);

        ResponseEntity<?> resultado = clienteController.getByRun(responseDTO.getRun());

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("registrar - Debe retornar 201 con el cliente creado")
    void registrar_Return201() {
        when(clienteService.registrar(any(ClienteRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<?> resultado = clienteController.registrar(requestDTO);

        assertEquals(201, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("registrar - Correo ya registrado debe propagar la excepcion")
    void registrar_WhenEmailAlreadyExists_ShouldThrow() {
        when(clienteService.registrar(any(ClienteRequestDTO.class)))
                .thenThrow(new RuntimeException("Ya existe un cliente con este email."));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.registrar(requestDTO));

        assertEquals("Ya existe un cliente con este email.", ex.getMessage());
    }


    @Test
    @DisplayName("login - Credenciales correctas debe retornar 200")
    void login_ReturnOk() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo(responseDTO.getCorreo());
        loginDTO.setContrasenia("clave123");

        when(clienteService.login(loginDTO)).thenReturn(responseDTO);

        ResponseEntity<?> resultado = clienteController.login(loginDTO);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("login - Credenciales incorrectas debe propagar la excepcion")
    void login_WithWrongCredentials_ShouldThrow() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo(responseDTO.getCorreo());
        loginDTO.setContrasenia("incorrecta");

        when(clienteService.login(loginDTO))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.login(loginDTO));

        assertEquals("Contraseña incorrecta", ex.getMessage());
    }


    @Test
    @DisplayName("update - Debe retornar 200 con el cliente actualizado")
    void update_ReturnUpdatedCliente() {
        when(clienteService.updateCliente(eq(1L), any(ClienteRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<?> resultado = clienteController.update(1L, requestDTO);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }


    @Test
    @DisplayName("delete - Debe retornar 200 con mensaje de exito")
    void delete_ReturnOk() {
        doNothing().when(clienteService).deleteCliente(1L);

        ResponseEntity<?> resultado = clienteController.delete(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals("Cliente eliminado con exito.", resultado.getBody());
        verify(clienteService, times(1)).deleteCliente(1L);
    }


    @Test
    @DisplayName("exist - Debe retornar true cuando el cliente existe")
    void exist_ReturnTrue() {
        when(clienteService.existsById(1L)).thenReturn(true);

        ResponseEntity<Boolean> resultado = clienteController.exist(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertTrue(resultado.getBody());
    }

    @Test
    @DisplayName("exist - Debe retornar false cuando el cliente no existe")
    void exist_ReturnFalse() {
        when(clienteService.existsById(99L)).thenReturn(false);

        ResponseEntity<Boolean> resultado = clienteController.exist(99L);

        assertEquals(200, resultado.getStatusCode().value());
        assertFalse(resultado.getBody());
    }


    @Test
    @DisplayName("getMisPedidos - Debe delegar en el service y retornar 200")
    void getMisPedidos_ReturnPedidos() {
        Map<String, Object> pedidosFake = Map.of("total", 3);
        when(clienteService.getMisPedidos(1L)).thenReturn(pedidosFake);

        ResponseEntity<?> resultado = clienteController.getMisPedidos(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(pedidosFake, resultado.getBody());
        verify(clienteService, times(1)).getMisPedidos(1L);
    }

    @Test
    @DisplayName("getMisPedidos - Cliente inexistente debe propagar la excepcion")
    void getMisPedidos_WhenNotFound_ShouldThrow() {
        when(clienteService.getMisPedidos(99L))
                .thenThrow(new RuntimeException("Cliente no encontrado: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.getMisPedidos(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }


    @Test
    @DisplayName("getStatsAnio - Debe delegar en el service y retornar 200")
    void getStatsAnio_ReturnStats() {
        Map<String, Object> statsFake = Map.of("totalPedidos", 12);
        when(clienteService.getStatsAnio(1L, 2026)).thenReturn(statsFake);

        ResponseEntity<?> resultado = clienteController.getStatsAnio(1L, 2026);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(statsFake, resultado.getBody());
        verify(clienteService, times(1)).getStatsAnio(1L, 2026);
    }

    @Test
    @DisplayName("getStatsAnio - Cliente inexistente debe propagar la excepcion")
    void getStatsAnio_WhenNotFound_ShouldThrow() {
        when(clienteService.getStatsAnio(99L, 2026))
                .thenThrow(new RuntimeException("Cliente no encontrado: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.getStatsAnio(99L, 2026));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }


    @Test
    @DisplayName("getMisBoletas - Debe delegar en el service y retornar 200")
    void getMisBoletas_ReturnBoletas() {
        List<Object> boletasFake = List.of(Map.of("id", 1), Map.of("id", 2));
        when(clienteService.getMisBoletas(1L)).thenReturn(boletasFake);

        ResponseEntity<?> resultado = clienteController.getMisBoletas(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(boletasFake, resultado.getBody());
        verify(clienteService, times(1)).getMisBoletas(1L);
    }

    @Test
    @DisplayName("getMisBoletas - Cliente inexistente debe propagar la excepcion")
    void getMisBoletas_WhenNotFound_ShouldThrow() {
        when(clienteService.getMisBoletas(99L))
                .thenThrow(new RuntimeException("Cliente no encontrado: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.getMisBoletas(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }


    @Test
    @DisplayName("getResumen - Debe delegar en el service y retornar 200")
    void getResumen_ReturnResumen() {
        Map<String, Object> resumenFake = Map.of(
                "clienteId", 1L,
                "anio", 2026,
                "statsAnio", Map.of("totalPedidos", 5),
                "boletas", List.of()
        );
        when(clienteService.getResumen(1L, 2026)).thenReturn(resumenFake);

        ResponseEntity<?> resultado = clienteController.getResumen(1L, 2026);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(resumenFake, resultado.getBody());
        verify(clienteService, times(1)).getResumen(1L, 2026);
    }

    @Test
    @DisplayName("getResumen - Cliente inexistente debe propagar la excepcion")
    void getResumen_WhenNotFound_ShouldThrow() {
        when(clienteService.getResumen(99L, 2026))
                .thenThrow(new RuntimeException("Cliente no encontrado: 99"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteController.getResumen(99L, 2026));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }
}