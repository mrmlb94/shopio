package com.example.onlineshop;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.example.onlineshop.model.ShopItem;

public class ShopItemWebControllerE2E {

    private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
    private static String baseUrl = "http://localhost:" + port;

    private WebDriver driver;
    private RestTemplate restTemplate;

    @BeforeClass
    public static void setupClass() {

    }

    @Before
    public void setup() {
        driver = new ChromeDriver();
        restTemplate = new RestTemplate();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    private ShopItem postShopItem(ShopItem item) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ShopItem> request = new HttpEntity<>(item, headers);
        
        return restTemplate.postForObject(
            baseUrl + "/api/shopitems/new", request, ShopItem.class);
    }

    @Test
    public void testCreateNewShopItem() {
        // Navigate to new item page
        driver.get(baseUrl + "/new");

        // Fill form
        driver.findElement(By.name("name")).sendKeys("E2E Laptop");
        driver.findElement(By.name("description")).sendKeys("E2E Gaming laptop");
        driver.findElement(By.name("price")).sendKeys("1799.99");
        driver.findElement(By.name("quantity")).sendKeys("3");

        // Submit form
        driver.findElement(By.name("btn_submit")).click();

        // Verify redirect to home page and item is displayed
        assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl + "/");
        assertThat(driver.getPageSource()).contains("E2E Laptop", "1799.99");
    }

    @Test
    public void testEditShopItem() {
        // Create item via API
        ShopItem item = postShopItem(
            new ShopItem("Original Mouse", "Basic mouse", 29.99, 50));

        // Navigate to edit page
        driver.get(baseUrl + "/edit/" + item.getId());

        // Update fields
        WebElement nameInput = driver.findElement(By.name("name"));
        nameInput.clear();
        nameInput.sendKeys("Updated Mouse");

        WebElement priceInput = driver.findElement(By.name("price"));
        priceInput.clear();
        priceInput.sendKeys("79.99");

        // Submit form
        driver.findElement(By.name("btn_submit")).click();

        // Verify updates are displayed
        assertThat(driver.getPageSource()).contains("Updated Mouse", "79.99");
    }

    @Test
    public void testViewShopItem() {

        // Navigate to home page
        driver.get(baseUrl);

        // Click view link
        driver.findElement(By.linkText("View")).click();

        // Verify item details page
        assertThat(driver.getCurrentUrl()).contains("/view/");
        assertThat(driver.getPageSource())
            .contains("View Monitor", "4K Display", "599.99", "8");
    }

    @Test
    public void testDeleteShopItem() {


        // Navigate to home page
        driver.get(baseUrl);
        assertThat(driver.getPageSource()).contains("Delete Keyboard");

        // Click delete link
        driver.findElement(By.linkText("Delete")).click();

        // Verify item is removed
        assertThat(driver.getPageSource()).doesNotContain("Delete Keyboard");
    }

    @Test
    public void testCompleteWorkflow_CreateViewEditDelete() {
        // S1: Create new item
        driver.get(baseUrl + "/new");
        driver.findElement(By.name("name")).sendKeys("Workflow Tablet");
        driver.findElement(By.name("description")).sendKeys("Android tablet");
        driver.findElement(By.name("price")).sendKeys("399.99");
        driver.findElement(By.name("quantity")).sendKeys("12");
        driver.findElement(By.name("btn_submit")).click();

        // S2: View item details
        driver.findElement(By.linkText("View")).click();
        assertThat(driver.getPageSource())
            .contains("Workflow Tablet", "Android tablet", "399.99", "12");

        // S3: Edit item
        driver.findElement(By.linkText("Edit")).click();
        WebElement quantityInput = driver.findElement(By.name("quantity"));
        quantityInput.clear();
        quantityInput.sendKeys("20");
        driver.findElement(By.name("btn_submit")).click();

        // S4: Verify edit
        assertThat(driver.getPageSource()).contains("20");

        // S5: Delete item
        driver.findElement(By.linkText("Delete")).click();

        // S6: Verify deletion
        assertThat(driver.getPageSource()).doesNotContain("Workflow Tablet");
    }
}
