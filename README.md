# Test Automation Framework

BDD-driven API and UI test automation using:

| Component | Technology |
|-----------|-----------|
| Language | Java 11 |
| UI & API driver | Playwright (Java) |
| BDD layer | Cucumber 7 |
| Test runner | TestNG 7 |
| Build tool | Maven 3.8+ |
| Reporting | Cucumber HTML + masterthought |

## Project Structure

```
src/
├── main/java/com/automation/
│   ├── config/          # ConfigManager, PlaywrightManager
│   ├── api/client/      # PetstoreApiClient
│   ├── api/models/      # Pet, Category
│   └── ui/pages/        # BasePage, LoginPage, InventoryPage, CartPage, CheckoutPage
└── test/
    ├── java/com/automation/
    │   ├── hooks/        # @Before/@After per tag (@api, @ui)
    │   ├── stepdefs/     # PetStoreStepDefs, SauceDemoStepDefs
    │   └── runners/      # ApiTestRunner, UiTestRunner
    └── resources/
        ├── features/     # petstore_management.feature, saucedemo_shopping.feature
        ├── config.properties
        └── testng.xml
```

## Prerequisites

- Java 11+, Maven 3.8+, internet access
- Playwright downloads its own browser binaries automatically

## Quick Start

```bash
git clone <repository-url>
cd automation-testing
mvn install -DskipTests   # download deps + install Playwright browsers
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
mvn test                  # run all tests
```

**Run by tag:**
```bash
mvn test -Dcucumber.filter.tags="@api"    # API tests only
mvn test -Dcucumber.filter.tags="@ui"     # UI tests only
mvn test -Dcucumber.filter.tags="@smoke"  # smoke tests only
```

**Headed browser (local debug):**
```bash
BROWSER_HEADLESS=false mvn test -Dcucumber.filter.tags="@ui"
```

## Configuration

`src/test/resources/config.properties` — env vars override file values:

| Property | Env var | Default |
|---|---|---|
| `api.base.url` | `API_BASE_URL` | `https://petstore.swagger.io/v2` |
| `ui.base.url` | `UI_BASE_URL` | `https://www.saucedemo.com` |
| `browser.headless` | `BROWSER_HEADLESS` | `true` |
| `api.timeout` | `API_TIMEOUT` | `30000` |
| `ui.timeout` | `UI_TIMEOUT` | `30000` |

## Reports

After each run, open in a browser:

| Report | Path |
|--------|------|
| API HTML | `target/cucumber-reports/api/api-report.html` |
| UI HTML | `target/cucumber-reports/ui/ui-report.html` |
| TestNG XML | `target/surefire-reports/` |

Screenshots are attached automatically on UI test failure.

## Tags

| Tag | Meaning |
|-----|---------|
| `@api` | API test — no browser |
| `@ui` | UI test — launches Chromium |
| `@smoke` | Core happy-path; run first in CI |
| `@positive` | Happy-path scenario |
| `@negative` | Error/invalid-input scenario |
| `@e2e` | Full end-to-end journey |

## CI/CD

```yaml
- name: Run tests
  run: |
    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
    mvn test
  env:
    BROWSER_HEADLESS: "true"
```

## Troubleshooting

| Problem | Solution |
|---------|---------|
| `Executable doesn't exist` | Run `install chromium` step from Quick Start |
| `ConnectionRefused` on API tests | Check internet access and `petstore.swagger.io` reachability |
| Flaky UI tests on CI | Increase `ui.timeout` or ensure `BROWSER_HEADLESS=true` |
| Dependency resolution fails | Run `mvn dependency:resolve`, check Maven proxy settings |
