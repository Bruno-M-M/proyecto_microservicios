package com.Pago.Metodo_de_pago.Controller;

import com.Pago.Metodo_de_pago.DTO.BoletaRequestDTO;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import com.Pago.Metodo_de_pago.Service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
            description = "Genera una nueva boleta de pago a partir de los datos del pedido. Calcula el IVA automáticamente y marca los pedidos como PAGADOS en Carrito."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Boleta emitida con éxito",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BoletaResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o pedidos no confirmados",
                    content = @Content(mediaType = "application/json")
            )
    })
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



    @Operation(
            summary = "Notificar confirmación de pedido desde Carrito",
            description = "Endpoint interno llamado por el microservicio Carrito cuando un pedido pasa a estado CONFIRMADO. " +
                    "Esto habilita el pedido para ser facturado/pagado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notificación recibida correctamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos"
            )
    })
    @PostMapping("/notificar-confirmacion")
    public ResponseEntity<String> notificarConfirmacion(
            @Parameter(description = "ID del cliente", example = "1", required = true)
            @RequestParam Long clienteId,

            @Parameter(description = "ID del pedido confirmado", example = "15", required = true)
            @RequestParam Long pedidoId) {

        boletaService.notificarPedidoConfirmado(clienteId, pedidoId);
        return ResponseEntity.ok("Notificación recibida: Pedido " + pedidoId +
                " del cliente " + clienteId + " está listo para facturación.");
    }
}

