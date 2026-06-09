package proyecto.inventario;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import proyecto.inventario.model.Product;
import proyecto.inventario.repository.ProductRepository;
import proyecto.inventario.service.ProductService;

import java.util.List;
import java.util.Optional;


@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    public void testFindAll(){
        Long cod = anyLong();
        when(productRepository.findAll()).thenReturn(List.of(new Product(cod, "productoTest", "DescripcionTest", 10000, 404, Product.Categorias.Carnes)));

        List<Product> productsList = productService.getProducts();

        assertNotNull(productsList);
        assertEquals(1, productsList.size());
    }

    @Test
    public void testFindCodigo(){
        Long codigo = anyLong();
        Product product = new Product(codigo, "productoTest", "DescripcionTest", 10000, 404, Product.Categorias.Carnes);

        when(productRepository.findById(codigo)).thenReturn(Optional.of(product));

        Product found = productService.getProductById(codigo);

        assertNotNull(found);
        assertEquals(codigo, found.getId());

    }


}
