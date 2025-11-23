package com.example.onlineshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import com.example.onlineshop.services.ShopItemService;

@RunWith(SpringRunner.class)
@DataMongoTest
@Import(ShopItemService.class)
public class ShopItemServiceRepositoryIT {

    @Autowired
    private ShopItemService shopItemService;

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Before
    public void setup() {
        shopItemRepository.deleteAll();
    }

    @Test
    public void testServiceCanInsertIntoRepository() {
        // Create new item
        ShopItem item = new ShopItem("Laptop", "Gaming laptop", 1499.99, 5);
        
        // Insert via service
        ShopItem savedItem = shopItemService.insertNewShopItem(item);
        
        // Verify in repository
        assertThat(shopItemRepository.findById(savedItem.getId()))
            .isPresent()
            .get()
            .hasFieldOrPropertyWithValue("name", "Laptop")
            .hasFieldOrPropertyWithValue("price", 1499.99);
    }

    @Test
    public void testServiceCanUpdateRepository() {
        // Insert initial item
        ShopItem item = new ShopItem("Mouse", "Basic mouse", 29.99, 50);
        ShopItem savedItem = shopItemRepository.save(item);
        
        // Update via service
        ShopItem updatedItem = new ShopItem("Mouse", "Gaming mouse", 79.99, 30);
        shopItemService.updateShopItemById(savedItem.getId(), updatedItem);
        
        // Verify update in repository
        assertThat(shopItemRepository.findById(savedItem.getId()))
            .isPresent()
            .get()
            .hasFieldOrPropertyWithValue("description", "Gaming mouse")
            .hasFieldOrPropertyWithValue("price", 79.99)
            .hasFieldOrPropertyWithValue("quantity", 30);
    }

    @Test
    public void testServiceCanDeleteFromRepository() {
        // Insert item
        ShopItem item = new ShopItem("Keyboard", "Mechanical", 149.99, 10);
        ShopItem savedItem = shopItemRepository.save(item);
        String itemId = savedItem.getId();
        
        // Delete via service
        shopItemService.deleteShopItem(itemId);
        
        // Verify deletion
        assertThat(shopItemRepository.findById(itemId)).isNotPresent();
    }
}
