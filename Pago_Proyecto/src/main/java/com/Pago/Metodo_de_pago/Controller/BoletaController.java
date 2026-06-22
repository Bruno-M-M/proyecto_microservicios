package com.Pago.Metodo_de_pago.Controller;

import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(
            summary = "Emitir una boleta",
            description = "Genera una nueva boleta de pago a partir de los datos del pedido. Calcula el IVA automáticamente"
    )
    @ApiResponses(
            {@ApiResponse(
                    responseCode = "201",
                    description = "Boleta emitida con exito",
                    content = @Content(mediaType = "application ")

            )}
    )
    @PostMapping
    public ResponseEntity<BoletaResponseDTO> emitir(@Valid @RequestBody BoletaRequestDTO request) {
        return ResponseEntity.status(201).body(boletaService.emitirBoleta(request));
    }

    @GetMapping
    public ResponseEntity<List<BoletaResponseDTO>> getAll() {
        return ResponseEntity.ok(boletaService.getBoletas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.getBoletaById(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<BoletaResponseDTO>> getByCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(boletaService.getBoletasByCliente(clienteId));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<BoletaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(boletaService.anular(id));
    }
}
