package com.example.onlineshop;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ShopItemWebControllerIT {

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

    private ShopItem testItem;

    @BeforeEach
    void setUp() {
        shopItemRepository.deleteAll();
        testItem = new ShopItem("Laptop", "Gaming laptop", 1499.99, 5);
        testItem = shopItemRepository.save(testItem);
    }

    @AfterEach
    void tearDown() {
        shopItemRepository.deleteAll();
    }

    @Test
    void testIndexPageWithItems() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shopitems"))
                .andExpect(model().attribute("shopitems", hasSize(1)))
                .andExpect(model().attribute("message", is("")));
    }

    @Test
    void testIndexPageWithNoItems() throws Exception {
        shopItemRepository.deleteAll();
        
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shopitems"))
                .andExpect(model().attribute("shopitems", hasSize(0)))
                .andExpect(model().attribute("message", is("No shop items found.")));
    }

    @Test
    void testViewItemPage() throws Exception {
        String itemId = testItem.getId();
        
        mockMvc.perform(get("/view/" + itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("view"))
                .andExpect(model().attributeExists("shopitem"))
                .andExpect(model().attribute("message", is("")));
    }

    @Test
    void testViewItemPageNotFound() throws Exception {
        mockMvc.perform(get("/view/nonexistent-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("view"))
                .andExpect(model().attribute("message", containsString("No item found with id")));
    }

    @Test
    void testEditItemPage() throws Exception {
        String itemId = testItem.getId();
        
        mockMvc.perform(get("/edit/" + itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("shopitem"))
                .andExpect(model().attribute("message", is("")));
    }

    @Test
    void testEditItemPageNotFound() throws Exception {
        mockMvc.perform(get("/edit/nonexistent-id"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("message", containsString("No item found with id")));
    }

    @Test
    void testNewItemPage() throws Exception {
        mockMvc.perform(get("/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("new"))
                .andExpect(model().attributeExists("shopitem"))
                .andExpect(model().attribute("message", is("")));
    }

    @Test
    void testSaveNewItem() throws Exception {
        mockMvc.perform(post("/save")
                        .param("name", "Monitor")
                        .param("description", "4K Monitor")
                        .param("price", "399.99")
                        .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testSaveUpdateItem() throws Exception {
        String itemId = testItem.getId();
        
        mockMvc.perform(post("/save")
                        .param("id", itemId)
                        .param("name", "Updated Laptop")
                        .param("description", "Updated description")
                        .param("price", "1599.99")
                        .param("quantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testDeleteItem() throws Exception {
        String itemId = testItem.getId();
        
        mockMvc.perform(get("/delete/" + itemId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testCompleteWebWorkflow() throws Exception {
        mockMvc.perform(get("/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("new"));
        
        mockMvc.perform(post("/save")
                        .param("name", "Webcam")
                        .param("description", "HD Webcam")
                        .param("price", "89.99")
                        .param("quantity", "15"))
                .andExpect(status().is3xxRedirection());
        
        ShopItem webcam = shopItemRepository.findFirstByName("Webcam");
        String webcamId = webcam.getId();
        
        mockMvc.perform(get("/view/" + webcamId))
                .andExpect(status().isOk())
                .andExpect(view().name("view"));
        
        mockMvc.perform(get("/edit/" + webcamId))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"));
        
        mockMvc.perform(post("/save")
                        .param("id", webcamId)
                        .param("name", "Premium Webcam")
                        .param("description", "4K HD Webcam")
                        .param("price", "149.99")
                        .param("quantity", "10"))
                .andExpect(status().is3xxRedirection());
        
        mockMvc.perform(get("/delete/" + webcamId))
                .andExpect(status().is3xxRedirection());
    }
}
