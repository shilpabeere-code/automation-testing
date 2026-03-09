package com.automation.ui.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object covering the multi-step Sauce Demo checkout flow:
 *   Step 1 – Your Information (first name, last name, postal code)
 *   Step 2 – Overview
 *   Complete – Order confirmation
 */
public class CheckoutPage extends BasePage {

    // Step 1 – Your Information
    private static final String FIRST_NAME_FIELD  = "[data-test='firstName']";
    private static final String LAST_NAME_FIELD   = "[data-test='lastName']";
    private static final String ZIP_CODE_FIELD    = "[data-test='postalCode']";
    private static final String CONTINUE_BUTTON   = "[data-test='continue']";

    // Step 2 – Overview
    private static final String FINISH_BUTTON     = "[data-test='finish']";

    // Complete page
    private static final String CONFIRMATION_HEADER = ".complete-header";

    public CheckoutPage(Page page) {
        super(page);
    }

    public CheckoutPage fillCustomerInfo(String firstName, String lastName, String zipCode) {
        waitForVisible(FIRST_NAME_FIELD);
        fill(FIRST_NAME_FIELD, firstName);
        fill(LAST_NAME_FIELD, lastName);
        fill(ZIP_CODE_FIELD, zipCode);
        return this;
    }

    public CheckoutPage clickContinue() {
        click(CONTINUE_BUTTON);
        return this;
    }

    public CheckoutPage finishOrder() {
        waitForVisible(FINISH_BUTTON);
        click(FINISH_BUTTON);
        return this;
    }

    public String getConfirmationMessage() {
        waitForVisible(CONFIRMATION_HEADER);
        return getText(CONFIRMATION_HEADER);
    }

    public boolean isOrderComplete() {
        return getCurrentUrl().contains("/checkout-complete.html");
    }

}
