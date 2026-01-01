package net.accel_tech.product_service.controllers;

import jakarta.validation.Valid;
import net.accel_tech.product_service.dto.ApiResponse;
import net.accel_tech.product_service.dto.DeleteProductResponseDto;
import net.accel_tech.product_service.dto.ProductDto;
import net.accel_tech.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
        List<ProductDto> list = productService.findAllProducts();
        list = list.stream()
                .sorted(Comparator.comparing(ProductDto::getName).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
        return ResponseEntity.ok(new ApiResponse<>(true, list));
    }

    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> createNewProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto creatingProduct = productService.addProduct(productDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, creatingProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        ProductDto productDto = productService.findProductById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, productDto));
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ProductDto>> updateProductById(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(new ApiResponse<>(true, updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteProductResponseDto>> deleteProductById(@PathVariable Long id) {
        Long deletedId = productService.deleteProductById(id);
        DeleteProductResponseDto response = new DeleteProductResponseDto();
        response.setId(deletedId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<ProductDto> products = productService.findProductsByCategoryId(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(true, products));
    }

}