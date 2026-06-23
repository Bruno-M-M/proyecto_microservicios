package com.Carrito.Carrito_compras.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "metodo-de-pago", url = "${microservicio.pago.url}")
public interface PagoFeingClient {

    @PostMapping("/api/v1/boletas/notificar-confirmacion")
    void notificarConfirmacion(@RequestParam long pedidoId, @RequestParam long idCliente);
}
