package com.example.playwright.enums;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public enum ColourSchema {
// todo: addera färger för map icons, tex färgen för flera ikoner sammanslagna

    PRIMARY("rgba(0, 55, 99, 1)", "primary"),   //active
    SECONDARY("rgba(0, 0, 0, 0.54)", "secondary"),  // non selected datareport or tab
    NEGATIVE("rgba(255, 87, 34, 1)", "negative"),   // red
    DISABLED("rgba(0, 0, 0, 0.38)", "disabled"),    //inactive
    LIGHT_BLUE("rgba(0, 142, 255, 1)", "light-blue-1"),   // planned Blast
    POSITIVE("rgba(2, 180, 44, 1)", "positive"),        // green
    WARNING("", "warning"),                             // carrot red,
    LIGHT_RED("", "light-red"),
    RED("", "red"),
    NEUTRAL("rgba(115, 115, 115, 1)", "neutral"),
    GREY("", "grey"),
    LIGHT_GREY("", "light-grey"),
    INHERIT_UNKNOWN("", "inherit"),
    UNDEFINED("", "undefined"),  // no project-button for C50 details,

    //    DARK("rgba(255, 255, 255, 1)", "dark-mask"),
//    WHITE("rgba(0, 0, 0, 1)", "white")
    DARK("rgba(0, 0, 0, 0.4)", "dark-mask"),
    WHITE("rgba(255, 255, 255, 1)", "white");

    private final String cssValue;
    private final String partOfClassName;

    ColourSchema(String cssValue, String partOfClassName) {
        this.cssValue = cssValue;
        this.partOfClassName = partOfClassName;
    }

    private static final Map<String, ColourSchema> lookupByClassName = new HashMap<>();
    private static final Map<String, ColourSchema> lookupByCssValue = new HashMap<>();

    static {
        for (ColourSchema colourSchema : ColourSchema.values()) {
            lookupByClassName.put(colourSchema.getPartOfClassName(), colourSchema);
            lookupByCssValue.put(colourSchema.getCssValue(), colourSchema);
        }
    }

    @JsonIgnore
    public String getCssValue() {
        return cssValue;
    }

    @JsonIgnore
    public String getPartOfClassName() {
        return partOfClassName;
    }

    @JsonIgnore
    public static ColourSchema getTextColourFromClassName(String className) {
//        System.out.println("ColourSchema.fromClassName : " + className);
        // For some reason have 'normal' text no text info but could have 'text-left'. Remove that so that next code snippet do not need to handle that.
        if (className.contains("text-left")) {
            className = className.replace("text-left", "text-primary");
        }

        // A checked checkbox can only be green
        if (className.contains("checkbox_true")) {
            className = className.replace("checkbox_true", "text-positive");
        }
        if (className.contains("checkbox_false")) {
            className = className.replace("checkbox_false", "text-white");
        }
        if (className.contains("checkbox__inner--falsy")) {
            className = className.replace("checkbox__inner--falsy", "text-white");
        }

        String classNameIdentifier;
        if (className.contains("text-infra-")) {
            classNameIdentifier = extractTextAfterPrefix("text-infra-", className);
        } else if (className.contains("text-")) {
            classNameIdentifier = extractTextAfterPrefix("text-", className);
        } else {
            throw new IllegalStateException("ClassName '" + className + "' did not contain 'text-'");
        }

        String classNameTextIdentifier = (classNameIdentifier.contains("text-"))
                ? classNameIdentifier.replace("text-", "")
                : classNameIdentifier;

        return lookupByClassName.entrySet().stream()
                .filter(entry -> entry.getKey().contains(classNameTextIdentifier))

                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("No matching ColourSchema found by classNameIdentifier: " + classNameIdentifier))
                .getValue();

    }

    @JsonIgnore
    public static ColourSchema getBackgroundColourFromClassName(String className) {

        if (className.contains("inherit")) {
            return null;
        }

        String classNameCleaned = (className.contains("infra-"))
                ? className.replace("infra-", "")
                : className;

        if (classNameCleaned.contains("bg-")) {
            String backgroundIdentifier = extractTextAfterPrefix("bg-", classNameCleaned);

            return lookupByClassName.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(backgroundIdentifier))
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalArgumentException("No matching ColourSchema found by ClassName: " + className))
                    .getValue();
        } else {
            return WHITE;
        }
    }

    @JsonIgnore
    public static ColourSchema fromCssValue(String cssValue) {
        return lookupByCssValue.get(cssValue);
    }

    /**
     * Returns the text immediately after the given prefix and before the next space.
     * If the prefix is not found or there's no text after it, returns null.
     */
    @JsonIgnore
    public static String extractTextAfterPrefix(String prefix, String className) {
        int start = className.indexOf(prefix);
        if (start == -1) {
            throw new IllegalArgumentException("Could not extract '" + prefix + "' from: " + className);
        }

        start += prefix.length();
        int end = className.indexOf(' ', start);

        String textAfterPrefix = (end == -1)
                ? className.substring(start)
                : className.substring(start, end);

        return textAfterPrefix;
    }
}
