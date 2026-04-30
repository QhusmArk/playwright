package com.example.playwright.components.panels.scheduled_report;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledReportGeneralPanel {

    PanelHeader panelHeader;
    Preface preface;

    FieldWrapper generalWrapper;
    FieldWrapper dataWrapper;
    FieldWrapper sendingTimeWrapper;
}
