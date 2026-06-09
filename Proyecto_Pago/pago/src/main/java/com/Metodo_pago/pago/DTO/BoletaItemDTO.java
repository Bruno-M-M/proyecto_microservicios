package com.Metodo_pago.pago.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletaItemDTO {
    private Long pedidoId;
    private String productoNombre;
    private String productoCategoria;
    private Integer cantidad;
    private Integer precioUnitario;
    private Double subtotal;
}
