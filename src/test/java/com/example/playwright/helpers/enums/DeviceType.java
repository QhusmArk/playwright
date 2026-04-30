package com.example.playwright.helpers.enums;

import com.example.playwright.config.DeviceProperties;

import java.util.*;
import java.util.stream.Collectors;

public enum DeviceType {
    // todo: addera iconType till DeviceType. Då kan jag testa att alla devices ikoner visas rätt i klienten
    // Enum constants
    D10("D10", "D10", "LEGACY", true, false),
    MASTER("MASTER", "IM", "LEGACY", true, false),
    MINI("MINI", "IM", "LEGACY", true, false),
    MICRO("MICRO", "IM", "LEGACY", true, false),
    POINT("POINT", "POINT", "LEGACY", true, false),

    C10("C10", "COMPACT", "LEGACY", true, true),
    C12("C12", "COMPACT", "LEGACY", true, true),
    C10_LOGGER("C10_LOGGER", "COMPACT", "LEGACY", true, false),
    C12_LOGGER("C12_LOGGER", "COMPACT", "LEGACY", true, false),
    C10_SENSOR("C10_SENSOR", "COMPACT", "LEGACY", false, true),
    C12_SENSOR("C12_SENSOR", "COMPACT", "LEGACY", false, true),

    C20("C20", "C20", "CREATOR", true, true),
    C22("C22", "C22", "CREATOR", true, true),
    C50("C50", "C50", "CREATOR", true, true),

    VS10("VS10", "VS10", "CREATOR", false, true),
    VS12("VS12", "VS12", "CREATOR", false, true),
    UNDEFINED("undefined", null, null, false, false),   // for POINT wo sensor
    A10("A10", "A10", "LEGACY", false, true),
    A12("A12", "A12", "LEGACY", false, true),
    V10("V10", "V10", "LEGACY", false, true),
    V11("V11", "V11", "LEGACY", false, true),
    V12("V12", "V12", "LEGACY", false, true),
    V12B("V12B", "V12B", "LEGACY", false, true),
    V12R("V12R", "V12R", "LEGACY", false, true),
    V20W("V12W", "V12W", "LEGACY", false, true),
    S10("S10", "S10", "LEGACY", false, true),
    S11("S11", "S11", "LEGACY", false, true),
    S50("S50", "S50", "LEGACY", false, true),
    S51("S51", "S51", "LEGACY", false, true),
    P10("P10", "P10", "LEGACY", false, true),
    P12("P12", "P12", "LEGACY", false, true),
    X20("X20", "X20", "LEGACY", false, true),
    X20A("X20A", "X20A", "LEGACY", false, true),
    X20BP("X20BP", "X20BP", "LEGACY", false, true),
    X20CO("X20CO", "X20CO", "LEGACY", false, true),
    X20CO2("X20CO2", "X20CO2", "LEGACY", false, true),
    X20DM2("X20DM2", "X20DM2", "LEGACY", false, true),
    X20H("X20H", "X20H", "LEGACY", false, true),
    X20H2S("X20H2S", "X20H2S", "LEGACY", false, true),
    X20NH3("X20NH3", "X20NH3", "LEGACY", false, true),
    X20NO("X20NO", "X20NO", "LEGACY", false, true),
    X20NO2("X20NO2", "X20NO2", "LEGACY", false, true),
    X20O2("X20O2", "X20O2", "LEGACY", false, true),
    X20R("X20R", "X20R", "LEGACY", false, true),
    X20SR("X20SR", "X20SR", "LEGACY", false, true),
    X20V("X20V", "X20V", "LEGACY", false, true),
    X20WMT("X20WMT", "X20WMT", "LEGACY", false, true),
    X20WXT("X20WXT", "X20WXT", "LEGACY", false, true),

    // These versions below exist only for mapping api-response to DeviceType
    COMPACT("COMPACT", "COMPACT", "LEGACY", true, false),
    IM("IM", "IM", "LEGACY", true, false),
    C5x("C5x", "C5x", "CREATOR", false, false),
    EDI("EDI", "EDI", "LEGACY", false, false)
    ;

