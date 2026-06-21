package proyecto.cliente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;

@Slf4j
@Profile("test")//"test" para que funcione el test
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception{
        Faker faker = new Faker();

        log.info(">>> Generando 10 clientes de prueba con DataFaker...");
        log.info(">>> Credenciales en texto plano (solo para pruebas, no se guardan asi en la BD):");

        for (int i = 0; i < 10; i++){
            String correo = faker.internet().emailAddress();
            String passwordPlano = faker.internet().password();

            Cliente cliente = new Cliente();
            cliente.setRun(faker.number().numberBetween(1,30000000));
            cliente.setDv(faker.number().digit());
            cliente.setNombre(faker.name().fullName());
            cliente.setCorreo(correo);
            cliente.setDireccion(faker.pokemon().location());
            cliente.setTelefono(faker.number().numberBetween(100000000,999999999));
            cliente.setContraseña(passwordEncoder.encode(passwordPlano));
            clienteRepository.save(cliente);

            log.info("    correo: {} | password: {}", correo, passwordPlano);
        }
    }

}