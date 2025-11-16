package com.example.onlineshop.services;


import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.repositories.ShopItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopItemService {

    private final ShopItemRepository shopItemRepository;

    public ShopItemService(ShopItemRepository shopItemRepository) {
        this.shopItemRepository = shopItemRepository;
    }

    public List<ShopItem> getAllItems() {
        return shopItemRepository.findAll();
    }

    public ShopItem getItemById(String id) {
        return shopItemRepository.findById(id).orElse(null);
    }

    public ShopItem insertNewShopItem(ShopItem item) {
        item.setId(null);
        return shopItemRepository.save(item);
    }

    public ShopItem updateShopItemById(String id, ShopItem replacement) {
        replacement.setId(id);
        return shopItemRepository.save(replacement);
    }

    public void deleteShopItem(String id) {
        shopItemRepository.deleteById(id);
    }
}
