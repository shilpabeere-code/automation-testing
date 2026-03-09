package com.automation.ui.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object for the Sauce Demo shopping cart page.
 * URL: https://www.saucedemo.com/cart.html
 */
public class CartPage extends BasePage {

    private static final String CHECKOUT_BUTTON = "[data-test='checkout']";

    public CartPage(Page page) {
        super(page);
    }

    public CheckoutPage proceedToCheckout() {
        waitForVisible(CHECKOUT_BUTTON);
        click(CHECKOUT_BUTTON);
        return new CheckoutPage(page);
    }
}
