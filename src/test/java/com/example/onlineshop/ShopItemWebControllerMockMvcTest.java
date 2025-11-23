package com.example.onlineshop;

import com.example.onlineshop.controllers.ShopItemWebController;
import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class ShopItemWebControllerMockMvcTest {

    private ShopItemWebController shopItemWebController;
    private ShopItemService shopItemService;
    private Model model;

    @BeforeEach
    void setUp() {
        shopItemService = mock(ShopItemService.class);
        model = mock(Model.class);
        shopItemWebController = new ShopItemWebController(shopItemService);
    }

    @Test
    void testIndexWhenNoItemsExist() {
        // Arrange
        when(shopItemService.getAllItems()).thenReturn(emptyList());

        // Act
        String viewName = shopItemWebController.index(model);

        // Assert
        assertThat(viewName).isEqualTo("index");
        verify(model).addAttribute("shopitems", emptyList());
        verify(model).addAttribute("message", "No shop items found.");
        verify(shopItemService).getAllItems();
    }

    @Test
    void testIndexWhenItemsExist() {
        // Arrange
        ShopItem laptop = new ShopItem("Dell XPS 15", "Premium laptop", 1299.99, 10);
        laptop.setId("1");
        ShopItem monitor = new ShopItem("LG UltraWide", "34-inch monitor", 599.99, 15);
        monitor.setId("2");
        List<ShopItem> items = asList(laptop, monitor);
        
        when(shopItemService.getAllItems()).thenReturn(items);

        // Act
        String viewName = shopItemWebController.index(model);

        // Assert
        assertThat(viewName).isEqualTo("index");
        verify(model).addAttribute("shopitems", items);
        verify(model).addAttribute("message", "");
        verify(shopItemService).getAllItems();
    }

    @Test
    void testViewItemWhenItemExists() {
        // Arrange
        ShopItem keyboard = new ShopItem("Keychron K8", "Mechanical keyboard", 89.99, 25);
        keyboard.setId("kb-001");
        
        when(shopItemService.getItemById("kb-001")).thenReturn(keyboard);

        // Act
        String viewName = shopItemWebController.viewItem("kb-001", model);

        // Assert
        assertThat(viewName).isEqualTo("view");
        verify(model).addAttribute("shopitem", keyboard);
        verify(model).addAttribute("message", "");
        verify(shopItemService).getItemById("kb-001");
    }

    @Test
    void testViewItemWhenItemDoesNotExist() {
        // Arrange
        when(shopItemService.getItemById("nonexistent")).thenReturn(null);

        // Act
        String viewName = shopItemWebController.viewItem("nonexistent", model);

        // Assert
        assertThat(viewName).isEqualTo("view");
        verify(model).addAttribute("shopitem", null);
        verify(model).addAttribute("message", "No item found with id: nonexistent");
        verify(shopItemService).getItemById("nonexistent");
    }

    @Test
    void testEditItemWhenItemExists() {
        // Arrange
        ShopItem mouse = new ShopItem("Logitech MX Master 3", "Ergonomic mouse", 99.99, 50);
        mouse.setId("ms-001");
        
        when(shopItemService.getItemById("ms-001")).thenReturn(mouse);

        // Act
        String viewName = shopItemWebController.editItem("ms-001", model);

        // Assert
        assertThat(viewName).isEqualTo("edit");
        verify(model).addAttribute("shopitem", mouse);
        verify(model).addAttribute("message", "");
        verify(shopItemService).getItemById("ms-001");
    }

    @Test
    void testEditItemWhenItemDoesNotExist() {
        // Arrange
        when(shopItemService.getItemById("missing-id")).thenReturn(null);

        // Act
        String viewName = shopItemWebController.editItem("missing-id", model);

        // Assert
        assertThat(viewName).isEqualTo("edit");
        verify(model).addAttribute("shopitem", null);
        verify(model).addAttribute("message", "No item found with id: missing-id");
        verify(shopItemService).getItemById("missing-id");
    }

    @Test
    void testNewItemCreatesEmptyShopItem() {
        // Act
        String viewName = shopItemWebController.newItem(model);

        // Assert
        assertThat(viewName).isEqualTo("new");
        verify(model).addAttribute(eq("shopitem"), any(ShopItem.class));
        verify(model).addAttribute("message", "");
        verifyNoInteractions(shopItemService);
    }

    @Test
    void testSaveItemInsertsNewItemWhenIdIsNull() {
        // Arrange
        ShopItem newItem = new ShopItem("New Headphones", "Wireless headphones", 149.99, 20);
        newItem.setId(null);
        
        ShopItem savedItem = new ShopItem("New Headphones", "Wireless headphones", 149.99, 20);
        savedItem.setId("generated-id");
        
        when(shopItemService.insertNewShopItem(newItem)).thenReturn(savedItem);

        // Act
        String viewName = shopItemWebController.saveItem(newItem);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).insertNewShopItem(newItem);
        verify(shopItemService, never()).updateShopItemById(anyString(), any(ShopItem.class));
    }

    @Test
    void testSaveItemInsertsNewItemWhenIdIsEmpty() {
        // Arrange
        ShopItem newItem = new ShopItem("New Tablet", "Android tablet", 399.99, 12);
        newItem.setId("");
        
        ShopItem savedItem = new ShopItem("New Tablet", "Android tablet", 399.99, 12);
        savedItem.setId("generated-id");
        
        when(shopItemService.insertNewShopItem(newItem)).thenReturn(savedItem);

        // Act
        String viewName = shopItemWebController.saveItem(newItem);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).insertNewShopItem(newItem);
        verify(shopItemService, never()).updateShopItemById(anyString(), any(ShopItem.class));
    }

    @Test
    void testSaveItemUpdatesExistingItemWhenIdIsPresent() {
        // Arrange
        ShopItem existingItem = new ShopItem("Updated Webcam", "4K webcam", 199.99, 8);
        existingItem.setId("wc-001");
        
        ShopItem updatedItem = new ShopItem("Updated Webcam", "4K webcam", 199.99, 8);
        updatedItem.setId("wc-001");
        
        when(shopItemService.updateShopItemById("wc-001", existingItem)).thenReturn(updatedItem);

        // Act
        String viewName = shopItemWebController.saveItem(existingItem);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).updateShopItemById("wc-001", existingItem);
        verify(shopItemService, never()).insertNewShopItem(any(ShopItem.class));
    }

    @Test
    void testDeleteItemRedirectsToIndex() {
        // Arrange
        doNothing().when(shopItemService).deleteShopItem("item-to-delete");

        // Act
        String viewName = shopItemWebController.deleteItem("item-to-delete");

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).deleteShopItem("item-to-delete");
    }

    @Test
    void testDeleteItemWhenServiceThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(shopItemService).deleteShopItem("error-id");

        // Act & Assert
        try {
            shopItemWebController.deleteItem("error-id");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Delete failed");
        }
        
        verify(shopItemService).deleteShopItem("error-id");
    }

    @Test
    void testIndexHandlesLargeNumberOfItems() {
        // Arrange
        List<ShopItem> manyItems = asList(
                createItem("1", "Item 1", 10.0),
                createItem("2", "Item 2", 20.0),
                createItem("3", "Item 3", 30.0),
                createItem("4", "Item 4", 40.0),
                createItem("5", "Item 5", 50.0)
        );
        
        when(shopItemService.getAllItems()).thenReturn(manyItems);

        // Act
        String viewName = shopItemWebController.index(model);

        // Assert
        assertThat(viewName).isEqualTo("index");
        verify(model).addAttribute("shopitems", manyItems);
        verify(model).addAttribute("message", "");
    }

    @Test
    void testViewItemWithSpecialCharactersInId() {
        // Arrange
        ShopItem specialItem = new ShopItem("Special Item", "Item with special ID", 99.99, 5);
        specialItem.setId("item-123-abc-xyz");
        
        when(shopItemService.getItemById("item-123-abc-xyz")).thenReturn(specialItem);

        // Act
        String viewName = shopItemWebController.viewItem("item-123-abc-xyz", model);

        // Assert
        assertThat(viewName).isEqualTo("view");
        verify(model).addAttribute("shopitem", specialItem);
        verify(shopItemService).getItemById("item-123-abc-xyz");
    }

    @Test
    void testSaveItemWithCompleteShopItemData() {
        // Arrange
        ShopItem completeItem = new ShopItem(
                "Complete Product",
                "Full description with all details",
                1999.99,
                100
        );
        completeItem.setId("complete-001");
        
        when(shopItemService.updateShopItemById("complete-001", completeItem)).thenReturn(completeItem);

        // Act
        String viewName = shopItemWebController.saveItem(completeItem);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).updateShopItemById("complete-001", completeItem);
    }

    @Test
    void testMultipleConsecutiveIndexCalls() {
        // Arrange
        List<ShopItem> items = asList(
                createItem("1", "Item 1", 100.0),
                createItem("2", "Item 2", 200.0)
        );
        when(shopItemService.getAllItems()).thenReturn(items);

        // Act
        shopItemWebController.index(model);
        shopItemWebController.index(model);
        shopItemWebController.index(model);

        // Assert
        verify(shopItemService, times(3)).getAllItems();
        verify(model, times(3)).addAttribute("shopitems", items);
    }

    @Test
    void testEditItemFollowedByViewItem() {
        // Arrange
        ShopItem item = new ShopItem("Test Item", "Test description", 50.0, 10);
        item.setId("test-001");
        
        when(shopItemService.getItemById("test-001")).thenReturn(item);

        // Act
        String editView = shopItemWebController.editItem("test-001", model);
        String viewView = shopItemWebController.viewItem("test-001", model);

        // Assert
        assertThat(editView).isEqualTo("edit");
        assertThat(viewView).isEqualTo("view");
        verify(shopItemService, times(2)).getItemById("test-001");
    }

    @Test
    void testSaveNewItemThenViewIt() {
        // Arrange
        ShopItem newItem = new ShopItem("Brand New Item", "Just created", 299.99, 15);
        newItem.setId(null);
        
        ShopItem savedItem = new ShopItem("Brand New Item", "Just created", 299.99, 15);
        savedItem.setId("new-generated-id");
        
        when(shopItemService.insertNewShopItem(newItem)).thenReturn(savedItem);
        when(shopItemService.getItemById("new-generated-id")).thenReturn(savedItem);

        // Act
        String saveView = shopItemWebController.saveItem(newItem);
        String viewName = shopItemWebController.viewItem("new-generated-id", model);

        // Assert
        assertThat(saveView).isEqualTo("redirect:/");
        assertThat(viewName).isEqualTo("view");
        verify(shopItemService).insertNewShopItem(newItem);
        verify(shopItemService).getItemById("new-generated-id");
    }

    @Test
    void testModelAttributesAreSetCorrectlyForEmptyState() {
        // Arrange
        when(shopItemService.getAllItems()).thenReturn(emptyList());

        // Act
        shopItemWebController.index(model);

        // Assert
        verify(model).addAttribute("shopitems", emptyList());
        verify(model).addAttribute("message", "No shop items found.");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testNewItemSetsEmptyMessageAttribute() {
        // Act
        shopItemWebController.newItem(model);

        // Assert
        verify(model).addAttribute(eq("shopitem"), any(ShopItem.class));
        verify(model).addAttribute("message", "");
        verifyNoMoreInteractions(model);
    }

    @Test
    void testDeleteMultipleItemsInSequence() {
        // Arrange
        doNothing().when(shopItemService).deleteShopItem(anyString());

        // Act
        shopItemWebController.deleteItem("item-1");
        shopItemWebController.deleteItem("item-2");
        shopItemWebController.deleteItem("item-3");

        // Assert
        verify(shopItemService).deleteShopItem("item-1");
        verify(shopItemService).deleteShopItem("item-2");
        verify(shopItemService).deleteShopItem("item-3");
    }

    @Test
    void testSaveItemWithZeroQuantity() {
        // Arrange
        ShopItem outOfStockItem = new ShopItem("Out of Stock", "Currently unavailable", 49.99, 0);
        outOfStockItem.setId(null);
        
        ShopItem savedItem = new ShopItem("Out of Stock", "Currently unavailable", 49.99, 0);
        savedItem.setId("out-of-stock-id");
        
        when(shopItemService.insertNewShopItem(outOfStockItem)).thenReturn(savedItem);

        // Act
        String viewName = shopItemWebController.saveItem(outOfStockItem);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).insertNewShopItem(outOfStockItem);
    }

    @Test
    void testSaveItemWithVeryHighPrice() {
        // Arrange
        ShopItem expensiveItem = new ShopItem("Luxury Item", "Premium product", 99999.99, 1);
        expensiveItem.setId(null);
        
        ShopItem savedItem = new ShopItem("Luxury Item", "Premium product", 99999.99, 1);
        savedItem.setId("luxury-id");
        
        when(shopItemService.insertNewShopItem(expensiveItem)).thenReturn(savedItem);

        // Act
        String viewName = shopItemWebController.saveItem(expensiveItem);

        // Assert
        assertThat(viewName).isEqualTo("redirect:/");
        verify(shopItemService).insertNewShopItem(expensiveItem);
    }

    // Helper method to create test items
    private ShopItem createItem(String id, String name, double price) {
        ShopItem item = new ShopItem(name, "Description for " + name, price, 10);
        item.setId(id);
        return item;
    }
}