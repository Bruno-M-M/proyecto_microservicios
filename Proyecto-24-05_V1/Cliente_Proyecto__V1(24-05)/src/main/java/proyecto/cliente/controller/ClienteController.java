package proyecto.cliente.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.cliente.dto.ClienteRequestDTO;
import proyecto.cliente.dto.ClienteResponseDTO;
import proyecto.cliente.dto.LoginRequestDTO;
import proyecto.cliente.feing.CarritoFeingCliente;
import proyecto.cliente.service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoFeingCliente carritoCliente;

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





}
