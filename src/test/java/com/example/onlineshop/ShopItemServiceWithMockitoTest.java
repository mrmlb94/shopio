package com.example.onlineshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import com.example.onlineshop.services.ShopItemService;

@RunWith(MockitoJUnitRunner.class)
public class ShopItemServiceWithMockitoTest {

    @Mock
    private ShopItemRepository shopItemRepository;

    @InjectMocks
    private ShopItemService shopItemService;

    @Test
    public void testGetAllItems() {
        ShopItem item1 = new ShopItem("Laptop", "Gaming laptop", 1499.99, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Mouse", "Gaming mouse", 79.99, 20);
        item2.setId("2");

        when(shopItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        List<ShopItem> allItems = shopItemService.getAllItems();

        assertThat(allItems).hasSize(2);
        verify(shopItemRepository).findAll();
    }

    @Test
    public void testGetItemById_found() {
        ShopItem item = new ShopItem("Keyboard", "Mechanical", 149.99, 10);
        item.setId("1");

        when(shopItemRepository.findById("1")).thenReturn(Optional.of(item));

        ShopItem result = shopItemService.getItemById("1");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Keyboard");

        verify(shopItemRepository).findById("1");
    }

    @Test
    public void testGetItemById_notFound() {
        when(shopItemRepository.findById(anyString())).thenReturn(Optional.empty());

        ShopItem result = shopItemService.getItemById("999");

        assertThat(result).isNull();
        verify(shopItemRepository).findById("999");
    }

    @Test
    public void testInsertNewShopItem_setsIdToNull_and_returnsSavedItem() {
        ShopItem itemWithId = new ShopItem("Monitor", "4K Monitor", 599.99, 8);
        itemWithId.setId("100");

        ShopItem savedItem = new ShopItem("Monitor", "4K Monitor", 599.99, 8);
        savedItem.setId("1");

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(savedItem);

        ShopItem result = shopItemService.insertNewShopItem(itemWithId);

        ArgumentCaptor<ShopItem> captor = ArgumentCaptor.forClass(ShopItem.class);
        verify(shopItemRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isNull();
        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    public void testUpdateShopItemById_setsIdCorrectly() {
        ShopItem itemToUpdate = new ShopItem("Tablet", "Updated tablet", 399.99, 12);

        ShopItem savedItem = new ShopItem("Tablet", "Updated tablet", 399.99, 12);
        savedItem.setId("5");

        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(savedItem);

        ShopItem result = shopItemService.updateShopItemById("5", itemToUpdate);

        ArgumentCaptor<ShopItem> captor = ArgumentCaptor.forClass(ShopItem.class);
        verify(shopItemRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo("5");
        assertThat(result.getId()).isEqualTo("5");
    }

    @Test
    public void testDeleteShopItem() {
        shopItemService.deleteShopItem("1");
        verify(shopItemRepository).deleteById("1");
    }
}
