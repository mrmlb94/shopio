package com.example.onlineshop;

import com.example.onlineshop.controllers.ShopItemRestController;
import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopItemRestController.class)
@DisplayName("ShopItemRestController Unit Tests")
class ShopItemRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShopItemService shopItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ShopItem testItem1;
    private ShopItem testItem2;

    @BeforeEach
    void setUp() {
        testItem1 = new ShopItem("Laptop", "Gaming laptop", 1200.00, 5);
        testItem1.setId("1");

        testItem2 = new ShopItem("Mouse", "Wireless mouse", 25.50, 20);
        testItem2.setId("2");
    }

    @Test
    @DisplayName("GET /api/shopitems should return all shop items")
    void testAllShopItems() throws Exception {
        // Arrange
        List<ShopItem> items = Arrays.asList(testItem1, testItem2);
        when(shopItemService.getAllItems()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/shopitems"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(1200.00)))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].name", is("Mouse")));

        verify(shopItemService, times(1)).getAllItems();
    }

    @Test
    @DisplayName("GET /api/shopitems should return empty array when no items")
    void testAllShopItemsWhenEmpty() throws Exception {
        // Arrange
        when(shopItemService.getAllItems()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/shopitems"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(shopItemService, times(1)).getAllItems();
    }

    @Test
    @DisplayName("GET /api/shopitems/{id} should return specific shop item")
    void testOneShopItem() throws Exception {
        // Arrange
        String itemId = "1";
        when(shopItemService.getItemById(itemId)).thenReturn(testItem1);

        // Act & Assert
        mockMvc.perform(get("/api/shopitems/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.description", is("Gaming laptop")))
                .andExpect(jsonPath("$.price", is(1200.00)))
                .andExpect(jsonPath("$.quantity", is(5)));

        verify(shopItemService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("GET /api/shopitems/{id} should return null when item not found")
    void testOneShopItemNotFound() throws Exception {
        // Arrange
        String itemId = "999";
        when(shopItemService.getItemById(itemId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/shopitems/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(shopItemService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("POST /api/shopitems/new should create new shop item")
    void testNewShopItem() throws Exception {
        // Arrange
        ShopItem newItem = new ShopItem("Keyboard", "Mechanical keyboard", 150.00, 10);
        ShopItem savedItem = new ShopItem("Keyboard", "Mechanical keyboard", 150.00, 10);
        savedItem.setId("3");

        when(shopItemService.insertNewShopItem(any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(post("/api/shopitems/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("3")))
                .andExpect(jsonPath("$.name", is("Keyboard")))
                .andExpect(jsonPath("$.price", is(150.00)))
                .andExpect(jsonPath("$.quantity", is(10)));

        verify(shopItemService, times(1)).insertNewShopItem(any(ShopItem.class));
    }

    @Test
    @DisplayName("POST /api/shopitems/new should handle item with all fields")
    void testNewShopItemWithAllFields() throws Exception {
        // Arrange
        ShopItem newItem = new ShopItem("Monitor", "4K Ultra HD", 599.99, 15);
        ShopItem savedItem = new ShopItem("Monitor", "4K Ultra HD", 599.99, 15);
        savedItem.setId("4");

        when(shopItemService.insertNewShopItem(any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(post("/api/shopitems/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("4K Ultra HD")));

        verify(shopItemService, times(1)).insertNewShopItem(any(ShopItem.class));
    }

    @Test
    @DisplayName("PUT /api/shopitems/update/{id} should update existing shop item")
    void testUpdateShopItem() throws Exception {
        // Arrange
        String itemId = "1";
        ShopItem updatedItem = new ShopItem("Updated Laptop", "Updated gaming laptop", 1500.00, 3);
        ShopItem savedItem = new ShopItem("Updated Laptop", "Updated gaming laptop", 1500.00, 3);
        savedItem.setId(itemId);

        when(shopItemService.updateShopItemById(eq(itemId), any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(put("/api/shopitems/update/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(itemId)))
                .andExpect(jsonPath("$.name", is("Updated Laptop")))
                .andExpect(jsonPath("$.price", is(1500.00)))
                .andExpect(jsonPath("$.quantity", is(3)));

        verify(shopItemService, times(1)).updateShopItemById(eq(itemId), any(ShopItem.class));
    }

    @Test
    @DisplayName("PUT /api/shopitems/update/{id} should update with partial data")
    void testUpdateShopItemPartial() throws Exception {
        // Arrange
        String itemId = "2";
        ShopItem updatedItem = new ShopItem("Premium Mouse", "Gaming mouse with RGB", 45.00, 15);
        ShopItem savedItem = new ShopItem("Premium Mouse", "Gaming mouse with RGB", 45.00, 15);
        savedItem.setId(itemId);

        when(shopItemService.updateShopItemById(eq(itemId), any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(put("/api/shopitems/update/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Premium Mouse")))
                .andExpect(jsonPath("$.price", is(45.00)));

        verify(shopItemService, times(1)).updateShopItemById(eq(itemId), any(ShopItem.class));
    }

    @Test
    @DisplayName("DELETE /api/shopitems/delete/{id} should delete shop item")
    void testDeleteShopItem() throws Exception {
        // Arrange
        String itemId = "1";
        doNothing().when(shopItemService).deleteShopItem(itemId);

        // Act & Assert
        mockMvc.perform(delete("/api/shopitems/delete/{id}", itemId))
                .andExpect(status().isOk());

        verify(shopItemService, times(1)).deleteShopItem(itemId);
    }

    @Test
    @DisplayName("DELETE /api/shopitems/delete/{id} should handle non-existing item")
    void testDeleteNonExistingShopItem() throws Exception {
        // Arrange
        String itemId = "999";
        doNothing().when(shopItemService).deleteShopItem(itemId);

        // Act & Assert
        mockMvc.perform(delete("/api/shopitems/delete/{id}", itemId))
                .andExpect(status().isOk());

        verify(shopItemService, times(1)).deleteShopItem(itemId);
    }
}
