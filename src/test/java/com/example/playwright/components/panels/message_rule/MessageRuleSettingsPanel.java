package com.example.playwright.components.panels.message_rule;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRuleSettingsPanel  {
    PanelHeader panelHeader;
    Preface preface;

    SettingsItem general;
    SettingsItem content;
    SettingsItem sendingThresholds;
    SettingsItem recipients;
    SettingsItem delete;
}
