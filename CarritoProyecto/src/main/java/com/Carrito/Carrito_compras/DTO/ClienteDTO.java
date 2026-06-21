package com.Carrito.Carrito_compras.DTO;

import lombok.Data;

@Data
public class ClienteDTO {

    private Long id;
    private Integer run;
    private String dv;
    private String nombre;
    private String correo;
    private String direccion;
    private Integer telefono;
}
