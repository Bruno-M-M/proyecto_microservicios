package com.Carrito.Carrito_compras.Client;

import com.Carrito.Carrito_compras.DTO.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventario", url = "${microservicio.inventario.url}")
public interface InventarioFeingClient {

    @GetMapping("/{id}")
    EntityModel<ProductoDTO> getProductoById(@PathVariable("id") Long id);

    @GetMapping("/{id}/check-stock")
    Boolean checkStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);

    @PutMapping("/{id}/reduce-stock")
    void reduceStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);
}