package com.example.playwright.components.panels;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeasuringPointSettingsPanel {
    PanelHeader panelHeader;
    Preface preface;

    SettingsItem general;
    SettingsItem timeFrame;
    SettingsItem location;
    SettingsItem activeChannels;
    SettingsItem connectedDevices;
    SettingsItem graphSettings;
    SettingsItem dataPresentation;
    SettingsItem vibrationReport;
    SettingsItem blastSettings;
    SettingsItem advancedSettings;
    SettingsItem delete;

}
