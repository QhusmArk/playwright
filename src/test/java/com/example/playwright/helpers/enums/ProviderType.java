package com.example.playwright.helpers.enums;

import java.util.*;

public enum ProviderType {
        // todo: add member 'provider_name' so MEASURING_POINT("measure_points", "measuring point")
//        ACCOUNT_OVERVIEW(""),
        OVERVIEW("overview"),
        DEVICE_INCL_BANNER(""),        // this enum exist, so we can limit time it takes to search compact list devices
        PROJECT("projects"),
        MEASURING_POINT("measure_points"),
        BLAST("blasts"),
        DEVICE("devices"),
        DATA_REPORT("views"),
        MESSAGE_RULE("message_rules"),
        USER("users"),
        COMMENT("comments"),
        BILLING_REPORT("billing_reports"),
        SCHEDULED_REPORT("scheduled_reports");

        private final String endpoint;
        private static final Map<String, ProviderType> lookup;

        ProviderType(String endpoint) {
                this.endpoint = endpoint;
        }

        static {
                lookup = new HashMap<>();
                for (ProviderType providerType : ProviderType.values()) {
                        lookup.put(providerType.getEndpoint(), providerType);
                }
        }

        public static ProviderType fromEndpoint(String endpoint) {
                return Optional.ofNullable(lookup.get(endpoint))
                        .orElseThrow(() -> new IllegalStateException("Unknown text: " + endpoint));
        }

        public String getEndpoint() {
                return endpoint;
        }

        public static List<ProviderType> getAllProviderTypes() {
                return Arrays.asList(ProviderType.values());
        }

        /**
         * Returns the ProviderType based on keywords in the URL.
         *
         * @param url the URL to check
         * @return the matching ProviderType
         */
        public static ProviderType getProviderTypeFromCurrentUrl(String url) {
                Map<String, ProviderType> keywordMap = new LinkedHashMap<>();
                keywordMap.put("projects", PROJECT);
                keywordMap.put("devices", DEVICE);
                keywordMap.put("users", USER);
                keywordMap.put("measure_points", MEASURING_POINT);
                keywordMap.put("blasts", BLAST);
                keywordMap.put("views", DATA_REPORT);
                keywordMap.put("message_rules", MESSAGE_RULE);
                keywordMap.put("comments", COMMENT);
                keywordMap.put("overview", OVERVIEW);
                keywordMap.put("scheduled_report", SCHEDULED_REPORT);

                return keywordMap.entrySet().stream()
                        .filter(entry -> url.contains(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Unexpected value: " + url));
        }
}
