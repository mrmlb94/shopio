package com.example.onlineshop;


import com.example.onlineshop.controllers.ShopItemRestController;
import com.example.onlineshop.controllers.ShopItemWebController;
import com.example.onlineshop.repositories.ShopItemRepository;
import com.example.onlineshop.services.ShopItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class OnlineShopApplicationIT {

    @SuppressWarnings("resource")
	@Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
        .waitingFor(Wait.forListeningPort())
        .withStartupTimeout(Duration.ofSeconds(3));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Autowired
    private ShopItemService shopItemService;

    @Autowired
    private ShopItemRestController shopItemRestController;

    @Autowired
    private ShopItemWebController shopItemWebController;

    @Test
    void contextLoads() {
        assertThat(shopItemRepository).isNotNull();
        assertThat(shopItemService).isNotNull();
        assertThat(shopItemRestController).isNotNull();
        assertThat(shopItemWebController).isNotNull();
    }

    @Test
    void mongoDBContainerIsRunning() {
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }
}
