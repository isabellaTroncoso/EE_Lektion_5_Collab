package com.krillinator.demo_5;
import com.krillinator.demo_5.product.ProductRepository;
import com.krillinator.demo_5.product.ProductService;
import com.krillinator.demo_5.product.dto.ProductValidatorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;
import com.krillinator.demo_5.product.Product;


import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clearDatabase() {
        productRepository.deleteAll().block(); // .block() så att rensningen sker synkront
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldCreateProductAndRetrieveIt() {
        ProductValidatorDTO productValidatorDTO = new ProductValidatorDTO(
                "Pears",
                "Delicious pears from the South",
                BigDecimal.valueOf(19.99),
                false
        );

        StepVerifier.create(
                        productService.createNewProduct(productValidatorDTO)
                                .flatMap(product -> productRepository.findById(product.id()))
                )
                .expectNextMatches(product -> product.name().equals("Pears"))
                .verifyComplete();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldDeleteProduct() {
        ProductValidatorDTO dto = new ProductValidatorDTO(
                "Bananas",
                "Tropical and sweet",
                BigDecimal.valueOf(29.90),
                true
        );

        Long id = productService.createNewProduct(dto)
                .map(Product::id)
                .block();

        // Radera produkten
        StepVerifier.create(productService.deleteProductById(id))
                .verifyComplete();

        // Och sen kontrollera att produkten inte längre finns
        StepVerifier.create(productService.getAllProducts())
                .expectNextCount(0)
                .verifyComplete();
    }
}
