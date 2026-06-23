package com.Pago.Metodo_de_pago.Client;

import com.Pago.Metodo_de_pago.DTO.CarritoDetalleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "carrito-compras", url = "${microservicio.carrito.url}")

public interface CarritoFeingClient {

    @GetMapping("/cliente/{clienteId}")
    CollectionModel<CarritoDetalleDTO> getPedidosDelCliente(@PathVariable("clienteId") Long clienteId);

    @PutMapping("/{id}/pagar")
    EntityModel<CarritoDetalleDTO> marcarComoPagado(@PathVariable("id") Long pedidoId);


}