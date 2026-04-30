package com.example.playwright.components.panels.blast;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /project/id/blasts/id/settings
 */
@Data
@NoArgsConstructor
public class BlastSettingsPanel {
    PanelHeader panelHeader;
    Preface preface;
    SettingsItem generalSettings;
    SettingsItem timeSettings;
    SettingsItem location;
    SettingsItem holes;
    SettingsItem charge;
    SettingsItem advancedSettings;
    SettingsItem measuringUnits;
    SettingsItem delete;
}
