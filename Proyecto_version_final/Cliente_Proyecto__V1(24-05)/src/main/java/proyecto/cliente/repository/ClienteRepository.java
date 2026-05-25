package proyecto.cliente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.cliente.model.Cliente;

import java.util.Optional;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByRun(Integer run);
    boolean existsByCorreo(String correo);
    Cliente findByCorreo(String correo);
}
