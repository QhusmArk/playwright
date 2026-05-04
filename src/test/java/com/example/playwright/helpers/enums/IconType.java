package com.example.playwright.helpers.enums;

import com.example.api.endpoints.DeviceApi;
import com.example.playwright.config.DeviceProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum IconType {
    HOLE("adjust"),
    MIC("mic"),
    CALCULATE("calculate"),
    AGENDA("date_range"),
    DELETE("delete"),
    CLOSE("close"),
    CHECKBOX_TICKED("checkbox_true"),
    CHECKBOX_NOT_TICKED("checkbox_false"),
    CHECKBOX_MIXED("checkbox_mixed"),
    COLUMNS_SELECT("view_column"),
    MONITORING_ON("monitoring-on"),
    MONITORING_OFF("monitoring-off"),
    SCHEDULE("schedule"), // or CLOCK?
    CLOCK("access_time"),
    DASHBOARD("dashboard"),    // Overview
    HOME("home"),  // ? home
    PLUS("add"),
    ARROW_FORWARD("arrow_forward"),  // arrow_forward
    ARROW_UP("arrow_up"),
//    ARROW_DOWN("arrow_down"),
    SETTINGS("settings"),   // This should really be called SETTING, as it's only one cogwheel, but I make an expection for this.
    MY_LOCATION("my_location"),   // my_location
    LOCATION("gps_fixed"),
    MAP_RULER("straighten"),  // straighten
    CLIPBOARD("content-paste"),
    COPY("content-copy"),
    GRID("grid_on"),
    FILTER_REPORT("tune"),
    FILTER("filter"),
    SEARCH("search"),
    SORT("sort"),
    REFRESH("refresh"),
    MENU("more_vert"),  // three dot menu button
    ARROW_BACK("arrow_back"),
    WARNING("report"),  // really confusing (see below)...
    ALERT("warning"),   // really confusing (see above)...  // NoticeItem, data report aggregated values
    CANCEL("cancel"),
//    CHART("interval-data"),
    DOWNLOAD("download"),
    PROJECT("projects"),
    USER("account_circle"),
    BLAST("blast"),
    // Project Overview
    MAP_UNITS("map-units"),
    HARDWARE("hardware"),   // Device
    VIEW("assessment"),   // Data Report
    REPORTS("reports"),
    COMMENTS("forum"), // ?    forum
    // MeasuringPoints
    VIB("vib"),
    GEO("geo"),
    SOUND("sound"),
    // Message Rule
    SMS("sms"),
    SMS_MAIL("smsmail"),   // Message Rule
    MAIL("mail"),
    //DataReport
    ABORT_SEARCH("clear"),
    SPINNER("spinner"),
    ABORTED("not_interested"), // not_interested
    INTERVAL("interval-data"),
    TRANSIENT("transient"),
    MAP_PIN("place"),
    REGRESSION("regression"),
    //    ALL_DATA("interval"),
    //Device
    C22("C22"),
    C12("C12"),
    C5X("C5x"),
    MASTER("Master"),
    V10("V10"),
    V12("V12"),
    S50("S50"),
    S10("S10"),
    VS10("Point-VS10"),
    VS12("Point-VS12"),
    X20("X20"),
    X20GAS("X20Gas"),
    X20DM("X20DM"),
    X20H("X20H"),
    X20R("X20R-outdoor"),
    X20SR("X20SR"),
    X20WMT("X20WXT-4"),
    X20WXT("X20WXT-4"),
    MAP_MARKER_PROJECT_GROUP("map-marker-project-group"),
    MAP_MARKER_DEVICE_GROUP_ACTIVE("device-group"),
    MAP_MARKER_DEVICE_GROUP_INACTIVE("device-group-off"),
    MAP_MARKER_BLAST_GROUP("blast-group"),
    MAP_MARKER_MP_GROUP_ACTIVE("mp-group"),
    MAP_MARKER_MP_GROUP_INACTIVE("mp-group"),
    MAP_MARKER_MP_GROUP("mp-group-off"),
    MAP_MARKER_PROJECT_ACTIVE("project-on"),
    MAP_MARKER_PROJECT_INACTIVE("project-off"),
    MAP_MARKER_DEVICE_ALERT("device-alert"),
    MAP_MARKER_DEVICE_ACTIVE("device-on"),
    MAP_MARKER_DEVICE_INACTIVE("device-off"),
    MAP_MARKER_BLAST_PASSED("blast-past"),
    MAP_MARKER_BLAST_HOVER("blast-hover"),
    MAP_MARKER_BLAST_FUTURE("blast-created"),
    MAP_MARKER_MP_VIB_ACTIVE("mp-vib-on"),
    MAP_MARKER_MP_VIB_INACTIVE("mp-vib-off"),
    MAP_MARKER_MP_GEO_ACTIVE("mp-geo-on"),
    MAP_MARKER_MP_GEO_INACTIVE("mp-geo-off"),
    MAP_MARKER_MP_SOUND_ACTIVE("mp-sound-on"),
    MAP_MARKER_MP_SOUND_INACTIVE("mp-sound-off"),
    ZOOM_IN("zoom-in"),
    ZOOM_OUT("zoom-out"),
    ZOOM_IN_DISABLED("zoom-in leaflet-disabled"),
    ZOOM_OUT_DISABLED("zoom-out leaflet-disabled"),
    GIANT_PLUS("block"),
    COLLAPSED("keyboard_arrow_down_collapsed"),
    EXPANDED("keyboard_arrow_down_expanded"),
    DROPDOWN_ARROW("arrow_drop_down"),
    HERTZ("hertz"),
    MEASURING("measuring"),
    INFO("info"),    // NoticeItem, C50 default timeslot
    MAP("map"),
    COMMENT("comment"),
    SEND("send"),
    PEOPLE("people"),
    ARROW_LEFT("arrow_left"),
    ARROW_RIGHT("arrow_right"),
    PLAY("play_circle_filled"),
    SAVE("save"),
    FLAG("flag"),
    SORTING_DESC("sort-icon-descending"),
    SORTING_ASC("sort-icon-ascending"),
    RENEW("autorenew"),
    EVENT("event"),
    EXPAND_MORE("expand_more"), // Device/Mp Billing Report
    EXPAND_LESS("expand_less"),  // Device/Mp Billing Report
    DONE("done"),
    OVERLOAD("overload"),
    MESSAGE("message"),
    COMMUNICATION("communication"),
    BATTERY_100("Battery-100"),
    BATTERY_75("Battery-75"),
    BATTERY_50("Battery-50"),
    BATTERY_25("Battery-25"),
    BATTERY_0("Battery-0"),
    BATTERY_CONNECTED("battery-connected"),
    GSM_100("GSM-100"),
    GSM_75("GSM-75"),
    GSM_50("GSM-50"),
    GSM_25("GSM-25"),
    GSM_0("GSM-0"),
    HUMIDITY("humidity"),
    TEMPERATURE("temp"),
    MEMORY("memory"),
    STAR("star"),
    CALENDAR("calender"),
    PADLOCK("lock"),
    CHEVRON_RIGHT("chevron-right"),
    EDIT("edit"),
    CHANNELS("channels"),
    ROOM("room")
    ;

    private final String partOfClassName;
    private static final Map<String, IconType> lookupByClassName = new HashMap<>();

    static {
        for (IconType iconType : IconType.values()) {
            lookupByClassName.put(iconType.getPartOfClassName(), iconType);
        }
    }

    IconType(String partOfClassName) {
        this.partOfClassName = partOfClassName;
    }

    /**
     * Icon has two ways to be identified:
     * - A generic className + DOM-text, e.g., 'q-icon text-white notranslate material-icons + account_circle
     * - ClassName, e.g., 'q-icon text-white icon-projects'
     * @param providerIconText, either empty or has text from the webelement
     */
    public static IconType fromWebElement(String providerIconClassName, String providerIconText) {
        if (providerIcontext().isEmpty()) {
            return IconType.fromClassName(providerIconClassName);
        } else {
            return IconType.fromClassName(providerIconText);
        }
    }

    public String getPartOfClassName() {
        return partOfClassName;
    }

    /**
     * This solution iterates over the lookupByClassName entries, sorted by the length of the key in descending order,
     * ensuring that the first match found will be the most specific one that exists within the input className.
     * @param className like "q-icon text-infra-disabled icon-C22"
     * @return  ColourSchema where className matches most with partOfClassName.
     */
    public static IconType fromClassName(String className) {
        // Some icons have text/class names that are very similar.
        String textToMatch = switch (className) {
            case "Download" -> "download";
            default -> className;
        };

        if (className.equals("q-icon")) {
            return null;
        }

        return lookupByClassName.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getKey().length(), entry1.getKey().length())) // Sort by key length in descending order
                .filter(entry -> textToMatch.contains(entry.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching IconType found by ClassName: " + textToMatch))
                .getValue();
    }

    /**
     * @return 'projects' from "q-icon text-primary icon-projects" or "q-icon text-primary icon-projects q-mr-sm".
     */
    public static String extractIconTypeFromIconClassName(String iconClassName) {
        if (iconClassName.contains("icon-")) {
            int startIndex = iconClassName.indexOf(" icon-") + 6; // Find the start of "icon-" and move back to the space before it
            int endIndex = iconClassName.indexOf(" ", startIndex);

            if (endIndex == -1) { // If there's no space after "icon-"
                endIndex = iconClassName.length();
            }

            return iconClassName.substring(startIndex, endIndex);
        } else {
            return iconClassName;
        }
    }

    /**
     * This method deducts which IconType.BATTERY_* that should be expected depending on external_power and/or battery_level.
     */
    public static IconType getBatteryLevelIconType(String type) {
        IconType batteryIconType;
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial(type));

        boolean onExternalPower = switch (type) {
            case "C22", "POINT" -> {
                yield DeviceApi.getDevice(type, serial).getExternalPower();
            }
            case "D10" -> false;    // I don't know if D10 can "be" on external power, ie have the value in the response.
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        if (onExternalPower) {
            batteryIconType = BATTERY_CONNECTED;

        } else {
            Double batteryLevel = switch (type) {
                case "C22" -> {
                    List<Map<String, String>> batteryLevels = DeviceApi.getDevice(type, serial).getBatteryLevel();
                    String value = batteryLevels.get(batteryLevels.size() - 1)
                            .values()
                            .stream()
                            .reduce((first, second) -> second)
                            .orElse(null); // gets the last value
                    yield Double.parseDouble(value);
                }
                case "D10" -> {
                    yield DeviceApi.getDevice(type, serial).getStatus().getBattery();
                }
                default -> throw new IllegalArgumentException("Unexpected type: " + type);
            };

            batteryIconType = switch (type) {
                case "C22" -> {
                    yield IconType.parseCreatorBatteryPercentageToBatteryIconType(batteryLevel);
                }
                case "D10" -> {
                    yield IconType.parseD10BatteryVoltageToBatteryIconType(batteryLevel);
                }
                default -> throw new IllegalArgumentException("Unexpected type: " + type);
            };
        }

        return batteryIconType;
    }

    /**
     * Afaik D10 battery icon only have two icons, 50 or 0 %.
     */
    private static IconType parseD10BatteryVoltageToBatteryIconType(Double voltage) {
        if (voltage > 14.4) {   // Alter this upon new knowledge
            return IconType.BATTERY_50;
        } else {
            return IconType.BATTERY_0;
        }
    }

    /**
     * @param percentage Battery level in percentage.
     */
    private static IconType parseCreatorBatteryPercentageToBatteryIconType(Double percentage) {
        if (percentage == null || percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Value must be > 0 and <= 100");
        }
        // Guesses...
        if (percentage > 80) {
            return BATTERY_100;
        } else if (percentage <= 80 && percentage > 60) {
            return IconType.BATTERY_75;
        } else if (percentage <= 60 && percentage > 40) {
            return IconType.BATTERY_50;
        } else if (percentage <= 40 && percentage > 20) {
            return IconType.BATTERY_25;
        } else {
            return IconType.BATTERY_0;
        }
    }

    /**
     * This method deducts which IconType.BATTERY_* that should be expected depending on external_power and/or battery_level.
     * todo: a method like this should be created for IconType.GSM_*
     */
    public static IconType getGSMIconType(String type) {
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial(type));

        Double signalStrength = switch (type) {
            case "C22" -> {
                List<Map<String, String>> signalStrengths = DeviceApi.getDevice(type, serial).getRssi();
                String value = signalStrengths.get(signalStrengths.size() - 1)
                        .values()
                        .stream()
                        .reduce((first, second) -> second)
                        .orElse(null); // gets the last value
                yield Double.parseDouble(value);
            }
            case "D10" -> {
                yield DeviceApi.getDevice(type, serial).getStatus().getRssi();
            }
            default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };
        return IconType.parseSignalStrengthToGSMIconType(signalStrength);
    }

    private static IconType parseSignalStrengthToGSMIconType(Double value) {
        if (value == null || value < 0 || value > 100) {
            throw new IllegalArgumentException("Value must be > 0 and <= 100");
        }

        // Guesses...
        if (value >= 90) {
            return GSM_100;
        } else if (value < 90 && value > 64) {
            return IconType.GSM_75;
        } else if (value <= 64 && value > 45) {
            return IconType.GSM_50;
        } else if (value <= 44 && value > 20) {
            return IconType.GSM_25;
        } else {
            return IconType.GSM_0;
        }
    }

}
