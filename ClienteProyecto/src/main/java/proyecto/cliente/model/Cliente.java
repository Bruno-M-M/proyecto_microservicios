package proyecto.cliente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El RUN es obligatorio")
    @Column(nullable = false, unique = true)
    private Integer run;

    @NotBlank(message = "El digito verificador es obligatorio")
    @Column(nullable = false, length = 1)
    private String dv;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo invalido")
    @Column(nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "La direccion es obligatorio")
    @Column(nullable = false)
    private String direccion;

    @NotNull(message = "El telefono es obligatorio")
    @Column(nullable = false)
    private Integer telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "Minimo 6 caracteres")
    @Column(nullable = false)
    private String contrasenia;
}
