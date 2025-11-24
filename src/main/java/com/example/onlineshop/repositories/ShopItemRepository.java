package com.example.onlineshop.repositories;

import com.example.onlineshop.model.ShopItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShopItemRepository extends MongoRepository<ShopItem, String> {

    ShopItem findFirstByName(String name);  
    List<ShopItem> findByName(String name);   
    List<ShopItem> findByNameAndPrice(String name, double price);
    List<ShopItem> findByNameOrPrice(String name, double price);
}

