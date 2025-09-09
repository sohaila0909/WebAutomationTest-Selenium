package base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.TC_utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * Custom TestNG Listener integrated with ExtentReports.
 * Handles logging of test execution, captures screenshots on failures,
 * and generates an HTML report with detailed results.
 *
 * <p>Report is generated under <b>test-output/ExtentReport.html</b></p>
 * <p>Screenshots for failed tests are stored under <b>test-output/screenshots/</b></p>
 *
 */
public class TestListener implements ITestListener {
    private static ThreadLocal<ExtentTest> testLogger = new ThreadLocal<>();
    private static ExtentReports extent = ReportManager.getReporter();

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName() + " [" + BaseTest.getBrowser() + " | " + BaseTest.getSearchTerm() + "]";
        ExtentTest test = extent.createTest(name);
        testLogger.set(test);
        test.log(Status.INFO, "Test Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testLogger.get().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        testLogger.get().log(Status.FAIL, result.getThrowable());

        WebDriver driver = BaseTest.getDriver();
        if (driver != null) {
            String screenshot = TC_utils.captureScreenshot(driver, result.getMethod().getMethodName());
            testLogger.get().addScreenCaptureFromPath(screenshot);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        testLogger.get().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    public static ExtentTest getTestLogger() { return testLogger.get(); }
}
