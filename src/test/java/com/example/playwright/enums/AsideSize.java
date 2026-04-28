package com.example.playwright.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum AsideSize {
    COMPACT("__q_strn|compact"),
    MEDIUM("__q_strn|medium"),
    FULL("__q_strn|full");

    private final String localStorageValue;
    private static final Map<String, AsideSize> lookup;

    static {
        lookup = new HashMap<>();
        for (AsideSize state : AsideSize.values()) {
            lookup.put(state.getLocalStorageValue(), state);
        }
    }

    AsideSize(String localStorageValue) {
        this.localStorageValue = localStorageValue;
    }

    public String getLocalStorageValue() {
        return localStorageValue;
    }

    public static AsideSize fromLocalStorageValue(String asideSize) {
        return Optional.ofNullable(lookup.get(asideSize))
                .orElseThrow(() -> new IllegalStateException("Unknown aside size: " + asideSize));
    }

    public static AsideSize getAsideSizeByName(String name) {
        try {
            return AsideSize.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unknown AsideState name: " + name);
        }
    }

}

