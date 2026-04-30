package com.example.playwright.components.parts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.helpers.StatusAssesser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChartSectionHeader  {
    private Icon leftIcon;

    String mainText;
    String subText;

    Banner mpDataInformation;           // from not-expanded DataViewStatus

    private Icon expansionIcon;
    private Icon filterIcon;
    private Icon menuIcon;
    private Icon downloadIcon;
    private Icon settingsIcon;
    Icon calculationsIcon;

    private Dropdown frequencyDataBenchmarkSelector;    // from expanded FrequencyChartSection

    StatusAssesser.Status status;

    /**
     * Method to get Mp name from mainText, when in Intervals Chart
     */
    @JsonIgnore
    public String getMpNameFromChartSectionHeader() {
        return (this.mainText.contains(","))
                ? this.mainText.substring(0, mainText.indexOf(","))
                : this.mainText;
    }

    /**
     * Method to get Mp description from mainText, when in Intervals Chart
     */
    @JsonIgnore
    public String getMpDescriptionFromChartSectionHeader() {
        return (this.mainText.contains(","))
                ? this.mainText.substring(mainText.indexOf(", "))
                : null;
    }



}
