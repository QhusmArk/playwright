package com.example.playwright.navigation;

import com.example.playwright.config.TestEnvironment;
import com.example.playwright.steps.Hooks;

public class Navigate {

    private final StringBuilder path = new StringBuilder();

    private Navigate(String start) {
        path.append(start);
    }

    private static String base() {
        return "https://" + TestEnvironment.getWebUrl() + "/";
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
}