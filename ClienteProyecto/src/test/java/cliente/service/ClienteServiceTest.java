package cliente.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import proyecto.cliente.dto.ClienteMapper;
import proyecto.cliente.dto.ClienteRequestDTO;
import proyecto.cliente.dto.ClienteResponseDTO;
import proyecto.cliente.dto.LoginRequestDTO;
import proyecto.cliente.feing.CarritoFeingCliente;
import proyecto.cliente.feing.InventarioFeingClient;
import proyecto.cliente.feing.PagoFeingClient;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;
import proyecto.cliente.service.ClienteService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CarritoFeingCliente carritoCliente;

    @Mock
    private PagoFeingClient pagoCliente;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;
    private ClienteResponseDTO responseDTO;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, 206888807, "5", "Bruno Mateluna",
                "br.mateluna@duocuc.cl", "Calle quinta vergara 666", 949000000,
                "hashEncriptado123");

        responseDTO = new ClienteResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setRun(206888807);
        responseDTO.setDv("5");
        responseDTO.setNombre("Bruno Mateluna");
        responseDTO.setCorreo("br.mateluna@duocuc.cl");
        responseDTO.setDireccion("Calle quinta vergara 666");
        responseDTO.setTelefono(949000000);

        requestDTO = new ClienteRequestDTO();
        requestDTO.setRun(206888807);
        requestDTO.setDv("5");
        requestDTO.setNombre("Bruno Mateluna");
        requestDTO.setCorreo("br.mateluna@duocuc.cl");
        requestDTO.setDireccion("Calle quinta vergara 666");
        requestDTO.setTelefono(949000000);
        requestDTO.setContrasenia("abc123.");
    }

    @Test
    void getClientes_deberiaRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);

        List<ClienteResponseDTO> resultado = clienteService.getClientes();

        assertEquals(1, resultado.size());
        assertEquals("Bruno Mateluna", resultado.get(0).getNombre());
    }


    @Test
    void getClienteById_cuandoExiste_deberiaRetornarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.getClienteById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void getClienteById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.getClienteById(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
    }


    @Test
    void getByRun_cuandoExiste_deberiaRetornarCliente() {
        when(clienteRepository.findByRun(206888807)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.getByRun(206888807);

        assertEquals(206888807, resultado.getRun());
    }

    @Test
    void getByRun_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findByRun(99999999)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.getByRun(99999999));

        assertTrue(ex.getMessage().contains("RUN"));
    }


    @Test
    void registrar_cuandoCorreoNoExiste_deberiaRegistrarCliente() {
        when(clienteRepository.existsByCorreo(requestDTO.getCorreo())).thenReturn(false);
        when(mapper.toEntity(requestDTO)).thenReturn(cliente);
        when(passwordEncoder.encode(requestDTO.getContrasenia())).thenReturn("hashEncriptado123");
        when(clienteRepository.save(cliente)).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.registrar(requestDTO);

        assertNotNull(resultado);
        assertEquals("Bruno Mateluna", resultado.getNombre());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    void registrar_cuandoCorreoYaExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.existsByCorreo(requestDTO.getCorreo())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.registrar(requestDTO));

        assertTrue(ex.getMessage().contains("Ya existe un cliente"));
        verify(clienteRepository, never()).save(any(Cliente.class));
    }


    @Test
    void login_cuandoCredencialesCorrectas_deberiaRetornarCliente() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo("br.mateluna@duocuc.cl");
        loginDTO.setContrasenia("abc123.");

        when(clienteRepository.findByCorreo(loginDTO.getCorreo())).thenReturn(cliente);
        when(passwordEncoder.matches(loginDTO.getContrasenia(), cliente.getContrasenia())).thenReturn(true);
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.login(loginDTO);

        assertNotNull(resultado);
        assertEquals("br.mateluna@duocuc.cl", resultado.getCorreo());
    }

    @Test
    void login_cuandoCorreoNoRegistrado_deberiaLanzarExcepcion() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo("noexiste@correo.cl");
        loginDTO.setContrasenia("abc123.");

        when(clienteRepository.findByCorreo(loginDTO.getCorreo())).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.login(loginDTO));

        assertTrue(ex.getMessage().contains("Correo no registrado"));
    }

    @Test
    void login_cuandoContraseñaIncorrecta_deberiaLanzarExcepcion() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo("br.mateluna@duocuc.cl");
        loginDTO.setContrasenia("incorrecta");

        when(clienteRepository.findByCorreo(loginDTO.getCorreo())).thenReturn(cliente);
        when(passwordEncoder.matches(loginDTO.getContrasenia(), cliente.getContrasenia())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.login(loginDTO));

        assertTrue(ex.getMessage().contains("Contraseña incorrecta"));
    }


    @Test
    void updateCliente_cuandoExiste_deberiaActualizarYRetornarCliente() {
        ClienteRequestDTO update = new ClienteRequestDTO();
        update.setRun(206888807);
        update.setDv("5");
        update.setNombre("Bruno Mateluna Actualizado");
        update.setCorreo("nuevo@correo.cl");
        update.setDireccion("Nueva direccion 123");
        update.setTelefono(912345678);
        update.setContrasenia("nuevaPass1");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.encode(update.getContrasenia())).thenReturn("nuevoHash");
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            ClienteResponseDTO dto = new ClienteResponseDTO();
            dto.setId(c.getId());
            dto.setNombre(c.getNombre());
            dto.setCorreo(c.getCorreo());
            return dto;
        });

        ClienteResponseDTO resultado = clienteService.updateCliente(1L, update);

        assertEquals("Bruno Mateluna Actualizado", resultado.getNombre());
        assertEquals("nuevo@correo.cl", resultado.getCorreo());
    }


    @Test
    void deleteCliente_cuandoExiste_deberiaEliminarCliente() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        clienteService.deleteCliente(1L);

        verify(clienteRepository, times(1)).deleteById(1L);
    }


    @Test
    void existsById_cuandoExiste_deberiaRetornarTrue() {
        when(clienteRepository.existsById(1L)).thenReturn(true);

        assertTrue(clienteService.existsById(1L));
    }

    @Test
    void existsById_cuandoNoExiste_deberiaRetornarFalse() {
        when(clienteRepository.existsById(99L)).thenReturn(false);

        assertFalse(clienteService.existsById(99L));
    }

    @Test
    void getMisPedidos_cuandoClienteExiste_deberiaRetornarPedidos() {
        Map<String, Object> pedidosFake = Map.of("total", 3, "estado", "PENDIENTE");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);
        when(carritoCliente.getPedidosDelCliente(1L)).thenReturn(pedidosFake);

        Map<String, Object> resultado = clienteService.getMisPedidos(1L);

        assertNotNull(resultado);
        assertEquals(3, resultado.get("total"));
        verify(carritoCliente, times(1)).getPedidosDelCliente(1L);
    }

    @Test
    void getMisPedidos_cuandoClienteNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.getMisPedidos(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(carritoCliente, never()).getPedidosDelCliente(any());
    }


    @Test
    void getStatsAnio_cuandoClienteExiste_deberiaRetornarEstadisticas() {
        Map<String, Object> statsFake = Map.of("anio", 2024, "totalPedidos", 12);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);
        when(carritoCliente.getStatsPorAnio(1L, 2024)).thenReturn(statsFake);

        Map<String, Object> resultado = clienteService.getStatsAnio(1L, 2024);

        assertNotNull(resultado);
        assertEquals(12, resultado.get("totalPedidos"));
        verify(carritoCliente, times(1)).getStatsPorAnio(1L, 2024);
    }

    @Test
    void getStatsAnio_cuandoClienteNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.getStatsAnio(99L, 2024));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(carritoCliente, never()).getStatsPorAnio(any(), anyInt());
    }


    @Test
    void getMisBoletas_cuandoClienteExiste_deberiaRetornarBoletas() {
        List<Object> boletasFake = List.of(
                Map.of("id", 1, "total", 15000),
                Map.of("id", 2, "total", 8500)
        );

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);
        when(pagoCliente.getBoletasByCliente(1L)).thenReturn(boletasFake);

        List<Object> resultado = clienteService.getMisBoletas(1L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pagoCliente, times(1)).getBoletasByCliente(1L);
    }

    @Test
    void getMisBoletas_cuandoClienteNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.getMisBoletas(99L));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(pagoCliente, never()).getBoletasByCliente(any());
    }


    @Test
    void getResumen_cuandoClienteExiste_deberiaRetornarResumenCompleto() {
        Map<String, Object> statsFake = Map.of("totalPedidos", 5);
        List<Object> boletasFake = List.of(Map.of("id", 1, "total", 20000));

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);
        when(carritoCliente.getStatsPorAnio(eq(1L), anyInt())).thenReturn(statsFake);
        when(pagoCliente.getBoletasByCliente(1L)).thenReturn(boletasFake);

        Map<String, Object> resultado = clienteService.getResumen(1L, 2024);

        assertNotNull(resultado);
        assertEquals(1L, resultado.get("clienteId"));
        assertEquals(2024, resultado.get("anio"));
        assertNotNull(resultado.get("statsAnio"));
        assertNotNull(resultado.get("boletas"));
    }

    @Test
    void getResumen_cuandoAnioEsCero_deberiaUsarAnioActual() {
        int anioActual = java.time.LocalDateTime.now().getYear();
        Map<String, Object> statsFake = Map.of("totalPedidos", 3);
        List<Object> boletasFake = List.of();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);
        when(carritoCliente.getStatsPorAnio(1L, anioActual)).thenReturn(statsFake);
        when(pagoCliente.getBoletasByCliente(1L)).thenReturn(boletasFake);

        Map<String, Object> resultado = clienteService.getResumen(1L, 0);

        assertEquals(anioActual, resultado.get("anio"));
        verify(carritoCliente, times(1)).getStatsPorAnio(1L, anioActual);
    }

    @Test
    void getResumen_cuandoClienteNoExiste_deberiaLanzarExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.getResumen(99L, 2024));

        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(carritoCliente, never()).getStatsPorAnio(any(), anyInt());
        verify(pagoCliente, never()).getBoletasByCliente(any());
    }
}