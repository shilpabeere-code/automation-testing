package com.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features/ui",
        glue = {
                "com.automation.stepdefs.ui",
                "com.automation.hooks"
        },
        tags = "@ui",
        plugin = {
                "pretty",
                "html:target/cucumber-reports/ui/ui-report.html",
                "json:target/cucumber-reports/ui/ui-report.json",
                "timeline:target/cucumber-reports/ui/timeline"
        },
        monochrome = true
)
public class UiTestRunner extends AbstractTestNGCucumberTests {
}
