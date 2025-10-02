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

    public static void createDriver() {
        // Prefer Grid when a URL is provided via any of these knobs
        String discoveredGridUrl = firstNonBlank(
                System.getProperty("selenium.grid.url"),
                System.getProperty("gridUrl"),
                System.getenv("SELENIUM_GRID_URL")
        );

        String browser  = sys("browser", "chrome");
        boolean headless = Boolean.parseBoolean(sys("headless", "true"));

        // If a grid URL is present, force runTarget=grid. Otherwise honor runTarget/local/vendor.
        String runTarget = sys("runTarget", discoveredGridUrl.isBlank() ? "local" : "grid");

        WebDriver driver;
        switch (runTarget) {
            case "grid" -> {
                String gridUrl = discoveredGridUrl.isBlank()
                        ? sys("gridUrl", "http://localhost:4444")  // final fallback
                        : discoveredGridUrl;
                driver = remoteDriver(browser, headless, gridUrl, null);
            }

            case "browserstack" -> {
                String user = envOrSys("BSTACK_USERNAME");
                String key  = envOrSys("BSTACK_ACCESS_KEY");
                String url  = "https://" + user + ":" + key + "@hub.browserstack.com/wd/hub";
                Map<String, Object> bsOpts = new HashMap<>();
                bsOpts.put("os", "Windows");
                bsOpts.put("osVersion", "11");
                bsOpts.put("projectName", "webtests");
                bsOpts.put("buildName", "build-" + System.currentTimeMillis());
                bsOpts.put("sessionName", browser + " run");
                driver = remoteDriver(browser, headless, url, Map.of("bstack:options", bsOpts));
            }

            case "sauce" -> {
                String user = envOrSys("SAUCE_USERNAME");
                String key  = envOrSys("SAUCE_ACCESS_KEY");
                String url  = "https://ondemand.us-west-1.saucelabs.com/wd/hub";
                Map<String, Object> sauce = new HashMap<>();
                sauce.put("name", "webtests");
                sauce.put("build", "build-" + System.currentTimeMillis());
                driver = remoteDriver(browser, headless, url, Map.of("sauce:options", sauce));
            }

            case "local" -> driver = localDriver(browser, headless);

            default -> throw new IllegalArgumentException("Unknown runTarget: " + runTarget);
        }

        driver.manage().timeouts().implicitlyWait(
                Duration.ofMillis(Long.parseLong(sys("implicitWaitMs","0"))));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        TL_DRIVER.set(driver);
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