package com.example.playwright.components.aside.asideItems.listItems;

import com.example.playwright.components.aside.asideItems.AsideItem;
import com.example.playwright.components.parts.Banner;
import com.example.playwright.helpers.enums.DeviceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeviceItem extends AsideItem {

    @JsonIgnore
    public List<Banner> getBanners() {
        return super.getBanners();
    }

    @JsonIgnore
    public String getLastRead() {
        return super.getSubText();
    }

    @JsonIgnore
    public String getSerial() {
        return extractPart(super.getMainText(), "serial");
    }

    @JsonIgnore
    public int getSerialNumber() {
        return Integer.parseInt(getSerial());
    }

    /**
     * COMPACT are a nuisance. They have same serial for logger- and sensor-part, but are displayed as a logger-sensor-relation.
     */
    @JsonIgnore
    public DeviceType getDeviceType() {
        String type = extractPart(this.getMainText(), "type");

        if (type.contains("C10") || type.contains("C12")) {
            // Only COMPACTS with monStatusIcon can be logger
            if (this.getListItemIcon() == null) {
                return DeviceType.fromType(type + "_SENSOR");
            } else {
                return DeviceType.fromType(type + "_LOGGER");
            }
        } else {
            return DeviceType.fromType(type);
        }
    }

    /**
     * Returns the part of the string either before or after the '#' character.
     *
     * @param input     the full input string (e.g., "C50 #100182")
     * @return the trimmed part before or after '#' or null if '#' is not present
     */
    private static String extractPart(String input, String getThisPart) {
        if (input == null || !input.contains("#")) {
            return null;
        }

        String[] parts = input.split("#", 2);
        return switch (getThisPart) {
            case "type" -> parts[0].trim();
            case "serial" -> parts[1].trim();
            default -> throw new IllegalArgumentException("Unknown getThisPart: " + getThisPart);
        };
    }


}
