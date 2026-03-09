package com.automation.ui.pages;

import com.automation.config.ConfigManager;
import com.microsoft.playwright.Page;

/**
 * Page Object for the Sauce Demo login page.
 * URL: https://www.saucedemo.com
 */
public class LoginPage extends BasePage {

    private static final String USERNAME_FIELD   = "[data-test='username']";
    private static final String PASSWORD_FIELD   = "[data-test='password']";
    private static final String LOGIN_BUTTON     = "[data-test='login-button']";
    private static final String ERROR_MESSAGE    = "[data-test='error']";

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage open() {
        navigateTo(ConfigManager.getUiBaseUrl());
        waitForVisible(USERNAME_FIELD);
        return this;
    }

    public LoginPage enterUsername(String username) {
        fill(USERNAME_FIELD, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        fill(PASSWORD_FIELD, password);
        return this;
    }

    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    public InventoryPage loginAs(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new InventoryPage(page);
    }

    public String getErrorMessage() {
        waitForVisible(ERROR_MESSAGE);
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isVisible(ERROR_MESSAGE);
    }
}
