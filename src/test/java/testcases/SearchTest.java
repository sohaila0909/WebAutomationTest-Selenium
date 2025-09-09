package testcases;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import base.BaseTest;
import base.TestListener;
import utils.DataProviderClass;
import utils.TC_utils;
import org.testng.annotations.Listeners;


import com.aventstack.extentreports.Status;

/**
 * Test cases for Bing search functionality.
 * Includes validation of home page title, first two results,
 * and results count consistency across multiple pages.
 *
 * Each step is logged in ExtentReports with pass/fail status,
 * and screenshots are attached automatically on failures.
 */
@Listeners(base.TestListener.class)
public class SearchTest extends BaseTest {

    private FluentWait<WebDriver> getFluentWait() {
        return new FluentWait<>(BaseTest.getDriver())
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(Exception.class);
    }

    /**
     * TC1 - Validate Bing home page title.
     * @param searchTerm String that shall be executed in search test
     */
    @Test(priority = 1, groups = "SmokeTest")
    @Parameters({ "searchTerm" })
    public void validateBingHomePage(@Optional("") String search_term) throws IOException {
        if (search_term.isEmpty()) {
            search_term = prop.getProperty("search_Term");
        }

        SoftAssert softAssert = new SoftAssert();
        JavascriptExecutor jse = (JavascriptExecutor) BaseTest.getDriver();

        TestListener.getTestLogger().log(Status.INFO, "Starting Bing Home Page Validation for: " + search_term);

        try {
            String actualTitle = ((String) jse.executeScript("return document.title;")).toLowerCase();
            String expectedTitle = prop.getProperty("expected_title").toLowerCase();

            System.out.println("Actual title: " + actualTitle);
            System.out.println("Expected title: " + expectedTitle);

            softAssert.assertTrue(actualTitle.contains(expectedTitle),"The title doesn't contain Bing");

            TestListener.getTestLogger().log(Status.PASS, "Actual_title: "+ actualTitle + "\n" + "home page title validated successfully.");
        } catch (Exception e) {
            softAssert.fail("Error validating Bing Home Page: " + e.getMessage());
            TestListener.getTestLogger().log(Status.FAIL, "Exception during home page validation: " + e.getMessage());
        } finally {
            softAssert.assertAll();
            TestListener.getTestLogger().log(Status.INFO, "Home page validation completed.");
        }
    }

    /**
     * TC2 - Validate the first two search results are related to the search term.
     * @param searchTerm String that shall be executed and compared to while search test
     */
//    @Test(priority = 2, groups = "SmokeTest",
//            dataProvider = "SearchTermTest", dataProviderClass = DataProviderClass.class)
	@Test(priority = 2, groups = "SmokeTest")
    @Parameters({ "searchTerm" })
    public void validateFirstTwoResults(@Optional("") String search_term) throws IOException, InterruptedException {
        if (search_term.isEmpty()) {
            search_term = prop.getProperty("search_Term");
        }

        SoftAssert softAssert = new SoftAssert();
        TC_utils utils = new TC_utils();

        TestListener.getTestLogger().log(Status.INFO, "Starting First Two Results Validation for: " + search_term);

        try {
            WebElement searchBox = BaseTest.getDriver().findElement(By.id(loc.getProperty("bing_SBox")));
            utils.type_with_random_delay(searchBox, search_term);
            searchBox.sendKeys(Keys.ENTER);

            TestListener.getTestLogger().log(Status.INFO, "Search submitted for: " + search_term);

            getFluentWait().until(ExpectedConditions
                    .visibilityOfAllElementsLocatedBy(By.xpath(loc.getProperty("all_results_selector"))));

            List<WebElement> allResults = BaseTest.getDriver().findElements(By.xpath(loc.getProperty("all_results_selector")));
            System.out.println("Found " + allResults.size() + " search results on page 1.");
            TestListener.getTestLogger().log(Status.INFO, "Found " + allResults.size() + " search results on page 1.");

            int resultsToCheck = Math.min(2, allResults.size());
            for (int i = 0; i < resultsToCheck; i++) {
                try {
                    List<WebElement> freshResults =
                            BaseTest.getDriver().findElements(By.xpath(loc.getProperty("all_results_selector")));
                    WebElement result = freshResults.get(i);

                    String text = result.getText();
                    boolean isValid = utils.validateResult(result, i + 1, search_term);

                    System.out.println("Text for Result #" + (i + 1) + ": " + text);
                    TestListener.getTestLogger().log(Status.INFO,
                            "Result #" + (i + 1) + " text: " + text);

                    if (isValid) {
                        TestListener.getTestLogger().log(Status.PASS,
                                "Result #" + (i + 1) + " is valid for search term: " + search_term);
                    } else {
                        softAssert.fail("Result #" + (i + 1) + " is not related to search term: " + search_term);
                        TestListener.getTestLogger().log(Status.FAIL,
                                "Result #" + (i + 1) + " is not related to search term: " + search_term);
                    }
                } catch (StaleElementReferenceException sere) {
                    System.out.println("Stale element found for Result #" + (i + 1) + ", retrying...");
                    TestListener.getTestLogger().log(Status.WARNING,
                            "Stale element for Result #" + (i + 1) + ", retrying...");
                    i--;
                }
            }

        } catch (Exception e) {
            softAssert.fail("Error validating first two results: " + e.getMessage());
            TestListener.getTestLogger().log(Status.FAIL, "Exception: " + e.getMessage());
        } finally {
            softAssert.assertAll();
            TestListener.getTestLogger().log(Status.INFO, "First two results validation completed.");
        }
    }

