package com.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Centralized configuration manager.
 * Loads properties from config.properties; environment variables take precedence.
 */
public class ConfigManager {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigManager() {}

    public static String get(String key) {
        // Environment variables override file properties
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return properties.getProperty(key, "");
    }

    public static String getApiBaseUrl() {
        return get("api.base.url");
    }

    public static String getUiBaseUrl() {
        return get("ui.base.url");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("browser.headless"));
    }

    public static int getApiTimeout() {
        return Integer.parseInt(get("api.timeout"));
    }

    public static int getUiTimeout() {
        return Integer.parseInt(get("ui.timeout"));
    }
}
