package com.example.playwright.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestEnvironment {

    private static final String FILE_NAME = "test_env.properties";
    private static final Properties PROPERTIES = loadProperties();

    public static String getWebUrl() {
        return required("WEB_URL");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = TestEnvironment.class
                .getClassLoader()
                .getResourceAsStream(FILE_NAME)) {

            if (inputStream == null) {
                throw new IllegalStateException("Could not find " + FILE_NAME);
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException e) {
            throw new IllegalStateException("Could not load " + FILE_NAME, e);
        }
    }

    private static String required(String key) {
        String value = PROPERTIES.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }

        return value;
    }
}