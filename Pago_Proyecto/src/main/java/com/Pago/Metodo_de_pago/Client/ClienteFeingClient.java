package com.Pago.Metodo_de_pago.Client;

import com.Pago.Metodo_de_pago.DTO.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CLIENTE" )
public interface ClienteFeingClient {

    @GetMapping("/api/clientes/{id}")
    ClienteDTO getClienteById(@PathVariable("id") Long id);

    @GetMapping("/api/clientes/{id}/exists")
    Boolean existsById(@PathVariable("id") Long id);
}