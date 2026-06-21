package proyecto.inventario.controller;

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
import proyecto.inventario.assemblers.ProductModelAssembler;
import proyecto.inventario.dto.ProductRequestDTO;
import proyecto.inventario.dto.ProductResponseDTO;
import proyecto.inventario.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Inventario", description = "Operaciones relacionadas con los productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtiene todos los productos", description = "Obtiene todos los productos y los muestra en una lista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos listados exitosamente"),
            @ApiResponse(responseCode = "404", description = "No hay productos en el inventario")
    })
    public CollectionModel<EntityModel<ProductResponseDTO>> getAllProduct(){
        List<EntityModel<ProductResponseDTO>> products = productService.getProducts().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(products,
                linkTo(methodOn(ProductController.class).getAllProduct()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Obtiene un producto", description = "Obtiene un producto por su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No hay producto con ese Id")
    })
    public EntityModel<ProductResponseDTO> getById(@PathVariable Long id){
        ProductResponseDTO product = productService.getProductById(id);
        return assembler.toModel(product);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtiene productos por categoria", description = "Obtiene todos los productos que pertenezcan a la categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria listada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe esa categora")
    })
    public List<ProductResponseDTO> getProductByCategory(@PathVariable String categoria){
        return productService.getProductByCategory(categoria);
    }

    @PostMapping
    @Operation(summary = "Agrega un producto", description = "Agrega un producto a la base de datos con sus datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto agregado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se logro agregar nuevo producto")
    })
    public ResponseEntity<?> crearProduct(@Valid @RequestBody ProductRequestDTO dto){
        try{
            ProductResponseDTO product = productService.createProduct(dto);
            return ResponseEntity.status(201).body(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un producto", description = "Actualiza un producto por su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se logro actualizar el producto")
    })
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto){
        try{
            return ResponseEntity.ok(productService.updateProduct(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto", description = "Elimina un producto por su Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se logro eliminar el producto")
    })
    public ResponseEntity<?> delete(@PathVariable Long id){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok("Eliminado con Exito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/check-stock")
    @Operation(summary = "Revisa si hay stock", description = "Revisa si existe la cantidad que el usuario necesite para su pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Existe stock suficiente"),
            @ApiResponse(responseCode = "404", description = "No hay suficiente stock")
    })
    public ResponseEntity<Boolean> checkStock(@PathVariable Long id, @RequestParam Integer cantidad){
        return ResponseEntity.ok(productService.checkStock(id, cantidad));
    }

    @PutMapping("/{id}/reduce-stock")
    @Operation(summary = "Reduce el stock", description = "Reduce el stock al ser pagado el producto")//pagado o confirmado?
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stocl reducido exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se logro reducir el stock")
    })
    public ResponseEntity<Void> reduceStock(@PathVariable Long id, @RequestParam Integer cantidad){
        productService.reduceStock(id, cantidad);
        return ResponseEntity.ok().build();
    }
}