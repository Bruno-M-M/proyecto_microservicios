package proyecto.inventario.dto;

import org.springframework.stereotype.Component;
import proyecto.inventario.model.Product;

@Component
public class ProductMapper {

    public ProductResponseDTO toResponse(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setDescripcion(product.getDescripcion());
        dto.setPrecio(product.getPrecio());
        dto.setStock(product.getStock());
        dto.setCategoria(product.getCategoria());
        return dto;
    }

    public Product toEntity(ProductRequestDTO dto) {
        Product product = new Product();
        product.setNombre(dto.getNombre());
        product.setDescripcion(dto.getDescripcion());
        product.setPrecio(dto.getPrecio());
        product.setStock(dto.getStock());
        product.setCategoria(dto.getCategoria());
        return product;
    }
}