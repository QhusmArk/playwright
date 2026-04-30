package com.example.playwright.components.panels.measuringPoint;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class MeasuringPointDetailsPanel {
    private PanelHeader panelHeader;
    private Preface preface;

    Map<String, String> summaryMap;

    SettingsItem connectedSensor;
    SettingsItem settings;
}