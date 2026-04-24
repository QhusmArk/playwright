package com.example.playwright.testUsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class TestUserLoader {

    private static final String FILE_NAME = "test_env.properties";

    public static List<TestUser> loadUsers() {
        Properties properties = loadProperties();

        return properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("test.user."))
                .filter(key -> key.endsWith(".email"))
                .map(key -> key.substring("test.user.".length(), key.length() - ".email".length()))
                .distinct()
                .sorted(Comparator.naturalOrder())
                .map(userKey -> createTestUser(properties, userKey))
                .toList();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = TestUserLoader.class
                .getClassLoader()
                .getResourceAsStream(FILE_NAME)) {

            if (inputStream == null) {
                throw new IllegalStateException("Could not find " + FILE_NAME + " in test resources.");
            }

            properties.load(inputStream);
            return properties;

        } catch (IOException e) {
            throw new IllegalStateException("Could not load " + FILE_NAME, e);
        }
    }

    private static TestUser createTestUser(Properties properties, String userKey) {
        String prefix = "test.user." + userKey + ".";

        return new TestUser(
                required(properties, prefix + "email"),
                required(properties, prefix + "role"),
                Integer.parseInt(required(properties, prefix + "id")),
                required(properties, prefix + "password"),
                required(properties, prefix + "token")
        );
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }

        return value;
    }
}