package proyecto.inventario.dto;

import lombok.Data;
import proyecto.inventario.model.Product;

@Data
public class ProductResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Integer stock;
    private Product.Categorias categoria;
}