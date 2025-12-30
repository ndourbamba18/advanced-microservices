package net.accel_tech.product_service.repositories;

import net.accel_tech.product_service.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsProductByName(String name);
    Product findProductByName(String name);
    // Optionnel : trouver les produits par catégorie
    List<Product> findByCategoryId(Long categoryId);
}