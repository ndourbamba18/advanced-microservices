package net.accel_tech.category_service.repository;

import net.accel_tech.category_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsCategoryByName(String name);
    Category findCategoryByName(String name);
}