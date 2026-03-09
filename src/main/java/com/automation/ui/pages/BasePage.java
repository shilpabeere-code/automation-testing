package com.automation.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Base page providing common Playwright interactions.
 * All page objects extend this class.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected void navigateTo(String url) {
        page.navigate(url);
    }

    protected void click(String selector) {
        page.locator(selector).click();
    }

    protected void fill(String selector, String value) {
        page.locator(selector).fill(value);
    }

    protected String getText(String selector) {
        return page.locator(selector).textContent();
    }

    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    protected void waitForVisible(String selector) {
        page.locator(selector).waitFor(
                new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    protected String getCurrentUrl() {
        return page.url();
    }
}
