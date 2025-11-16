package com.example.onlineshop.controllers;

import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShopItemWebController {

    private static final String MESSAGE_ATTRIBUTE = "message";
    private static final String ITEM_ATTRIBUTE = "shopitem";
    private static final String ITEMS_ATTRIBUTE = "shopitems";

    private final ShopItemService shopItemService;

    public ShopItemWebController(ShopItemService shopItemService) {
        this.shopItemService = shopItemService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<ShopItem> shopItems = shopItemService.getAllItems();
        model.addAttribute(ITEMS_ATTRIBUTE, shopItems);
        model.addAttribute(MESSAGE_ATTRIBUTE,
                shopItems.isEmpty() ? "No shop items found." : "");
        return "index";
    }

    @GetMapping("/view/{id}")
    public String viewItem(@PathVariable String id, Model model) {
        ShopItem item = shopItemService.getItemById(id);
        model.addAttribute(ITEM_ATTRIBUTE, item);
        model.addAttribute(MESSAGE_ATTRIBUTE,
                item == null ? "No item found with id: " + id : "");
        return "view";
    }

    @GetMapping("/edit/{id}")
    public String editItem(@PathVariable String id, Model model) {
        ShopItem item = shopItemService.getItemById(id);
        model.addAttribute(ITEM_ATTRIBUTE, item);
        model.addAttribute(MESSAGE_ATTRIBUTE,
                item == null ? "No item found with id: " + id : "");
        return "edit";
    }

    @GetMapping("/new")
    public String newItem(Model model) {
        model.addAttribute(ITEM_ATTRIBUTE, new ShopItem());
        model.addAttribute(MESSAGE_ATTRIBUTE, "");
        return "new";
    }

    @PostMapping("/save")
    public String saveItem(@ModelAttribute("shopitem") ShopItem shopItem) {
        final String id = shopItem.getId();
        if (id == null || id.isEmpty()) {
            shopItemService.insertNewShopItem(shopItem);
        } else {
            shopItemService.updateShopItemById(id, shopItem);
        }
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteItem(@PathVariable String id) {
        shopItemService.deleteShopItem(id);
        return "redirect:/";
    }
}
