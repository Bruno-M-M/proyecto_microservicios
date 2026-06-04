package com.Pago.Metodo_de_pago.Client;

import com.Pago.Metodo_de_pago.DTO.CarritoDetalleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name= "carrito", url ="${microservicio.carrito.url}")

public interface CarritoFeingClient {

    @GetMapping("/cliente/{clienteId}")
    List<CarritoDetalleDTO> getPedidosDelCliente(@PathVariable("clienteId") Long clienteId);

    @PutMapping("/{id}/pagar")
    CarritoDetalleDTO marcarComoPagado(@PathVariable("id") Long pedidoId);


}
