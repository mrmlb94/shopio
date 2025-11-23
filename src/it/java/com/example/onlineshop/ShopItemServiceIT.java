package com.example.onlineshop;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import com.example.onlineshop.services.ShopItemService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = OnlineshopApplication.class)

class ShopItemServiceIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ShopItemService shopItemService;

    @Autowired
    private ShopItemRepository shopItemRepository;

    private ShopItem testItem;

    @BeforeEach
    void setUp() {
        shopItemRepository.deleteAll();
        testItem = new ShopItem("Tablet", "Android tablet", 299.99, 20);
        shopItemRepository.save(testItem);
    }

    @AfterEach
    void tearDown() {
        shopItemRepository.deleteAll();
    }

    @Test
    void testGetAllItems() {
        List<ShopItem> items = shopItemService.getAllItems();
        
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Tablet");
    }

    @Test
    void testGetItemById() {
        String itemId = testItem.getId();
        
        ShopItem found = shopItemService.getItemById(itemId);
        
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(itemId);
        assertThat(found.getName()).isEqualTo("Tablet");
    }

    @Test
    void testGetItemByIdNotFound() {
        ShopItem found = shopItemService.getItemById("nonexistent-id");
        
        assertThat(found).isNull();
    }

    @Test
    void testInsertNewShopItem() {
        ShopItem newItem = new ShopItem("Headphones", "Noise-cancelling", 199.99, 25);
        newItem.setId("should-be-ignored");
        
        ShopItem saved = shopItemService.insertNewShopItem(newItem);
        
        assertThat(saved.getId()).isNotEqualTo("should-be-ignored");
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Headphones");
        assertThat(shopItemRepository.findAll()).hasSize(2);
    }

    @Test
    void testUpdateShopItemById() {
        String itemId = testItem.getId();
        ShopItem updatedItem = new ShopItem("Updated Tablet", "iOS tablet", 399.99, 15);
        
        ShopItem result = shopItemService.updateShopItemById(itemId, updatedItem);
        
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Updated Tablet");
        assertThat(result.getDescription()).isEqualTo("iOS tablet");
        assertThat(result.getPrice()).isEqualTo(399.99);
        assertThat(result.getQuantity()).isEqualTo(15);
    }

    @Test
    void testDeleteShopItem() {
        String itemId = testItem.getId();
        
        shopItemService.deleteShopItem(itemId);
        
        assertThat(shopItemRepository.findById(itemId)).isEmpty();
        assertThat(shopItemRepository.findAll()).isEmpty();
    }

    @Test
    void testMultipleOperations() {
        ShopItem item1 = shopItemService.insertNewShopItem(
                new ShopItem("Item1", "Description1", 10.00, 5));
        ShopItem item2 = shopItemService.insertNewShopItem(
                new ShopItem("Item2", "Description2", 20.00, 10));
        
        assertThat(shopItemService.getAllItems()).hasSize(3);
        
        shopItemService.deleteShopItem(item1.getId());
        
        assertThat(shopItemService.getAllItems()).hasSize(2);
        
        ShopItem updated = new ShopItem("Updated Item2", "New Desc", 25.00, 8);
        shopItemService.updateShopItemById(item2.getId(), updated);
        
        ShopItem retrieved = shopItemService.getItemById(item2.getId());
        assertThat(retrieved.getName()).isEqualTo("Updated Item2");
        assertThat(retrieved.getPrice()).isEqualTo(25.00);
    }
}
