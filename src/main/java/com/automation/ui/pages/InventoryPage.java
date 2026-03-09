package com.automation.ui.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object for the Sauce Demo inventory (products) page.
 * URL: https://www.saucedemo.com/inventory.html
 */
public class InventoryPage extends BasePage {

    private static final String PAGE_TITLE       = ".title";
    private static final String CART_LINK        = ".shopping_cart_link";

    public InventoryPage(Page page) {
        super(page);
    }

    public String getPageTitle() {
        waitForVisible(PAGE_TITLE);
        return getText(PAGE_TITLE);
    }

    public boolean isOnInventoryPage() {
        return getCurrentUrl().contains("/inventory.html");
    }

    /**
     * Adds a product to the cart by its displayed name.
     * Derives the data-test attribute from the product name.
     */
    public InventoryPage addProductToCart(String productName) {
        String buttonSelector = buildAddToCartSelector(productName);
        waitForVisible(buttonSelector);
        click(buttonSelector);
        return this;
    }

    public CartPage goToCart() {
        click(CART_LINK);
        return new CartPage(page);
    }

    /**
     * Converts a product name to the Sauce Demo data-test button attribute pattern.
     * Example: "Sauce Labs Backpack" → "add-to-cart-sauce-labs-backpack"
     */
    private String buildAddToCartSelector(String productName) {
        String normalized = productName.toLowerCase().replace(" ", "-");
        return "[data-test='add-to-cart-" + normalized + "']";
    }
}
