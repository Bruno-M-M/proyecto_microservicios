package com.Carrito.Carrito_compras.Model;

import com.Carrito.Carrito_compras.Controller.CarritoController;
import com.Carrito.Carrito_compras.DTO.CarritoDetalleDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CarritoModelAssembler implements RepresentationModelAssembler<CarritoDetalleDTO, EntityModel<CarritoDetalleDTO>> {

    @Override
    public EntityModel<CarritoDetalleDTO> toModel(CarritoDetalleDTO carrito) {
        return EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).getTodo()).withRel("todos"),
                linkTo(methodOn(CarritoController.class).getByCliente(carrito.getClienteId())).withRel("carrito-cliente"),
                linkTo(methodOn(CarritoController.class).confirmar(carrito.getPedidoId())).withRel("confirmar"),
                linkTo(methodOn(CarritoController.class).cancelar(carrito.getPedidoId())).withRel("cancelar"),
                linkTo(methodOn(CarritoController.class).marcarComoPagado(carrito.getPedidoId())).withRel("pagar"),
                linkTo(methodOn(CarritoController.class).eliminar(carrito.getPedidoId())).withRel("eliminar")
        );
    }
}