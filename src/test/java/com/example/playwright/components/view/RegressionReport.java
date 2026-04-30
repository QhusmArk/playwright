package com.example.playwright.components.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.playwright.components.parts.Banner;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.ChartSectionHeader;
import com.example.playwright.components.parts.Tab;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RegressionReport {

    PanelHeader panelHeader;
    List<Tab> reportTabs;
    String reportDuration;

    // Expanded filter OR expanded calculation

    ChartSectionHeader chartSectionHeader;

    // Instead of using ChartSectionBody we'll set the basic components individually for now.
//    ChartSectionBody regressionAnalysis;

    // Regession Chart Calculations
    List<Banner> chartBanners;

    /*
    BlastList
    MpList
     */

    Button applyButton;

    @JsonIgnore
    public Banner getBanner(String bannerText) {
        return chartBanners.stream()
                .filter(banner -> banner.getText().equals(bannerText))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("No "+bannerText+" banner was found"));
    }

}
