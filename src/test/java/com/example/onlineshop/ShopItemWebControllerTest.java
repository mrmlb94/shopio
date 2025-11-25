package com.example.onlineshop;

import com.example.onlineshop.controllers.ShopItemWebController;
import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopItemWebController.class)
@DisplayName("ShopItemWebController Unit Tests")
class ShopItemWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShopItemService shopItemService;
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
    @DisplayName("GET / should display index page with all items")
    void testIndexWithItems() throws Exception {
        // Arrange
        List<ShopItem> items = Arrays.asList(testItem1, testItem2);
        when(shopItemService.getAllItems()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shopitems"))
                .andExpect(model().attribute("shopitems", hasSize(2)))
                .andExpect(model().attribute("shopitems", hasItem(testItem1)))
                .andExpect(model().attribute("shopitems", hasItem(testItem2)))
                .andExpect(model().attribute("message", ""));

        verify(shopItemService, times(1)).getAllItems();
    }

    @Test
    @DisplayName("GET / should display message when no items found")
    void testIndexWithNoItems() throws Exception {
        // Arrange
        when(shopItemService.getAllItems()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("shopitems"))
                .andExpect(model().attribute("shopitems", hasSize(0)))
                .andExpect(model().attribute("message", "No shop items found."));

        verify(shopItemService, times(1)).getAllItems();
    }

    @Test
    @DisplayName("GET /view/{id} should display item details")
    void testViewItem() throws Exception {
        // Arrange
        String itemId = "1";
        when(shopItemService.getItemById(itemId)).thenReturn(testItem1);

        // Act & Assert
        mockMvc.perform(get("/view/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("view"))
                .andExpect(model().attributeExists("shopitem"))
                .andExpect(model().attribute("shopitem", testItem1))
                .andExpect(model().attribute("message", ""));

        verify(shopItemService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("GET /view/{id} should display message when item not found")
    void testViewItemNotFound() throws Exception {
        // Arrange
        String itemId = "999";
        when(shopItemService.getItemById(itemId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/view/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("view"))
                .andExpect(model().attributeDoesNotExist("shopitem"))
                .andExpect(model().attribute("message", "No item found with id: " + itemId));

        verify(shopItemService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("GET /edit/{id} should display edit form with item")
    void testEditItem() throws Exception {
        // Arrange
        String itemId = "1";
        when(shopItemService.getItemById(itemId)).thenReturn(testItem1);

        // Act & Assert
        mockMvc.perform(get("/edit/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("shopitem"))
                .andExpect(model().attribute("shopitem", testItem1))
                .andExpect(model().attribute("message", ""));

        verify(shopItemService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("GET /edit/{id} should display message when item not found")
    void testEditItemNotFound() throws Exception {
        // Arrange
        String itemId = "999";
        when(shopItemService.getItemById(itemId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/edit/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("shopitem", nullValue()))
                .andExpect(model().attribute("message", "No item found with id: " + itemId));

        verify(shopItemService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("GET /new should display new item form")
    void testNewItem() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("new"))
                .andExpect(model().attributeExists("shopitem"))
                .andExpect(model().attribute("shopitem", instanceOf(ShopItem.class)))
                .andExpect(model().attribute("message", ""));

        verifyNoInteractions(shopItemService);
    }

    @Test
    @DisplayName("POST /save should insert new item when id is null")
    void testSaveNewItem() throws Exception {
        // Arrange
        ShopItem savedItem = new ShopItem("Keyboard", "Mechanical keyboard", 150.00, 10);
        savedItem.setId("3");

        when(shopItemService.insertNewShopItem(any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(post("/save")
                        .param("name", "Keyboard")
                        .param("description", "Mechanical keyboard")
                        .param("price", "150.00")
                        .param("quantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(shopItemService, times(1)).insertNewShopItem(any(ShopItem.class));
        verify(shopItemService, never()).updateShopItemById(any(), any());
    }

    @Test
    @DisplayName("POST /save should insert new item when id is empty string")
    void testSaveNewItemWithEmptyId() throws Exception {
        // Arrange
        ShopItem savedItem = new ShopItem("Monitor", "4K monitor", 500.00, 8);
        savedItem.setId("4");

        when(shopItemService.insertNewShopItem(any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(post("/save")
                        .param("id", "")
                        .param("name", "Monitor")
                        .param("description", "4K monitor")
                        .param("price", "500.00")
                        .param("quantity", "8"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(shopItemService, times(1)).insertNewShopItem(any(ShopItem.class));
        verify(shopItemService, never()).updateShopItemById(any(), any());
    }

    @Test
    @DisplayName("POST /save should update existing item when id is provided")
    void testSaveExistingItem() throws Exception {
        // Arrange
        String itemId = "1";
        ShopItem updatedItem = new ShopItem("Updated Laptop", "Updated description", 1500.00, 3);
        updatedItem.setId(itemId);

        when(shopItemService.updateShopItemById(eq(itemId), any(ShopItem.class))).thenReturn(updatedItem);

        // Act & Assert
        mockMvc.perform(post("/save")
                        .param("id", itemId)
                        .param("name", "Updated Laptop")
                        .param("description", "Updated description")
                        .param("price", "1500.00")
                        .param("quantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(shopItemService, times(1)).updateShopItemById(eq(itemId), any(ShopItem.class));
        verify(shopItemService, never()).insertNewShopItem(any());
    }

    @Test
    @DisplayName("POST /save should handle item with zero quantity")
    void testSaveItemWithZeroQuantity() throws Exception {
        // Arrange
        ShopItem savedItem = new ShopItem("Out of Stock", "No stock", 100.00, 0);
        savedItem.setId("5");

        when(shopItemService.insertNewShopItem(any(ShopItem.class))).thenReturn(savedItem);

        // Act & Assert
        mockMvc.perform(post("/save")
                        .param("name", "Out of Stock")
                        .param("description", "No stock")
                        .param("price", "100.00")
                        .param("quantity", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(shopItemService, times(1)).insertNewShopItem(any(ShopItem.class));
    }

    @Test
    @DisplayName("GET /delete/{id} should delete item and redirect to index")
    void testDeleteItem() throws Exception {
        // Arrange
        String itemId = "1";
        doNothing().when(shopItemService).deleteShopItem(itemId);

        // Act & Assert
        mockMvc.perform(get("/delete/{id}", itemId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(shopItemService, times(1)).deleteShopItem(itemId);
    }

    @Test
    @DisplayName("GET /delete/{id} should handle deletion of non-existing item")
    void testDeleteNonExistingItem() throws Exception {
        // Arrange
        String itemId = "999";
        doNothing().when(shopItemService).deleteShopItem(itemId);

        // Act & Assert
        mockMvc.perform(get("/delete/{id}", itemId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(shopItemService, times(1)).deleteShopItem(itemId);
    }
}
