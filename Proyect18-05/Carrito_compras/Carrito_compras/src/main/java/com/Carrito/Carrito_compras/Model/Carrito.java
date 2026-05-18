package com.Carrito.Carrito_compras.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carrito")
@AllArgsConstructor
@NoArgsConstructor
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    @Column(nullable = false)
    private String nombre_producto;

    @NotNull(message = "El clienteId es obligatorio")
    @Column(nullable = false)
    private Long clienteId;

    @NotNull(message = "El productId es obligatorio")
    @Column(nullable = false)
    private Long productoId;

    @Column
    private LocalDateTime fechaConfirmacion;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value= 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    public enum EstadoPedido{
        PENDIENTE, CONFIRMADO, CANCELADO
    }
}
