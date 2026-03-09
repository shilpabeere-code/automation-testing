# Test Design Explanation

This document describes the test design decisions, rationale for scenario selection, and the
prioritised list of additional scenarios that would be automated given more time.

---

## 1. Framework Architecture

### Why Playwright for both API and UI?

The task specified Playwright as the preferred tool. Using Playwright's `APIRequestContext`
for HTTP tests keeps the dependency footprint small (one library, one driver model) and
means engineers only need to learn a single tool. In a Java context, RestAssured would be
the more common API-testing choice, but Playwright's request API is expressive enough and
produces consistent reports alongside the UI tests.

### Page Object Model (POM)

All Sauce Demo page interactions are encapsulated in dedicated page classes
(`LoginPage`, `InventoryPage`, `CartPage`, `CheckoutPage`). This keeps step definitions
readable, prevents duplicated selectors, and means that if a selector changes, only one
class needs updating.

### BDD with Cucumber

Cucumber feature files serve as living documentation. Non-engineers can read them to
understand what is being tested without looking at Java code. Scenarios are tagged (`@api`,
`@ui`, `@smoke`, `@positive`, `@negative`) so subsets can be run in CI pipelines at
different cadences (smoke on every commit, full suite nightly).

### TestNG integration

TestNG drives parallel execution, retry logic, and structured reporting. `AbstractTestNGCucumberTests`
bridges Cucumber and TestNG, giving us the best of both worlds: Cucumber's BDD DSL and TestNG's
enterprise-grade runner.

---

## 2. Implemented Scenarios

### 2.1 API – POST /pet (Add a new pet)

**Chosen endpoint rationale:** `POST /pet` is the most fundamental write operation.
Validating it confirms the API can accept data, persist it, and return a correct
representation – a foundational check for any downstream scenario.

#### Positive – Successfully add a new pet

| Aspect | Detail |
|--------|--------|
| Input | Valid JSON: name="Buddy", status="available", category="Dogs" |
| Expected | HTTP 200, response body echoes name and status |
| Rationale | Confirms the happy path and that the API schema is understood correctly |

#### Negative – Retrieve a pet with a non-existent ID

| Aspect | Detail |
|--------|--------|
| Input | `GET /pet/999999999` (ID unlikely to exist) |
| Expected | HTTP 404 with error body containing "Pet not found" |
| Rationale | Validates that the API returns a meaningful 404 rather than a 500 or an empty 200 |

---

### 2.2 UI – Sauce Demo (https://www.saucedemo.com)

#### Positive – Successful login

| Aspect | Detail |
|--------|--------|
| Input | username=`standard_user`, password=`secret_sauce` |
| Expected | Redirected to `/inventory.html`, page title "Products" |
| Rationale | Login is the gateway to all other functionality; it is the highest-priority UI check |

#### Negative – Login with invalid credentials

| Aspect | Detail |
|--------|--------|
| Input | username=`invalid_user`, password=`wrong_password` |
| Expected | Error banner: "Epic sadface: Username and password do not match..." |
| Rationale | Verifies the application surfaces actionable errors rather than silently failing |

#### End-to-end – Add to cart and checkout

| Aspect | Detail |
|--------|--------|
| Flow | Login → add "Sauce Labs Backpack" → cart → checkout form → finish |
| Expected | Confirmation page with "Thank you for your order!" |
| Rationale | The checkout flow is the business-critical happy path; an e2e test validates the entire pipeline works together |

---

## 3. When to Test at the API Layer vs the UI Layer

A key engineering trade-off is where in the test pyramid to place a given check.

| Concern | API layer | UI layer |
|---------|-----------|----------|
| Speed | Fast (ms) | Slow (seconds) |
| Stability | High – no DOM changes | Lower – selectors can drift |
| Scope | Business logic, data contracts | Rendering, user journeys |
| Feedback loop | Immediate in CI | Slower |

**Rule of thumb applied in this project:**

- **API tests** own: authentication tokens, data validation, error responses, status codes, schema
  checks, and anything that does not require a rendered page.
- **UI tests** own: visual presentation, user-journey orchestration, form interactions, navigation
  flows, and accessibility concerns.

For example, verifying that an invalid password returns an error *could* be done at the API
layer by calling the auth endpoint directly. The UI test is included here only to confirm the
error message is displayed correctly to the user – the *presentation* of the error, not just
its existence.

---

## 4. Additional Test Scenarios (Prioritised)

### 4.1 API – Swagger Petstore

| Priority | Scenario | Positive / Negative | Rationale |
|----------|----------|---------------------|-----------|
| 1 | `POST /pet` with missing `name` field | Negative | Required-field validation is the most critical negative path after the happy path |
| 2 | `POST /pet` with invalid status value (e.g. `"flying"`) | Negative | Enum validation prevents bad data entering the store |
| 3 | `GET /pet/findByStatus?status=available` returns non-empty list | Positive | Confirms read-by-filter works and the store has data |
| 4 | `PUT /pet` to update an existing pet's name | Positive | Core CRUD – update path |
| 5 | `DELETE /pet/{id}` for an existing pet returns 200 | Positive | Core CRUD – delete path |
| 6 | `DELETE /pet/{id}` for a non-existent ID returns 404 | Negative | Idempotency and error handling of delete |
| 7 | `POST /pet` with malformed JSON returns 400/415 | Negative | Defensive – ensures API doesn't crash on bad input |
| 8 | `GET /pet/findByStatus` with no status param | Negative | Missing required query param handling |

### 4.2 UI – Sauce Demo

| Priority | Scenario | Positive / Negative | Rationale |
|----------|----------|---------------------|-----------|
| 1 | Login with `locked_out_user` → specific error | Negative | Named user type with known locked state; important regression check |
| 2 | Login with empty username/password → validation | Negative | Form validation; must surface clear error to user |
| 3 | Sort products by "Price (low to high)" → correct order | Positive | Sorting is a high-traffic feature |
| 4 | Add multiple items → cart badge shows correct count | Positive | Cart state management |
| 5 | Remove item from cart → item absent and badge decrements | Negative | Cart mutability |
| 6 | Checkout with missing postal code → validation error | Negative | Form validation on checkout step 1 |
| 7 | Logout via burger menu → redirect to login page | Positive | Session management |
| 8 | Checkout overview shows correct total price | Positive | Financial accuracy – highest business risk |
| 9 | Login with `performance_glitch_user` → page loads within SLA | Non-functional | Performance regression guard |
| 10 | Verify product details page matches inventory page data | Positive | Data consistency across views |

---

## 5. Challenges and Trade-offs

### Petstore API stability
The public Swagger Petstore is a shared sandbox. Tests that create data (POST) may see ID
collisions or data from other test runs. In a real project, a dedicated test environment or
API mocking (WireMock) would be used to guarantee isolation.

### Playwright Java API vs RestAssured
Playwright's `APIRequestContext` in Java lacks some of the fluent assertion helpers that
RestAssured provides (e.g., Hamcrest matchers, JSON path extraction). We work around this
with Jackson's `ObjectMapper`. In a larger project, RestAssured would be the preferred API
testing tool while keeping Playwright purely for UI.

### Headless vs headed browser
`browser.headless=true` is the default for CI. Running headed locally makes debugging
easier. The `PlaywrightManager` reads this from config so no code change is needed.

### Screenshot on failure
The `@After("@ui")` hook automatically captures and embeds a screenshot in the Cucumber
report when a scenario fails. This significantly reduces debugging time in CI.
