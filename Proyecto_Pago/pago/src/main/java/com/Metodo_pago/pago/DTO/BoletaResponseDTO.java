package com.Metodo_pago.pago.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletaResponseDTO {
    private Long boletaId;
    private LocalDateTime fechaEmision;
    private String estado;

    private Long clienteId;
    private String clienteNombre;
    private String clienteRun;
    private String clienteCorreo;
    private String clienteDireccion;
    private Integer clienteTelefono;

    private String metodoPago;

    private List<BoletaItemDTO> items;
    private Double totalNeto;
    private Double iva;
    private Double totalConIva;
}

