package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.Entity.Product;
import com.InvetoryManagement.InventoryManagement.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(String id){
        Optional<Product> product = productRepository.findById(id);

        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product no found"));
    }

    public List<Product> getProductByCategory(String category){
        return productRepository.findByCategory(category);
    }

    public List<Product> getallProduct(){
        return productRepository.findAll();
    }

    public Product updateProduct(String id, Product updatedproduct) {

        Product product = productRepository.findById(id)
                .orElse(null);

        if (product == null) {
            return null;
        }

        product.setName(updatedproduct.getName());
        product.setCategory(updatedproduct.getCategory());
        product.setDescription(updatedproduct.getDescription());
        product.setMinstock(updatedproduct.getMinstock());

        return productRepository.save(product);
    }

    public String deleteProduct (String id){

        Product deleteproduct = productRepository.findById(id).orElse(null);

        if(deleteproduct == null){
            return null;
        }

        productRepository.deleteById(id);

        return "Deleted";
    }

    public Product addProduct(Product product){

        return productRepository.save(product);
    }

}
