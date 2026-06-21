package inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import proyecto.inventario.assemblers.ProductModelAssembler;
import proyecto.inventario.dto.ProductRequestDTO;
import proyecto.inventario.dto.ProductResponseDTO;
import proyecto.inventario.model.Product;
import proyecto.inventario.service.ProductService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = proyecto.inventario.InventarioApplication.class)
class ProductControllerTest {

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductModelAssembler assembler;

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
    @DisplayName("GET /api/products - Debe retornar todos los productos con HATEOAS")
    void getAllProduct_ShouldReturnCollectionWithHateoas() throws Exception {
        when(productService.getProducts()).thenReturn(List.of(responseDTO));
        when(assembler.toModel(any(ProductResponseDTO.class)))
                .thenReturn(EntityModel.of(responseDTO));

        mvc.perform(get("/api/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    @DisplayName("GET /api/products/{id} - Debe retornar un producto")
    void getById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(responseDTO);
        when(assembler.toModel(any())).thenReturn(EntityModel.of(responseDTO));

        mvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value(responseDTO.getNombre()));
    }

    @Test
    @DisplayName("GET /api/products/categoria/{categoria} - Debe filtrar por categoría")
    void getProductByCategory_ShouldReturnFilteredProducts() throws Exception {
        when(productService.getProductByCategory("ELECTRONICA"))
                .thenReturn(List.of(responseDTO));

        mvc.perform(get("/api/products/categoria/{categoria}", "ELECTRONICA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("ELECTRONICA"));
    }

    @Test
    @DisplayName("POST /api/products - Debe crear producto correctamente")
    void crearProduct_ShouldReturn201() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenReturn(responseDTO);

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value(requestDTO.getNombre()));
    }

    @Test
    @DisplayName("POST /api/products - Validación: nombre vacío debe fallar")
    void crearProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        requestDTO.setNombre("");  // inválido

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id} - Debe actualizar producto")
    void update_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductRequestDTO.class)))
                .thenReturn(responseDTO);

        mvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} - Debe eliminar producto")
    void delete_ShouldReturnOk() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Eliminado con Exito."));
    }

    @Test
    @DisplayName("GET /api/products/{id}/check-stock - Verificar stock")
    void checkStock_ShouldReturnTrue() throws Exception {
        when(productService.checkStock(1L, 10)).thenReturn(true);

        mvc.perform(get("/api/products/{id}/check-stock", 1L)
                        .param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("PUT /api/products/{id}/reduce-stock - Reducir stock")
    void reduceStock_ShouldReturnOk() throws Exception {
        doNothing().when(productService).reduceStock(1L, 5);

        mvc.perform(put("/api/products/{id}/reduce-stock", 1L)
                        .param("cantidad", "5"))
                .andExpect(status().isOk());
    }
}