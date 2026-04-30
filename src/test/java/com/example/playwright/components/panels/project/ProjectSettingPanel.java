package com.example.playwright.components.panels.project;

import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /projects/id/settings
 */
@Data
@NoArgsConstructor
public class ProjectSettingPanel {

    private PanelHeader panelHeader;
    private Preface preface;

    private SettingsItem projectDetails;
    private SettingsItem location;
    private SettingsItem mapSettings;
    private SettingsItem agendas;
    private SettingsItem delete;
}

