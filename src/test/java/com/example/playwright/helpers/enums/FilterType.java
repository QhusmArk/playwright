package com.example.playwright.helpers.enums;

import java.util.*;

public enum FilterType {
    //todo: kan jag förvara data-qa-id i enum? Typ:
    // ADMINISTRATOR("Administrator", "admin"),

    ACTIVE_ONLY("Active only", true),
    ALL_PROJECTS("All projects", true),
    DEVICES_WITH_WARNINGS("Devices with warnings", true),
    LOW_BATTERY("Low battery", true),
    LOW_GSM_SIGNAL("Low GSM signal", true),
    COMMUNICATION("Communication", true),
    OTHER("Other", true),
    FAVORITES("Favorites", true),
    MONITORING_ON("Monitoring On", true),
    UNCOMMITTED("Battery", true),
    COMMUNICATING_DEVICES("Communicating devices", true),
    MONITORING_DEVICES("Monitoring devices", true),

    C20("C20", false),
    C22("C22", false),
    C50("C50", false),
    S50("S50", false),
    C10("C10", false),
    C12("C12", false),
    A10("A10", false),
    S51("S51", false),
    V10("V10", false),
    V11("V11", false),
    V12("V12", false),
    V12R("V12R", false),
    VS10("VS10", false),
    VS12("VS12", false),
    X20A("X20A", false),
    X20BP("X20BP", false),
    X20CO("X20CO", false),
    X20DM2("X20DM2", false),
    X20H("X20H", false),
    X20NH3("X20NH3", false),
    X20NO("X20NO", false),
    X20NO2("X20NO2", false),
    X20O2("X20O2", false),
    X20R("X20R", false),
    X20SR("X20SR", false),
    X20WMT("X20WMT", false),
    X20WXT("X20WXT", false),
    D10("D10", false),
    MASTER("MASTER", false),
    MINI("MINI", false),
    POINT("POINT", false),
    S10("S10", false),
    ALL("All", true),
    ADMINISTRATOR("Administrator", true),
    USER("User", true),
    CLIENT_PLUS("Client+", true),
    CLIENT("Client", true),
    BLASTER("Blaster", true),
    ALL_MEASURING_POINT("All measuring point", true),
    ACTIVE("Active", true),
    INACTIVE("Inactive", true),
    ALL_BLASTS("All blasts", true),
    BLASTS_WITH_LOADED_DATA("Blasts with loaded data", true),
    ALL_REPORTS("All reports", false),
    TEMPORARY("Temporary", false),
    SAVED("Saved",false),
    SHARED("Shared",false),
    ALL_MESSAGE_RULES("All message rules", true),
    EMAILS("Emails", true),
    TRANSIENT_REPORT("Transient report", true),
    SMS("SMS", true);

    private final String text;
    private final boolean autocloseOnFilterInteraction;
    private static final Map<String, FilterType> lookup;

    static {
        lookup = new HashMap<>();
        for (FilterType filterType : FilterType.values()) {
            lookup.put(filterType.getText(), filterType);
        }
    }

    FilterType(String text, boolean autocloseOnFilterInteraction) {
        this.text = text;
        this.autocloseOnFilterInteraction = autocloseOnFilterInteraction;
    }

    public String getText() {
        return text;
    }
    public boolean getAutocloseOnFilterInteraction() { return autocloseOnFilterInteraction; }

    public static FilterType fromText(String text) {
        return Optional.ofNullable(lookup.get(text))
                .orElseThrow(() -> new IllegalStateException("Unknown text: " + text));
    }

    public static FilterType fromName(String name) {
        try {
            return FilterType.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown ListFilter name: " + name);
        }
    }

    public static List<FilterType> fromNames(List<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }

        List<FilterType> filters = new ArrayList<>();
        for (String name : names) {
            filters.add(fromName(name));
        }
        return filters;
    }
}
