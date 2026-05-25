package proyecto.cliente.feing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;


@FeignClient(name = "pago", url = "${microservicio.pago.url}")
public interface PagoFeingClient {

    @GetMapping("/cliente/{clienteId}")
    List<Object> getBoletasByCliente(@PathVariable("clienteId") Long clienteId);
}