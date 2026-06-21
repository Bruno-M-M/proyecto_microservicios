package com.Carrito.Carrito_compras.Controller;

import com.Carrito.Carrito_compras.DTO.CarritoDetalleDTO;
import com.Carrito.Carrito_compras.DTO.CarritoRequestDTO;
import com.Carrito.Carrito_compras.Model.Carrito;
import com.Carrito.Carrito_compras.Model.CarritoModelAssembler;
import com.Carrito.Carrito_compras.Service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/Carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoModelAssembler carritoAssembler;
    /******************************************************************************************/
    @Operation(
            summary = "Obtener todos los pedidos",
            description = "Retorna la lista completa de carritos/pedidos registrados en el sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pedidos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CarritoDetalleDTO.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CarritoDetalleDTO>>> getTodo() {
        List<EntityModel<CarritoDetalleDTO>> carritos = carritoService.getTodo()
                .stream().map(carritoAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(carritos,
                linkTo(methodOn(CarritoController.class).getTodo()).withSelfRel()));
    }
    /******************************************************************************************/
    @Operation(
            summary = "Obtener pedidos por cliente",
            description = "Retorna todos los pedidos asociados al ID de cliente indicado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedidos del cliente obtenidos exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CarritoDetalleDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CollectionModel<EntityModel<CarritoDetalleDTO>>> getByCliente(@PathVariable Long clienteId) {
        List<EntityModel<CarritoDetalleDTO>> carritos = carritoService.getByCliente(clienteId)
                .stream().map(carritoAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(carritos,
                linkTo(methodOn(CarritoController.class).getByCliente(clienteId)).withSelfRel()));
    }
/******************************************************************************************/
    @Operation(
            summary = "Obtener pedidos por estado",
            description = "Filtra los pedidos según su estado: PENDIENTE, CONFIRMADO, CANCELADO o PAGADO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedidos filtrados por estado obtenidos exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CarritoDetalleDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content)
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<CarritoDetalleDTO>>> getByEstado(@PathVariable String estado) {
        List<EntityModel<CarritoDetalleDTO>> carritos = carritoService.getByEstado(estado)
                .stream().map(carritoAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(carritos,
                linkTo(methodOn(CarritoController.class).getByEstado(estado)).withSelfRel()));
    }

    /******************************************************************************************/
    @Operation(
            summary = "Crear un nuevo pedido",
            description = "Crea un nuevo carrito/pedido con los productos e ítems indicados. El estado inicial es PENDIENTE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pedido creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoDetalleDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente o producto no encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<CarritoDetalleDTO>> agregar(@Valid @RequestBody CarritoRequestDTO request) {
        return ResponseEntity.status(201).body(carritoAssembler.toModel(carritoService.agregar(request)));
    }
    /******************************************************************************************/
    @Operation(
            summary = "Confirmar un pedido",
            description = "Cambia el estado del pedido a CONFIRMADO. Solo se puede confirmar un pedido en estado PENDIENTE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pedido confirmado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoDetalleDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "El pedido no puede confirmarse en su estado actual", content = @Content)
    })
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<EntityModel<CarritoDetalleDTO>> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoAssembler.toModel(carritoService.confirmar(id)));
    }

    /******************************************************************************************/

    @Operation(summary = "Cancelar pedido",
            description = "Cancela un pedido del carrito por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado correctamente",
                    content = @Content(schema = @Schema(implementation = CarritoDetalleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "El pedido no se puede cancelar")
    })
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<EntityModel<CarritoDetalleDTO>> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(carritoAssembler.toModel(carritoService.cancelar(id)));
    }
/*****************************************************************************/
@Operation(
        summary = "Marcar pedido como pagado",
        description = "Cambia el estado del pedido a PAGADO. Solo aplica sobre pedidos CONFIRMADOS."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Pedido marcado como pagado exitosamente",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CarritoDetalleDTO.class)
                )
        ),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content),
        @ApiResponse(responseCode = "400", description = "El pedido no puede marcarse como pagado en su estado actual", content = @Content)
})
@PutMapping("/{id}/pagar")
public ResponseEntity<EntityModel<CarritoDetalleDTO>> marcarComoPagado(@PathVariable Long id) {
    return ResponseEntity.ok(carritoAssembler.toModel(carritoService.marcarComoPagado(id)));
}

/*********************************************************************************/
@Operation(
        summary = "Eliminar un pedido",
        description = "Elimina permanentemente el pedido del sistema según su ID."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Pedido eliminado exitosamente",
                content = @Content(mediaType = "text/plain", schema = @Schema(example = "Pedido eliminado del carrito."))
        ),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
})
@DeleteMapping("/{id}")
public ResponseEntity<String> eliminar(
        @Parameter(description = "ID del pedido a eliminar", required = true, example = "1")
        @PathVariable Long id) {
    carritoService.eliminar(id);
    return ResponseEntity.ok("Pedido eliminado del carrito.");
}
/**************************************************************************/
@Operation(
        summary = "Estadísticas de pedidos por mes",
        description = "Retorna el total de pedidos confirmados de un cliente en un mes y año específico."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Estadísticas obtenidas exitosamente",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\"clienteId\":1,\"mes\":6,\"anio\":2025,\"totalPedidosConfirmados\":3}")
                )
        ),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
})
@GetMapping("/cliente/{clienteId}/stats")
public ResponseEntity<?> getEstadisticasMes(
        @Parameter(description = "ID del cliente", required = true, example = "1")
        @PathVariable Long clienteId,
        @Parameter(description = "Mes a consultar (1-12)", required = true, example = "6")
        @RequestParam int mes,
        @Parameter(description = "Año a consultar", required = true, example = "2025")
        @RequestParam int anio) {
    long total = carritoService.countPedidosPorMes(clienteId, mes, anio);
    return ResponseEntity.ok(Map.of(
            "clienteId", clienteId, "mes", mes, "anio", anio,
            "totalPedidosConfirmados", total));
}
/********************************************************************/
@Operation(
        summary = "Estadísticas de pedidos por año",
        description = "Retorna el total de pedidos confirmados de un cliente durante un año completo."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Estadísticas anuales obtenidas exitosamente",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(example = "{\"clienteId\":1,\"anio\":2025,\"totalPedidosConfirmados\":15}")
                )
        ),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado", content = @Content)
})
@GetMapping("/cliente/{clienteId}/stats/anio")
public ResponseEntity<?> getEstadisticasAnio(
        @Parameter(description = "ID del cliente", required = true, example = "1")
        @PathVariable Long clienteId,
        @Parameter(description = "Año a consultar", required = true, example = "2025")
        @RequestParam int anio) {
    long total = carritoService.countPedidosPorAnio(clienteId, anio);
    return ResponseEntity.ok(Map.of(
            "clienteId", clienteId, "anio", anio,
            "totalPedidosConfirmados", total));
    }
}

