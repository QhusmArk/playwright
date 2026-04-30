package com.example.playwright.config;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestDataReader extends Properties {

    private final Properties properties;
    private final File configFile;

    /**
     * Cookie files are reused. If cookie is for other user than the one from Hooks, then a new Cookie will be
     * created and its values overwrites the ones in the file.
     * NB. If cookie.properties is removed then create file manually at same folder as config.properties.
     */
    public TestDataReader(final String filePath) {
        properties = new Properties();
        configFile = new File(filePath);

        if (!configFile.exists() && filePath.contains("cookie")) {
            createNewFile(filePath);
        } else {
            loadProperties(filePath);
        }
    }

    public static void deleteCookieFile() {
        System.out.println("Deleting cookie file.");
        Path path = Paths.get("src/test/resources/cookie.properties");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadProperties(String filePath) {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    private void createNewFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createFile(path);
            System.out.println("New file created: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error creating new file: " + filePath, e);
        }
    }

    public void addProperty(String name, String value) {
        properties.setProperty(name, value);

        try (FileOutputStream output = new FileOutputStream(configFile)) {
            properties.store(output, "ConfigFile");
        } catch (IOException ioe) {
            System.out.println("File " + configFile + " does not exist");
            throw new RuntimeException(ioe);
        }
    }

    public List<String> getKeys() {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            keys.add(entry.getKey().toString());
        }
        return keys;
    }

    public boolean hasKey(final String key) {
        Set<Object> keys = properties.keySet();
        return keys.contains(key);
    }

    @Override
    public String getProperty(String prop) throws NullPointerException {
        return properties.getProperty(prop) != null ? properties.getProperty(prop) : "";
    }

    @Override
    public String getProperty(String prop, String defaultValue) throws NullPointerException {
        return properties.getProperty(prop, defaultValue);
    }

    public String getString(String key) throws NullPointerException {
        return properties.getProperty(key);
    }

    // todo: hantera detta. Vi ska i n t e kasta nullpointer... Eller vill api ha null ibland?
    public Integer getInteger(String prop) throws NullPointerException {
        String value = properties.getProperty(prop);
        if (value != null) {
            return Integer.parseInt(value);
        } else {
            return null;
        }
    }


    public List<Integer> getIntArray(String prop, String delimeter) {
        List<Integer> list = new ArrayList<>();
        try {
            String[] split = properties.getProperty(prop).split(delimeter);
            for (String s : split) {
                list.add(Integer.parseInt(s));
            }
        } catch (NullPointerException e) {
            // This is Ok, variable has not been set
        }
        return list;
    }

    public Double getDouble(String prop) throws NullPointerException {
        String value = properties.getProperty(prop);
        if (value != null) {
            return Double.parseDouble(value);
        } else {
            return null;
        }
    }

    public List<Double> getDoubleArray(String prop, String delimeter) {
        List<Double> list = new ArrayList<>();
        try {
            String[] split = properties.getProperty(prop).split(delimeter);
            for (String s : split) {
                list.add(Double.parseDouble(s));
            }
        } catch (NullPointerException e) {
            // This is Ok, variable has not been set
        }
        return list;
    }

    public Boolean getBoolean(String prop) {
        return Boolean.parseBoolean(properties.getProperty(prop));
    }

    public boolean getBoolean(String prop, String defaultValue) {
        String property = properties.getProperty(prop, defaultValue);
        return Boolean.parseBoolean(property);
    }

    public List<Boolean> getBooleanArray(String prop, String delimeter) {
        List<Boolean> list = new ArrayList<>();
        try {
            String[] split = properties.getProperty(prop).split(delimeter);
            for (String s : split) {
                list.add(Boolean.parseBoolean(s.trim()));
            }
        } catch (NullPointerException e) {
            // This is Ok, variable has not been set
        }
        return list;
    }

    /**
     * Splits the input string by comma and returns a list of substrings.
     *
     * @param input the string to split
     * @return list of substrings, or an empty list if input is null or blank
     */
    public List<String> splitByComma(String input) {
        return (input == null || input.isBlank())
                ? List.of()
                : Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    public List<String> getStringArray(String prop, String delimeter) {
        List<String> list = new ArrayList<>();
        try {
            String[] split = properties.getProperty(prop).split(delimeter);
            for (String s : split) {
                list.add(s.trim());
            }
        } catch (NullPointerException e) {
            // This is Ok, variable has not been set
        }
        return list;
    }

    public File getFileProperty(String prop) throws NullPointerException {
        return new File(properties.getProperty(prop));
    }

    public URL getUrlProperty(String prop) throws NullPointerException, MalformedURLException, URISyntaxException {
        return new URI(properties.getProperty(prop)).toURL();
    }

    public List<String> getProperties() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            list.add(entry.getKey() + ": " + entry.getValue());
        }
        return list;
    }

    public List<String> getProperties(String prop, String delimeter) {
        List<String> list = new ArrayList<>();
        try {
            String[] split = properties.getProperty(prop).split(delimeter);
            Collections.addAll(list, split);
        } catch (NullPointerException e) {
            // This is Ok, variable has not been set
        }
        return list;
    }
}
