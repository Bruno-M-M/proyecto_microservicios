package proyecto.cliente.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.cliente.dto.ClienteRequestDTO;
import proyecto.cliente.dto.ClienteResponseDTO;
import proyecto.cliente.dto.LoginRequestDTO;
import proyecto.cliente.feing.CarritoFeingCliente;
import proyecto.cliente.feing.InventarioFeingClient;
import proyecto.cliente.feing.PagoFeingClient;
import proyecto.cliente.service.ClienteService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoFeingCliente carritoCliente;

    @Autowired
    private PagoFeingClient pagoCliente;

    @Autowired
    private InventarioFeingClient inventarioClient;

    @GetMapping
    public List<ClienteResponseDTO> getClientes(){
        return clienteService.getClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.getClienteById(id));
    }

    @GetMapping("/run/{run}")
    public ResponseEntity<?> getByRun(@PathVariable Integer run){
        return ResponseEntity.ok(clienteService.getByRun(run));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@Valid @RequestBody ClienteRequestDTO dto){
        return ResponseEntity.status(201).body(clienteService.registrar(dto));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(clienteService.login(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClienteRequestDTO dto){
        return ResponseEntity.status(200).body(clienteService.updateCliente(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        clienteService.deleteCliente(id);
        return ResponseEntity.ok("Cliente eliminado con exito.");
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exist(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.existsById(id));
    }

    @GetMapping("/{id}/mis-pedidos")
    public ResponseEntity<?> getMisPedidos(@PathVariable Long id){
        clienteService.getClienteById(id);
        return ResponseEntity.ok(carritoCliente.getPedidosDelCliente(id));
    }

    @GetMapping("/{id}/mis-pedidos/stats")
    public ResponseEntity<?> getStatsMes(@PathVariable Long id,
                                         @RequestParam int mes,
                                         @RequestParam int anio){
        clienteService.getClienteById(id);
        return ResponseEntity.ok(carritoCliente.getStatsPorMes(id,mes,anio));
    }

    @GetMapping("/{id}/mis-pedidos/stats/anio")
    public ResponseEntity<?> getStatsAnio(@PathVariable Long id,
                                          @RequestParam int anio){
        clienteService.getClienteById(id);
        return ResponseEntity.ok(carritoCliente.getStatsPorAnio(id,anio));
    }

    @GetMapping("/{id}/mis-boletas")
    public ResponseEntity<?> getMisBoletas(@PathVariable Long id) {
        clienteService.getClienteById(id);
        return ResponseEntity.ok(pagoCliente.getBoletasByCliente(id));
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<?> getResumen(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "0") int mes,
            @RequestParam(required = false, defaultValue = "0") int anio) {

        clienteService.getClienteById(id);

        int anioFinal = anio == 0 ? java.time.LocalDateTime.now().getYear() : anio;
        int mesFinal  = mes  == 0 ? java.time.LocalDateTime.now().getMonthValue() : mes;

        Object pedidosMes  = carritoCliente.getStatsPorMes(id, mesFinal, anioFinal);
        Object pedidosAnio = carritoCliente.getStatsPorAnio(id, anioFinal);
        Object boletas     = pagoCliente.getBoletasByCliente(id);

        return ResponseEntity.ok(Map.of(
                "clienteId",    id,
                "mes",          mesFinal,
                "anio",         anioFinal,
                "statsMes",     pedidosMes,
                "statsAnio",    pedidosAnio,
                "boletas",      boletas
        ));
    }

    @GetMapping("/catalogo")
    public ResponseEntity<?> getCatalogo() {
        return ResponseEntity.ok(inventarioClient.getProductos());
    }

    @GetMapping("/catalogo/categoria/{categoria}")
    public ResponseEntity<?> getCatalogoByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(inventarioClient.getByCategoria(categoria));
    }

}
