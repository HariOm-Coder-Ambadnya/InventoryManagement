package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.Entity.Product;
import com.InvetoryManagement.InventoryManagement.Exception.ResourceNotFoundException;
import com.InvetoryManagement.InventoryManagement.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<Product> getProductByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getallProduct() {
        return productRepository.findAll();
    }

    public Product updateProduct(String id, Product updatedproduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(updatedproduct.getName());
        product.setCategory(updatedproduct.getCategory());
        product.setDescription(updatedproduct.getDescription());
        product.setMinstock(updatedproduct.getMinstock());

        return productRepository.save(product);
    }

    public String deleteProduct(String id) {
        Product deleteproduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.deleteById(id);
        return "Deleted";
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }
}
