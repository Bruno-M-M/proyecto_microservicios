package com.Carrito.Carrito_compras.Repository;

import com.Carrito.Carrito_compras.Model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarritoRepository  extends JpaRepository<Carrito,Long> {
    List<Carrito> findByClienteId(Long clienteId);

    List<Carrito> findByEstado(Carrito.EstadoPedido estado);

    @Query("SELECT c FROM Carrito c WHERE c.clienteId = :clienteId " +
            "AND c.estado = 'PAGADO' OR c.estado ='CONFIRMADO'" +
            "AND MONTH(c.fechaConfirmacion) = :mes " +
            "AND YEAR(c.fechaConfirmacion) = :anio")
    List<Carrito> findConfirmadosByClienteYMes(@Param("clienteId") Long clienteId,
                                               @Param("mes") int mes,
                                               @Param("anio") int anio);

    @Query("SELECT COUNT(c) FROM Carrito c WHERE c.clienteId = :clienteId " +
            "AND c.estado = 'PAGADO' " +
            "AND YEAR(c.fechaConfirmacion) = :anio")
    long countConfirmadosByClienteYAnio(@Param("clienteId") Long clienteId,
                                        @Param("anio") int anio);
}
