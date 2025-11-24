package com.example.onlineshop;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End test class for ShopItem Web functionality.
 */


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductWebControllerE2ETest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductWebControllerE2ETest.class);
    private static final int TIMEOUT_SECONDS = 10;
    private static final String MONGO_IMAGE = "mongo:7.0";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(MONGO_IMAGE);

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;
    private WebDriverWait wait;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().clearDriverCache().setup();
        LOGGER.info("WebDriverManager configured for Chrome");
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        
        RestAssured.baseURI = baseUrl;
        deleteAllProducts();
        
        LOGGER.info("Test setup completed. Base URL: {}", baseUrl);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
            LOGGER.debug("WebDriver closed");
        }
    }

    /**
     * Helper method to set number input values using JavaScript to avoid locale/formatting issues
     */
    private void setNumberFieldValue(String fieldId, String value) {
        WebElement field = driver.findElement(By.id(fieldId));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", field, value);
        LOGGER.debug("Set field {} to value {}", fieldId, value);
    }

    @Test
    void testCreateNewProduct() {
        LOGGER.info("Starting testCreateNewProduct");
        
        driver.get(baseUrl + "/");
        LOGGER.debug("Navigated to home page");

        WebElement addProductLink = wait.until(
            ExpectedConditions.elementToBeClickable(By.linkText("Create New Item"))
        );
        addProductLink.click();
        LOGGER.debug("Clicked 'Create New Item' link");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        
        driver.findElement(By.id("name")).sendKeys("Laptop Pro");
        driver.findElement(By.id("description")).sendKeys("High-performance laptop");
        setNumberFieldValue("price", "1299.99");
        setNumberFieldValue("quantity", "15");
        LOGGER.debug("Filled product form fields");

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();
        LOGGER.debug("Clicked submit button");

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        
        String pageSource = driver.getPageSource();
        
        assertThat(pageSource)
        .contains("Laptop Pro")
        .contains("1299.99");

        
        LOGGER.info("testCreateNewProduct completed successfully");
    }

    @Test
    void testEditProduct() {
        LOGGER.info("Starting testEditProduct");
        
        String productId = postProduct("Gaming Mouse", "RGB gaming mouse", 79.99, 50);
        LOGGER.debug("Created product with ID: {}", productId);

        driver.get(baseUrl + "/");
        
        WebElement editLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//td[text()='Gaming Mouse']/following-sibling::td//a[text()='Edit']")
            )
        );
        editLink.click();
        LOGGER.debug("Clicked edit link for product");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        
        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Gaming Mouse Pro");
        
        setNumberFieldValue("price", "89.99");
        setNumberFieldValue("quantity", "75");
        
        LOGGER.debug("Updated form fields");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        
        String pageSource = driver.getPageSource();
        
        // because of damn Sonar issues !
        assertThat(pageSource)
        .contains("Gaming Mouse Pro")
        .contains("89.99");

        LOGGER.info("testEditProduct completed successfully");
    }

    @Test
    void testDeleteProduct() {
        LOGGER.info("Starting testDeleteProduct");
        
        String productId = postProduct("Keyboard", "Mechanical keyboard", 129.99, 30);
        LOGGER.debug("Created product with ID: {}", productId);

        driver.get(baseUrl + "/");
        
        String pageSourceBefore = driver.getPageSource();
        assertThat(pageSourceBefore).contains("Keyboard");
        
        WebElement deleteLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//td[text()='Keyboard']/following-sibling::td//a[text()='Delete']")
            )
        );
        deleteLink.click();
        LOGGER.debug("Clicked delete link for product");

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        
        String pageSource = driver.getPageSource();
        
        // because of damn Sonar issues !
        assertThat(pageSource)
        .doesNotContain("Keyboard")
        .doesNotContain("129.99");

        
        LOGGER.info("testDeleteProduct completed successfully");
    }

    @Test
    void testViewProductDetails() {
        LOGGER.info("Starting testViewProductDetails");
        
        String productId = postProduct(
            "Monitor 4K", 
            "Ultra HD 4K monitor with HDR support", 
            599.99, 
            20
        );
        LOGGER.debug("Created product with ID: {}", productId);

        driver.get(baseUrl + "/");
        
        WebElement detailsLink = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//td[text()='Monitor 4K']/following-sibling::td//a[text()='View']")
            )
        );
        detailsLink.click();
        LOGGER.debug("Clicked view link for product");

        wait.until(ExpectedConditions.urlContains("/view/" + productId));
        
        String pageSource = driver.getPageSource();
        
        assertThat(pageSource)
        .contains("Monitor 4K")
        .contains("Ultra HD 4K monitor with HDR support")
        .contains("599.99")
        .contains("20");

        
        LOGGER.info("testViewProductDetails completed successfully");
    }

    @Test
    void testCompleteWorkflow() {
        LOGGER.info("Starting testCompleteWorkflow");
        
        // S1: Create a  new item
        driver.get(baseUrl + "/new");
        driver.findElement(By.id("name")).sendKeys("Workflow Tablet");
        driver.findElement(By.id("description")).sendKeys("Android tablet");
        setNumberFieldValue("price", "399.99");
        setNumberFieldValue("quantity", "12");
        
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        LOGGER.debug("Created new item");

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        assertThat(driver.getPageSource()).contains("Workflow Tablet");

        // S2: View item details
        WebElement viewLink = driver.findElement(
            By.xpath("//td[text()='Workflow Tablet']/following-sibling::td//a[text()='View']")
        );
        viewLink.click();
        wait.until(ExpectedConditions.urlContains("/view/"));
        
        
        // as Sonar said !
        String viewPageSource = driver.getPageSource();
        assertThat(viewPageSource)
        .contains("Workflow Tablet")
        .contains("Android tablet")
        .contains("399.99")
        .contains("12");
        
        
        LOGGER.debug("Viewed item details");

        // S3 Edit item (from view page)
        driver.findElement(By.linkText("Edit")).click();
        wait.until(ExpectedConditions.urlContains("/edit/"));
        
        setNumberFieldValue("quantity", "20");
        
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        LOGGER.debug("Edited item");

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        assertThat(driver.getPageSource()).contains("20");

        // Step 4: Delete item
        WebElement deleteLink = driver.findElement(
            By.xpath("//td[text()='Workflow Tablet']/following-sibling::td//a[text()='Delete']")
        );
        deleteLink.click();
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        LOGGER.debug("Deleted item");

        // Step 5: Verify deletion
        assertThat(driver.getPageSource()).doesNotContain("Workflow Tablet");
        
        LOGGER.info("testCompleteWorkflow completed successfully");
    }

    private String postProduct(String name, String description, double price, int quantity) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("description", description);
        product.put("price", price);
        product.put("quantity", quantity);

        String productId = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(product)
            .when()
            .post("/api/shopitems/new")
            .then()
            .statusCode(200)
            .extract()
            .path("id");

        LOGGER.debug("Created product via API - ID: {}, Name: {}, Price: {}", productId, name, price);
        return productId;
    }

    private void deleteAllProducts() {
        try {
            List<String> itemIds = RestAssured.given()
                .when()
                .get("/api/shopitems")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("id", String.class);
            
            for (String id : itemIds) {
                RestAssured.given()
                    .when()
                    .delete("/api/shopitems/delete/" + id);
            }
            LOGGER.debug("All products deleted successfully. Count: {}", itemIds.size());
        } catch (Exception e) {
            LOGGER.warn("Failed to delete all products: {}", e.getMessage());
        }
    }
}
