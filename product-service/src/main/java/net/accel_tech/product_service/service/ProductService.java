package net.accel_tech.product_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.accel_tech.product_service.dto.ProductDto;
import net.accel_tech.product_service.entities.Product;
import net.accel_tech.product_service.exception.BadRequestException;
import net.accel_tech.product_service.exception.ResourceNotFoundException;
import net.accel_tech.product_service.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("productService")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> findAllProducts() {
        List<ProductDto>  productDtos = new ArrayList<>();
        List<Product> list = productRepository.findAll();
        list.stream()
                .forEach(product -> {
                    ProductDto productDto = mapEntityToDto(product);
                    productDtos.add(productDto);
                });
        return productDtos;
    }

    public ProductDto addProduct(ProductDto dto) {
        if (productRepository.existsProductByName(dto.getName())) {
            throw new BadRequestException("Product with name '" + dto.getName() + "' already exists");
        }
        Product product = new Product();
        mapDtoToEntity(dto, product);
        return mapEntityToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(Long id, ProductDto updateDto) {
        if(!this.productRepository.existsById(id)){
            throw  new ResourceNotFoundException("Product not found with id : "+id);
        }
        if(this.productRepository.existsProductByName(updateDto.getName())
                && this.productRepository.findProductByName(updateDto.getName()).getId() != id){
            throw new BadRequestException("Category with name '" + updateDto.getName() + "' already exists");
        }
        Product findProduct = findById(id);
        mapDtoToEntity(updateDto, findProduct);
        Product updatedProduct = productRepository.save(findProduct);
        return mapEntityToDto(updatedProduct);
    }

    public Long deleteProductById(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
        return id;
    }

    public ProductDto findProductById(Long id) {
        Product findingProduct = findById(id);
        return mapEntityToDto(findingProduct);
    }

    public Product findById(Long id){
        return productRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Product not found with id:"+id));
    }

    private ProductDto mapEntityToDto(Product product){
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setImage(product.getImage());
        productDto.setDescription(product.getDescription());
        productDto.setQuantity(product.getQuantity());
        productDto.setCategoryId(product.getCategoryId());
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());
        return productDto;
    }

    private void mapDtoToEntity(ProductDto productDto, Product product){
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setImage(productDto.getImage());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());
        product.setCategoryId(productDto.getCategoryId());
        //product.setCreatedAt(new Date()); // On ne doit pas modifier la date de création
        product.setUpdatedAt(productDto.getUpdatedAt());
    }
}
