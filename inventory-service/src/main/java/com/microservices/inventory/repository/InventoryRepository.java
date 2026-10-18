package com.microservices.inventory.repository;

import com.microservices.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    
    Optional<InventoryItem> findBySkuCode(String skuCode);
    
    List<InventoryItem> findBySkuCodeIn(List<String> skuCodes);
}
