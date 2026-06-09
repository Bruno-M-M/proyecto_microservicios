package com.Metodo_pago.pago.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private Long id;
    private Integer run;
    private String dv;
    private String nombre;
    private String correo;
    private String direccion;
    private Integer telefono;
}
