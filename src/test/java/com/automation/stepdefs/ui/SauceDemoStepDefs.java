package com.automation.stepdefs.ui;

import com.automation.config.PlaywrightManager;
import com.automation.ui.pages.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.*;

/**
 * Step definitions for Sauce Demo UI scenarios.
 *
 * Design notes:
 * - Page Object Model (POM) pattern: each page has its own class with selectors
 *   and actions encapsulated. Step definitions only call page methods; they never
 *   interact with the Playwright Page object directly.
 * - Pages are created on demand (lazy initialisation) and carried as instance
 *   fields. Cucumber creates one instance per scenario so state never leaks.
 * - Assertions use TestNG's Assert for consistent reporting across the suite.
 */
public class SauceDemoStepDefs {

    private static final Logger log = LoggerFactory.getLogger(SauceDemoStepDefs.class);

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("I am on the Sauce Demo login page")
    public void iAmOnTheSauceDemoLoginPage() {
        loginPage = new LoginPage(PlaywrightManager.getPage()).open();
        log.info("Navigated to Sauce Demo login page");
    }

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedInAs(String username, String password) {
        loginPage = new LoginPage(PlaywrightManager.getPage()).open();
        inventoryPage = loginPage.loginAs(username, password);
        assertTrue(inventoryPage.isOnInventoryPage(),
                "Login failed: not redirected to inventory page");
        log.info("Logged in as '{}'", username);
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("I enter username {string} and password {string}")
    public void iEnterUsernameAndPassword(String username, String password) {
        loginPage.enterUsername(username).enterPassword(password);
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        loginPage.clickLogin();
    }

    @When("I add {string} to the cart")
    public void iAddProductToCart(String productName) {
        inventoryPage.addProductToCart(productName);
        log.info("Added '{}' to cart", productName);
    }

    @When("I go to the cart")
    public void iGoToTheCart() {
        cartPage = inventoryPage.goToCart();
    }

    @When("I proceed to checkout")
    public void iProceedToCheckout() {
        checkoutPage = cartPage.proceedToCheckout();
    }

    @When("I fill in first name {string} last name {string} and zip code {string}")
    public void iFillInCheckoutDetails(String firstName, String lastName, String zipCode) {
        checkoutPage.fillCustomerInfo(firstName, lastName, zipCode).clickContinue();
    }

    @When("I complete the order")
    public void iCompleteTheOrder() {
        checkoutPage.finishOrder();
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("I should be on the inventory page")
    public void iShouldBeOnTheInventoryPage() {
        inventoryPage = new InventoryPage(PlaywrightManager.getPage());
        assertTrue(inventoryPage.isOnInventoryPage(),
                "Expected to be on inventory page but URL was: "
                        + PlaywrightManager.getPage().url());
    }

    @Then("the page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {
        inventoryPage = new InventoryPage(PlaywrightManager.getPage());
        assertEquals(inventoryPage.getPageTitle(), expectedTitle,
                "Inventory page title mismatch");
    }

    @Then("I should see the login error {string}")
    public void iShouldSeeTheLoginError(String expectedError) {
        assertTrue(loginPage.isErrorDisplayed(),
                "Error message is not displayed on the login page");
        assertEquals(loginPage.getErrorMessage(), expectedError,
                "Login error message mismatch");
    }

    @Then("I should see the order confirmation {string}")
    public void iShouldSeeTheOrderConfirmation(String expectedMessage) {
        assertTrue(checkoutPage.isOrderComplete(),
                "Expected checkout-complete URL but got: "
                        + PlaywrightManager.getPage().url());
        assertEquals(checkoutPage.getConfirmationMessage(), expectedMessage,
                "Order confirmation message mismatch");
    }
}
