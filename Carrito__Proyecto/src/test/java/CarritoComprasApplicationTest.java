package Carrito_compras;

import com.Carrito.Carrito_compras.CarritoComprasApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.Carrito.Carrito_compras.Client.ClienteFeingClient;
import com.Carrito.Carrito_compras.Client.InventarioFeingClient;

@SpringBootTest(classes = CarritoComprasApplication.class)
class CarritoComprasApplicationTest {

	@MockitoBean
	private ClienteFeingClient clienteFeingClient;

	@MockitoBean
	private InventarioFeingClient inventarioFeingClient;

	@Test
	void contextLoads() {
	}
}