@ui
Feature: Sauce Demo Login and Shopping Flow
  As a Sauce Demo user
  I want to log in and complete a purchase
  So that I can verify the core e-commerce journey works end-to-end

  Background:
    Given I am on the Sauce Demo login page

  # ── Implemented scenarios ─────────────────────────────────────────────────────

  @smoke @positive
  Scenario: Successful login with valid credentials redirects to inventory page
    When I enter username "standard_user" and password "secret_sauce"
    And I click the login button
    Then I should be on the inventory page
    And the page title should be "Products"

  @negative
  Scenario: Login with invalid credentials shows an error message
    When I enter username "invalid_user" and password "wrong_password"
    And I click the login button
    Then I should see the login error "Epic sadface: Username and password do not match any user in this service"

  @positive @e2e
  Scenario: Add product to cart and complete checkout
    Given I am logged in as "standard_user" with password "secret_sauce"
    When I add "Sauce Labs Backpack" to the cart
    And I go to the cart
    And I proceed to checkout
    And I fill in first name "John" last name "Doe" and zip code "12345"
    And I complete the order
    Then I should see the order confirmation "Thank you for your order!"

  # ── Additional scenarios (documented, not implemented) ──────────────────────
  # Priority 1 – Login with locked_out_user → error "Sorry, this user has been locked out"
  # Priority 2 – Login with empty credentials → field-level validation errors
  # Priority 3 – Sort products by price (low to high) and verify order
  # Priority 4 – Add multiple items to cart and verify badge count
  # Priority 5 – Remove item from cart before checkout
  # Priority 6 – Attempt checkout with missing postal code → validation error
  # Priority 7 – Logout from the burger menu and verify redirect to login page
  # Priority 8 – Verify checkout overview shows correct total price
