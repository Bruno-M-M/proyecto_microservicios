package com.Carrito.Carrito_compras.Client;

import com.Carrito.Carrito_compras.DTO.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "INVENTARIO")
public interface InventarioFeingClient {

    @GetMapping("/api/products/{id}")
    ProductoDTO getProductoById(@PathVariable("id") Long id);

    @GetMapping("/api/products/{id}/check-stock")
    Boolean checkStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);

    @PutMapping("/api/products/{id}/reduce-stock")
    void reduceStock(@PathVariable("id") Long id, @RequestParam("cantidad") Integer cantidad);
}
