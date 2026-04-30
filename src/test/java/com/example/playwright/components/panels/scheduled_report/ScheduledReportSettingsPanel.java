package com.example.playwright.components.panels.scheduled_report;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledReportSettingsPanel {
    PanelHeader panelHeader;
    Preface preface;

    SettingsItem general;
    SettingsItem measuringPoints;
    SettingsItem recipients;
    SettingsItem advancedSettings;
    SettingsItem deleteSettings;

}
