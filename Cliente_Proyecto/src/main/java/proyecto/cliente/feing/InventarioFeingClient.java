package proyecto.cliente.feing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "inventario-desde-cliente", url = "${microservicio.inventario.url}")
public interface InventarioFeingClient {

    @GetMapping
    List<Object> getProductos();

    @GetMapping("/categoria/{categoria}")
    List<Object> getByCategoria(@PathVariable("categoria") String categoria);
}