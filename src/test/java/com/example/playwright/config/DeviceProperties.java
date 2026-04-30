package com.example.playwright.config;

import com.example.playwright.enums.DeviceType;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DeviceProperties {

    private static final String FILE_NAME = "test_env_devices.properties";

    private static final Properties properties = new Properties();
    private static final Map<Integer, String> reverseMap = new HashMap<>();

    static {
        loadProperties();
        buildReverseMap();
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
     * Builds reverse lookup map (value -> key).
     */
    private static void buildReverseMap() {
        for (String key : properties.stringPropertyNames()) {
            int value = Integer.parseInt(properties.getProperty(key));
            reverseMap.put(value, key);
        }
    }

    /**
     * Returns value as int based on key.
     */
    public static int getValue(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException("No value found for key: " + key);
        }

        return Integer.parseInt(value);
    }

    /**
     * Returns key as String based on value (provided as String).
     */
    public static String getKey(String value) {
        int intValue;

        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value format: " + value, e);
        }

        return getKey(intValue);
    }

    /**
     * Returns key as String based on value.
     */
    public static String getKey(int value) {
        String key = reverseMap.get(value);

        if (key == null) {
            throw new IllegalArgumentException("No key found for value: " + value);
        }

        return key;
    }

    public static int getConnectedSerial(DeviceType deviceType) {
        return Integer.parseInt(getConnectedSerial(deviceType.getType()));
    }

    public static String getConnectedSerial(String deviceType) {
        String type = (deviceType.equals("C22"))
                ? "C22-1"
                : deviceType;

        return String.valueOf(getValue(type));
    }

    /**
     * Returns the key whose value matches the provided string.
     * Returns null if no match is found.
     */
    public static String getKeyByValue(String valueToFind) {
        int intValue =  Integer.parseInt(valueToFind);
        return getKey(intValue);
    }


}
