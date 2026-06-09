package proyecto.inventario.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import proyecto.inventario.model.Product;
import proyecto.inventario.repository.ProductRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository repository;

    @Override
    public void run(String... args){
        if (repository.count()>0){
            log.info(">>> Productos ya cargados. Se omite inicializacion");
            return;
        }
        repository.save(new Product(null,"Chocapic", "Cereal sabor chocolate 400gr", 2990, 50, Product.Categorias.Despensa));
        repository.save(new Product(null,"Nescafe seleccion", "Cafe instantaneo soluble 200gr", 12990, 40, Product.Categorias.Despensa));
        repository.save(new Product(null, "Poett frescura lavanda", "Limpiapiso con aroma a lavanda 900ml", 1730, 64, Product.Categorias.Limpieza));
        repository.save(new Product(null, "Leche Colun entera", "Leche colun entera obtenida a partir de leche fresca 1lt", 1380, 85, Product.Categorias.Lacteos));
        repository.save(new Product(null, "Filetitos de pollo", "Filetitos de pechuga de pollo 700gr", 7490, 36, Product.Categorias.Carnes));
        log.info(">>> 5 Productos cargados Ok.");
    }
}
