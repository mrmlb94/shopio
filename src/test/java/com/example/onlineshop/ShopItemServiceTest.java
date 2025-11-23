package com.example.onlineshop;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import com.example.onlineshop.services.ShopItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShopItemService Unit Tests")
class ShopItemServiceTest {

    @Mock
    private ShopItemRepository shopItemRepository;

    @InjectMocks
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
    @DisplayName("getAllItems should return all items from repository")
    void testGetAllItems() {
        // Arrange
        List<ShopItem> expectedItems = Arrays.asList(testItem1, testItem2);
        when(shopItemRepository.findAll()).thenReturn(expectedItems);

        // Act
        List<ShopItem> actualItems = shopItemService.getAllItems();

        // Assert
        assertThat(actualItems).isNotNull();
        assertThat(actualItems).hasSize(2);
        assertThat(actualItems).containsExactlyInAnyOrder(testItem1, testItem2);
        verify(shopItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllItems should return empty list when no items exist")
    void testGetAllItemsWhenEmpty() {
        // Arrange
        when(shopItemRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ShopItem> actualItems = shopItemService.getAllItems();

        // Assert
        assertThat(actualItems).isNotNull();
        assertThat(actualItems).isEmpty();
        verify(shopItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getItemById should return item when it exists")
    void testGetItemByIdWhenExists() {
        // Arrange
        String itemId = "1";
        when(shopItemRepository.findById(itemId)).thenReturn(Optional.of(testItem1));

        // Act
        ShopItem actualItem = shopItemService.getItemById(itemId);

        // Assert
        assertThat(actualItem).isNotNull();
        assertThat(actualItem.getId()).isEqualTo(itemId);
        assertThat(actualItem.getName()).isEqualTo("Laptop");
        assertThat(actualItem.getPrice()).isEqualTo(1200.00);
        verify(shopItemRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("getItemById should return null when item does not exist")
    void testGetItemByIdWhenNotExists() {
        // Arrange
        String itemId = "999";
        when(shopItemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act
        ShopItem actualItem = shopItemService.getItemById(itemId);

        // Assert
        assertThat(actualItem).isNull();
        verify(shopItemRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("insertNewShopItem should set id to null and save item")
    void testInsertNewShopItem() {
        // Arrange
        ShopItem newItem = new ShopItem("Keyboard", "Mechanical keyboard", 150.00, 10);
        newItem.setId("existing-id");

        ShopItem savedItem = new ShopItem("Keyboard", "Mechanical keyboard", 150.00, 10);
        savedItem.setId("generated-id");

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(savedItem);

        // Act
        ShopItem result = shopItemService.insertNewShopItem(newItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("generated-id");
        assertThat(result.getName()).isEqualTo("Keyboard");
        
        verify(shopItemRepository, times(1)).save(argThat(item -> 
            item.getId() == null && 
            item.getName().equals("Keyboard")
        ));
    }

    @Test
    @DisplayName("insertNewShopItem should handle item with null id")
    void testInsertNewShopItemWithNullId() {
        // Arrange
        ShopItem newItem = new ShopItem("Monitor", "4K monitor", 500.00, 8);

        ShopItem savedItem = new ShopItem("Monitor", "4K monitor", 500.00, 8);
        savedItem.setId("new-id");

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(savedItem);

        // Act
        ShopItem result = shopItemService.insertNewShopItem(newItem);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("new-id");
        verify(shopItemRepository, times(1)).save(any(ShopItem.class));
    }

    @Test
    @DisplayName("updateShopItemById should set correct id and save item")
    void testUpdateShopItemById() {
        // Arrange
        String itemId = "1";
        ShopItem replacement = new ShopItem("Updated Laptop", "Updated description", 1500.00, 3);

        ShopItem updatedItem = new ShopItem("Updated Laptop", "Updated description", 1500.00, 3);
        updatedItem.setId(itemId);

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(updatedItem);

        // Act
        ShopItem result = shopItemService.updateShopItemById(itemId, replacement);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getName()).isEqualTo("Updated Laptop");
        assertThat(result.getPrice()).isEqualTo(1500.00);
        
        verify(shopItemRepository, times(1)).save(argThat(item -> 
            item.getId().equals(itemId) && 
            item.getName().equals("Updated Laptop")
        ));
    }

    @Test
    @DisplayName("updateShopItemById should override existing id in replacement")
    void testUpdateShopItemByIdOverridesReplacementId() {
        // Arrange
        String targetId = "target-id";
        ShopItem replacement = new ShopItem("Item", "Description", 100.00, 5);
        replacement.setId("wrong-id");

        ShopItem updatedItem = new ShopItem("Item", "Description", 100.00, 5);
        updatedItem.setId(targetId);

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(updatedItem);

        // Act
        ShopItem result = shopItemService.updateShopItemById(targetId, replacement);

        // Assert
        assertThat(result.getId()).isEqualTo(targetId);
        verify(shopItemRepository, times(1)).save(argThat(item -> 
            item.getId().equals(targetId)
        ));
    }

    @Test
    @DisplayName("deleteShopItem should call repository deleteById")
    void testDeleteShopItem() {
        // Arrange
        String itemId = "1";
        doNothing().when(shopItemRepository).deleteById(itemId);

        // Act
        shopItemService.deleteShopItem(itemId);

        // Assert
        verify(shopItemRepository, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("deleteShopItem should handle deletion of non-existing item")
    void testDeleteNonExistingShopItem() {
        // Arrange
        String itemId = "non-existing-id";
        doNothing().when(shopItemRepository).deleteById(itemId);

        // Act
        shopItemService.deleteShopItem(itemId);

        // Assert
        verify(shopItemRepository, times(1)).deleteById(itemId);
    }
}
