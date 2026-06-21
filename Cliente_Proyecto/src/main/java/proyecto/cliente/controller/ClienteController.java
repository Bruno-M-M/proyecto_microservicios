package proyecto.cliente.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.cliente.assemblers.ClienteModelAssembler;
import proyecto.cliente.dto.ClienteRequestDTO;
import proyecto.cliente.dto.ClienteResponseDTO;
import proyecto.cliente.dto.LoginRequestDTO;
import proyecto.cliente.feing.CarritoFeingCliente;
import proyecto.cliente.feing.InventarioFeingClient;
import proyecto.cliente.feing.PagoFeingClient;
import proyecto.cliente.service.ClienteService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Operaciones relacionadas con el Cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CarritoFeingCliente carritoCliente;

    @Autowired
    private PagoFeingClient pagoCliente;

    @Autowired
    private InventarioFeingClient inventarioClient;

    @Autowired
    private ClienteModelAssembler assembler;


    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtiene todos los clientes", description = "Obtiene una lista con todos los clientes registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
            @ApiResponse(responseCode = "404", description = "No se encontraron clientes registrados")
    })
    public CollectionModel<EntityModel<ClienteResponseDTO>> getClientes(){
        List<EntityModel<ClienteResponseDTO>> clientes = clienteService.getClientes().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(clientes,
                linkTo(methodOn(ClienteController.class).getClientes()).withSelfRel());
    }


    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtiene un cliente por Id", description = "Obtiene un cliente por su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se encontro cliente con ese Id")
    })
    public EntityModel<ClienteResponseDTO> getById(@PathVariable Long id){
        ClienteResponseDTO cliente = clienteService.getClienteById(id);
        return assembler.toModel(cliente);
    }


    @GetMapping("/run/{run}")
    @Operation(summary = "Obtiene un cliente por run", description = "Obtiene un cliente por su run")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontro cliente con ese run")
    })
    public ResponseEntity<?> getByRun(@PathVariable Integer run){
        return ResponseEntity.ok(clienteService.getByRun(run));
    }


    @PostMapping("/register")
    @Operation(summary = "Se registra un cliente", description = "Se registra un cliente con sus datos y se agrega a la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente registrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontro cliente con ese Id")
    })
    public ResponseEntity<?> registrar(@Valid @RequestBody ClienteRequestDTO dto){
        return ResponseEntity.status(201).body(clienteService.registrar(dto));

    }


    @PostMapping("/login")
    @Operation(summary = "Inicia sesion un cliente", description = "El cliente ingresa sus credenciales para iniciar sesion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesion exitoso"),
            @ApiResponse(responseCode = "404", description = "No se logro iniciar sesion")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(clienteService.login(dto));
    }


    @PutMapping("/{id}")@Operation(summary = "Se actualizan datos de un cliente", description = "Se modifica los datos de un cliente y se actualiza en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizaron exitosamente los datos"),
            @ApiResponse(responseCode = "404", description = "No se encontro cliente con ese Id")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ClienteRequestDTO dto){
        return ResponseEntity.status(200).body(clienteService.updateCliente(id, dto));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Se elimina un cliente", description = "Se elimina un cliente por si Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente eliminado con exito"),
            @ApiResponse(responseCode = "404", description = "No se logro agregar nuevo cliente")
    })
    public ResponseEntity<?> delete(@PathVariable Long id){
        clienteService.deleteCliente(id);
        return ResponseEntity.ok("Cliente eliminado con exito.");
    }


    @GetMapping("/{id}/exists")
    @Operation(summary = "Se verifica existencia de cliente", description = "Se verifica en base de datos si existe el cliente con el id ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se existe cliente con ese Id")
    })
    public ResponseEntity<Boolean> exist(@PathVariable Long id){
        return ResponseEntity.ok(clienteService.existsById(id));
    }


    @GetMapping("/{id}/mis-pedidos")
    @Operation(summary = "Se obtiene los pedidos", description = "Se obtiene una lista de todos los pedidos realizados por un cliente a travez de su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontro cliente con ese Id")
    })
    public ResponseEntity<?> getMisPedidos(@PathVariable Long id){
        clienteService.getClienteById(id);
        return ResponseEntity.ok(carritoCliente.getPedidosDelCliente(id));
    }


    @GetMapping("/{id}/mis-pedidos/stats")
    @Operation(summary = "Estadisticas de un cliente por mes", description = "Se obtiene una lista de estadisticas del mes de un cliente a travez de su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadisticas mostradas exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se existe cliente con ese Id")
    })
    public ResponseEntity<?> getStatsMes(@PathVariable Long id,
                                         @RequestParam int mes,
                                         @RequestParam int anio){
        clienteService.getClienteById(id);
        return ResponseEntity.ok(carritoCliente.getStatsPorMes(id,mes,anio));
    }


    @GetMapping("/{id}/mis-pedidos/stats/anio")
    @Operation(summary = "Estadisticas de un cliente por año", description = "Se obtiene una lista de estadisticas del año de un cliente a travez de su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadisticas mostradas exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se existe cliente con ese Id")
    })
    public ResponseEntity<?> getStatsAnio(@PathVariable Long id,
                                          @RequestParam int anio){
        clienteService.getClienteById(id);
        return ResponseEntity.ok(carritoCliente.getStatsPorAnio(id,anio));
    }


    @GetMapping("/{id}/mis-boletas")
    @Operation(summary = "Lista las boletas de un cliente", description = "Se lista las boletas de un cliente a travez de su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boletas obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se existe cliente con ese Id")
    })
    public ResponseEntity<?> getMisBoletas(@PathVariable Long id) {
        clienteService.getClienteById(id);
        return ResponseEntity.ok(pagoCliente.getBoletasByCliente(id));
    }


    @GetMapping("/{id}/resumen")
    @Operation(summary = "Obtiene el resumen del cliente", description = "Se obtiene todos los datos asociados a sus boletas y pedidos en un resumen a travez del Id del cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadisticas mostradas exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se existe cliente con ese Id")
    })
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


    // no se si se mantendran
    @GetMapping("/catalogo")
    public ResponseEntity<?> getCatalogo() {
        return ResponseEntity.ok(inventarioClient.getProductos());
    }


    @GetMapping("/catalogo/categoria/{categoria}")
    public ResponseEntity<?> getCatalogoByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(inventarioClient.getByCategoria(categoria));
    }

}