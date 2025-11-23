package com.example.onlineshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.onlineshop.controllers.ShopItemWebController;
import com.example.onlineshop.model.ShopItem;
import com.example.onlineshop.services.ShopItemService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ShopItemWebController.class)
public class ShopItemWebControllerHtmlUnitIT {

    @Autowired
    private WebClient webClient;

    @MockitoBean
    private ShopItemService shopItemService;

    @Test
    public void testHomePageTitle() throws IOException {
        HtmlPage page = webClient.getPage("/");
        assertThat(page.getTitleText()).isEqualTo("Shop Items");
    }

    @Test
    public void testHomePageWithNoShopItems() throws IOException {
        when(shopItemService.getAllItems()).thenReturn(Collections.emptyList());

        HtmlPage page = webClient.getPage("/");

        assertThat(page.getBody().asNormalizedText()).contains("No shop items available");
    }

    @Test
    public void testHomePage_ShouldProvideALinkForCreatingANewShopItem() throws IOException {
        when(shopItemService.getAllItems()).thenReturn(Collections.emptyList());

        HtmlPage page = webClient.getPage("/");

        assertThat(page.getAnchorByText("Add New Item").getHrefAttribute())
            .isEqualTo("/new");
    }

    @Test
    public void testHomePageWithShopItems_ShouldShowThemInATable() throws IOException {

        ShopItem item1 = new ShopItem("Laptop", "Gaming laptop", 1499.99, 5);
        item1.setId("1");
        ShopItem item2 = new ShopItem("Mouse", "Gaming mouse", 79.99, 20);
        item2.setId("2");
        
        when(shopItemService.getAllItems()).thenReturn(Arrays.asList(item1, item2));

        HtmlPage page = webClient.getPage("/");

        HtmlTable table = page.getHtmlElementById("shopitem_table");
        assertThat(table.asNormalizedText()).contains("Laptop", "Mouse", "1499.99", "79.99");
        
        // Verify edit links
        page.getAnchorByHref("/edit/1");
        page.getAnchorByHref("/edit/2");
        
        // Verify view links
        page.getAnchorByHref("/view/1");
        page.getAnchorByHref("/view/2");
        
        // Verify delete links
        page.getAnchorByHref("/delete/1");
        page.getAnchorByHref("/delete/2");
    }

    @Test
    public void testEditNonExistentShopItem() throws IOException {
        when(shopItemService.getItemById("999")).thenReturn(null);

        HtmlPage page = webClient.getPage("/edit/999");

        assertThat(page.getBody().asNormalizedText()).contains("Shop item not found");
    }

    @Test
    public void testEditExistentShopItem() throws IOException {

        ShopItem item = new ShopItem("Keyboard", "Mechanical keyboard", 149.99, 10);
        item.setId("1");
        when(shopItemService.getItemById("1")).thenReturn(item);

        HtmlPage page = webClient.getPage("/edit/1");

        HtmlForm form = page.getFormByName("shopitem_form");
        assertThat(form.getInputByName("id").getValueAttribute()).isEqualTo("1");
        assertThat(form.getInputByName("name").getValueAttribute()).isEqualTo("Keyboard");
        assertThat(form.getInputByName("description").getValueAttribute())
            .isEqualTo("Mechanical keyboard");
        assertThat(form.getInputByName("price").getValueAttribute()).isEqualTo("149.99");
        assertThat(form.getInputByName("quantity").getValueAttribute()).isEqualTo("10");
    }

    @Test
    public void testEditNewShopItem() throws IOException {
        HtmlPage page = webClient.getPage("/new");

        HtmlForm form = page.getFormByName("shopitem_form");
        assertThat(form.getInputByName("id").getValueAttribute()).isEmpty();
        assertThat(form.getInputByName("name").getValueAttribute()).isEmpty();
        assertThat(form.getInputByName("description").getValueAttribute()).isEmpty();
    }

    @Test
    public void testViewShopItem() throws IOException {

        ShopItem item = new ShopItem("Monitor", "4K Display", 599.99, 8);
        item.setId("1");
        when(shopItemService.getItemById("1")).thenReturn(item);

        HtmlPage page = webClient.getPage("/view/1");

        assertThat(page.getBody().asNormalizedText())
            .contains("Monitor", "4K Display", "599.99", "8");
        
        // Verify back link exists
        page.getAnchorByText("Back to List");
    }
}
