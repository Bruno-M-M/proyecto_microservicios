package com.Pago.Metodo_de_pago.Repostory;

import com.Pago.Metodo_de_pago.Model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoletaRepository extends JpaRepository<MetodoPago, Long> {
    List<MetodoPago> findByClienteId(Long clienteId);


}
