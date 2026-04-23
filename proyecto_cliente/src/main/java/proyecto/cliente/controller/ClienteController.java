package proyecto.cliente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import proyecto.cliente.model.Cliente;
import proyecto.cliente.service.ClienteService;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> getClientes(){
        return clienteService.getClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        try{
            return ResponseEntity.ok(clienteService.getClienteById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{run}")
    public ResponseEntity<?> getByRun(@PathVariable Integer run){
        try{
            return ResponseEntity.ok(clienteService.getByRun(run));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Cliente cliente){
        try{
            return ResponseEntity.status(201).body(clienteService.registrar(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String correo, @RequestParam String contraseña){
        try{
            return ResponseEntity.ok(clienteService.login(correo, contraseña));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Cliente update){
        try{
            return ResponseEntity.ok(clienteService.updateCliente(id, update));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try{
            clienteService.deleteCliente(id);
            return ResponseEntity.ok("Cliente eliminado con exito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exist(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.existsById(id));
    }

}
