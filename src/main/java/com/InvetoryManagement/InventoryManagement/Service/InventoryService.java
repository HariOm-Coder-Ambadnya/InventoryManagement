package com.InvetoryManagement.InventoryManagement.Service;

import com.InvetoryManagement.InventoryManagement.DTO.StockRequest;
import com.InvetoryManagement.InventoryManagement.Entity.Product;
import com.InvetoryManagement.InventoryManagement.Entity.StockHistory;
import com.InvetoryManagement.InventoryManagement.Entity.StockType;
import com.InvetoryManagement.InventoryManagement.Repository.ProductRepository;
import com.InvetoryManagement.InventoryManagement.Repository.StockHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockHistoryRepository stockHistoryRepository;

    public InventoryService(ProductRepository productRepository,
                            StockHistoryRepository stockHistoryRepository) {

        this.productRepository = productRepository;
        this.stockHistoryRepository = stockHistoryRepository;
    }

    public StockHistory updateStock(String productId,
                                    StockRequest request) {

        Product product = productRepository.findById(productId)
                .orElse(null);

        if (product == null) {
            return null;
        }

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        int currentQuantity = product.getQuantity();

        if (request.getType() == StockType.STOCK_IN
                || request.getType() == StockType.RETURN) {

            product.setQuantity(
                    currentQuantity + request.getQuantity()
            );

        } else {

            if (currentQuantity < request.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            product.setQuantity(
                    currentQuantity - request.getQuantity()
            );
        }

        productRepository.save(product);

        StockHistory history = StockHistory.builder()
                .product(product)
                .type(request.getType())
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .build();

        return stockHistoryRepository.save(history);
    }

    public List<StockHistory> getProductStockHistory(String productId) {

        return stockHistoryRepository.findByProductId(productId);
    }

    public List<StockHistory> getAllStockHistory() {

        return stockHistoryRepository.findAll();
    }
}