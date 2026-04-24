package com.example.playwright.navigation;

import com.example.playwright.config.TestEnvironment;
import com.example.playwright.steps.Hooks;
import com.microsoft.playwright.TimeoutError;

public class Navigate {

    private final StringBuilder path = new StringBuilder();

    private Navigate(String start) {
        path.append(start);
    }

    public static String webUrl() {
        return TestEnvironment.getWebUrl();
    }

    private static String base() {
        return "https://" + webUrl() + "/";
    }

    /***************************** Starters **************************/

    public static Navigate domain() {
        return new Navigate(base() + "/");
    }

    public static Navigate company() {
        return new Navigate(base() + "/" + "#/company");
    }

    public static Navigate project(final int projectId) {
        return new Navigate(base() + "/" + "#/project/" + projectId);
    }

    /***************************** Endpoints **************************/

    public Navigate login() {
        path.append("/login");
        return this;
    }

    public Navigate overview() {
        path.append("/overview");
        return this;
    }

    public Navigate devices() {
        path.append("/devices");
        return this;
    }

    public Navigate projects() {
        path.append("/projects");
        return this;
    }

    public Navigate users() {
        path.append("/users");
        return this;
    }

    public void get() {
        Hooks.getPage().navigate(path.toString());
    }

    /***************************** Helpers **************************/

    public static String getCurrentUrl() {
        return Hooks.getPage().url();
    }

    public static void waitUntilUrlContains(String value) {
        Hooks.getPage().waitForURL(url -> url.contains(value));
    }

    public static void validateUrlContains(String value) {
        try {
            Hooks.getPage().waitForURL(url -> url.contains(value));
        } catch (TimeoutError e) {
            throw new RuntimeException("Timed out waiting for URL: " + value);
        }
    }

    public static void refreshBrowser() {
        Hooks.getPage().reload();
    }
}