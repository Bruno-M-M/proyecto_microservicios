package com.Pago.Metodo_de_pago.Model;

import com.Pago.Metodo_de_pago.Controller.BoletaController;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;

@Entity
@Table(name ="boletas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    private String clienteRun;

    @Column(nullable = false)
    private String clienteCorreo;

    @Column(nullable = false)
    private String clienteDireccion;

    @Column(nullable = false)
    private Integer clienteTelefono;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPago tipoPago;

    @Column(nullable = false)
    private Double totalNeto;

    @Column(nullable = false)
    private Double iva;

    @Column(nullable = false)
    private Double totalConIva;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoBoleta estado = EstadoBoleta.EMITIDA;

    @Column(nullable = false, length = 1000)
    private String pedidosIds;

    public enum TipoPago {
        EFECTIVO, DEBITO, CREDITO, TRANSFERENCIA
    }

    public enum EstadoBoleta {
        EMITIDA, ANULADA
    }


}
