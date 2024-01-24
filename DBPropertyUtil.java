package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DBPropertyUtil {

    public static Map<String, String> readPropertiesFile(String filePath) throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }

        // Convert Properties to HashMap
        Map<String, String> propertiesMap = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            propertiesMap.put(key.trim(), properties.getProperty(key));
        }
        return propertiesMap;
    }

}

