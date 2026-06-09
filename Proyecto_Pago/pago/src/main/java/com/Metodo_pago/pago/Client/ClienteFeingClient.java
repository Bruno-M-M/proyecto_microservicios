package com.Metodo_pago.pago.Client;

import com.Metodo_pago.pago.DTO.ClienteDTO;
import com.Pago.Metodo_de_pago.DTO.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente-desde-pago", url = "${microservicio.clientes.url}")
public interface ClienteFeingClient {

    @GetMapping("/{id}")
    ClienteDTO getClienteById(@PathVariable("id") Long id);

    @GetMapping("/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}