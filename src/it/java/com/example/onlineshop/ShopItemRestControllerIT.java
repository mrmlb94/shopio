package com.example.onlineshop;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ShopItemRestControllerIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ShopItem testItem1;
    private ShopItem testItem2;

    @BeforeEach
    void setUp() {
        shopItemRepository.deleteAll();
        
        testItem1 = new ShopItem("Phone", "Smartphone", 699.99, 30);
        testItem2 = new ShopItem("Charger", "USB-C charger", 19.99, 100);
        
        testItem1 = shopItemRepository.save(testItem1);
        testItem2 = shopItemRepository.save(testItem2);
    }

    @AfterEach
    void tearDown() {
        shopItemRepository.deleteAll();
    }

    @Test
    void testGetAllShopItems() throws Exception {
        mockMvc.perform(get("/api/shopitems"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Phone", "Charger")));
    }

    @Test
    void testGetOneShopItem() throws Exception {
        String itemId = testItem1.getId();
        
        mockMvc.perform(get("/api/shopitems/" + itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is("Phone")))
                .andExpect(jsonPath("$.description", is("Smartphone")))
                .andExpect(jsonPath("$.price", is(699.99)))
                .andExpect(jsonPath("$.quantity", is(30)));
    }

    @Test
    void testGetOneShopItemNotFound() throws Exception {
        mockMvc.perform(get("/api/shopitems/nonexistent-id"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void testCreateNewShopItem() throws Exception {
        ShopItem newItem = new ShopItem("Camera", "Digital camera", 449.99, 12);
        
        mockMvc.perform(post("/api/shopitems/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Camera")))
                .andExpect(jsonPath("$.description", is("Digital camera")))
                .andExpect(jsonPath("$.price", is(449.99)))
                .andExpect(jsonPath("$.quantity", is(12)));
    }

    @Test
    void testUpdateShopItem() throws Exception {
        String itemId = testItem1.getId();
        ShopItem updatedItem = new ShopItem("Updated Phone", "5G Smartphone", 799.99, 25);
        
        mockMvc.perform(put("/api/shopitems/update/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is("Updated Phone")))
                .andExpect(jsonPath("$.description", is("5G Smartphone")))
                .andExpect(jsonPath("$.price", is(799.99)))
                .andExpect(jsonPath("$.quantity", is(25)));
    }

    @Test
    void testDeleteShopItem() throws Exception {
        String itemId = testItem2.getId();
        
        mockMvc.perform(delete("/api/shopitems/delete/" + itemId))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/shopitems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testCompleteRestWorkflow() throws Exception {
        ShopItem newItem = new ShopItem("Speaker", "Bluetooth speaker", 79.99, 40);
        
        String response = mockMvc.perform(post("/api/shopitems/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        ShopItem created = objectMapper.readValue(response, ShopItem.class);
        String createdId = created.getId();
        
        mockMvc.perform(get("/api/shopitems/" + createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Speaker")));
        
        ShopItem updateData = new ShopItem("Premium Speaker", "High-end Bluetooth", 149.99, 20);
        mockMvc.perform(put("/api/shopitems/update/" + createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Premium Speaker")));
        
        mockMvc.perform(delete("/api/shopitems/delete/" + createdId))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/shopitems/" + createdId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
