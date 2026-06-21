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
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;
import proyecto.cliente.service.ClienteService;

import java.util.List;
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
        requestDTO.setContraseña("abc123.");
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
        when(passwordEncoder.encode(requestDTO.getContraseña())).thenReturn("hashEncriptado123");
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
        loginDTO.setContraseña("abc123.");

        when(clienteRepository.findByCorreo(loginDTO.getCorreo())).thenReturn(cliente);
        when(passwordEncoder.matches(loginDTO.getContraseña(), cliente.getContraseña())).thenReturn(true);
        when(mapper.toResponse(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = clienteService.login(loginDTO);

        assertNotNull(resultado);
        assertEquals("br.mateluna@duocuc.cl", resultado.getCorreo());
    }

    @Test
    void login_cuandoCorreoNoRegistrado_deberiaLanzarExcepcion() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo("noexiste@correo.cl");
        loginDTO.setContraseña("abc123.");

        when(clienteRepository.findByCorreo(loginDTO.getCorreo())).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.login(loginDTO));

        assertTrue(ex.getMessage().contains("Correo no registrado"));
    }

    @Test
    void login_cuandoContraseñaIncorrecta_deberiaLanzarExcepcion() {
        LoginRequestDTO loginDTO = new LoginRequestDTO();
        loginDTO.setCorreo("br.mateluna@duocuc.cl");
        loginDTO.setContraseña("incorrecta");

        when(clienteRepository.findByCorreo(loginDTO.getCorreo())).thenReturn(cliente);
        when(passwordEncoder.matches(loginDTO.getContraseña(), cliente.getContraseña())).thenReturn(false);

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
        update.setContraseña("nuevaPass1");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(passwordEncoder.encode(update.getContraseña())).thenReturn("nuevoHash");
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
}