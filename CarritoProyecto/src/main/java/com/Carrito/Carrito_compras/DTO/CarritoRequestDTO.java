package com.Carrito.Carrito_compras.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CarritoRequestDTO {

    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;

    @NotNull(message = "La lista de productos no puede ser nula")
    @Size(min = 1, message = "El pedido debe contener al menos 1 producto")
    @Valid
    private List<CarritoItemRequestDTO> items;
}
