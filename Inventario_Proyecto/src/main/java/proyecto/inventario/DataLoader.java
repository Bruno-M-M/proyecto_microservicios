package proyecto.inventario;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import proyecto.inventario.model.Product;
import proyecto.inventario.repository.ProductRepository;

import java.util.List;
import java.util.Random;

@Profile("test")//"test" para que funcione el test
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception{
        Faker faker = new Faker();
        Random random = new Random();
        Product.Categorias[] categorias = Product.Categorias.values();

        for (int i = 0; i < 10; i++){
            Product product = new Product();
            product.setNombre(faker.name().name());
            product.setDescripcion(faker.pokemon().name());
            product.setPrecio(random.nextInt(1, 99999999));
            product.setStock(random.nextInt(10,9999));
            product.setCategoria(categorias[random.nextInt(categorias.length)]);
            productRepository.save(product);
        }

        List<Product> productList = productRepository.findAll();
    }

}
