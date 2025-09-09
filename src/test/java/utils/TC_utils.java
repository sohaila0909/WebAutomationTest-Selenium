package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Test utility class for Selenium test automation.
 * Provides common methods for input simulation and search result validation.
 */
public class TC_utils {

    /**
     * Types text character by character with random delays to simulate human typing.
     * Helps avoid triggering reCAPTCHA by mimicking natural typing patterns.
     * 
     * @param searchBox WebElement to type into
     * @param query text string to type
     * @throws InterruptedException if thread sleep is interrupted
     */
    public void type_with_random_delay(WebElement searchBox, String query) throws InterruptedException {
        for (char c : query.toCharArray()) {
            searchBox.sendKeys(Character.toString(c));
            Thread.sleep(200 + (int) (Math.random() * 200)); // 200-400ms delay
        }
    }
    /**
     * random delays to simulate human interaction.
     * Helps avoid triggering reCAPTCHA by mimicking natural typing patterns.
     * 
     * @param minMillis integer to define minimum milliseconds
     * @param maxMillis integer to define minimum milliseconds
     * @throws InterruptedException if thread sleep is interrupted
     */
    
    public void human_delay(int minMillis, int maxMillis) throws InterruptedException {
        Random rand = new Random();
        int delay = rand.nextInt((maxMillis - minMillis) + 1) + minMillis;
        Thread.sleep(delay);
    }
    
    

    /**
     * Validates if a search result is relevant to the given search term.
     * Uses direct matching and partial word matching strategies.
     * 
     * @param result WebElement containing the search result
     * @param resultNumber position number of this result for logging
     * @param searchTerm original search term to validate against
     * @return true if result is relevant, false otherwise
     */
    public boolean validateResult(WebElement result, int resultNumber, String searchTerm) {
        try {
            String resultText = result.getText().trim();
            
            if (resultText.isEmpty()) {
                return false;
            }

            return isResultValid(resultText, searchTerm);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Determines result relevance using two strategies:
     * 1. Direct string containment check
     * 2. Partial word matching with 50% threshold
     * 
     * @param resultText extracted text content from search result
     * @param searchTerm original search term
     * @return true if result is relevant, false otherwise
     */
    private boolean isResultValid(String resultText, String searchTerm) {
        String lowerResult = resultText.toLowerCase();
        String lowerSearch = searchTerm.toLowerCase();
        
        // Direct match check
        if (lowerResult.contains(lowerSearch)) {
            return true;
        }

        // Word matching for multi-word terms
        String[] words = lowerSearch.split("\\s+");
        int matches = 0;
        
        for (String word : words) {
            if (word.length() > 2 && lowerResult.contains(word)) {
                matches++;
            }
        }

        // Valid if at least half the words match
        return (double) matches / words.length >= 0.5;
    }
    

	/**
	 * Incremental scroll function simulating human-like reading behavior
	 */
	public void incrementalScroll(JavascriptExecutor jse, TC_utils utils) throws InterruptedException {
	    int scrollTimes = 3 + (int) (Math.random() * 3); // 3-5 scrolls per page
	    for (int i = 0; i < scrollTimes; i++) {
	        int scrollPx = 100 + (int) (Math.random() * 150); // 100-250px per scroll
	        jse.executeScript("window.scrollBy(0," + scrollPx + ");");
	        utils.human_delay(500, 1200); // random pause per scroll
	    }
	}


	    /**
	     * Automatically waits if a captcha is detected, then continues when it disappears.
	     */
	    public void waitForCaptchaToDisappear(JavascriptExecutor jse) throws InterruptedException {
	        int waitTimeSec = 0;
	        boolean captchaPresent;

	        do {
	            captchaPresent = (Boolean) jse.executeScript(
	                "return document.querySelector('iframe[src*=\"captcha\"], div[class*=\"captcha\"]') != null;"
	            );

	            if (captchaPresent) {
	                System.out.println("Captcha detected! Waiting for it to disappear...");
	                Thread.sleep(2000); // wait 2 seconds before checking again
	                waitTimeSec += 2;
	            }
	        } while (captchaPresent && waitTimeSec < 120); // max wait 2 minutes

	        if (captchaPresent) {
	            System.out.println("Captcha still present after 2 minutes, test may fail.");
	        } else {
	            System.out.println("Captcha cleared, continuing test...");
	        }
	    }
	    public static String captureScreenshot(WebDriver driver, String name) {
	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String dirPath = System.getProperty("user.dir") + "/reports/screenshots/";
	        File dir = new File(dirPath);
	        if (!dir.exists()) dir.mkdirs();

	        String path = dirPath + name + "_" + timestamp + ".png";
	        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	        File dest = new File(path);
	        try { Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING); } 
	        catch (IOException e) { e.printStackTrace(); }
	        return path;
	    }	


}

