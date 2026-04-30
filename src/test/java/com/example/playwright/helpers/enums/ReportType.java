package com.example.playwright.helpers.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum ReportType {

    INTERVALS_CHART("intervals chart"),
    INTERVALS_TABLE("intervals table"),
    TRANSIENTS_TABLE("transients table"),
    ANALYSIS("analysis"),
    NOISE_REPORT("noise report"),
    MEASURING_REPORT("measuring report"),
    BLASTS("blasts"),
    TRANSIENT("transient"),
    TIME_DOMAIN("time domain"),
    FREQUENCY_DOMAIN("time domain"),
    PROJECT_BILLING("project billing"),
    DEVICE_BILLING("device billing"),
    MEASURING_POINT_BILLING("measuring point billing"),
    BLASTS_REPORT("blasts report"),
    REGRESSION_REPORT("regression report")
    ;

    private final String type;
    private static final Map<String, ReportType> lookup;

    ReportType(String type) {
        this.type = type;
    }

    static {
        lookup = new HashMap<>();
        for (ReportType reportType : ReportType.values()) {
            lookup.put(reportType.getType(), reportType);
        }
    }

    public static ReportType fromType(String type) {
        return Optional.ofNullable(lookup.get(type))
                .orElseThrow(() -> new IllegalStateException("Unknown text: " + type));
    }

    public String getType() {
        return type;
    }
}
