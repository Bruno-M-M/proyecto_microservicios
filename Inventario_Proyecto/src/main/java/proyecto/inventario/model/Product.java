package proyecto.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto incremental
    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "Debe contener una descripcion el producto.")
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String descripcion;

    @NotNull(message = "El producto debe tener un precio para ser vendido.")
    @Max(value = 99999999)
    @Column( nullable = false, length = 10)
    private Integer precio;

    @NotNull(message = "El producto debe tener existencias para ser vendido.")
    @Max(value = 9999)
    @Column(nullable = false, length = 4)
    private Integer stock;

    @NotBlank(message = "El producto debe pertenecer a alguna categoria")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String categoria;

}