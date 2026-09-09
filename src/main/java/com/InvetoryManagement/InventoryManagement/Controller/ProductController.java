package com.InvetoryManagement.InventoryManagement.Controller;

import com.InvetoryManagement.InventoryManagement.Entity.Product;
import com.InvetoryManagement.InventoryManagement.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    //Done
    @GetMapping("/{id}")
    public ResponseEntity<Product> getproductbyid(@PathVariable String id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    //Done
    @PostMapping
    public String addproduct(@RequestBody Product product){
        productService.addProduct(product);
        return "added successfully";
    }

    //Done
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getproductbycategory(@PathVariable String category){
        return ResponseEntity.ok(productService.getProductByCategory(category));
    }

    //Done
    @GetMapping
    public ResponseEntity<List<Product>> getallProduct(){
        return ResponseEntity.ok(productService.getallProduct());
    }

    //Done
    @DeleteMapping("/{id}")
    public String deletedproduct(@PathVariable String id){

        productService.deleteProduct(id);

        return "Deleted successfully";
    }

    //Done
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateproduct(@PathVariable String id , @RequestBody Product product){

        Product updatedproduct = productService.updateProduct(id,product);

        return ResponseEntity.ok(updatedproduct);


    }

}
