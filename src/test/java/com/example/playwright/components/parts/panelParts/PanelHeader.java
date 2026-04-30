package com.example.playwright.components.parts.panelParts;

import com.example.playwright.components.parts.Button;
import com.example.playwright.helpers.enums.DeviceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PanelHeader {
    String headerText;
    Button leftButton;
    Button rightButton;


    /*
    /company/devices/COMPACT/62420/details
    panelheader = C12 #62420
    preface = INFRA Compact C12 Triaxial vibration monitor

    /company/devices/C12/62420/details
    panelheader = INFRA C12 #62420
    preface = INFRA Compact C12 Triaxial vibration monitor

    /company/devices/S50/5307/details
    panelheader = INFRA S50 #5307
    preface = INFRA S50

    /company/devices/D10/103748/settings/sensor/5307
    panelheader = Monitoring
    preface = INFRA S50 #5307

     */
    public DeviceType deductDeviceTypeFromPanelHeader() {
        String type = extractPart(this.headerText, "type");

        System.out.println("deductDeviceTypeFromPanelHeader: " + type);
        if (headerText.contains("C12")) {
            return headerText.contains("INFRA")
                    ? DeviceType.C12_SENSOR
                    : DeviceType.C12_LOGGER;
        }

        if (headerText.contains("C10")) {
            return headerText.contains("INFRA")
                    ? DeviceType.C10_SENSOR
                    : DeviceType.C10_LOGGER;
        }

        if (headerText.contains("S50")) {
            return DeviceType.fromType("S50");
        }

        if (headerText.contains("VS12")) {
            return DeviceType.fromType("VS12");
        }

        return DeviceType.fromType(type);
    }

    public int deductSerial() {
        String serial = extractPart(this.headerText, "serial");
        return Integer.parseInt(serial);
    }

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
