package com.acme.qa.core;

import org.openqa.selenium.WebDriver;

public class TestContext {
    public WebDriver driver() { return DriverManager.getDriver(); }
    public String baseUrl() { return System.getProperty("baseUrl", "https://www.google.com"); }
}