package com.example.playwright.pageObjects;

import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.view.TransientAnalysisReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.playwright.helpers.enums.IconType.EXPANDED;
import static com.example.playwright.helpers.enums.IconType.TRANSIENT;

public class TransientChartsPO extends CommonPO {

    // Transient report consist of TDA and sometimes FDA
    public TransientAnalysisReport getTransientView() {
        TransientAnalysisReport tcv = new TransientAnalysisReport();

        PanelHeader panelHeader = getPanelHeader("report");
        tcv.setPanelHeader(panelHeader);

        String reportDuration = getDrPO().getReportDuration(TRANSIENT);
        tcv.setTransientTime(reportDuration);

        // TDA is always in a transient view
        ChartSectionHeader tdaChartSectionHeader = getTransientAnalysisExpansionHeader("//div[@data-qa-id='time_domain_analysis'] //div[@role='button']");
        tcv.setTdaChartSectionHeader(tdaChartSectionHeader);

        if (tdaChartSectionHeader.getExpansionIcon().getType().equals(EXPANDED)) {
            ChartSectionBody tda = getTimeDomainChartSection();
            tcv.setTda(tda);
        }

        ChartSectionHeader fdaChartSectionHeader = getTransientAnalysisExpansionHeader("//div[@data-qa-id='frequency_domain_analysis'] //div[@role='button']");
        tcv.setFdaChartSectionHeader(fdaChartSectionHeader);

        if (fdaChartSectionHeader.getExpansionIcon().getType().equals(EXPANDED)) {
            ChartSectionBody fda = getFrequencyDomainChartSection();
            tcv.setFda(fda);
        }

        return tcv;
    }

    private ChartSectionBody getTimeDomainChartSection() {
        ChartSectionBody chartSectionBody = new ChartSectionBody();
        String chartPath = "//div[@data-qa-id='time_domain_analysis']";

        Map<String, String> timeDomainMetaData = getTimeDomainMetaData();
        chartSectionBody.setMetaData(timeDomainMetaData);

        Map<String, String> standardDropdownMap = getDropdownParts(chartPath + " //label[.//div[text()='Standard']]");
        chartSectionBody.addDropdown(standardDropdownMap);

        Map<String, String> operatorDropdownMap = getDropdownParts(chartPath + " //label[.//div[text()='Operator']]");
        chartSectionBody.addDropdown(operatorDropdownMap);

        InputField highpassInputMap = getInputFieldByHeader("Highpass");
        chartSectionBody.addInputField(highpassInputMap);

        InputField lowpassInputMap = getInputFieldByHeader("Lowpass");
        chartSectionBody.addInputField(lowpassInputMap);

        Button reset = getButton(chartPath + " //button[.//div[contains(text(),'Reset')]]");
        chartSectionBody.setReset(reset);

        Button apply = getButton(chartPath + " //button[.//span[contains(text(),'Apply')]]");
        chartSectionBody.setApply(apply);

        List<String> chartsMetaData = getTransientChartMetadata(chartPath);
        chartSectionBody.setChartsMetadata(chartsMetaData);
        return chartSectionBody;
    }

    /**
     * Such as 'Max: 0.375 mm/s,  0.09 m/s²,  5.19 um,  32.5 Hz Trigger type: external'
     */
    private List<String> getTransientChartMetadata(String chartPath) {
        List<String> transientChartsMetaData = new ArrayList<>();

        String transientChartPath = chartPath + " //div[@id='TransientChart']";

        int chartCount = actions().countHowManyElements(transientChartPath + " //*[contains(@id, 'scichart__text-annotation')]");

        for (int c = 1; c <= chartCount; c++) {
            String chartsMetaDataPath = transientChartPath + " //*[contains(@id, 'scichart__text-annotation')]["+c+"]";

            String metadata = actions().findOneElementsText(chartsMetaDataPath);
            transientChartsMetaData.add(metadata);
        }
        return transientChartsMetaData;
    }

