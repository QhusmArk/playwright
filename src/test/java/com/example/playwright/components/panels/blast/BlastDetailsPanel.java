package com.example.playwright.components.panels.blast;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * project/id/blasts/id/details
 */
@Getter
@Setter
@NoArgsConstructor
public class BlastDetailsPanel {
    PanelHeader panelHeader;
    Preface preface;

    Map<String, String> summaryMap;

    SettingsItem calculatedValues;
    SettingsItem blastSettings;

    Table mpTable;
}
