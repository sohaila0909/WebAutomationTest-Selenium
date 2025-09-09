package base;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Manages the initialization and configuration of the ExtentReports instance.
 * Generates a detailed HTML execution report containing the status of each test,
 * along with logs and screenshots (for failed steps).
 *
 * <p>This class ensures that only a single instance of ExtentReports is created
 * throughout the execution (Singleton pattern).</p>
 *
 * <p><b>Usage:</b>
 * <pre>
 * ExtentReports extent = ReportManager.getReporter();
 * </pre></p>
 */
public class ReportManager {
    private static ExtentReports extent;

    public static ExtentReports getReporter() {
        if (extent == null) {
            synchronized (ReportManager.class) {
                if (extent == null) {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String reportPath = "reports/Execution_Report_" + timestamp + ".html";

                    ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportPath);
                    htmlReporter.config().setTheme(Theme.STANDARD);
                    htmlReporter.config().setDocumentTitle("Automation Execution Report");
                    htmlReporter.config().setReportName("Web Automation Results");
                    htmlReporter.config().setEncoding("utf-8");

                    extent = new ExtentReports();
                    extent.attachReporter(htmlReporter);
                    extent.setSystemInfo("Framework", "Selenium + TestNG");
                    extent.setSystemInfo("Author", "Automation QA");
                    extent.setSystemInfo("Execution Date", timestamp);
                }
            }
        }
        return extent;
    }
}