    private ChartSectionBody getFrequencyDomainChartSection() {
        ChartSectionBody chartSectionBody = new ChartSectionBody();
        String chartPath = "//div[@data-qa-id='frequency_domain_analysis']";

        // Mandatory fields
        Map<String, String> operatorDropdownMap = getDropdownParts(chartPath + " //label[.//div[text()='Operator']]");
        chartSectionBody.addDropdown(operatorDropdownMap);

        Map<String, String> xScaleMap = getDropdownParts(chartPath + " //label[.//div[text()='X scale']]");
        chartSectionBody.addDropdown(xScaleMap);

        Map<String, String> yScaleMap = getDropdownParts(chartPath + " //label[.//div[text()='Y scale']]");
        chartSectionBody.addDropdown(yScaleMap);

        // Optionals fields
        boolean hasOctaveType = actions().elementExistAndVisible(chartPath + " //label[.//div[text()='Octave type']]", false, 0);
        boolean hasFftType = actions().elementExistAndVisible(chartPath + " //label[.//div[text()='FFT Type']]", false, 0);
        boolean hasFftWindow = actions().elementExistAndVisible(chartPath + " //label[.//div[text()='FFT Window']]", false, 0);
        boolean hasQ = actions().elementExistAndVisible(chartPath + " //label[.//div[text()='Q']]", false, 0);

        boolean hasFirstSample = actions().elementExistAndVisible(chartPath + " //label[.//div[text()='First sample [s]']]", false, 0);
        boolean hasLastSample = actions().elementExistAndVisible(chartPath + " //label[.//div[text()='Last sample [s]']]", false, 0);

        if (hasOctaveType) {
            Map<String, String> octaveTypeDropdownMap = getDropdownParts(chartPath + " //label[.//div[text()='Octave type']]");
            chartSectionBody.addDropdown(octaveTypeDropdownMap);
        }
        if (hasFftType) {
            Map<String, String> fftTypeDropdownMap = getDropdownParts(chartPath + " //label[.//div[text()='FFT Type']]");
            chartSectionBody.addDropdown(fftTypeDropdownMap);
        }
        if (hasFftWindow) {
            Map<String, String> fftWindowDropdownMap = getDropdownParts(chartPath + " //label[.//div[text()='FFT Window']]");
            chartSectionBody.addDropdown(fftWindowDropdownMap);
        }
        if (hasQ) {
            Map<String, String> qInputMap = getDropdownParts(chartPath + " //label[.//div[text()='Q']]");
            chartSectionBody.addDropdown(qInputMap);
        }
        if (hasFirstSample) {
            InputField firstSample = getInputFieldByHeader("First sample [s]");
            chartSectionBody.addInputField(firstSample);
        }
        if (hasLastSample) {
            InputField lastSample = getInputFieldByHeader("Last sample [s]");
            chartSectionBody.addInputField(lastSample);
        }

        // Get state of toggle
        ToggleField useOriginalData = getToggle("right", "//form //div[@role='switch' and ./div[text()='Use original data']]/parent::*");
        chartSectionBody.setUseOriginalData(useOriginalData);

        Button apply = getButton(chartPath + " //button[.//span[contains(text(),'Apply')]]");
        chartSectionBody.setApply(apply);

        return chartSectionBody;
    }

    /**
     * Such keys as Measuring Point:, Description:, Sensor:, Serial number:, Latest calibration:, Standard:, Operator:, Resultant:, Time:,
     */
    private Map<String, String> getTimeDomainMetaData() {
        Map<String, String> timeDomainHeader = new HashMap<>();

        int headers = actions().countHowManyElements("//div[@class='info-field']");

        for (int headerIndex = 1; headerIndex <= headers; headerIndex++) {
            String key = actions().findOneElementsText("(//div[@class='info-field-label q-mr-xs'])["+headerIndex+"]").replace(":", "");
            String value = actions().findOneElementsText("(//div[@class='info-field-value'])["+headerIndex+"]");

            timeDomainHeader.put(key, value);
        }
        return timeDomainHeader;
    }

    // ../project/10523/views/0e6725cf-873e-4b1a-9c28-593ea19020e5/transients
    public void clickOnTransientCell(int measuringPointColumn, int rowNumber) {
        actions().makeClick("//table/tbody[@class='q-virtual-scroll__content']/tr["+ rowNumber +"]/td["+ (measuringPointColumn + 1) +"]");
    }
}

