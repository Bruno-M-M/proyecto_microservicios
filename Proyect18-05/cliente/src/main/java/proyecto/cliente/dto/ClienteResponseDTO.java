package proyecto.cliente.dto;

import lombok.Data;

@Data
public class ClienteResponseDTO {

    private Long id;
    private Integer run;
    private String dv;
    private String nombre;
    private String correo;
    private String direccion;
    private Integer telefono;
}
