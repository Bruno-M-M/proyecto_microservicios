package com.Pago.Metodo_de_pago.Model;

import com.Pago.Metodo_de_pago.Controller.BoletaController;
import com.Pago.Metodo_de_pago.DTO.BoletaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class BoletaModelAssembler implements RepresentationModelAssembler<BoletaResponseDTO, EntityModel<BoletaResponseDTO>> {

    @Override
    public EntityModel<BoletaResponseDTO> toModel(BoletaResponseDTO boleta) {
        return EntityModel.of(boleta,
                    linkTo(methodOn(BoletaController.class).getById(boleta.getBoletaId())).withSelfRel(),
                    linkTo(methodOn(BoletaController.class).anular(boleta.getBoletaId())).withRel("anular"),
                    linkTo(methodOn(BoletaController.class).getAll()).withRel("todas"),
                    linkTo(methodOn(BoletaController.class).getByCliente(boleta.getClienteId())).withRel("boletas-cliente")
            );
        }
}
