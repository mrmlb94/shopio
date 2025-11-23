package com.example.onlineshop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.onlineshop.model.ShopItem;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ShopItem Model Unit Tests")
class ShopItemTest {

    @Test
    @DisplayName("Default constructor should create empty ShopItem")
    void testDefaultConstructor() {
        // Act
        ShopItem item = new ShopItem();

        // Assert
        assertThat(item).isNotNull();
        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isNull();
        assertThat(item.getDescription()).isNull();
        assertThat(item.getPrice()).isZero();
        assertThat(item.getQuantity()).isZero();
    }

    @Test
    @DisplayName("Parameterized constructor should set all fields except id")
    void testParameterizedConstructor() {
        // Act
        ShopItem item = new ShopItem("Laptop", "Gaming laptop", 1200.00, 5);

        // Assert
        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isEqualTo("Laptop");
        assertThat(item.getDescription()).isEqualTo("Gaming laptop");
        assertThat(item.getPrice()).isEqualTo(1200.00);
        assertThat(item.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Setters and getters should work correctly")
    void testSettersAndGetters() {
        // Arrange
        ShopItem item = new ShopItem();

        // Act
        item.setId("123");
        item.setName("Mouse");
        item.setDescription("Wireless mouse");
        item.setPrice(25.50);
        item.setQuantity(20);

        // Assert
        assertThat(item.getId()).isEqualTo("123");
        assertThat(item.getName()).isEqualTo("Mouse");
        assertThat(item.getDescription()).isEqualTo("Wireless mouse");
        assertThat(item.getPrice()).isEqualTo(25.50);
        assertThat(item.getQuantity()).isEqualTo(20);
    }

    @Test
    @DisplayName("toString should return correct string representation")
    void testToString() {
        // Arrange
        ShopItem item = new ShopItem("Keyboard", "Mechanical", 150.00, 10);
        item.setId("456");

        // Act
        String result = item.toString();

        // Assert
        assertThat(result).contains("id='456'");
        assertThat(result).contains("name='Keyboard'");
        assertThat(result).contains("description='Mechanical'");
        assertThat(result).contains("price=150.0");
        assertThat(result).contains("quantity=10");
    }

    @Test
    @DisplayName("equals should return true for same object")
    void testEqualsSameObject() {
        // Arrange
        ShopItem item = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item.setId("1");

        // Act & Assert
        assertThat(item.equals(item)).isTrue();
    }

    @Test
    @DisplayName("equals should return true for objects with same values")
    void testEqualsSameValues() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item2.setId("1");

        // Act & Assert
        assertThat(item1.equals(item2)).isTrue();
        assertThat(item2.equals(item1)).isTrue();
    }

    @Test
    @DisplayName("equals should return false for objects with different ids")
    void testEqualsDifferentIds() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item2.setId("2");

        // Act & Assert
        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    @DisplayName("equals should return false for objects with different names")
    void testEqualsDifferentNames() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Desktop", "Gaming", 1200.00, 5);
        item2.setId("1");

        // Act & Assert
        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    @DisplayName("equals should return false for objects with different prices")
    void testEqualsDifferentPrices() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Laptop", "Gaming", 1500.00, 5);
        item2.setId("1");

        // Act & Assert
        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    @DisplayName("equals should return false for objects with different quantities")
    void testEqualsDifferentQuantities() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Laptop", "Gaming", 1200.00, 10);
        item2.setId("1");

        // Act & Assert
        assertThat(item1.equals(item2)).isFalse();
    }

    @Test
    @DisplayName("equals should return false for null")
    void testEqualsNull() {
        // Arrange
        ShopItem item = new ShopItem("Laptop", "Gaming", 1200.00, 5);

        // Act & Assert
        assertThat(item.equals(null)).isFalse();
    }

    @Test
    @DisplayName("equals should return false for different class")
    void testEqualsDifferentClass() {
        // Arrange
        ShopItem item = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        String other = "Not a ShopItem";

        // Act & Assert
        //assertThat(item.equals(other)).isFalse();
        assertThat(item).isNotEqualTo(other);

    }

    @Test
    @DisplayName("hashCode should be equal for equal objects")
    void testHashCodeEqual() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item2.setId("1");

        // Act & Assert
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    @DisplayName("hashCode should be different for different objects")
    void testHashCodeDifferent() {
        // Arrange
        ShopItem item1 = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item1.setId("1");

        ShopItem item2 = new ShopItem("Mouse", "Wireless", 25.00, 20);
        item2.setId("2");

        // Act & Assert
        assertThat(item1.hashCode()).isNotEqualTo(item2.hashCode());
    }

    @Test
    @DisplayName("hashCode should be consistent")
    void testHashCodeConsistent() {
        // Arrange
        ShopItem item = new ShopItem("Laptop", "Gaming", 1200.00, 5);
        item.setId("1");

        // Act
        int hash1 = item.hashCode();
        int hash2 = item.hashCode();

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Should handle null values in equals")
    void testEqualsWithNullFields() {
        // Arrange
        ShopItem item1 = new ShopItem();
        ShopItem item2 = new ShopItem();

        // Act & Assert
        assertThat(item1.equals(item2)).isTrue();
    }

    @Test
    @DisplayName("Should handle price with decimals correctly")
    void testPriceWithDecimals() {
        // Arrange & Act
        ShopItem item = new ShopItem("Item", "Description", 99.99, 1);

        // Assert
        assertThat(item.getPrice()).isEqualTo(99.99);
    }

    @Test
    @DisplayName("Should handle large quantity values")
    void testLargeQuantity() {
        // Arrange & Act
        ShopItem item = new ShopItem("Item", "Description", 10.00, 1000000);

        // Assert
        assertThat(item.getQuantity()).isEqualTo(1000000);
    }
}
