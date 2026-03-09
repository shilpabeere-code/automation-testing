# Test Automation Framework

A BDD-driven test automation framework covering both **API** and **UI** testing, built with:

| Component | Technology |
|-----------|-----------|
| Language | Java 11 |
| UI & API driver | Playwright (Java) |
| BDD layer | Cucumber 7 |
| Test runner | TestNG 7 |
| Build tool | Maven 3.8+ |
| Reporting | Cucumber HTML + masterthought |

---

## Project Structure

```
automation-testing/
├── pom.xml
├── README.md
├── TEST_DESIGN.md
└── src/
    ├── main/java/com/automation/
    │   ├── config/
    │   │   ├── ConfigManager.java        # Reads config.properties / env vars
    │   │   └── PlaywrightManager.java    # Thread-safe Playwright lifecycle
    │   ├── api/
    │   │   ├── client/
    │   │   │   └── PetstoreApiClient.java  # Petstore HTTP client (Playwright API)
    │   │   └── models/
    │   │       ├── Pet.java               # Pet domain model
    │   │       └── Category.java          # Category domain model
    │   └── ui/pages/
    │       ├── BasePage.java              # Shared Playwright helpers
    │       ├── LoginPage.java             # Sauce Demo login page
    │       ├── InventoryPage.java         # Products/inventory page
    │       ├── CartPage.java              # Shopping cart page
    │       └── CheckoutPage.java          # Multi-step checkout pages
    └── test/
        ├── java/com/automation/
        │   ├── hooks/
        │   │   └── Hooks.java             # @Before/@After per tag (@api, @ui)
        │   ├── stepdefs/
        │   │   ├── api/PetStoreStepDefs.java
        │   │   └── ui/SauceDemoStepDefs.java
        │   └── runners/
        │       ├── ApiTestRunner.java     # TestNG runner – @api scenarios
        │       └── UiTestRunner.java      # TestNG runner – @ui scenarios
        └── resources/
            ├── features/
            │   ├── api/petstore_management.feature
            │   └── ui/saucedemo_shopping.feature
            ├── config.properties          # Base URLs, browser settings
            └── testng.xml                 # TestNG suite definition
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK | 11 or higher |
| Maven | 3.8 or higher |
| Internet access | Required (tests hit live APIs/sites) |

> **Note:** Playwright downloads its own browser binaries automatically. You do **not** need to install Chromium separately.

---

## Quick Start

### 1 – Clone and build

```bash
git clone <repository-url>
cd automation-testing
mvn install -DskipTests    # download dependencies + install Playwright browsers
```

### 2 – Install Playwright browsers (first time only)

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

### 3 – Run all tests

```bash
mvn test
```

### 4 – Run only API tests

```bash
mvn test -Dcucumber.filter.tags="@api"
```

### 5 – Run only UI tests

```bash
mvn test -Dcucumber.filter.tags="@ui"
```

### 6 – Run smoke tests only

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

---

## Configuration

All settings live in `src/test/resources/config.properties`.
**Environment variables override file values** (useful for CI). The mapping is:

| Property key | Env variable | Default |
|---|---|---|
| `api.base.url` | `API_BASE_URL` | `https://petstore.swagger.io/v2` |
| `ui.base.url` | `UI_BASE_URL` | `https://www.saucedemo.com` |
| `browser.headless` | `BROWSER_HEADLESS` | `true` |
| `api.timeout` | `API_TIMEOUT` | `30000` |
| `ui.timeout` | `UI_TIMEOUT` | `30000` |

### Run with a headed browser (useful for local debugging)

```bash
BROWSER_HEADLESS=false mvn test -Dcucumber.filter.tags="@ui"
```

---

## Test Reports

After each run, reports are written to `target/cucumber-reports/`:

| Report | Path |
|--------|------|
| API HTML report | `target/cucumber-reports/api/api-report.html` |
| API JSON | `target/cucumber-reports/api/api-report.json` |
| UI HTML report | `target/cucumber-reports/ui/ui-report.html` |
| UI JSON | `target/cucumber-reports/ui/ui-report.json` |
| TestNG XML results | `target/surefire-reports/` |

Open the HTML reports in any browser to view scenario results, step timings, and embedded screenshots (attached automatically on UI test failure).

---

## Tagging Strategy

| Tag | Meaning |
|-----|---------|
| `@api` | API test – uses `APIRequestContext`, no browser |
| `@ui` | UI test – launches Chromium via Playwright |
| `@smoke` | Core happy-path scenario; run first in CI |
| `@positive` | Happy-path scenario |
| `@negative` | Error / invalid-input scenario |
| `@e2e` | Full end-to-end journey spanning multiple pages |

---

## CI / CD Usage

The suite is designed to run in CI without modification:

```yaml
# Example GitHub Actions step
- name: Run tests
  run: |
    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
    mvn test
  env:
    BROWSER_HEADLESS: "true"
```

---

## Troubleshooting

| Problem | Solution |
|---------|---------|
| `Executable doesn't exist` on Playwright launch | Run `mvn exec:java ... install chromium` (see Quick Start step 2) |
| `ConnectionRefused` on API tests | Verify internet access and that `petstore.swagger.io` is reachable |
| UI tests flaky on CI | Increase `ui.timeout` in config or set `BROWSER_HEADLESS=true` |
| Dependency resolution fails | Run `mvn dependency:resolve` and check Maven settings for proxy |
