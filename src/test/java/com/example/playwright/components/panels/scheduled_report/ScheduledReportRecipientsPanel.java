package com.example.playwright.components.panels.scheduled_report;

import com.example.playwright.components.parts.Icon;
import com.example.playwright.components.parts.InputField;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.ToggleField;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledReportRecipientsPanel {

    PanelHeader panelHeader;
    Preface preface;

    Icon searchIcon;
    InputField searchField;
    ToggleField activeToggle;

    Table recipientTable;
}
