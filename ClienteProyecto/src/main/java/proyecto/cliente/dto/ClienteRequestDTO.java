package proyecto.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDTO {

    @NotNull(message = "El run es obligatorio")
    private Integer run;

    @NotBlank(message = "El digito verificador es obligatorio")
    @Size(max = 1)
    private String dv;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo invalido")
    private String correo;

    @NotBlank(message = "La direccion es obligatorio")
    private String direccion;

    @NotNull(message = "El telefono es obligatorio")
    private Integer telefono;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "Minimo 6 caracteres")
    private String contrasenia;
}
