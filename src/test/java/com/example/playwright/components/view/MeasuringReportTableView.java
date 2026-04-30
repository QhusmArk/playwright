package com.example.playwright.components.view;

import com.example.playwright.components.parts.Icon;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MeasuringReportTableView {

    private String reportDuration;
    private Icon filterIcon;

    private List<Map<String, String>> cellHeaderAndValues;  // Measuring points x number of transients during search period

//    public MeasuringReportTableView(PanelHeader panelHeader, List<Tab> reportTabs) {
//        super(panelHeader, reportTabs);
//    }
}
