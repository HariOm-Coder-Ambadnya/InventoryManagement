package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.Entity.Category;
import com.InvetoryManagement.InventoryManagement.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Create category
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get category by ID
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElse(null);
    }

    // Update category
    public Category updateCategory(String id, Category updatedCategory) {

        Category category = categoryRepository.findById(id)
                .orElse(null);

        if (category == null) {
            return null;
        }

        category.setName(updatedCategory.getName());
        category.setDescription(updatedCategory.getDescription());
        category.setActive(updatedCategory.isActive());

        return categoryRepository.save(category);
    }

    // Delete category
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
}
