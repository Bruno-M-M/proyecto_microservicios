package com.Carrito.Carrito_compras.Repository;

import com.Carrito.Carrito_compras.Model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CarritoRepository  extends JpaRepository<Carrito,Long> {
    List<Carrito> findByClienteId(Long clienteId);

    List<Carrito> findByEstado(Carrito.EstadoPedido estado);

    @Query("SELECT COUNT(c) FROM Carrito c WHERE c.clienteId = :clienteId " +
            "AND (c.estado = :est1 OR c.estado = :est2) " +
            "AND MONTH(c.fechaConfirmacion) = :mes " +
            "AND YEAR(c.fechaConfirmacion) = :anio")
    long countConfirmadosByClienteYMes(@Param("clienteId") Long clienteId,
                                       @Param("mes") int mes,
                                       @Param("anio") int anio,
                                       @Param("est1") Carrito.EstadoPedido est1,
                                       @Param("est2") Carrito.EstadoPedido est2);


    @Query("SELECT COUNT(c) FROM Carrito c WHERE c.clienteId = :clienteId " +
            "AND c.estado = 'PAGADO' " +
            "AND YEAR(c.fechaConfirmacion) = :anio")
    long countConfirmadosByClienteYAnio(@Param("clienteId") Long clienteId,
                                        @Param("anio") int anio);
}
