package com.example.playwright.components.view;

import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * .../views/c7a0a685-9e94-413f-8350-cb325a36774f/intervals/chart
 */
@Getter
@Setter
@NoArgsConstructor
public class IntervalsChartView {
    PanelHeader panelHeader;
    List<Tab> reportTabs;
    private String reportDuration;
    private Icon copyIcon;
    private Icon gridIcon;
    private Button exportButton;

    private DataViewStatus dataViewStatus;

    private List<MeasuringPointData> measuringPointDataList;

    /**
     * MeasuringPointData consists of 1-2 ExpansionHeader and 1-2 ChartSection, depending on if FrequencyData is present.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MeasuringPointData {
        private Icon warningIcon;   // todo: make optional?
        private String warningText;     // todo: make optional?

        // @NonNull
        private ChartSectionHeader mpChartSectionHeader;  // always visible
        private ChartSectionBody mpChartSectionBody;

        private ChartSectionHeader freqDataChartSectionHeader;    // todo: make optional?
        private ChartSectionBody freqDataChartSectionBody;          // todo: make optional?

        private ChartSectionHeader octaveDataChartSectionHeader;
        private ChartSectionBody octaveDataChartSectionBody;
    }
}
