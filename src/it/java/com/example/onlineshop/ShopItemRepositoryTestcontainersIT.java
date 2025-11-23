package com.example.onlineshop;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class ShopItemRepositoryTestcontainersIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ShopItemRepository shopItemRepository;

    private ShopItem testItem1;
    private ShopItem testItem2;
    private ShopItem testItem3;

    @BeforeEach
    void setUp() {
        shopItemRepository.deleteAll();
        
        testItem1 = new ShopItem("Laptop", "High-performance laptop", 1200.00, 10);
        testItem2 = new ShopItem("Mouse", "Wireless mouse", 25.50, 50);
        testItem3 = new ShopItem("Keyboard", "Mechanical keyboard", 89.99, 30);
        
        shopItemRepository.save(testItem1);
        shopItemRepository.save(testItem2);
        shopItemRepository.save(testItem3);
    }

    @AfterEach
    void tearDown() {
        shopItemRepository.deleteAll();
    }

    @Test
    void testFindAll() {
        List<ShopItem> items = shopItemRepository.findAll();
        
        assertThat(items).hasSize(3);
        assertThat(items).extracting(ShopItem::getName)
                .containsExactlyInAnyOrder("Laptop", "Mouse", "Keyboard");
    }

    @Test
    void testFindById() {
        String savedId = testItem1.getId();
        
        Optional<ShopItem> found = shopItemRepository.findById(savedId);
        
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Laptop");
        assertThat(found.get().getPrice()).isEqualTo(1200.00);
    }

    @Test
    void testFindByName() {
        ShopItem found = shopItemRepository.findFirstByName("Mouse");
        
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Mouse");
        assertThat(found.getPrice()).isEqualTo(25.50);
        assertThat(found.getQuantity()).isEqualTo(50);
    }

    @Test
    void testFindByNameAndPrice() {
        List<ShopItem> items = shopItemRepository.findByNameAndPrice("Keyboard", 89.99);
        
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Keyboard");
        assertThat(items.get(0).getPrice()).isEqualTo(89.99);
    }

    @Test
    void testFindByNameAndPriceNotFound() {
        List<ShopItem> items = shopItemRepository.findByNameAndPrice("Keyboard", 100.00);
        
        assertThat(items).isEmpty();
    }

    @Test
    void testFindByNameOrPrice() {
        List<ShopItem> items = shopItemRepository.findByNameOrPrice("Mouse", 89.99);
        
        assertThat(items).hasSize(2);
        assertThat(items).extracting(ShopItem::getName)
                .containsExactlyInAnyOrder("Mouse", "Keyboard");
    }

    @Test
    void testSaveNewItem() {
        ShopItem newItem = new ShopItem("Monitor", "4K Monitor", 350.00, 15);
        
        ShopItem saved = shopItemRepository.save(newItem);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Monitor");
        assertThat(shopItemRepository.findAll()).hasSize(4);
    }

    @Test
    void testUpdateItem() {
        String itemId = testItem1.getId();
        testItem1.setPrice(1100.00);
        testItem1.setQuantity(8);
        
        shopItemRepository.save(testItem1);
        
        Optional<ShopItem> updated = shopItemRepository.findById(itemId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getPrice()).isEqualTo(1100.00);
        assertThat(updated.get().getQuantity()).isEqualTo(8);
    }

    @Test
    void testDeleteItem() {
        String itemId = testItem2.getId();
        
        shopItemRepository.deleteById(itemId);
        
        assertThat(shopItemRepository.findById(itemId)).isEmpty();
        assertThat(shopItemRepository.findAll()).hasSize(2);
    }

    @Test
    void testDeleteAll() {
        shopItemRepository.deleteAll();
        
        assertThat(shopItemRepository.findAll()).isEmpty();
    }
}
