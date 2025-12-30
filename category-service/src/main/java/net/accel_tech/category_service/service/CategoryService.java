package net.accel_tech.category_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.accel_tech.category_service.dto.CategoryDto;
import net.accel_tech.category_service.entity.Category;
import net.accel_tech.category_service.exception.BadRequestException;
import net.accel_tech.category_service.exception.ResourceNotFoundException;
import net.accel_tech.category_service.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("categoryService")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> findAllCategories() {
        List<CategoryDto>  categoryDtos = new ArrayList<>();
        List<Category> list = categoryRepository.findAll();
        list.stream()
                .forEach(category -> {
                    CategoryDto categoryDto = mapEntityToDto(category);
                    categoryDtos.add(categoryDto);
                });
        return categoryDtos;
    }

    public CategoryDto addCategory(CategoryDto dto) {
        if (categoryRepository.existsCategoryByName(dto.getName())) {
            throw new BadRequestException("Category with name '" + dto.getName() + "' already exists");
        }
        Category category = new Category();
        mapDtoToEntity(dto, category);
        return mapEntityToDto(categoryRepository.save(category));
    }

    public CategoryDto updateCategory(Long id, CategoryDto updateDto) {
        if(!this.categoryRepository.existsById(id)){
            throw  new ResourceNotFoundException("Category not found with id : "+id);
        }
        if(this.categoryRepository.existsCategoryByName(updateDto.getName())
                && this.categoryRepository.findCategoryByName(updateDto.getName()).getId() != id){
            throw new BadRequestException("Category with name '" + updateDto.getName() + "' already exists");
        }
        Category findCategory = findById(id);
        mapDtoToEntity(updateDto, findCategory);
        Category updatedCategory = categoryRepository.save(findCategory);
        return mapEntityToDto(updatedCategory);
    }

    public Long deleteCategoryById(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
        return id;
    }

    public CategoryDto findCategoryById(Long id) {
        Category findingCategory = findById(id);
        return mapEntityToDto(findingCategory);
    }

    public Category findById(Long id){
        return categoryRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Category not found with id:"+id));
    }

    private CategoryDto mapEntityToDto(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setCreatedAt(category.getCreatedAt());
        categoryDto.setUpdatedAt(category.getUpdatedAt());
        return categoryDto;
    }

    private void mapDtoToEntity(CategoryDto categoryDto, Category category){
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        //category.setCreatedAt(new Date()); // On ne doit pas modifier la date de création
        category.setUpdatedAt(categoryDto.getUpdatedAt());
    }
}
