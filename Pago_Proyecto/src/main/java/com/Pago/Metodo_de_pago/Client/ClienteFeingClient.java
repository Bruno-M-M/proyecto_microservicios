package com.Pago.Metodo_de_pago.Client;

import com.Pago.Metodo_de_pago.DTO.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente", url = "${microservicio.clientes.url}")
public interface ClienteFeingClient {

    @GetMapping("/{id}")
    EntityModel<ClienteDTO> getClienteById(@PathVariable("id") Long id);

    @GetMapping("/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}