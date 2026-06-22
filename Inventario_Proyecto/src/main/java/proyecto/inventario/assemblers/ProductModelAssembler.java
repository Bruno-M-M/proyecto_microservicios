package proyecto.inventario.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import proyecto.inventario.controller.ProductController;
import proyecto.inventario.dto.ProductResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductModelAssembler implements RepresentationModelAssembler<ProductResponseDTO, EntityModel<ProductResponseDTO>> {

    @Override
    public EntityModel<ProductResponseDTO> toModel(ProductResponseDTO product){
        return EntityModel.of(product,
                linkTo(methodOn(ProductController.class).getById(product.getId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAllProduct()).withRel("products"));
    }
}