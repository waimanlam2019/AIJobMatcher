package site.raylambytes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration", ex);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
