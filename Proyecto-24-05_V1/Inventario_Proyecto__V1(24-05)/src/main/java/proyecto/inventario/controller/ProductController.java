package proyecto.inventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.inventario.model.Product;
import proyecto.inventario.service.ProductService;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProduct(){
        List<Product> products = productService.getProducts();
        if(products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id){
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categoria/{categoria}")
    public List<Product> getProductByCategory(@PathVariable String categoria){
        return productService.getProductByCategory(categoria);
    }

    @PostMapping
    public ResponseEntity<?> crearProduct(@RequestBody Product product){
        try{
            Product product2 = productService.createProduct(product);
            return ResponseEntity.status(201).body(product2);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product){
        try{
            return ResponseEntity.ok(productService.updateProduct(id, product));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try{
            productService.deleteProduct(id);
            return ResponseEntity.ok("Eliminado con Exito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/check-stock")
    public ResponseEntity<Boolean> checkStock(@PathVariable Long id, @RequestParam Integer cantidad){
        return ResponseEntity.ok(productService.checkStock(id, cantidad));
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<Void> reduceStock(@PathVariable Long id, @RequestParam Integer cantidad){
        productService.reduceStock(id, cantidad);
        return ResponseEntity.ok().build();
    }
}
