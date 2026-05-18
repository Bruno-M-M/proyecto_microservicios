package com.Carrito.Carrito_compras.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarritoDetalleDTO {
    private Long pedidoId;
    private Integer cantidad;
    private String estadoPedido;
    private Double subtotal;


    private Long ClienteId;
    private String clienteRun;
    private String clienteNombre;
    private String clienteCorreo;
    private String clienteDireccion;
    private Integer clienteTelefono;



    private Long productoId;
    private String productoNombre;
    private String productoDescripcion;
    private Integer productoPrecio;
    private Integer productoStock;
    private  String productoCategoria;
}
