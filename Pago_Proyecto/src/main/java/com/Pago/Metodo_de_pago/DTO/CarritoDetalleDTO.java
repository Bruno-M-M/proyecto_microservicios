package com.Pago.Metodo_de_pago.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarritoDetalleDTO {
    private Long pedidoId;
    private String estadoPedido;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaConfirmacion;
    private Double totalPedido;

    private Long clienteId;
    private String clienteRun;
    private String clienteNombre;
    private String clienteCorreo;
    private String clienteDireccion;
    private Integer clienteTelefono;

    private List<CarritoItemDetalleDTO> items;
}
