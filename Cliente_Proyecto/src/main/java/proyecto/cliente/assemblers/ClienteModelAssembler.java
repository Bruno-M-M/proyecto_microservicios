package proyecto.cliente.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import proyecto.cliente.controller.ClienteControllerV2;
import proyecto.cliente.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<Cliente, EntityModel<Cliente>> {

    @Override
    public EntityModel<Cliente> toModel(Cliente cliente){
        return EntityModel.of(cliente,
                linkTo(methodOn(ClienteControllerV2.class).getById(cliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteControllerV2.class).getClientes()).withRel("clientes"));
    }
}
