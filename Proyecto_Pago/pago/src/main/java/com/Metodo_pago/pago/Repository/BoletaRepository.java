package com.Metodo_pago.pago.Repository;

import com.Metodo_pago.pago.Model.MetodoPago;
import com.Metodo_pago.pago.Model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletaRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByClienteId(Long clienteId);


}
