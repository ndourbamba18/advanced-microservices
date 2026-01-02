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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("productService")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final String CATEGORY_URL = "http://CATEGORY-SERVICE/api/categories/";


    private String fetchCategoryName(Long categoryId) {
        if (categoryId == null) return null;
        try {
            Map<String, Object> response = restTemplate.getForObject(CATEGORY_URL + "/" + categoryId, Map.class);

            if (response != null && (Boolean) response.get("success")) {
                Map<String, Object> categoryData = (Map<String, Object>) response.get("data");
                return (String) categoryData.get("name");
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du nom de la catégorie: {}", e.getMessage());
        }
        return "Inconnue"; // Valeur par défaut en cas d'erreur
    }

    public List<ProductDto> findAllProducts() {
        List<Product> list = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();

        for (Product product : list) {
            ProductDto dto = mapEntityToDto(product);

            // Pour chaque produit, on enrichit (Optionnel : voir note sur la performance)
            if (product.getCategoryId() != null) {
                try {
                    Map<String, Object> response = restTemplate.getForObject(CATEGORY_URL + product.getCategoryId(), Map.class);

                    if (response != null && (Boolean) response.get("success")) {
                        Map<String, Object> categoryData = (Map<String, Object>) response.get("data");
                        dto.setCategoryName((String) categoryData.get("name"));
                    }
                } catch (Exception e) {
                    dto.setCategoryName("Inconnue");
                }
            }
            productDtos.add(dto);
        }
        return productDtos;
    }

    public ProductDto addProduct(ProductDto dto) {
        // 1. Validation locale de l'unicité du nom du produit
        if (productRepository.existsProductByName(dto.getName())) {
            throw new BadRequestException("Product with name '" + dto.getName() + "' already exists");
        }

        // 2. Communication Inter-service pour transformer le NOM en ID
        if (dto.getCategoryName() != null && !dto.getCategoryName().trim().isEmpty()) {
            try {
                // On appelle le endpoint du category-service : /api/categories/name/{name}
                Map<String, Object> response = restTemplate.getForObject(CATEGORY_URL + "/name/" + dto.getCategoryName(), Map.class);

                if (response != null && (Boolean) response.get("success")) {
                    Map<String, Object> categoryData = (Map<String, Object>) response.get("data");

                    // On récupère l'ID technique de la catégorie
                    Long foundId = Long.valueOf(categoryData.get("id").toString());

                    // On met à jour le DTO avec l'ID trouvé pour le mapping vers l'entité
                    dto.setCategoryId(foundId);

                    log.info("Catégorie '{}' trouvée. ID assigné : {}", dto.getCategoryName(), foundId);
                } else {
                    throw new ResourceNotFoundException("Category not found with name : '" + dto.getCategoryName() + "'.");
                }
            } catch (Exception e) {
                log.error("Erreur lors de la récupération de la catégorie par nom : {}", e.getMessage());
                throw new BadRequestException("Impossible de valider la catégorie : " + dto.getCategoryName());
            }
        } else {
            throw new BadRequestException("Le nom de la catégorie est obligatoire.");
        }

        // 3. Mapping et Sauvegarde
        Product product = new Product();
        mapDtoToEntity(dto, product);
        Product savedProduct = productRepository.save(product);

        // 4. Retour du DTO enrichi
        ProductDto resultDto = mapEntityToDto(savedProduct);

        // CORRECTION ICI : On reprend le nom qui était dans le dto d'entrée
        resultDto.setCategoryName(dto.getCategoryName());

        return resultDto;
    }

    public ProductDto updateProduct(Long id, ProductDto updateDto) {
        // 1. Vérifier si le produit existe
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id : " + id));

        // 2. Vérifier l'unicité du nom (si le nom a changé)
        if (productRepository.existsProductByName(updateDto.getName())
                && !existingProduct.getName().equals(updateDto.getName())) {
            throw new BadRequestException("Product with name '" + updateDto.getName() + "' already exists");
        }

        // 3. Validation de la catégorie par NOM (Communication Inter-service)
        if (updateDto.getCategoryName() != null && !updateDto.getCategoryName().trim().isEmpty()) {
            try {
                Map<String, Object> response = restTemplate.getForObject(CATEGORY_URL + "/name/" + updateDto.getCategoryName(), Map.class);

                if (response != null && (Boolean) response.get("success")) {
                    Map<String, Object> categoryData = (Map<String, Object>) response.get("data");
                    Long foundId = Long.valueOf(categoryData.get("id").toString());

                    // On met à jour l'ID technique dans le DTO pour le mapping
                    updateDto.setCategoryId(foundId);
                } else {
                    throw new ResourceNotFoundException("La catégorie '" + updateDto.getCategoryName() + "' n'existe pas.");
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'update : {}", e.getMessage());
                throw new BadRequestException("Mise à jour impossible : catégorie '" + updateDto.getCategoryName() + "' invalide.");
            }
        }

        // 4. Mapping des nouvelles valeurs sur l'entité existante
        mapDtoToEntity(updateDto, existingProduct);

        // Sécurité : s'assurer que l'ID ne change pas
        existingProduct.setId(id);

        Product updatedProduct = productRepository.save(existingProduct);

        // 5. Retourner le DTO mis à jour
        ProductDto resultDto = mapEntityToDto(updatedProduct);
        resultDto.setCategoryName(updateDto.getCategoryName()); // On garde le nom pour le retour

        return resultDto;
    }

    public ProductDto findProductById(Long id) {
        Product product = findById(id);
        ProductDto dto = mapEntityToDto(product);

        // Enrichissement : On va chercher le nom de la catégorie
        if (product.getCategoryId() != null) {
            try {
                Map<String, Object> response = restTemplate.getForObject(CATEGORY_URL + product.getCategoryId(), Map.class);

                if (response != null && (Boolean) response.get("success")) {
                    // On descend dans l'objet "data" du JSON pour prendre le "name"
                    Map<String, Object> categoryData = (Map<String, Object>) response.get("data");
                    dto.setCategoryName((String) categoryData.get("name"));
                }
            } catch (Exception e) {
                log.error("Impossible de récupérer le nom de la catégorie : {}", e.getMessage());
                dto.setCategoryName("Inconnue");
            }
        }
        return dto;
    }

    // Dans ProductService.java

    public List<ProductDto> findProductsByCategoryId(Long categoryId) {
        // 1. Récupération de la liste brute depuis la BDD locale
        List<Product> products = productRepository.findByCategoryId(categoryId);

        // 2. Si la liste est vide, on peut soit retourner une liste vide,
        // soit lever une exception selon ta préférence
        if (products.isEmpty()) {
            log.info("Aucun produit trouvé pour la catégorie ID : {}", categoryId);
            return new ArrayList<>();
        }

        // 3. Transformation en DTO avec enrichissement (le nom de la catégorie)
        List<ProductDto> productDtos = new ArrayList<>();

        // On récupère le nom une seule fois pour toute la liste pour optimiser un peu
        String categoryName = fetchCategoryName(categoryId);

        products.forEach(product -> {
            ProductDto dto = mapEntityToDto(product);
            // On s'assure que le nom est bien setté (fetchCategoryName est déjà dans mapEntityToDto)
            dto.setCategoryName(categoryName);
            productDtos.add(dto);
        });

        return productDtos;
    }

    public Long deleteProductById(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
        return id;
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
        productDto.setCreatedAt(product.getCreatedAt());
        productDto.setUpdatedAt(product.getUpdatedAt());
        productDto.setCategoryId(product.getCategoryId());

        // On va chercher le nom pour remplir le champ categoryName
        //productDto.setCategoryName(fetchCategoryName(product.getCategoryId()));
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
