package cliente.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import proyecto.cliente.controller.ClienteController;
import proyecto.cliente.feing.CarritoFeingCliente;
import proyecto.cliente.feing.InventarioFeingClient;
import proyecto.cliente.feing.PagoFeingClient;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;
import proyecto.cliente.service.ClienteService;

import java.lang.ref.Cleaner;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = proyecto.cliente.ClienteApplication.class)
public class ClienteControllerTest {

    @Autowired
    private ClienteController clienteController;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarritoFeingCliente carritoFeingCliente;

    @Autowired
    private InventarioFeingClient inventarioFeingClient;

    @Autowired
    private PagoFeingClient pagoFeingClient;

    private Faker faker;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClienteService clienteService;

    @BeforeEach
    void setUp(){ faker = new Faker();}

    private Cliente agregarClienteFake(){
        String passwordPlano = faker.text().text(7,14,true,true,true);
        Cliente cliente = new Cliente();
        cliente.setId(faker.number().randomNumber());
        cliente.setRun(faker.number().numberBetween(7,8));
        cliente.setDv(faker.number().digit());
        cliente.setNombre(faker.pokemon().name());
        cliente.setCorreo(faker.internet().emailAddress());
        cliente.setDireccion(faker.address().fullAddress());
        cliente.setTelefono(faker.number().numberBetween(9,9));
        cliente.setContraseña(passwordEncoder.encode(passwordPlano));

        return cliente;
    }

    @Test
    void getClientes(){
        Cliente cl1 = agregarClienteFake();
        Cliente cl2 = agregarClienteFake();

        when(clienteRepository.findAll()).thenReturn(List.of(cl1, cl2));

        var resultado = clienteService.getClientes();

        assertNotNull(resultado);
        assertEquals(2,resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void getById(){
        Cliente cliente = agregarClienteFake();
        Long clienteId = cliente.getId();

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        var resultado = clienteService.getClienteById(clienteId);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).findById(clienteId);
    }

    @Test
    void getByRun(){
        Cliente cliente = agregarClienteFake();
        Integer clienteRun = cliente.getRun();

        when(clienteRepository.findByRun(clienteRun)).thenReturn(Optional.of(cliente));

        var resultado = clienteService.getByRun(clienteRun);

        assertNotNull(resultado);
        verify(clienteRepository, times(1)).findByRun(clienteRun);

    }

    @Test
    void registrar(){
        Cliente cliente = agregarClienteFake();

        when(clienteRepository.save(cliente)).thenReturn(cliente);

        assertNotNull(cliente);
        verify(clienteRepository, times(1)).save(cliente);

    }

    @Test
    void login(){
        Cliente cliente = agregarClienteFake();
        String correo = cliente.getCorreo();
        String token = clienteService.login(cliente);    ///PENDIENTE

        when(clienteRepository.findByCorreo(correo)).thenReturn(cliente);

        assertNotNull(cliente);
        verify(clienteService, times(1)).login(cliente);

    }

    @Test
    void update(){
        Cliente cliente = agregarClienteFake();
        Cliente nuevo = agregarClienteFake();
        cliente.setRun(nuevo.getRun());
        cliente.setDv(nuevo.getDv());
        cliente.setNombre(nuevo.getNombre());
        cliente.setCorreo(nuevo.getCorreo());
        cliente.setDireccion(nuevo.getDireccion());
        cliente.setTelefono(nuevo.getTelefono());
        cliente.setContraseña(nuevo.getContraseña());

        when(clienteRepository.save(cliente)).thenReturn(cliente);   ///revisar

        assertNotNull(cliente);
        verify(clienteService, times(1)).updateCliente(cliente, nuevo)
    }

    @Test
    void delete(){
        Cliente cliente = agregarClienteFake();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        clienteService.deleteCliente(cliente.getId());

        verify(clienteRepository, times(1)).deleteById(cliente.getId());

    }

    @Test
    void getMisPedidos(){
        Cliente cliente = agregarClienteFake();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        clienteService.getClienteById(cliente.getId());

        verify(carritoFeingCliente.getPedidosDelCliente(cliente.getId()));
    }

    @Test
    void getStatsMes(){
        Cliente cliente = agregarClienteFake();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        clienteService.getClienteById(cliente.getId());

        verify(carritoFeingCliente.getStatsPorMes(cliente.getId(), faker.number().numberBetween(1,12),faker.number().numberBetween(1980,2026)));
    }

    @Test
    void getStatsAnio(){
        Cliente cliente = agregarClienteFake();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        clienteService.getClienteById(cliente.getId());

        verify(carritoFeingCliente.getStatsPorAnio(cliente.getId(), faker.number().numberBetween(1980,2026)), times(1));
    }

    @Test
    void getMisBoletas(){
        Cliente cliente = agregarClienteFake();

        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        clienteService.getClienteById(cliente.getId());

        verify(pagoFeingClient.getBoletasByCliente(cliente.getId()), times(1));
    }
}
