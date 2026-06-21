package proyecto.cliente.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import proyecto.cliente.controller.ClienteController;
import proyecto.cliente.dto.ClienteResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<ClienteResponseDTO, EntityModel<ClienteResponseDTO>> {

    @Override
    public EntityModel<ClienteResponseDTO> toModel(ClienteResponseDTO cliente){
        return EntityModel.of(cliente,
                linkTo(methodOn(ClienteController.class).getById(cliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).getClientes()).withRel("clientes"));
    }
}