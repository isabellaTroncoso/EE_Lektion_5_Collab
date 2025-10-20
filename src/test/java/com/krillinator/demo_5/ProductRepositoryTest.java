package com.krillinator.demo_5;
import com.krillinator.demo_5.product.Product;
import com.krillinator.demo_5.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void clearDatabase() {
        productRepository.deleteAll().block();
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
        // Clarity
    void shouldSaveAndRetrieveProduct() {
        Product product_apples = new Product(
                null,
                "Apples",
                "Fresh apples from the North",
                BigDecimal.valueOf(19.9),
                false,
                null
        );

        Product savedproduct = productRepository.save(product_apples).block();

        Assertions.assertNotNull(savedproduct);

        // Async Controlled Environment
        StepVerifier.create(
                        productRepository.findById(savedproduct.id())
                )
                .expectNextMatches(product -> product.name().equals("Apples"))
                .verifyComplete();

        /* --Code Example-- Same as above code: Using JUnit assertions instead

            StepVerifier.create(
                productRepository.findById(savedproduct.id())
                )
                .assertNext(product -> Assertions.assertEquals("Apples", product.name()))
                .verifyComplete();

        */

        /* You can use .flatMap to chain a response instead of using .block()
         *   A bit Harder to debug
         *   But quicker to write and is async
         * */

        /* --Code Example--
        StepVerifier.create(
                productRepository.save(product_apples)
                        .flatMap(product -> productRepository.findById(product.id())
                        )
        )
                .expectNextMatches(product -> product.name().equals("Apples"))
                .verifyComplete();
         */

    }
}
