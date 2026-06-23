package inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proyecto.inventario.dto.ProductMapper;
import proyecto.inventario.dto.ProductRequestDTO;
import proyecto.inventario.dto.ProductResponseDTO;
import proyecto.inventario.model.Product;
import proyecto.inventario.repository.ProductRepository;
import proyecto.inventario.service.ProductService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponseDTO responseDTO;
    private ProductRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Leche Entera", "Leche entera 1L",
                1200, 50, Product.Categorias.Lacteos);

        responseDTO = new ProductResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombre("Leche Entera");
        responseDTO.setDescripcion("Leche entera 1L");
        responseDTO.setPrecio(1200);
        responseDTO.setStock(50);
        responseDTO.setCategoria(Product.Categorias.Lacteos);

        requestDTO = new ProductRequestDTO();
        requestDTO.setNombre("Leche Entera");
        requestDTO.setDescripcion("Leche entera 1L");
        requestDTO.setPrecio(1200);
        requestDTO.setStock(50);
        requestDTO.setCategoria(Product.Categorias.Lacteos);
    }

    @Test
    void getProducts_deberiaRetornarListaDeProductos() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(mapper.toResponse(product)).thenReturn(responseDTO);

        List<ProductResponseDTO> resultado = productService.getProducts();

        assertEquals(1, resultado.size());
        assertEquals("Leche Entera", resultado.get(0).getNombre());
    }

    @Test
    void getProductById_cuandoExiste_deberiaRetornarProducto() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mapper.toResponse(product)).thenReturn(responseDTO);

        ProductResponseDTO resultado = productService.getProductById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Leche Entera", resultado.getNombre());
    }

    @Test
    void getProductById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.getProductById(99L));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void createProduct_deberiaCrearYRetornarProducto() {
        when(mapper.toEntity(requestDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(mapper.toResponse(product)).thenReturn(responseDTO);

        ProductResponseDTO resultado = productService.createProduct(requestDTO);

        assertNotNull(resultado);
        assertEquals("Leche Entera", resultado.getNombre());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_cuandoExiste_deberiaActualizarYRetornarProducto() {
        ProductRequestDTO update = new ProductRequestDTO();
        update.setNombre("Leche Descremada");
        update.setDescripcion("Leche descremada 1L");
        update.setPrecio(1300);
        update.setStock(40);
        update.setCategoria(Product.Categorias.Lacteos);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            ProductResponseDTO dto = new ProductResponseDTO();
            dto.setId(p.getId());
            dto.setNombre(p.getNombre());
            dto.setPrecio(p.getPrecio());
            dto.setStock(p.getStock());
            return dto;
        });

        ProductResponseDTO resultado = productService.updateProduct(1L, update);

        assertEquals("Leche Descremada", resultado.getNombre());
        assertEquals(1300, resultado.getPrecio());
        assertEquals(40, resultado.getStock());
    }

    @Test
    void updateProduct_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.updateProduct(99L, requestDTO));

        assertTrue(ex.getMessage().contains("Producto no encontrado"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_deberiaEliminarProducto() {
        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void checkStock_cuandoHayStockSuficiente_deberiaRetornarTrue() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean resultado = productService.checkStock(1L, 10);

        assertTrue(resultado);
    }

    @Test
    void checkStock_cuandoNoHayStockSuficiente_deberiaRetornarFalse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        boolean resultado = productService.checkStock(1L, 999);

        assertFalse(resultado);
    }

    @Test
    void reduceStock_cuandoHayStockSuficiente_deberiaReducirStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        productService.reduceStock(1L, 20);

        assertEquals(30, product.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void reduceStock_cuandoStockEsInsuficiente_deberiaLanzarExcepcion() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.reduceStock(1L, 999));

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(productRepository, never()).save(any(Product.class));
    }
}