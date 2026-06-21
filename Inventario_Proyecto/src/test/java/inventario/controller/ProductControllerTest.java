package inventario.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import proyecto.inventario.assemblers.ProductModelAssembler;
import proyecto.inventario.controller.ProductController;
import proyecto.inventario.dto.ProductRequestDTO;
import proyecto.inventario.dto.ProductResponseDTO;
import proyecto.inventario.model.Product;
import proyecto.inventario.service.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests del ProductController, sin MockMvc ni contexto de Spring.
 * El controller se instancia directamente con @InjectMocks (igual patron
 * que ProductServiceTest) y se llaman sus metodos como codigo Java normal,
 * revisando el objeto/ResponseEntity devuelto.
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductModelAssembler assembler;

    @InjectMocks
    private ProductController productController;

    private final Faker faker = new Faker();
    private ProductResponseDTO responseDTO;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombre(faker.commerce().productName());
        responseDTO.setDescripcion(faker.lorem().sentence(10));
        responseDTO.setPrecio(faker.number().numberBetween(1000, 50000));
        responseDTO.setStock(faker.number().numberBetween(5, 200));
        responseDTO.setCategoria(Product.Categorias.Despensa);

        requestDTO = new ProductRequestDTO();
        requestDTO.setNombre(responseDTO.getNombre());
        requestDTO.setDescripcion(responseDTO.getDescripcion());
        requestDTO.setPrecio(responseDTO.getPrecio());
        requestDTO.setStock(responseDTO.getStock());
        requestDTO.setCategoria(responseDTO.getCategoria());
    }

    @Test
    @DisplayName("getAllProduct - Debe retornar CollectionModel con HATEOAS")
    void getAllProduct_ShouldReturnCollectionWithHateoas() {
        when(productService.getProducts()).thenReturn(List.of(responseDTO));
        when(assembler.toModel(any(ProductResponseDTO.class)))
                .thenReturn(EntityModel.of(responseDTO));

        CollectionModel<EntityModel<ProductResponseDTO>> resultado = productController.getAllProduct();

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertTrue(resultado.getLink("self").isPresent());
    }

    @Test
    @DisplayName("getById - Debe retornar un producto envuelto en EntityModel")
    void getById_ShouldReturnProduct() {
        when(productService.getProductById(1L)).thenReturn(responseDTO);
        when(assembler.toModel(responseDTO)).thenReturn(EntityModel.of(responseDTO));

        EntityModel<ProductResponseDTO> resultado = productController.getById(1L);

        assertNotNull(resultado);
        assertEquals(responseDTO.getNombre(), resultado.getContent().getNombre());
    }

    @Test
    @DisplayName("getProductByCategory - Debe retornar productos filtrados con HATEOAS")
    void getProductByCategory_ShouldReturnFilteredProducts() {
        when(productService.getProductByCategory("ELECTRONICA"))
                .thenReturn(List.of(responseDTO));
        when(assembler.toModel(any(ProductResponseDTO.class)))
                .thenReturn(EntityModel.of(responseDTO));

        CollectionModel<EntityModel<ProductResponseDTO>> resultado =
                productController.getProductByCategory("ELECTRONICA");

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertTrue(resultado.getLink("self").isPresent());
    }

    @Test
    @DisplayName("getProductByCategory - Sin resultados debe retornar coleccion vacia")
    void getProductByCategory_WhenEmpty_ShouldReturnEmptyCollection() {
        when(productService.getProductByCategory("Carnes")).thenReturn(List.of());

        CollectionModel<EntityModel<ProductResponseDTO>> resultado =
                productController.getProductByCategory("Carnes");

        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
    }

    @Test
    @DisplayName("crearProduct - Debe retornar 201 con el producto creado")
    void crearProduct_ShouldReturn201() {
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<?> resultado = productController.crearProduct(requestDTO);

        assertEquals(201, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("crearProduct - Error de negocio debe retornar 404")
    void crearProduct_WhenServiceThrows_ShouldReturn404() {
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenThrow(new RuntimeException("No se pudo crear el producto"));

        ResponseEntity<?> resultado = productController.crearProduct(requestDTO);

        assertEquals(404, resultado.getStatusCode().value());
        assertEquals("No se pudo crear el producto", resultado.getBody());
    }

    @Test
    @DisplayName("update - Debe retornar 200 con el producto actualizado")
    void update_ShouldReturnUpdatedProduct() {
        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<?> resultado = productController.update(1L, requestDTO);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals(responseDTO, resultado.getBody());
    }

    @Test
    @DisplayName("update - Producto inexistente debe retornar 404")
    void update_WhenNotFound_ShouldReturn404() {
        when(productService.updateProduct(eq(99L), any(ProductRequestDTO.class)))
                .thenThrow(new RuntimeException("Producto no encontrado: 99"));

        ResponseEntity<?> resultado = productController.update(99L, requestDTO);

        assertEquals(404, resultado.getStatusCode().value());
        assertEquals("Producto no encontrado: 99", resultado.getBody());
    }

    @Test
    @DisplayName("delete - Debe retornar 200 con mensaje de exito")
    void delete_ShouldReturnOk() {
        doNothing().when(productService).deleteProduct(1L);

        ResponseEntity<?> resultado = productController.delete(1L);

        assertEquals(200, resultado.getStatusCode().value());
        assertEquals("Eliminado con Exito.", resultado.getBody());
        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("delete - Producto inexistente debe retornar 404")
    void delete_WhenNotFound_ShouldReturn404() {
        doThrow(new RuntimeException("Producto no encontrado: 99"))
                .when(productService).deleteProduct(99L);

        ResponseEntity<?> resultado = productController.delete(99L);

        assertEquals(404, resultado.getStatusCode().value());
        assertEquals("Producto no encontrado: 99", resultado.getBody());
    }

    @Test
    @DisplayName("checkStock - Debe retornar true cuando hay stock suficiente")
    void checkStock_ShouldReturnTrue() {
        when(productService.checkStock(1L, 10)).thenReturn(true);

        ResponseEntity<Boolean> resultado = productController.checkStock(1L, 10);

        assertEquals(200, resultado.getStatusCode().value());
        assertTrue(resultado.getBody());
    }

    @Test
    @DisplayName("checkStock - Debe retornar false cuando no hay stock suficiente")
    void checkStock_ShouldReturnFalse() {
        when(productService.checkStock(1L, 999)).thenReturn(false);

        ResponseEntity<Boolean> resultado = productController.checkStock(1L, 999);

        assertEquals(200, resultado.getStatusCode().value());
        assertFalse(resultado.getBody());
    }

    @Test
    @DisplayName("reduceStock - Debe retornar 200 al reducir correctamente")
    void reduceStock_ShouldReturnOk() {
        doNothing().when(productService).reduceStock(1L, 5);

        ResponseEntity<Void> resultado = productController.reduceStock(1L, 5);

        assertEquals(200, resultado.getStatusCode().value());
        verify(productService, times(1)).reduceStock(1L, 5);
    }
}