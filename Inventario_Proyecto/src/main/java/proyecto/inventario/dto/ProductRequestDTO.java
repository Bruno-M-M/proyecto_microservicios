package proyecto.inventario.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import proyecto.inventario.model.Product;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "Debe contener una descripcion el producto.")
    @Size(max = 300)
    private String descripcion;

    @NotNull(message = "El producto debe tener un precio para ser vendido.")
    @Max(value = 99999999)
    private Integer precio;

    @NotNull(message = "El producto debe tener existencias para ser vendido.")
    @Max(value = 9999)
    private Integer stock;

    @NotNull(message = "El producto debe pertenecer a alguna categoria")
    private Product.Categorias categoria;
}
