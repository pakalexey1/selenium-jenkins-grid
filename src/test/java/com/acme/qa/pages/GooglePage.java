package com.acme.qa.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GooglePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public GooglePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(Long.parseLong(System.getProperty("explicitWaitSec", "10"))));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
        // accept cookie banners if present (optional)
        try {
            WebElement agree = wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label*='Accept']," +
                            "button:has-text('I agree'), div[role='button']:has-text('I agree')")));
            agree.click();
        } catch (Exception ignored) {}
    }

    public void search(String q) {
        WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
        box.clear();
        box.sendKeys(q + Keys.ENTER);
    }

    public boolean resultsContain(String needle) {
        return driver.getPageSource().toLowerCase().contains(needle.toLowerCase());
    }
}