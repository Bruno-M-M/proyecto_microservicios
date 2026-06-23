package com.Carrito.Carrito_compras.Client;

import com.Carrito.Carrito_compras.DTO.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cliente", url = "${microservicio.clientes.url}")
public interface ClienteFeingClient {

    @GetMapping("/{id}")
    EntityModel<ClienteDTO> getClientById(@PathVariable("id") Long id);

    @GetMapping("/{id}/exists")
    boolean existsById(@PathVariable("id")Long id);
}