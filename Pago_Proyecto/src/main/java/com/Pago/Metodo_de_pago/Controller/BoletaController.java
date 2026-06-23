package com.Pago.Metodo_de_pago.Controller;

import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            summary = "Emitir una boleta / Realizar Pago",
            description = "Genera una boleta y marca los pedidos como PAGADOS en el Carrito. " +
                    "Este es el único endpoint permitido para realizar pagos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Boleta emitida y pago procesado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pedidos no confirmados")
    })
    @PostMapping
    public ResponseEntity<BoletaResponseDTO> emitir(@Valid @RequestBody BoletaRequestDTO request) {
        return ResponseEntity.status(201).body(boletaService.emitirBoleta(request));
    }

    @Operation(summary = "Listar todas las boletas")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<BoletaResponseDTO>> getAll() {
        return ResponseEntity.ok(boletaService.getBoletas());
    }

    @Operation(summary = "Obtener boleta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleta encontrada"),
            @ApiResponse(responseCode = "404", description = "Boleta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BoletaResponseDTO> getById(
            @Parameter(description = "ID de la boleta") @PathVariable Long id) {
        return ResponseEntity.ok(boletaService.getBoletaById(id));
    }

    @Operation(summary = "Obtener boletas por cliente")
    @ApiResponse(responseCode = "200", description = "Lista de boletas del cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<BoletaResponseDTO>> getByCliente(
            @Parameter(description = "ID del cliente") @PathVariable Long clienteId) {
        return ResponseEntity.ok(boletaService.getBoletasByCliente(clienteId));
    }

    @Operation(summary = "Anular boleta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Boleta anulada correctamente"),
            @ApiResponse(responseCode = "404", description = "Boleta no encontrada")
    })
    @PutMapping("/{id}/anular")
    public ResponseEntity<BoletaResponseDTO> anular(
            @Parameter(description = "ID de la boleta a anular") @PathVariable Long id) {
        return ResponseEntity.ok(boletaService.anular(id));
    }



}
