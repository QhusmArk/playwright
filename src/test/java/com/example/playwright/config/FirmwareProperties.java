package com.example.playwright.config;

import java.io.InputStream;
import java.util.Properties;

public class FirmwareProperties {

    private static final String FILE_NAME = "device.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    /**
     * Loads properties file into memory.
     */
    private static void loadProperties() {
        try (InputStream input = DeviceProperties.class
                .getClassLoader()
                .getResourceAsStream(FILE_NAME)) {

            if (input == null) {
                throw new IllegalStateException("Could not find " + FILE_NAME);
            }

            properties.load(input);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load " + FILE_NAME, e);
        }
    }

    /**
     * Returns value as int based on key.
     */
    public static String getValue(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException("No value found for key: " + key);
        }

        return value;
    }
}
