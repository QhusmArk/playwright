package com.example.playwright.components.view;

import com.example.playwright.components.parts.ChartSectionBody;
import com.example.playwright.components.parts.ChartSectionHeader;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * .../views/c7a0a685-9e94-413f-8350-cb325a36774f/transients/133946?tr_ids=dHJzLQIAAAAAAAAAG44BAAAAAAAAAAAAAAAAAFgxiibwFgAA
 */
@Getter
@Setter
@NoArgsConstructor
public class TransientAnalysisReport {

    private PanelHeader panelHeader;
    private String transientTime;

    private ChartSectionHeader tdaChartSectionHeader;
    private ChartSectionBody tda;

    private ChartSectionHeader fdaChartSectionHeader;
    private ChartSectionBody fda;
}
