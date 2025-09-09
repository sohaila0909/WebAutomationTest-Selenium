package base;

import java.io.IOException;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.File_utils;

/**
 * Base Test class providing common setup and teardown functionality.
 * Handles browser initialization, configuration loading, and cleanup operations.
 * Supports Chrome, Firefox, and Edge browsers with WebDriverManager.
 * Launches browsers in Incognito/Private mode to reduce CAPTCHA triggers.
 * Uses ThreadLocal for WebDriver to support parallel execution.
 * 
 */
public class BaseTest {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<String> browserName = new ThreadLocal<>();
    private static ThreadLocal<String> searchTerm = new ThreadLocal<>();
    public static String prop_path = "\\src\\test\\resources\\configfiles\\config.properties";
    public static String loc_path = "\\src\\test\\resources\\configfiles\\locators.properties";

    public static Properties prop = new Properties();
    public static Properties loc = new Properties();
    public File_utils futils = new File_utils();
    /**
     *        SETTERS AND GETTERS
     * Provides access to the current thread's WebDriver, broswer, searchTerm
     * and assigns current thread WebDriver, broswer, searchTerm
     * 
     */
    public static WebDriver getDriver() { return driver.get(); }
    public static void setDriver(WebDriver drv) { driver.set(drv); }

    public static String getBrowser() { return browserName.get(); }
    public static void setBrowser(String browser) { browserName.set(browser); }

    public static String getSearchTerm() { return searchTerm.get(); }
    public static void setSearchTerm(String term) { searchTerm.set(term); }

    @BeforeMethod(alwaysRun = true)
    @Parameters({ "browser", "searchTerm" })
    public void setUp(@Optional("") String browser, @Optional("") String term) throws IOException {
        // Load properties
        prop = futils.readFile(prop_path);
        loc = futils.readFile(loc_path);

        if (browser.isEmpty()) browser = prop.getProperty("browser");
        setBrowser(browser);

        if (term.isEmpty()) term = prop.getProperty("search_Term");
        setSearchTerm(term);

        WebDriver localDriver;
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--incognito");
                localDriver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("-private");
                localDriver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("-inprivate");
                localDriver = new EdgeDriver(edgeOptions);
                break;
            default:
                throw new RuntimeException("Unsupported browser: " + browser);
        }
        setDriver(localDriver);
        getDriver().get(prop.getProperty("url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver drv = getDriver();
        if (drv != null) {
            drv.quit();
            driver.remove();
        }
        System.out.println("Teardown successfully");
    }



    public static Properties getConfigProperties() {
        return prop;
    }

    public static Properties getLocatorProperties() {
        return loc;
    }
}
