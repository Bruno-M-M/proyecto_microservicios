package com.Metodo_pago.pago.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoItemDetalleDTO {
    private Long productoId;
    private String productoNombre;
    private String productoDescripcion;
    private Integer productoPrecio;
    private Integer productoStock;
    private String productoCategoria;
    private Integer cantidad;
    private Double subtotal;
}
