package com.example.playwright.components.panels.device;

import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * .../company/devices/connectionDeviceType/deviceSerial/status/projects
 */
@Getter
@Setter
@NoArgsConstructor
public class DeviceConnectionHistoryPanel  {

    PanelHeader panelHeader;
    Preface preface;
    Table deviceConnectionTable;
}
