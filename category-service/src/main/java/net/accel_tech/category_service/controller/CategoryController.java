package net.accel_tech.category_service.controller;

import jakarta.validation.Valid;
import net.accel_tech.category_service.dto.ApiResponse;
import net.accel_tech.category_service.dto.CategoryDto;
import net.accel_tech.category_service.dto.DeleteCategoryResponseDto;
import net.accel_tech.category_service.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> list = categoryService.findAllCategories();
        list = list.stream()
                .sorted(Comparator.comparing(CategoryDto::getName).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        return ResponseEntity.ok(new ApiResponse<>(true, list));
    }

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CategoryDto>> createNewCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.addCategory(categoryDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, createdCategory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryById(@PathVariable Long id) {
        CategoryDto categoryDto = categoryService.findCategoryById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, categoryDto));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryByName(@PathVariable String name) {
        CategoryDto categoryDto = categoryService.findCategoryByName(name);
        return ResponseEntity.ok(new ApiResponse<>(true, categoryDto));
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto updatedCategory = categoryService.updateCategory(id, categoryDto);
        return ResponseEntity.ok(new ApiResponse<>(true, updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteCategoryResponseDto>> deleteCategory(@PathVariable Long id) {
        Long deletedId = categoryService.deleteCategoryById(id);
        DeleteCategoryResponseDto response = new DeleteCategoryResponseDto();
        response.setId(deletedId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }
}