package proyecto.cliente.feing;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.lang.Object;
import java.util.List;
import java.util.Map;

@FeignClient(name = "carrito", url = "${microservicio.carrito.url}")
public interface CarritoFeingCliente {

    @GetMapping("/cliente/{clienteId}/stats")
    Map<String , Object> getStatsPorMes(@PathVariable Long clienteId,
                                        @RequestParam int mes,
                                        @RequestParam int anio);

    @GetMapping("/cliente/{clienteId}/stats/anio")
    Map<String , Object> getStatsPorAnio(@PathVariable Long clienteId,
                                          @RequestParam int anio);

    @GetMapping("/cliente/{clienteId}")
    List<Object> getPedidosDelCliente(@PathVariable Long clienteId);

}
