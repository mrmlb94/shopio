package com.example.onlineshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.onlineshop.model.ShopItem;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ShopItemMongoIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testMongoMapping() {
        // Create and save ShopItem
        ShopItem item = new ShopItem("Test Product", "Test Description", 99.99, 5);
        
        ShopItem saved = mongoTemplate.save(item);
        
        // Verify MongoDB generated ID
        assertThat(saved.getId()).isNotNull();
        
        // Retrieve from MongoDB
        ShopItem retrieved = mongoTemplate.findById(saved.getId(), ShopItem.class);
        
        // Verify mapping
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(saved.getId());
        assertThat(retrieved.getName()).isEqualTo("Test Product");
        assertThat(retrieved.getDescription()).isEqualTo("Test Description");
        assertThat(retrieved.getPrice()).isEqualTo(99.99);
        assertThat(retrieved.getQuantity()).isEqualTo(5);
    }
}
