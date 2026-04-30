package com.example.playwright.components.panels;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * /company/projects/id/details
 */
@Data
@NoArgsConstructor
public class CompanyProjectDetailsPanel {

    private PanelHeader panelHeader;    // with right button
    // todo: improve Preface
    private Preface preface;            // with active toggle etc   ProjectPreface

    private Map<String, String> summaryPanel;

    private SettingsItem comments;
    private SettingsItem settings;
}
