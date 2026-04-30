package com.example.playwright.components.panels;

import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyBillingReportCreatePanel {

    private PanelHeader panelHeader;
    private Dropdown reportDuration;

    private TimeFrame timeFrame;

    Tab deviceTab;
    Tab projectTab;

    ToggleField showActiveOnlyToggleField;
    ToggleField showSelectedToggleField;

    Table table;    // Device-, Project- or Measuring_point-list
}
