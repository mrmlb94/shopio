package com.example.onlineshop;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import com.example.onlineshop.controllers.ShopItemRestController;
import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;

@RunWith(MockitoJUnitRunner.class)
public class ShopItemRestControllerRestAssuredTest {

    @Mock
    private ShopItemService shopItemService;

    @InjectMocks
    private ShopItemRestController shopItemRestController;

    @Before
    public void setup() {
        standaloneSetup(shopItemRestController);
    }

    @Test
    public void testFindByIdWithExistingShopItem() {

    	ShopItem item = new ShopItem("Laptop", "Gaming laptop", 1499.99, 5);
        item.setId("1");
        when(shopItemService.getItemById("1")).thenReturn(item);

        given()
            .when()
            .get("/api/shopitems/1")
            .then()
            .statusCode(200)
            .body("id", equalTo("1"))
            .body("name", equalTo("Laptop"))
            .body("price", equalTo(1499.99f))
            .body("quantity", equalTo(5));
    }

    @Test
    public void testPostShopItem() {

        ShopItem savedItem = new ShopItem("Mouse", "Wireless mouse", 49.99, 30);
        savedItem.setId("2");
        when(shopItemService.insertNewShopItem(any(ShopItem.class))).thenReturn(savedItem);

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("{\"name\":\"Mouse\",\"description\":\"Wireless mouse\",\"price\":49.99,\"quantity\":30}")
            .when()
            .post("/api/shopitems/new")
            .then()
            .statusCode(200)
            .body("id", equalTo("2"))
            .body("name", equalTo("Mouse"));

        verify(shopItemService).insertNewShopItem(any(ShopItem.class));
    }

    @Test
    public void testUpdateShopItem() {

        ShopItem updatedItem = new ShopItem("Keyboard", "Updated mechanical", 169.99, 15);
        updatedItem.setId("1");
        when(shopItemService.updateShopItemById(anyString(), any(ShopItem.class))).thenReturn(updatedItem);

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("{\"name\":\"Keyboard\",\"description\":\"Updated mechanical\",\"price\":169.99,\"quantity\":15}")
            .when()
            .put("/api/shopitems/update/1")
            .then()
            .statusCode(200)
            .body("price", equalTo(169.99f));

        verify(shopItemService).updateShopItemById(anyString(), any(ShopItem.class));
    }

    @Test
    public void testDeleteShopItem() {
        given()
            .when()
            .delete("/api/shopitems/delete/1")
            .then()
            .statusCode(200);

        verify(shopItemService).deleteShopItem("1");
    }
}
