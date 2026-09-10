package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.Entity.Category;
import com.InvetoryManagement.InventoryManagement.Service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> addCategory(
            @RequestBody Category category) {

        return ResponseEntity.ok(
                categoryService.addCategory(category)
        );
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(
            @PathVariable String id) {

        Category category = categoryService.getCategoryById(id);

        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable String id,
            @RequestBody Category updatedCategory) {

        Category category =
                categoryService.updateCategory(id, updatedCategory);

        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(category);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable String id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}