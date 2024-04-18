package com.sa.clothingstore.repository.product;

import com.sa.clothingstore.model.product.Product;
import com.sa.clothingstore.model.product.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductItemRepository extends JpaRepository<ProductItem, UUID> {
    @Query("SELECT pi.id FROM ProductItem pi WHERE pi.product = ?1 AND pi.size.id = ?2 AND pi.color.id = ?3")
    UUID getProductItemByProductAndAttribute(Product product, int sizeId, int colorId);

    @Query("SELECT pi FROM ProductItem pi WHERE pi.product.id = ?1")
    List<ProductItem> getProductItemByProduct(UUID productId);
}
