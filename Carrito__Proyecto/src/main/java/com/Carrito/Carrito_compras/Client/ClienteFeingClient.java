package com.Carrito.Carrito_compras.Client;

import com.Carrito.Carrito_compras.DTO.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name= "CLIENTE")
public interface ClienteFeingClient {

    @GetMapping("/api/clientes/{id}")
    ClienteDTO getClientById(@PathVariable("id") Long id);

    @GetMapping("/api/clientes/{id}/exists")
    boolean existsById(@PathVariable("id")Long id);
}
