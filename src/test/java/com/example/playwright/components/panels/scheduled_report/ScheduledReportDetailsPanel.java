package com.example.playwright.components.panels.scheduled_report;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledReportDetailsPanel {

    PanelHeader panelHeader;
    Preface preface;

    Button mpSummary;
    Button recipientSummary;

    SettingsItem settings;
}
