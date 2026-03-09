package com.automation.config;

import com.microsoft.playwright.*;

import java.util.function.Consumer;

/**
 * Manages Playwright lifecycle using ThreadLocal storage for thread-safe parallel execution.
 * Provides separate initialization paths for UI (browser) and API (request context) scenarios.
 */
public class PlaywrightManager {

    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> browserContextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<APIRequestContext> apiRequestContextThreadLocal = new ThreadLocal<>();

    private PlaywrightManager() {}

    // ── UI Setup ────────────────────────────────────────────────────────────────

    public static void initBrowser() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(ConfigManager.isHeadless()));
        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1280, 720));
        Page page = context.newPage();
        page.setDefaultTimeout(ConfigManager.getUiTimeout());

        playwrightThreadLocal.set(playwright);
        browserThreadLocal.set(browser);
        browserContextThreadLocal.set(context);
        pageThreadLocal.set(page);
    }

    public static Page getPage() {
        return pageThreadLocal.get();
    }

    public static void closeBrowser() {
        closeSafely(pageThreadLocal.get(), Page::close);
        closeSafely(browserContextThreadLocal.get(), BrowserContext::close);
        closeSafely(browserThreadLocal.get(), Browser::close);
        closeSafely(playwrightThreadLocal.get(), Playwright::close);
        pageThreadLocal.remove();
        browserContextThreadLocal.remove();
        browserThreadLocal.remove();
        playwrightThreadLocal.remove();
    }

    // ── API Setup ────────────────────────────────────────────────────────────────

    public static void initApiContext() {
        Playwright playwright = Playwright.create();
        APIRequestContext apiContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(ConfigManager.getApiBaseUrl())
                        .setTimeout(ConfigManager.getApiTimeout()));

        playwrightThreadLocal.set(playwright);
        apiRequestContextThreadLocal.set(apiContext);
    }

    public static APIRequestContext getApiContext() {
        return apiRequestContextThreadLocal.get();
    }

    public static void closeApiContext() {
        closeSafely(apiRequestContextThreadLocal.get(), APIRequestContext::dispose);
        closeSafely(playwrightThreadLocal.get(), Playwright::close);
        apiRequestContextThreadLocal.remove();
        playwrightThreadLocal.remove();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private static <T> void closeSafely(T resource, Consumer<T> closer) {
        if (resource != null) {
            try {
                closer.accept(resource);
            } catch (Exception e) {
                System.err.println("Warning: error closing resource: " + e.getMessage());
            }
        }
    }
}
