package site.raylambytes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = new FileInputStream(System.getenv("CONFIG_FILE_PATH"))) {
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration from environment variable CONFIG_FILE_PATH", ex);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