    /**
     * TC3 - Validate results count consistency on page 2 and page 3.
     * @param searchTerm String that shall be executed in search test
     */
    @Test(priority = 3, groups = "SmokeTest")
//	@Test(priority = 3, groups = "SmokeTest", dataProvider = "SearchTermTest", dataProviderClass = DataProviderClass.class)
    @Parameters({ "searchTerm" })
    public void validateSecondAndThirdPageResultsCount(@Optional("") String search_term) throws Exception {
        if (search_term.isEmpty()) {
            search_term = prop.getProperty("search_Term");
        }

        SoftAssert softAssert = new SoftAssert();
        TC_utils utils = new TC_utils();
        JavascriptExecutor jse = (JavascriptExecutor) BaseTest.getDriver();

        TestListener.getTestLogger().log(Status.INFO,
                "Starting Page 2 and 3 Results Count Validation for: " + search_term);

        try {
            // --- PAGE 1 ---
            WebElement searchBox = BaseTest.getDriver().findElement(By.id(loc.getProperty("bing_SBox")));
            utils.type_with_random_delay(searchBox, search_term);
            searchBox.sendKeys(Keys.ENTER);

            TestListener.getTestLogger().log(Status.INFO, "Search submitted for: " + search_term);

            getFluentWait().until(ExpectedConditions
                    .visibilityOfAllElementsLocatedBy(By.xpath(loc.getProperty("all_results_selector"))));
            List<WebElement> page1Results = BaseTest.getDriver().findElements(By.xpath(loc.getProperty("all_results_selector")));
            TestListener.getTestLogger().log(Status.INFO, "Page 1 has " + page1Results.size() + " results.");

            // --- PAGE 2 ---
            WebElement nextButtonPage2 = getFluentWait().until(
                    ExpectedConditions.elementToBeClickable(By.xpath(loc.getProperty("next_button"))));
            jse.executeScript("arguments[0].scrollIntoView(true);", nextButtonPage2);
            nextButtonPage2.click();

            utils.waitForCaptchaToDisappear(jse);

            getFluentWait().until(ExpectedConditions
                    .visibilityOfAllElementsLocatedBy(By.xpath(loc.getProperty("all_results_selector"))));
            List<WebElement> page2Results = BaseTest.getDriver().findElements(By.xpath(loc.getProperty("all_results_selector")));
            TestListener.getTestLogger().log(Status.INFO, "Page 2 has " + page2Results.size() + " results.");

            // --- PAGE 3 ---
            WebElement nextButtonPage3 = getFluentWait().until(
                    ExpectedConditions.elementToBeClickable(By.xpath(loc.getProperty("next_button"))));
            jse.executeScript("arguments[0].scrollIntoView(true);", nextButtonPage3);
            nextButtonPage3.click();

            utils.waitForCaptchaToDisappear(jse);

            getFluentWait().until(ExpectedConditions
                    .visibilityOfAllElementsLocatedBy(By.xpath(loc.getProperty("all_results_selector"))));
            List<WebElement> page3Results = BaseTest.getDriver().findElements(By.xpath(loc.getProperty("all_results_selector")));
            TestListener.getTestLogger().log(Status.INFO, "Page 3 has " + page3Results.size() + " results.");

            // --- Validation ---
            softAssert.assertEquals(
                    page2Results.size(),
                    page3Results.size(),
                    "Mismatch in results count: Page 2 = " + page2Results.size() +
                            ", Page 3 = " + page3Results.size());

            TestListener.getTestLogger().log(Status.PASS,
                    "Page 2 and Page 3 results count are consistent: " + page2Results.size());

        } catch (Exception e) {
            softAssert.fail("Error validating results count: " + e.getMessage());
            TestListener.getTestLogger().log(Status.FAIL, "Exception: " + e.getMessage());
        } finally {
            softAssert.assertAll();
            TestListener.getTestLogger().log(Status.INFO,
                    "Results count validation completed for search term: " + search_term);
        }
    }
}
