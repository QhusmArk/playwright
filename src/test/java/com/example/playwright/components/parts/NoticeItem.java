package com.example.playwright.components.parts;

import com.example.helpers.StatusAssesser.Status;
import com.example.playwright.helpers.enums.ColourSchema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NoticeItem  {
    private Icon leftIcon;
    private String text;
    Icon rightIcon;
    ColourSchema noticeColour;
    Status status;
    Button button;

    // DeviceSettingsPanel, DeviceDetailsPanel
    // DeviceMonitoringPanel?
    /*
    batteryNotice;       // Device using an external power source
    monitoringNotice;    // The changes you commit will not reach the device until it’s set to Monitoring On or a data server connect is done.
    commitNotice;        // Un/committed
    sensorNotice;        // No sensor connected
    sensorNotice:        // V12 sensor with serial 17320 has inconsistent settings in: id, interval time, transient time and standard
     */
}
