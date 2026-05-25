package com.Pago.Metodo_de_pago.Controller;

import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boletas")
public class BoletaController {
    @Autowired
    private BoletaService boletaService;
    @PostMapping
    public ResponseEntity<BoletaResponseDTO> emitir(@Valid @RequestBody BoletaRequestDTO request) {
        return ResponseEntity.status(201).body(boletaService.emitirBoleta(request));
    }

    /** GET /api/v1/boletas  — listar todas */
    @GetMapping
    public ResponseEntity<List<BoletaResponseDTO>> getAll() {
        return ResponseEntity.ok(boletaService.getBoletas());
    }

    /** GET /api/v1/boletas/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.getBoletaById(id));
    }

    /** GET /api/v1/boletas/cliente/{clienteId} */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<BoletaResponseDTO>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(boletaService.getBoletasByCliente(clienteId));
    }

    /** PUT /api/v1/boletas/{id}/anular */
    @PutMapping("/{id}/anular")
    public ResponseEntity<BoletaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.anular(id));
    }
}
