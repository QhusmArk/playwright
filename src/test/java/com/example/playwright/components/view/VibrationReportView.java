package com.example.playwright.components.view;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.ChartSectionBody;
import com.example.playwright.components.parts.ChartSectionHeader;
import com.example.playwright.components.parts.Tab;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VibrationReportView {

    PanelHeader panelHeader;
    List<Tab> reportTabs;
    private String reportDuration;
    private Button exportButton;

    private ChartSectionHeader mpChartSectionHeader;  // always visible
    private ChartSectionBody mpChartSectionBody;

}
