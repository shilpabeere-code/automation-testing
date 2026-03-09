package com.automation.hooks;

import com.automation.config.PlaywrightManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks.
 *
 * Tagged hooks ensure each scenario type initialises only the resources it needs:
 *   @ui  – launches a Chromium browser (headed or headless via config.properties)
 *   @api – creates a Playwright APIRequestContext pointing at the Petstore base URL
 */
public class Hooks {

    private static final Logger log = LoggerFactory.getLogger(Hooks.class);

    // ── UI hooks ──────────────────────────────────────────────────────────────

    @Before(value = "@ui", order = 10)
    public void setUpBrowser(Scenario scenario) {
        log.info(">>> [UI] Starting scenario: {}", scenario.getName());
        PlaywrightManager.initBrowser();
    }

    @After(value = "@ui", order = 10)
    public void tearDownBrowser(Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }
        PlaywrightManager.closeBrowser();
        log.info("<<< [UI] Finished scenario: {} – {}", scenario.getName(), scenario.getStatus());
    }

    // ── API hooks ─────────────────────────────────────────────────────────────

    @Before(value = "@api", order = 10)
    public void setUpApiContext(Scenario scenario) {
        log.info(">>> [API] Starting scenario: {}", scenario.getName());
        PlaywrightManager.initApiContext();
    }

    @After(value = "@api", order = 10)
    public void tearDownApiContext(Scenario scenario) {
        PlaywrightManager.closeApiContext();
        log.info("<<< [API] Finished scenario: {} – {}", scenario.getName(), scenario.getStatus());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void captureScreenshot(Scenario scenario) {
        try {
            byte[] screenshot = PlaywrightManager.getPage().screenshot();
            scenario.attach(screenshot, "image/png", "Screenshot on failure: " + scenario.getName());
        } catch (Exception e) {
            log.warn("Could not capture screenshot: {}", e.getMessage());
        }
    }
}
