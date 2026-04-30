package com.example.playwright.hooks;

public final class ScenarioContext {

    private static final ThreadLocal<String> scenarioName = new ThreadLocal<>();

    private ScenarioContext() {
    }

    public static void setScenarioName(String name) {
        scenarioName.set(name);
    }

    public static String getScenarioName() {
        return scenarioName.get();
    }

    public static void clear() {
        scenarioName.remove();
    }
}
