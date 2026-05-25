package com.Carrito.Carrito_compras.Controller;

import com.Carrito.Carrito_compras.DTO.CarritoDetalleDTO;
import com.Carrito.Carrito_compras.DTO.CarritoRequestDTO;
import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/Carrito")
public class CarritoController {


    @Autowired
    private CarritoService carritoService;



    @GetMapping
    public ResponseEntity<List<CarritoDetalleDTO>> getTodo() {
        return ResponseEntity.ok(carritoService.getTodo());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoDetalleDTO>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoService.getByCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CarritoDetalleDTO>> getByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(carritoService.getByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<CarritoDetalleDTO> agregar(@Valid @RequestBody CarritoRequestDTO request) {
        return ResponseEntity.status(201).body(carritoService.agregar(request));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<CarritoDetalleDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.confirmar(id));
    }
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CarritoDetalleDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.cancelar(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        carritoService.eliminar(id);
        return ResponseEntity.ok("Ítem eliminado del carrito.");
    }
    @GetMapping("/cliente/{clienteId}/stats")
    public ResponseEntity<?> getEstadisticasMes(@PathVariable Long clienteId,
                                                @RequestParam int mes,
                                                @RequestParam int anio) {
        long total = carritoService.countPedidosPorMes(clienteId, mes, anio);
        return ResponseEntity.ok(Map.of(
                "clienteId", clienteId, "mes", mes, "anio", anio,
                "totalPedidosConfirmados", total
        ));
    }

    @GetMapping("/cliente/{clienteId}/stats/anio")
    public ResponseEntity<?> getEstadisticasAnio(@PathVariable Long clienteId,
                                                 @RequestParam int anio) {
        long total = carritoService.countPedidosPorAnio(clienteId, anio);
        return ResponseEntity.ok(Map.of(
                "clienteId", clienteId, "anio", anio,
                "totalPedidosConfirmados", total
        ));
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<CarritoDetalleDTO> marcarComoPagado(@PathVariable Long id) {
        return ResponseEntity.ok(carritoService.marcarComoPagado(id));
    }
}

