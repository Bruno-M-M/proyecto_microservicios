package proyecto.inventario.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.inventario.model.Product;
import proyecto.inventario.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts(){
        return productRepository.findAll();
    }

    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Producto no encontrado: " + id));
    }

    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    public List<Product> getProductByCategory(String categoria){
        return productRepository.findByCategoria(categoria);
    }

    public Product updateProduct(Long id, Product update){
        Product antiguo = getProductById(id);
        antiguo.setNombre(update.getNombre());
        antiguo.setDescripcion(update.getDescripcion());
        antiguo.setPrecio(update.getPrecio());
        antiguo.setStock(update.getStock());
        antiguo.setCategoria(update.getCategoria());
        return productRepository.save(antiguo);
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    //metodo para verificarStock
    public boolean checkStock(Long id, Integer cantidad) {
        Product product = getProductById(id);
        return product.getStock() >= cantidad;
    }

    //metodo para descontar stock
    @Transactional
    public void reduceStock(Long id, Integer cantidad){
        Product product = getProductById(id);
        if (product.getStock() < cantidad){
            throw new RuntimeException("Stock insuficiente para: " + product.getNombre());
        }
        product.setStock(product.getStock() - cantidad);
        productRepository.save(product);
    }

}
