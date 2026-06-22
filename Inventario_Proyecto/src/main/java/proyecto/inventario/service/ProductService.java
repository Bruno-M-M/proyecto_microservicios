package proyecto.inventario.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.inventario.dto.ProductMapper;
import proyecto.inventario.dto.ProductRequestDTO;
import proyecto.inventario.dto.ProductResponseDTO;
import proyecto.inventario.model.Product;
import proyecto.inventario.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public List<ProductResponseDTO> getProducts(){
        return productRepository.findAll()
                .stream().map(mapper::toResponse)
                .toList();
    }

    public ProductResponseDTO getProductById(Long id){
        return mapper.toResponse(findProductEntity(id));
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto){
        Product product = mapper.toEntity(dto);
        return mapper.toResponse(productRepository.save(product));
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO update){
        Product antiguo = findProductEntity(id);
        antiguo.setNombre(update.getNombre());
        antiguo.setDescripcion(update.getDescripcion());
        antiguo.setPrecio(update.getPrecio());
        antiguo.setStock(update.getStock());
        antiguo.setCategoria(update.getCategoria());
        return mapper.toResponse(productRepository.save(antiguo));
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    //metodo para verificarStock
    public boolean checkStock(Long id, Integer cantidad) {
        Product product = findProductEntity(id);
        return product.getStock() >= cantidad;
    }

    //metodo para descontar stock
    @Transactional
    public void reduceStock(Long id, Integer cantidad){
        Product product = findProductEntity(id);
        if (product.getStock() < cantidad){
            throw new RuntimeException("Stock insuficiente para: " + product.getNombre());
        }
        product.setStock(product.getStock() - cantidad);
        productRepository.save(product);
    }

    // metodo interno: devuelve la entidad para uso dentro del propio Service
    private Product findProductEntity(Long id){
        return productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Producto no encontrado: " + id));
    }

}