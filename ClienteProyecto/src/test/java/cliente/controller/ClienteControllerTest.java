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
import proyecto.cliente.feing.CarritoFeingCliente;
import proyecto.cliente.feing.InventarioFeingClient;
import proyecto.cliente.feing.PagoFeingClient;
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

    @Mock
    private CarritoFeingCliente carritoCliente;

    @Mock
    private PagoFeingClient pagoCliente;

    @Mock
    private InventarioFeingClient inventarioClient;

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
    void getClientes_ShouldReturnCollectionWithHateoas() {
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
    void getById_ShouldReturnCliente() {
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
    void getByRun_ShouldReturnOk() {
        when(clienteService.getByRun(responseDTO.getRun())).thenReturn(responseDTO);

        ResponseEntity<?> resultado = clienteController.getByRun(responseDTO.getRun());

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("registrar - Debe retornar 201 con el cliente creado")
    void registrar_ShouldReturn201() {
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
    void login_ShouldReturnOk() {
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
    void update_ShouldReturnUpdatedCliente() {
        when(clienteService.updateCliente(eq(1L), any(ClienteRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<?> resultado = clienteController.update(1L, requestDTO);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("delete - Debe retornar 200 con mensaje de exito")
    void delete_ShouldReturnOk() {
        doNothing().when(clienteService).deleteCliente(1L);

        ResponseEntity<?> resultado = clienteController.delete(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals("Cliente eliminado con exito.", resultado.getBody());
        verify(clienteService, times(1)).deleteCliente(1L);
    }

    @Test
    @DisplayName("exist - Debe retornar true cuando el cliente existe")
    void exist_ShouldReturnTrue() {
        when(clienteService.existsById(1L)).thenReturn(true);

        ResponseEntity<Boolean> resultado = clienteController.exist(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertTrue(resultado.getBody());
    }

    @Test
    @DisplayName("getMisPedidos - Debe delegar en CarritoFeingCliente")
    void getMisPedidos_ShouldReturnPedidos() {
        when(clienteService.getClienteById(1L)).thenReturn(responseDTO);
        when(carritoCliente.getPedidosDelCliente(1L)).thenReturn(List.of());

        ResponseEntity<?> resultado = clienteController.getMisPedidos(1L);

        assertEquals(200, resultado.getStatusCode().value());
        verify(carritoCliente, times(1)).getPedidosDelCliente(1L);
    }

    @Test
    @DisplayName("getStatsMes - Debe delegar stats por mes")
    void getStatsMes_ShouldReturnStats() {
        when(clienteService.getClienteById(1L)).thenReturn(responseDTO);
        when(carritoCliente.getStatsPorMes(1L, 6, 2026)).thenReturn(Map.of());

        ResponseEntity<?> resultado = clienteController.getStatsMes(1L, 6, 2026);

        assertEquals(200, resultado.getStatusCode().value());
        verify(carritoCliente, times(1)).getStatsPorMes(1L, 6, 2026);
    }

    @Test
    @DisplayName("getStatsAnio - Debe delegar stats por anio")
    void getStatsAnio_ShouldReturnStats() {
        when(clienteService.getClienteById(1L)).thenReturn(responseDTO);
        when(carritoCliente.getStatsPorAnio(1L, 2026)).thenReturn(Map.of());

        ResponseEntity<?> resultado = clienteController.getStatsAnio(1L, 2026);

        assertEquals(200, resultado.getStatusCode().value());
        verify(carritoCliente, times(1)).getStatsPorAnio(1L, 2026);
    }

    @Test
    @DisplayName("getMisBoletas - Debe delegar en PagoFeingClient")
    void getMisBoletas_ShouldReturnBoletas() {
        when(clienteService.getClienteById(1L)).thenReturn(responseDTO);
        when(pagoCliente.getBoletasByCliente(1L)).thenReturn(List.of());

        ResponseEntity<?> resultado = clienteController.getMisBoletas(1L);

        assertEquals(200, resultado.getStatusCode().value());
        verify(pagoCliente, times(1)).getBoletasByCliente(1L);
    }

    @Test
    @DisplayName("getCatalogo - Debe delegar en InventarioFeingClient")
    void getCatalogo_ShouldReturnProductos() {
        when(inventarioClient.getProductos()).thenReturn(List.of());

        ResponseEntity<?> resultado = clienteController.getCatalogo();

        assertEquals(200, resultado.getStatusCode().value());
        verify(inventarioClient, times(1)).getProductos();
    }

    @Test
    @DisplayName("getCatalogoByCategoria - Debe filtrar catalogo por categoria")
    void getCatalogoByCategoria_ShouldReturnFiltered() {
        when(inventarioClient.getByCategoria("Lacteos")).thenReturn(List.of());

        ResponseEntity<?> resultado = clienteController.getCatalogoByCategoria("Lacteos");

        assertEquals(200, resultado.getStatusCode().value());
        verify(inventarioClient, times(1)).getByCategoria("Lacteos");
    }
}