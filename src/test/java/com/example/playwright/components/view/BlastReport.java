package com.example.playwright.components.view;

import com.example.playwright.components.parts.Tab;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BlastReport {

    // Default
    PanelHeader panelHeader;
    List<Tab> reportTabs;
    String reportDuration;

    // Upper blast panel in report
    // tbc

    // Lower blast panel in report
    Table reportContent;
}
