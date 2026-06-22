package proyecto.cliente.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.repository.ClienteRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        if (repository.count() > 0) {
            log.info(">>> Cliente ya cargados. Se omitira inicializacion.");
            return;
        }

        log.info(">>> Cargando clientes iniciales...");
        repository.save(new Cliente(null, 20600000, "5", "Bruno Mateluna", "br.matadsd@asddc.cl", "Calle quinta 666", 949000000, passwordEncoder.encode("abc123.")));
        repository.save(new Cliente(null, 12345678, "9", "Claudio Bravo", "cl.bravo@chile.cl", "Av.santiago 522", 967342652, passwordEncoder.encode("chile1234")));
        log.info(">>> 2 Clientes cargados OK.");
    }
}
