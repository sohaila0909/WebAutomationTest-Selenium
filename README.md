# WebAutomationTest-Selenium

## Overview
This project is a cross-browser web automation testing framework for validating Bing search functionality. The framework is built using **Java (JSE 21)**, **Selenium WebDriver**, and **TestNG**, with **Maven** for dependency management. Test execution generates detailed HTML reports via **ExtentReports**, including screenshots on failures.

The framework supports parallel execution across multiple browsers and externalized test data to ensure maintainability and flexibility.

## Components

| Component        | Description |
|-----------------|-------------|
| `BaseTest`       | Initializes WebDriver for Chrome, Firefox, and Edge; loads configuration and locator files, manages setup and teardown. |
| `SearchTest`     | Implements test cases: validating Bing homepage title, first two search results, and results count on subsequent pages. |
| `TestListener`   | Captures test logs, handles screenshots on failures, and integrates with ExtentReports. |
| `ReportManager`  | Creates a singleton ExtentReports instance to generate HTML reports with test details. |
| `TC_utils`       | Contains reusable helper methods for typing, waiting, and validation. |
| `File_utils`     | Reads `config.properties` and `locators.properties` for dynamic test data. |
| `pom.xml`        | Manages dependencies: Selenium, TestNG, WebDriverManager, and ExtentReports. |
| `testng.xml`     | Defines test suites and cross-browser execution combinations. |

## Features
- **Cross-Browser Testing:** Supports Chrome, Firefox, and Edge.  
- **Externalized Test Data:** Search terms and configuration stored in `config.properties` and `locators.properties`.  
- **Parallel Execution:** Multiple browsers and search terms can run concurrently using TestNG `parallel` and `thread-count`.  
- **Detailed Reporting:** ExtentReports HTML output with logs and screenshots for failed tests.  
- **Reusable Utilities:** Common actions are implemented in `TC_utils` and `File_utils`.  
- **Flexible Configuration:** Easily add new search terms or browsers via `testng.xml` or properties files.  

## How It Works

### Initialization
`BaseTest` reads configuration and locator files, then launches the specified browser in Incognito/Private mode.

### Test Execution
`SearchTest` runs scenarios for each combination of search term and browser:  
1. Validate homepage title  
2. Validate the first two search results  
3. Validate results count on page 2 and 3  

Parameters for browsers and search terms are defined in `testng.xml`.

### Reporting
`TestListener` logs test execution details to ExtentReports. Screenshots are automatically captured on failure. `ReportManager` ensures a single HTML report per test run.

### Teardown
After each test, the WebDriver instance is closed to enable parallel execution and resource cleanup.

## Requirements
- **Java 21 (JSE)**  
- **Apache Maven 3.x** or higher  
- Latest **Chrome**, **Firefox**, and **Edge** browsers installed  
- Compatible **WebDriver executables** for each browser (managed via WebDriverManager)  

## How to Run
1. Clone the repository.  
2. Update `config.properties` and `locators.properties` as needed.  
3. Run the test suite using TestNG XML:

```bash
mvn test -DsuiteXmlFile=testng.xml
