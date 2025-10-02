package com.acme.qa.hooks;

import com.acme.qa.core.DriverManager;
import com.acme.qa.core.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;   // <-- add this
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {
    private final TestContext ctx;

    public Hooks(TestContext ctx) { this.ctx = ctx; }

    @Before(order = 0)
    public void start() {
        DriverManager.createDriver();
    }

    @After
    public void stop(Scenario scenario) {
        try {
            if (scenario.isFailed() && DriverManager.getDriver() != null) {
                byte[] png = ((org.openqa.selenium.TakesScreenshot) DriverManager.getDriver())
                        .getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                scenario.attach(png, "image/png", "screenshot");
            }
        } finally {
            DriverManager.quitDriver();
        }
    }
}
