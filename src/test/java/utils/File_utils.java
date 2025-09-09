package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for file operations, specifically for reading properties files.
 * This class provides methods to load configuration files used in test automation.
 * 
 */
public class File_utils {

    /**
     * Reads a properties file and returns a Properties object containing all key-value pairs.
     * The file path is constructed by combining the current working directory with the provided link.
     * 
     * @param link The relative path to the properties file from the project root directory
     *             (e.g., "/src/test/resources/config.properties")
     * @return Properties object containing all properties from the file
     * @throws IOException if the file cannot be found or read
     * 
     * @example
     * <pre>
     * File_utils fileUtils = new File_utils();
     * Properties config = fileUtils.readFile("/config/locators.properties");
     * String searchBoxId = config.getProperty("bing_SBox");
     * </pre>
     */
    public Properties readFile(String link) throws IOException {
        Properties prop = new Properties();
        
        // Construct full file path using system property for current directory
        String fullPath = System.getProperty("user.dir") + link;
        
        // Use try-with-resources to ensure FileReader is properly closed
        try (FileReader fr = new FileReader(fullPath)) {
            prop.load(fr);
        }
        
        return prop;
    }
}