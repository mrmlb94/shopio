package com.example.onlineshop.controllers;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopitems")
public class ShopItemRestController {

    private final ShopItemService shopItemService;

    public ShopItemRestController(ShopItemService shopItemService) {
        this.shopItemService = shopItemService;
    }

    @GetMapping
    public List<ShopItem> allShopItems() {
        return shopItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ShopItem oneShopItem(@PathVariable String id) {
        return shopItemService.getItemById(id);
    }

    @PostMapping("/new")
    public ShopItem newShopItem(@RequestBody ShopItem item) {
        return shopItemService.insertNewShopItem(item);
    }

    @PutMapping("/update/{id}")
    public ShopItem updateShopItem(@PathVariable String id, @RequestBody ShopItem item) {
        return shopItemService.updateShopItemById(id, item);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteShopItem(@PathVariable String id) {
        shopItemService.deleteShopItem(id);
    }
}
