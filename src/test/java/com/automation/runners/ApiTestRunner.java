package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/api",
        glue = {
                "com.automation.stepdefs.api",
                "com.automation.hooks"
        },
        tags = "@api",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/api/api-report.html",
                "json:target/cucumber-reports/api/api-report.json",
                "timeline:target/cucumber-reports/api/timeline"
        },
        monochrome = true
)
public class ApiTestRunner extends AbstractTestNGCucumberTests {
}
