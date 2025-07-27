package site.raylambytes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final Properties properties;

    public ConfigLoader(String configFilePath) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
            if (input == null) {
                throw new RuntimeException("Configuration file not found: " + configFilePath);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration from " + configFilePath, ex);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }
}
