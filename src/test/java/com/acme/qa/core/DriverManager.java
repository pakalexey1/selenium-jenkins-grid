package com.acme.qa.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private static final ThreadLocal<WebDriver> TL_DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return TL_DRIVER.get();
    }

    // ---- in DriverManager ----
    public static void createDriver() {
        String browser  = sys("browser",  "chrome");
        boolean headless = Boolean.parseBoolean(sys("headless", "true"));

        // Decide whether to use Grid
        String runTarget = sys("runTarget", "");                 // "grid" forces remote
        String gridUrl   = coalesce(                             // <— renamed helper
                sys("gridUrl", null),
                sys("selenium.grid.url", null),
                System.getenv("SELENIUM_GRID_URL")
        );

        boolean useGrid = "grid".equalsIgnoreCase(runTarget);

        WebDriver driver = useGrid
                ? remoteDriver(browser, headless, normalizeGridUrl(gridUrl), null)
                : localDriver(browser, headless);

        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofMillis(Long.parseLong(sys("implicitWaitMs","0")))
        );
        driver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(60));
        TL_DRIVER.set(driver);
    }

    // ---------- small helpers ----------
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String coalesce(String... vals) {            // <— new name
        for (String v : vals) if (!isBlank(v)) return v.trim();
        return null;
    }

    /** Accepts "http://host:4444" or "http://host:4444/wd/hub" */
    private static String normalizeGridUrl(String url) {
        if (isBlank(url)) return "http://localhost:4444";
        return url.trim();
    }

    public static void quitDriver() {
        WebDriver d = TL_DRIVER.get();
        if (d != null) {
            d.quit();
            TL_DRIVER.remove();
        }
    }

    /* ---------------- helpers ---------------- */

    private static WebDriver localDriver(String browser, boolean headless) {
        switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new");
                o.addArguments("--window-size=1920,1080");
                return new org.openqa.selenium.chrome.ChromeDriver(o);
            }
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                return new org.openqa.selenium.firefox.FirefoxDriver(o);
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new");
                return new org.openqa.selenium.edge.EdgeDriver(o);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static WebDriver remoteDriver(String browser, boolean headless, String remoteUrl,
                                          Map<String, Object> vendorCaps) {
        final URL url = toUrl(remoteUrl);

        switch (browser.toLowerCase()) {
            case "chrome" -> {
                ChromeOptions o = new ChromeOptions();
                if (headless) o.addArguments("--headless=new");
                // Helpful in containers
                o.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu",
                        "--window-size=1920,1080");
                if (vendorCaps != null) vendorCaps.forEach(o::setCapability);
                return new RemoteWebDriver(url, o);
            }
            case "firefox" -> {
                FirefoxOptions o = new FirefoxOptions();
                if (headless) o.addArguments("-headless");
                if (vendorCaps != null) vendorCaps.forEach(o::setCapability);
                return new RemoteWebDriver(url, o);
            }
            case "edge" -> {
                EdgeOptions o = new EdgeOptions();
                if (headless) o.addArguments("--headless=new");
                if (vendorCaps != null) vendorCaps.forEach(o::setCapability);
                return new RemoteWebDriver(url, o);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private static URL toUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Bad remote/grid URL: " + s, e);
        }
    }

    // Treat empty/blank system property as "missing" and fall back to default
    private static String sys(String key, String dflt) {
        String v = System.getProperty(key);
        return (v == null || v.isBlank()) ? dflt : v;
    }

    // Read from ENV first, then system property; throw if still blank/missing
    private static String envOrSys(String key) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = System.getProperty(key);
        if (v == null || v.isBlank()) throw new IllegalStateException("Missing credential: " + key);
        return v;
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (v != null && !v.isBlank()) return v.trim();
        return "";
    }
}