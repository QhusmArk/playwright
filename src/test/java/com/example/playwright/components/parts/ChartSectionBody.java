package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ChartSectionBody {

    //todo: Expanded filter

    // todo: ersätt med Banner
    private Map<String, String> metaData;

    private Dropdown standardSelector;  // IntervalsChart.MpChartSection
    private Dropdown intervalTimeSelector; // IntervalsChart.MpChartSection
//    private Dropdown freqWeightingSelector; // IntervalsChart.MpChartSection

    private List<Dropdown> dropdowns;   // IntervalsChart.MpChartSection
    List<ToggleField> chartToggles;     // IntervalsChart

    List<InputField> inputFields;       // TDA
    List<String> chartsMetadata;

    ToggleField useOriginalData;        // FDA

    Button reset;
    Button apply;

    Table vibrationReportDateTable;
    Table vibrationReportTimeslotTable;

    public void addDropdown(Map<String, String> dropdownMap) {
        if (dropdowns == null) {
            dropdowns = new ArrayList<>();
        }
        dropdowns.add(new Dropdown(dropdownMap));
    }

    public void addInputField(InputField inputField) {
        if (inputFields == null) {
            inputFields = new ArrayList<>();
        }
        inputFields.add(inputField);
    }

}