    private final String type;
    private final String url;
    private final String family;
    private final boolean communicatingDevice;
    private final boolean monitoringDevice;


    // Map for type lookup
    private static final Map<String, DeviceType> typeLookup;

    // Constructor
    DeviceType(String type, String url, String family, boolean communicatingDevice, boolean monitoringDevice) {
        this.type = type;
        this.url = url;
        this.family = family;
        this.communicatingDevice = communicatingDevice;
        this.monitoringDevice = monitoringDevice;
    }

    // Static block for initializing the type lookup map
    static {
        typeLookup = new HashMap<>();
        for (DeviceType deviceType : DeviceType.values()) {
            typeLookup.put(deviceType.getType(), deviceType);
        }
    }

    // Static method to retrieve DeviceType by type
    public static DeviceType fromType(String type) {
        return Optional.ofNullable(typeLookup.get(type))
                .orElseThrow(
                        () -> new IllegalStateException("Unknown type: " + type));
    }

    public static List<DeviceType> getCommunicatingDevices() {
        return Arrays.stream(DeviceType.values())
                .filter(DeviceType::isCommunicatingDevice)
                .collect(Collectors.toList());
    }

    public static List<DeviceType> getMonitoringDevices() {
        return Arrays.stream(DeviceType.values())
                .filter(DeviceType::isMonitoringDevice)
                .collect(Collectors.toList());
    }

    public static List<DeviceType> getCreators() {
        return Arrays.stream(DeviceType.values())
                .filter(deviceType -> deviceType.getFamily().equals("CREATOR"))
                .collect(Collectors.toList());
    }

    public static List<DeviceType> getLegacies() {
        return Arrays.stream(DeviceType.values())
                .filter(deviceType -> deviceType.getFamily().equals("LEGACY"))
                .collect(Collectors.toList());
    }

    public static DeviceType getIfC1xIsLoggerOrSensor(DeviceType compactType, boolean isLogger) {
        return switch (compactType) {
            case C10 -> isLogger ? C10_LOGGER : C10_SENSOR;
            case C12 -> isLogger ? C12_LOGGER : C12_SENSOR;
            default -> throw new IllegalStateException("Unexpected compactType: " + compactType);
        };
    }

    /**
     * Can also be 'undefined #undefined' for POINT
     * @param text e.g. 'D10 #103748' or 'A10 #5253'
     */
    public static DeviceType fromTypeHashAndSerial(String text) {
        if (text.contains("INFRA")) {
            text = text.replace("INFRA ", "");
        }

        String type = text.substring(0, text.indexOf(" #"));
        System.out.println("type: " + type);
        return DeviceType.fromType(type);
    }

    /**
     * @return
     * C22 from '/company/devices/C22/101915/settings/monitoring'
     * IM from '/company/devices/IM/3629/settings/sensor/12270'
     */
    public static DeviceType getCommunicatingDeviceFromCurrentUrl(String url) {
        try {
            String start = "devices/";
            int startIndex = url.indexOf(start) + start.length();
            int endIndex = url.indexOf('/', startIndex);
            return DeviceType.fromType(url.substring(startIndex, endIndex));

        } catch (Exception e) {
            throw new IllegalStateException("Could not get logger type from URL: " + url);
        }
    }

    /**
     * @return
     * C22 from '/company/devices/C22/101915/settings/monitoring'
     * S50 from '/company/devices/D10/103748/settings/sensor/5307'
     */
    public static DeviceType getMonitoringDeviceFromCurrentUrl(String url) {
        try {
            String start = "sensor/";
            int index = url.indexOf(start);
            String serial = url.substring(index + start.length());
            return DeviceType.fromType(DeviceProperties.getKeyByValue(serial));

        } catch (Exception e) {
            throw new IllegalStateException("Could not get sensor type from URL: " + url);
        }
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getFamily() {
        return family;
    }

    public boolean isCommunicatingDevice() { return communicatingDevice;}

    public boolean isMonitoringDevice() {
        return monitoringDevice;
    }
}
