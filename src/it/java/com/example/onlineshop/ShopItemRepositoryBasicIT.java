package com.example.onlineshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ShopItemRepositoryBasicIT {

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testFindByName() {
        // Setup test data using MongoTemplate
        ShopItem item1 = new ShopItem("Laptop", "High-performance laptop", 1299.99, 10);
        ShopItem item2 = new ShopItem("Mouse", "Wireless mouse", 29.99, 50);
        ShopItem item3 = new ShopItem("Laptop", "Budget laptop", 599.99, 5);

        mongoTemplate.save(item1);
        mongoTemplate.save(item2);
        mongoTemplate.save(item3);

        // Execute
        List<ShopItem> foundItems = shopItemRepository.findByName("Laptop");

        // Verify
        assertThat(foundItems).hasSize(2);
        assertThat(foundItems).extracting(ShopItem::getName)
                .containsOnly("Laptop");
    }


    @Test
    public void testFindByNameAndPrice() {
        ShopItem item1 = new ShopItem("Keyboard", "Mechanical keyboard", 149.99, 20);
        ShopItem item2 = new ShopItem("Keyboard", "Membrane keyboard", 49.99, 30);
        ShopItem item3 = new ShopItem("Monitor", "4K Monitor", 499.99, 15);

        mongoTemplate.save(item1);
        mongoTemplate.save(item2);
        mongoTemplate.save(item3);

        List<ShopItem> foundItems = shopItemRepository.findByNameAndPrice("Keyboard", 149.99);

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("Keyboard");
        assertThat(foundItems.get(0).getPrice()).isEqualTo(149.99);
    }

    @Test
    public void testFindByNameOrPrice() {
        ShopItem item1 = new ShopItem("Tablet", "Android tablet", 299.99, 25);
        ShopItem item2 = new ShopItem("Phone", "Smartphone", 799.99, 40);
        ShopItem item3 = new ShopItem("Tablet", "iPad", 599.99, 15);

        mongoTemplate.save(item1);
        mongoTemplate.save(item2);
        mongoTemplate.save(item3);

        List<ShopItem> foundItems = shopItemRepository.findByNameOrPrice("Tablet", 799.99);

        assertThat(foundItems).hasSize(3);
    }

    @Test
    public void firstLearningTest() {
        ShopItem item = new ShopItem("Headphones", "Noise-cancelling", 199.99, 30);

        ShopItem savedItem = shopItemRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Headphones");
    }

    @Test
    public void secondLearningTest() {
        shopItemRepository.deleteAll();

        List<ShopItem> allItems = shopItemRepository.findAll();

        assertThat(allItems).isEmpty();
    }
}
