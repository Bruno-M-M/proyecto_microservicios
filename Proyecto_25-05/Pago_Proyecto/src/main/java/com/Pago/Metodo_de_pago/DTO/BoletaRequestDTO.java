package com.Pago.Metodo_de_pago.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoletaRequestDTO {
    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;

    private List<Long> pedidosIds;

    @NotNull(message = "El método de pago es obligatorio")
    private String metodoPago;
}
