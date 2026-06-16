package proyecto.cliente;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;


@Profile("test")//"test" para que funcione el test
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public void run(String... args) throws Exception{
        Faker faker = new Faker();

        for (int i = 0; i < 10; i++){
            Cliente cliente = new Cliente();
            cliente.setRun(faker.number().numberBetween(1,30000000));
            cliente.setDv(faker.number().digit());
            cliente.setNombre(faker.name().fullName());
            cliente.setCorreo(faker.internet().emailAddress());
            cliente.setDireccion(faker.pokemon().location());
            cliente.setTelefono(faker.number().numberBetween(100000000,999999999));
            cliente.setContraseña(faker.internet().emailAddress());
            clienteRepository.save(cliente);
        }
    }

}