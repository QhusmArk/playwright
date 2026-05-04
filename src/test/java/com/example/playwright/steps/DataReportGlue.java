package com.example.playwright.steps;

import com.example.api.endpoints.MeasuringPointApi;
import com.example.api.endpoints.ReportApi;
import com.example.api.endpoints.SearchApi;
import com.example.api.models.ScheduledReport;
import com.example.api.models.blast.Blast;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.report.DataReport;
import com.example.api.models.report.Search;
import com.example.api.models.report.Transient;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.Randomizer;
import com.example.helpers.StatusAssesser.Status;
import com.example.helpers.TimeConverter;
import com.example.helpers.builders.BuilderFactory;
import com.example.helpers.builders.SearchBuilder;
import com.example.playwright.components.aside.Aside;
import com.example.playwright.components.aside.asideItems.listItems.DataReportItem;
import com.example.playwright.components.aside.asideItems.listItems.ScheduledReportItem;
import com.example.playwright.components.panels.scheduled_report.ScheduledReportMpPanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.view.*;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.ColourSchema;
import com.example.playwright.helpers.enums.IconType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.helpers.StatusAssesser.Status.CLICKABLE;
import static com.example.helpers.StatusAssesser.Status.DISABLED;
import static org.junit.jupiter.api.Assertions.*;

public class DataReportGlue extends BaseGlue {
    
    /**
     * When in Measuring Report, click on the top transient to go to TDA.
     */
    @And("I click on the uppermost transient")
    @And("I open time domain analysis")
    public void openTimeDomainAnalysis() {
        drPO.clickFirstMeasuringPointsShowButtonToGoToTransientAnalysis();
    }

    @Then("each measuring point has a graph panel in intervals chart")
    public void eachMeasuringPointHasAGraphPanelInIntervalsChart() {
        List<MeasuringPoint> measuringPoints = context().getMeasuringPoints();

        IntervalsChartView chartView = drPO.getIntervalChartReport();

        // top header counter and charts should be the same
        int expectedMpCount = measuringPoints.size();

        // Get the 'X of X measuring points have data' from the header
        String expansionHeaderBannerText = chartView.getDataViewStatus().getChartSectionHeader().getMpDataInformation().getText();

        // Get how many charts that's in the report view
        int actualMpCount = Integer.parseInt(expansionHeaderBannerText.substring(0, expansionHeaderBannerText.indexOf(" ")));
        int actualChartCount = chartView.getMeasuringPointDataList().size();

        assertTrue(expectedMpCount == actualMpCount
                && expectedMpCount == actualChartCount);

        List<String> expectedMpNames = measuringPoints.stream().map(MeasuringPoint::getName).toList();

        // Get each MpName from the chartSections
        List<String> actualChartHeaderTexts = chartView.getMeasuringPointDataList().stream()
                .map(mpData -> mpData.getMpChartSectionHeader().getMpNameFromChartSectionHeader())
                .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMpNames, actualChartHeaderTexts),
                () -> "Lists are not identical.");
    }

    @Then("each measuring point has a column in intervals table")
    public void eachMeasuringPointHasAColumnInIntervalsTable() {
        List<MeasuringPoint> measuringPoints = context().getMeasuringPoints();

        List<String> expected = measuringPoints.stream().map(MeasuringPoint::getName).toList();

        IntervalTableReport report = drPO.getIntervalTableReport();
        List<String> actualMpNames = report.getTable().getIntervalsTableHeaderMpNames();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expected, actualMpNames));
    }

    @Then("each measuring point has a column in transients table")
    public void eachMeasuringPointHasAColumnInTransientsTable() {
        List<MeasuringPoint> measuringPoints = context().getMeasuringPoints();

        // As TransientTable.mpHeader can contain mpDescription, we need to build matching strings
        List<String> expectedMpNameAndDescription = measuringPoints.stream()
                .map(mp -> {
                    String description = mp.getLocation().getDescription();
                    return (description != null)
                            ? mp.getName() + ",\n" + description
                            : mp.getName();
                })
                .toList();

        // Get the actual columns
        TransientTableReport report = drPO.getTransientTableReport();
        List<String> actualMpNames = report.getReportContent().getTransientTableHeaderMpNamesAndDescription();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMpNameAndDescription, actualMpNames));
    }

    @Then("there is a table with all blasts in the blast report")
    public void thereIsATableWithAllBlastsInTheBlastReport() {
        List<Blast> blasts = context().getBlasts();
        List<String> expectedBlasts = blasts.stream()
                .map(Blast::getBlastId)
                .toList();

        BlastReport report = drPO.getBlastReport();
        // For each row, get the first cell, and map it to a TableCell, and then get the text.
        List<String> actualBlasts = report.getReportContent().getContent().stream()
                .map(tableRow -> tableRow.getObjects(Table.TableCell.class).getFirst().getCellText())
                .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedBlasts, actualBlasts),
                "Lists are not identical.");
    }


    @Then("each measuring point has a row in blast journal table")
    public void eachMeasuringPointHasARowInBlastJournalTable() {
        List<MeasuringPoint> measuringPoints = context().getMeasuringPoints();

        List<String> expectedMpNames = measuringPoints.stream().map(MeasuringPoint::getName).toList();

        BlastJournalView blastJournalView = drPO.getBlastJournal();

        // Mp name share place with mpIcon
        List<String> actualMpNames = blastJournalView.getMpTable().getContent().stream()
                .map(tableRow -> tableRow.getStringSharedByIconByTableHeader(blastJournalView.getMpTable().getHeader(), "Name"))
                .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMpNames, actualMpNames),
                () -> "Lists are not identical.");
    }

    /**
     * Test that validate that cells in DataReport.MeasuringReport, contain correct data.
     */
    @Then("there is a table with all transients in the measuring report")
    public void thereIsATableWithAllTransientsInTheMeasuringReport() {
        List<MeasuringPoint> measuringPoints = context().getMeasuringPoints();

        MeasuringReportTableReport report = drPO.getMeasuringReportTableReport();

        String searchId = Navigate.getSearchIdFromUrl();
        List<Transient> transients = ReportApi.getData(context().getProject().getId(), searchId).getTransients();

        List<MeasuringPoint> mpsActiveDuringAnyTransient = new ArrayList<>(); // transient_time is after mp_start, and before mp_end

        // For every transient row, deduct which mps that was active when transient occurred
        for (Transient t : transients) {
            LocalDateTime transientTime = TimeConverter.parseToLDT(t.getDatetime(), "yyyy-MM-dd HH:mm:ss");

            // A Mp that's active during transient should exist in the table
            for (MeasuringPoint mp : measuringPoints) {
                LocalDateTime fromTime = TimeConverter.parseToLDT(mp.getDatetimeFrom(), "yyyy-MM-dd HH:mm");
                LocalDateTime toTime = TimeConverter.parseToLDT(mp.getDatetimeTo(), "yyyy-MM-dd HH:mm");

                boolean activeDuringTransient = TimeConverter.isBetween(transientTime, fromTime, toTime);
                if (activeDuringTransient) {
                    mpsActiveDuringAnyTransient.add(mp);
                }
            }
        }

        // Not yet in use...
//                List<Blast> blastsActiveDuringTransient = new ArrayList<>(); // transient_time is after blast_start, and before blast_end
//                // For every transient, deduct which blasts that was active when transient occurred
//                for (Transient t : transients) {
//                    LocalDateTime transientTime = LocalDateTime.parse(t.getDatetime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//                    // A Mp that's active during transient should be linked to a mp
//                    for (Blast blast : blasts) {
//                        Map<String, LocalDateTime> result = TimeConverter.calculateFromTo(blast.getDatetime(), blast.getTimeSpan());
//
//                        LocalDateTime fromTime = result.get("fromTime");
//                        LocalDateTime toTime = result.get("toTime");
//
//                        boolean activeDuringTransient = TimeConverter.isBetween(transientTime, fromTime, toTime);
//                        if (activeDuringTransient) {
//                            blastsActiveDuringTransient.add(blast);
//                        }
//                    }
//                }

        // Assert the rows match how expected
        int expectedRowsInTable = mpsActiveDuringAnyTransient.size();
        // Count the rows in the DataReport.MeasuringReport
        int actualRowsInTable = report.getTable().getContent().size();

        assertEquals(expectedRowsInTable, actualRowsInTable);

        Table.TableRow headerRow = report.getTable().getHeader();
        List<Table.TableRow> mpRows = report.getTable().getContent();

        // Every row "Time" should be equal to the time in the row's "Transient"
        mpRows.forEach(tableRow -> {
            String mpName = tableRow.getStringSharedByIconByTableHeader(headerRow, "Measuring point");
            String rowTransientTime = tableRow.getStringByTableHeader(headerRow, "Time");

            Navigate.assertUrlContain("measuring_report");

            // Open the transient in the leftmost link
            drPO.followShowLinkInMpReport(mpName, rowTransientTime);

            // Make sure the transient url is loaded, so that we can read TransientChartView
            Navigate.assertUrlContain("transients");

            TransientAnalysisReport transientChart = tcPO.getTransientView();
            String transientChartTimeStamp = transientChart.getTransientTime();

            assertEquals(rowTransientTime, transientChartTimeStamp,
                    () -> "rowTransientTime/transientChartTimeStamp: " + rowTransientTime + "/" + transientChartTimeStamp);

            drPO.useReturnArrow();
        });

        // MP without guide value should have '-' and warning, or being a Double.
        mpRows.forEach(tableRow -> {
            String mpName = tableRow.getStringSharedByIconByTableHeader(headerRow, "Measuring point");
            String guideValue = tableRow.getStringByTableHeader(headerRow, "Guide value (V10)");

            if (guideValue.contains("–")) {
                assertTrue(guideValue.contains("warning"));
            } else {
                MeasuringPoint matchingMp =
                        measuringPoints.stream()
                                .filter(mp -> mp.getName().equals(mpName)).findAny().get();
                assertEquals(matchingMp.getBlastProperties().getGuideValue(), Double.parseDouble(guideValue),
                        () -> "MpGuideValue/ReportGuideValue: " + matchingMp.getBlastProperties().getGuideValue() + "/" + Double.parseDouble(guideValue));
            }
        });

        // If no Guide Value, then no VCorr or Percentage
        mpRows.forEach(tableRow -> {
            String guideValue = tableRow.getStringByTableHeader(headerRow, "Guide value (V10)");
            if (guideValue.contains("–")) {
                String vCorr = tableRow.getStringByTableHeader(headerRow, "VCorr");
                String percentage = tableRow.getStringListInTableCellByTableHeader(headerRow, "Percentage").getFirst();

                assertEquals("–", vCorr,
                        () -> "expGuideValue/actGuideValue: " + "–" + "/" + vCorr);
                assertEquals("–", percentage,
                        () -> "expPercentage/actPercentage: " + "–"+ "/" + percentage);
            }
        });

        // If no Blast, then no VCorr, Percentage or Distance
        mpRows.forEach(tableRow -> {
            String blastName = tableRow.getStringByTableHeader(headerRow, "Blast");

            if (blastName.contains("–")) {
                String vCorr = tableRow.getStringByTableHeader(headerRow, "VCorr");
                String percentage = tableRow.getStringListInTableCellByTableHeader(headerRow, "Percentage").getFirst();
                String distance = tableRow.getStringByTableHeader(headerRow, "Distance");

                assertEquals("–", vCorr,
                        () -> "expGuideValue/actGuideValue: " + "–" + "/" + vCorr);
                assertEquals("–",  percentage,
                        () -> "expPercentage/actPercentage: " + "–"+ "/" + percentage);
                assertEquals("–", distance,
                        () -> "expDistance/actDistance: " + "–"+ "/" + distance);
            }
        });
    }

    @Then("the mp without guide value is added to table, but not showing any data")
    public void theMpWithoutGuideValueIsAddedToTableButNotShowingAnyData() {
        String expectedMpWithoutGuideValue = context().getMeasuringPoints().stream()
                .filter(mp -> mp.getBlastProperties() == null)  // Get the mp with no BlastProperties (ie without Guide Value)
                .toList().getFirst().getName();

        Table mpTable = drPO.getBlastJournal().getMpTable();
        Table.TableRow headerRow = mpTable.getHeader();
        String actualMpWithoutGuideValue = mpTable.getContent().stream()
                .filter(mpRow -> mpRow.getStringListInTableCellByTableHeader(headerRow, "Time").getFirst().equals("–"))
                .map(mpRow -> mpRow.getStringListInTableCellByTableHeader(headerRow, "Name").getFirst())
                .toList().getFirst();

        assertEquals(expectedMpWithoutGuideValue, actualMpWithoutGuideValue,
                () -> "expectedMpWithoutGuideValue/actualMpWithoutGuideValue: " + expectedMpWithoutGuideValue + "/" + actualMpWithoutGuideValue);
    }

    @And("table meta data {string} has {string}")
    public void tableMetaDataSensorSerialNoHasSensorTypeSensorSerial(String metaData, String expectedValue) {
        String mpSensorType = context().getMeasuringPoints().getFirst().getSensorType();
        String mpSensorSerial = context().getMeasuringPoints().getFirst().getSensors().getFirst().getSerial();

        // Use mp data to build the expected value
        String expectedMetaDataValue = expectedValue.replace("sensorType", mpSensorType)
                                                    .replace("sensorSerial", mpSensorSerial);

        // User mpName to locate in which column we shall look for value
        String mpName = context().getMeasuringPoints().getFirst().getName();

        TransientTableReport report = drPO.getTransientTableReport();
        String actualMetaDataValue = report.getReportContent().getMetaDataValueByMpNameAndMetaDataKey(mpName, metaData);

        assertEquals(expectedMetaDataValue, actualMetaDataValue,
                () -> "expectedMetaDataValue/actualMetaDataValue: " + expectedMetaDataValue + "/" + actualMetaDataValue);
    }

    @Then("Transient view has Time Domain Analysis")
    public void getTimeDomainAnalysis() {
        TransientAnalysisReport transientView = tcPO.getTransientView();

        String actualHeader = transientView.getTdaChartSectionHeader().getMainText();
        assertEquals("Time domain analysis", actualHeader,
                () -> "The report did not have the expected header.");
    }

    @Then("there are no reports in aside")
    public void listContainsNoDataReports() {
        // Give counter some time to adjust if we recently removed items from list.
        List<DataReportItem> reports = asidePO.getAside().getDataReportItems();
        assertEquals(0, reports.size(), "Report List was not empty.");
    }

    @When("I delete all data reports")
    public void deleteAllReports() {
        //Get how many reports there are in this filter view (here we assume it's 'All reports'
        int reportsToBeDeleted = asidePO.getDisplayCounter();
        drPO.deleteThisManyReports(reportsToBeDeleted);
    }

    @When("I {string} report")
    public void actionTheReport(String action) {
        if ("save the temporary interval".equals(action)) {
            // Try to find the temporary report in the list
            Aside aside = asidePO.getAside();

            DataReportItem foundReport = aside.getDataReportItems().stream()
                    .filter(report -> report.getName().contains("Temporary report"))
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalStateException("Could not find report: " + "Temporary report"));

            // Generate a new name for the report
            String newName = Randomizer.randomString(10);
            asidePO.openCompactListItemMenu(foundReport.getName());

            drPO.selectSaveMenuAndSaveDataReport(newName);

            // Update context with the new report
            updateContextWithModifiedReport(newName);

        } else if ("share the saved".equals(action)) {
            // Use the saved report's name from the context
            String savedSearchName = context().getSearches().getFirst().getName();
            asidePO.openCompactListItemMenu(savedSearchName);

            drPO.shareDataReport();

            // No name change occurs here, but we might need to update context if sharing changes report details
            updateContextWithModifiedReport(savedSearchName);
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    private void updateContextWithModifiedReport(String searchName) {
        List<Search> savedSearches = SearchApi.getSearches(context().getProject().getId());
        Optional<Search> savedSearch = savedSearches.stream()
                .filter(search -> search.getName() != null && search.getName().equals(searchName))
                .findFirst();
        savedSearch.ifPresent(search -> context().replaceSearch(0, search));
    }

    @Then("the {string} report is found in list")
    public void theReportIsFoundInList(String reportType) {
        if (!reportType.equals("shared") && !reportType.equals("saved")) {
            throw new IllegalArgumentException("Invalid report type: " + reportType);
        }

        IconType expectedIconType = ("saved".equalsIgnoreCase(reportType))
                ? IconType.SAVE
                : IconType.PEOPLE;

        // The latest changed report in context is the first in the list
        String expectedName = context().getSearches().getFirst().getName();

        Aside aside = asidePO.getAside();
        // Now try to find the report

        DataReportItem foundReport = aside.getDataReportItems().stream()
                .filter(report -> report.getName().equals(expectedName))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Could not find report: " + expectedName));

        String actualName = foundReport.getMainText();

        assertEquals(expectedName, actualName,
                () -> "expectedName/actualName: " + expectedName + "/" + actualName);

        boolean hasListItemIcon = foundReport.getListItemIcon() != null;
        if (hasListItemIcon) {
            IconType savedSharedIcon = foundReport.getListItemIcon().getType();

            assertEquals(expectedIconType, savedSharedIcon,
                    () -> "expectedIconType/savedSharedIcon: " + expectedIconType + "/" + savedSharedIcon);
        }
    }

    @When("I create a rolling {string} interval report")
    public void createRollingIntervalReport(final String reportDuration) {
        // Wait for automatic transient report is created
        asidePO.validateNoSpinner();

        drPO.createIntervalReportForAllMeasuringPoints(reportDuration);
        PlaywrightActions.sleep(3);
    }

    @When("I create a static time interval report")
    public void iCreateAStaticTimeIntervalReport() {
        Navigate.project(context().getProject().getId())
                .views()
                .create()
                .get();

        drPO.createIntervalReportForAllMeasuringPoints("static");
    }

    @When("the report can be updated")
    public void updateTempIntervalReport() {
        Aside aside = asidePO.getAside();

        DataReportItem foundReport = aside.getDataReportItems().stream()
                .filter(report -> report.getName().contains("Temporary report"))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Could not find report: " + "Temporary report"));

        asidePO.clickOnListItemMenuAndSelect(foundReport.getName(), "Update");
    }

    @When("Reports are filtered on {string}")
    public void filter(final String filterText) {
        filterPO.changeFilter(filterText);
    }

    @Then("Only {string} reports are shown in the left list")
    public void getFilteredResults(final String filterText) {
        boolean textMatches = drPO.validateAsideItemsHasFilterText(filterText);
        assertTrue(textMatches);
    }

    @Then("The DataReport Blast Tab is clickable")
    public void theDataReportBlastTabIsClickable() {
        IntervalsChartView report = drPO.getIntervalChartReport();
        Tab blastTab = report.getReportTabs().stream()
                .filter(tab -> tab.getText().equals("Blasts"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Blast tab found"));

        Status actual = blastTab.getStatus();
        assertEquals(CLICKABLE, actual);
    }

    @Then("The DataReport Blast Tab is not present")
    public void theDataReportBlastTabIsNotPresent() {
        IntervalsChartView report = drPO.getIntervalChartReport();
        // Find "Blasts" tab if present
        Tab blastTab = report.getReportTabs().stream()
                .filter(tab -> "Blasts".equals(tab.getText()))
                .findFirst()
                .orElse(null);

        // Verify it is null (no such tab exists)
        assertNull(blastTab, "Blasts tab should not be present.");
    }

    @Then("The DataReport Interval tab is not clickable")
    public void theDataReportIntervalTabIsNotClickable() {
        TransientTableReport report = drPO.getTransientTableReport();

        Tab intervalsTab = report.getReportTabs().stream()
                .filter(tab -> tab.getText().equals("Intervals"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No Intervals tab found"));

        Status actual = intervalsTab.getStatus();
        assertEquals(DISABLED, actual);
    }

    @Then("The display counter matches context")
    public void theDisplayCounterMatchesContext() {
        DataReport report = ReportApi.getData(context().getProject().getId(), context().getSearches().getFirst().getId());
        int expectedIntervals = report.getIntervals().size();

        //due to dynamic DOM we cannot count items in table if there are more than 30 rows.
        int actualIntervals = drPO.getCounter();
        assertEquals(expectedIntervals, actualIntervals, "Intervals in context did not match counter.");
    }

    @Then("the interval graph header contains {string} or {string}")
    public void theIntervalGraphHeaderContainsAOrB(String arg0, String arg1) {
        IntervalsChartView icv = drPO.getIntervalChartReport();
        String chartStandard = icv.getMeasuringPointDataList().getFirst().getMpChartSectionBody().getMetaData().get("Standard");
        assertTrue(chartStandard.contains(arg0) || chartStandard.contains(arg1));
    }

    @Then("there is a temporary interval report in the list")
    public void thereIsATemporaryIntervalReportInTheList() {
        // Check if there is an interval report in list
        assertTrue(drPO.checkListFor("interval"), "No interval report could be found in list.");
    }

    @Then("interval chart meta data for {string} should display {string} and {string}")
    public void intervalChartHeaderShouldDisplayDBCorrection(String mpName, String expectedHeader, String expectedValue) {
        Navigate.project(context().getProject().getId())
                .view(context().getLastSearch().getId())
                .intervals()
                .chart()
                .get();

        IntervalsChartView report = drPO.getIntervalChartReport();

        List<IntervalsChartView.MeasuringPointData> mpCharts = report.getMeasuringPointDataList();

        for (IntervalsChartView.MeasuringPointData mpChart : mpCharts) {

            // Check chart with actual header matches expected header
            String mpNameInChartHeader = mpChart.getMpChartSectionHeader().getMpNameFromChartSectionHeader();
            if (mpNameInChartHeader.contains(mpName)) {

                Map<String, String> headerList = mpChart.getMpChartSectionBody().getMetaData();

                // Validate that expected header key/value exist
                boolean matches = expectedValue.equals(headerList.get(expectedHeader));

                assertTrue(matches,
                        "headerList should contain a map with '"+expectedHeader+"' key and '"+expectedValue+"' value");
            }
        }
    }

    @Then("interval table meta data for {string} should display {string} and {string}")
    public void intervalTableHeaderForCShouldDisplayDBCorrAnd(String expectedMpName, String expectedHeader, String expectedValue) {
        Navigate.project(context().getProject().getId())
                .view(context().getLastSearch().getId())
                .intervals()
                .table()
                .get();

        IntervalTableReport report = drPO.getIntervalTableReport();

        // Use mpName and metadata as coordinates to get the value
        String actualValue = report.getTable().getMetaDataValueByMpNameAndMetaDataKey(expectedMpName, expectedHeader);

        assertEquals(expectedValue, actualValue,
                () -> "expectedValue/actualValue: " + expectedValue + "/" + actualValue);
    }

    @Then("transient table metadata for {string} should display {string} and {string}")
    public void transientTableMetaDataForC50ShouldDisplayDBCorrAndDB(String mpName, String metaDataKey, String expectedValue) {
        Navigate.project(context().getProject().getId())
                .view(context().getLastSearch().getId())
                .transients()
                .get();

        TransientTableReport report = drPO.getTransientTableReport();

        // Use the columnHeader and metaDataRow as coordinates to get the cell value
        String actualValue = report.getReportContent().getMetaDataValueByMpNameAndMetaDataKey(mpName, metaDataKey);
        assertEquals(expectedValue, actualValue,
                () -> "expectedValue/actualValue: " + expectedValue + "/" + actualValue);
    }

    @And("transient table measured values for {string} should be {string} dB compared to {string}")
    public void transientTableMeasuredValuesForMp1ShouldBeOtherComparedToMp2(String mp1, String value, String mp2) {
        TransientTableReport report = drPO.getTransientTableReport();

        // Get the first transient row and get the timestamp
        String topTransientTimestamp = report.getReportContent().getContent().getFirst().getStringAtPosition(0);

        // Get the value from mp1
        String dBCorrectedValue = report.getReportContent().getContentValueByMpNameAndTimestampKey(mp1, topTransientTimestamp);
        assertEquals("LAF: 68.20 dB", dBCorrectedValue,
                () -> "expectedValue/dBCorrectedValue: " + "LAF: 68.20 dB" + "/" + dBCorrectedValue);

        // Get the value from mp2
        String non_dBCorrectedValue = report.getReportContent().getContentValueByMpNameAndTimestampKey(mp2, topTransientTimestamp);
        assertEquals("LAF: 73.2 dB", non_dBCorrectedValue,
                () -> "expectedValue/non_dBCorrectedValue: " + "LAF: 73.2 dB" + "/" + non_dBCorrectedValue);
    }

    @And("interval table measured values for {string} should be {string} dB compared to {string}")
    public void intervalTableMeasuredValuesForCShouldBeDBComparedToC(String mp1, String value, String mp2) {
        double correctionValue = Double.parseDouble(value);
        IntervalTableReport report = drPO.getIntervalTableReport();

        // Use mpName and metadata as coordinates to get the value
        String actualValue1 = report.getTable().getIntervalTableIntervalCellValue(mp1, "2024-05-20 16:30:00", "LAeq");
        double correctedLAeq = Double.parseDouble(actualValue1.replace("LAeq: ", ""));

        String actualValue2 = report.getTable().getIntervalTableIntervalCellValue(mp2, "2024-05-20 16:30:00", "LAeq");
        double unCorrectedLAeq = Double.parseDouble(actualValue2.replace("LAeq: ", ""));

        assertEquals(correctedLAeq - correctionValue, unCorrectedLAeq,
                "correctedLAeq - correctionValue/actualValue: " + (correctedLAeq - correctionValue) + "/" + unCorrectedLAeq);
    }

    @And("I select the first transient")
    public void iSelectTableFirstTransientInFirstColumn() {
        drPO.openTransient(1,1);
    }

    @When("this operator is selected, first and last sample is {isPresent}")
    public void thisOperatorIsSelectedFirstAndLastSampleIsPresent(boolean isPresent, DataTable table) {
        List<String> operators = table.row(0);

        operators.forEach(newOperator -> {
            // Select the newOperator and click Apply
            System.out.println("Selecting '" +newOperator+"' and expecting first/last sample existence to be " + isPresent);

            PlaywrightActions.sleep(1);
            drPO.selectFrequencyDomainOperator(newOperator);
            PlaywrightActions.sleep(1);

            TransientAnalysisReport report = tcPO.getTransientView();
            PlaywrightActions.sleep(3);

            if (!report.getPanelHeader().getHeaderText().contains("(test-auto-project)")) {
                throw new IllegalStateException("Wrong report was opened.");
            }

            ChartSectionBody fda = report.getFda();

            // Make sure the new newOperator is successfully set
            Optional<Dropdown> actualOperator = fda.getDropdowns().stream()
                    .filter(dropdown -> "Operator".equals(dropdown.getHeader()))
                    .findFirst();

            // Make sure operator dropdown is present, and that the new operator is successfully set
            if (actualOperator.isPresent()
                    && actualOperator.get().getText().equals(newOperator)) {

            } else {
                System.out.println("Expected operator "+newOperator+" but actual operator was "+actualOperator.get().getText()+".");
                System.out.println("Trying again...");

                drPO.selectFrequencyDomainOperator(newOperator);
                fda = report.getFda();
            }

            if (!actualOperator.get().getText().equals(newOperator)) {
                throw new IllegalStateException("Expected operator "+newOperator+" but actual operator was "+actualOperator.get().getText()+".");
            }

            List<InputField> inputFields = fda.getInputFields();

            boolean hasFirstSample = inputFields != null &&
                    inputFields.stream()
                            .anyMatch(inputField -> "First sample [s]".equals(inputField.getHeader()));
            assertEquals(isPresent, hasFirstSample);

            boolean hasLastSample = inputFields != null &&
                    inputFields.stream()
                            .anyMatch(inputField -> "Last sample [s]".equals(inputField.getHeader()));
            assertEquals(isPresent, hasLastSample);
        });
    }

    @Then("frequency domain analysis show {string} as operator")
    public void frequencyDomainAnalysisShowAsOperator(String expectedOperator) {
        ChartSectionBody fda = tcPO.getTransientView().getFda();

        // Try to get the operator that matches expectedOperator
        Optional<Dropdown> actualOperator = fda.getDropdowns().stream()
                .filter(dropdown -> "Operator".equals(dropdown.getHeader()))
                .filter(dropdown -> expectedOperator.equals(dropdown.getText()))
                .findFirst();

        assertTrue(actualOperator.isPresent());
    }

    @And("I update the transient report")
    public void iUpdateTheTransientReport() {
        drPO.updateReportFromTransientsView();
    }

    @And("I am redirected to a new temporary data report")
    public void iAmRedirectedToANewTemporaryDataReport() {
        String searchId = Navigate.getSearchIdFromUrl();
        Search search = SearchApi.getSearch(context().getProject().getId(), searchId);

        // Make sure the new search is not shared, and not saved
        assertFalse(search.getShared());
        assertNull(search.getName());

        // Url is loaded faster than the search creation, wait for the search
        asidePO.validateNoSpinner();
    }

    @Then("save button should be {string}")
    public void saveButtonIs(String buttonState) {
        boolean expectedButtonDisabled = buttonState.equals("disabled");

        boolean actualButtonDisabled = drPO.getSaveReportButtonOpacity().equals("0.7");
        assertEquals(expectedButtonDisabled, actualButtonDisabled, "Save button state do not match.");
    }

    @Then("the header has not {string}")
    public void timeDomainChartHasNotThisHeader(String expectedMetaData) {
        TransientAnalysisReport transientAnalysisReport = tcPO.getTransientView();
        Map<String, String> actualMetaData = transientAnalysisReport.getTda().getMetaData();

        assertFalse(actualMetaData.containsKey(expectedMetaData),
                "MetaData should not contain key " + expectedMetaData);
    }

    @And("report duration is {string} to {string}")
    public void reportDurationIs(String startTime, String endTime) {
        LocalDateTime expectedStart = TimeConverter.parseTimeDescriptionToLDT(startTime);
        LocalDateTime expectedEnd = TimeConverter.parseTimeDescriptionToLDT(endTime);

        IntervalsChartView report = drPO.getIntervalChartReport();

        String reportDuration = report.getReportDuration();
        LocalDateTime actualStart = TimeConverter.getReportTime("startTime", reportDuration);
        LocalDateTime actualEnd = TimeConverter.getReportTime("endTime", reportDuration);

        if (startTime.contains("now")) {
            // Take into consideration that the test can run over minute mark, and then expected is one minute behind.
            assertTrue(expectedStart.equals(actualStart) || expectedStart.minusMinutes(1).equals(actualStart));
        } else {
            assertEquals(expectedStart, actualStart);
        }

        assertEquals(expectedEnd, actualEnd);
    }

    @Then("time frame for the new mp is {string} and {string}")
    public void timeFrameForTheNewMpIsTodayAndNow(String date, String time) {
        LocalDateTime expectedStartDate = TimeConverter.parseTimeDescriptionToLDT(date);
        LocalDateTime expectedStartTime = TimeConverter.parseTimeDescriptionToLDT(time);

        TimeFrame timeFrame = drPO.getTimeFrame();
        String fromDate = timeFrame.getFromDate().getValue();
        String fromTime = timeFrame.getFromTime().getValue();

        // Get the value in date and time and put them together to parse into a LDT for comparision
        LocalDateTime actualStartDateTime = TimeConverter.parseDateAndTime(fromDate + " " + fromTime);

        // Assert the date is correct
        assertEquals(expectedStartDate.toLocalDate(), actualStartDateTime.toLocalDate(),
                () ->  "The preloaded date in mp create is not current date.");

        // Assert time is correct
        // Take into consideration that the test can run over minute mark, and then expected is one minute behind.
        boolean isWithin = TimeConverter.isWithinThisManySeconds(expectedStartTime, actualStartDateTime, 90);
        assertTrue(isWithin,
                () -> "The preloaded time in mp create is not current time.");
    }

    @And("create an {string} report by api")
    public void createAReportByApi(String reportType) {
        MeasuringPoint c50_1 = MeasuringPointApi.getMeasuringPointByName(context().getProject().getId(), "C50-1");
        MeasuringPoint c50_2 = MeasuringPointApi.getMeasuringPointByName(context().getProject().getId(), "C50-2");

        SearchBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.SEARCH,
                SearchBuilder.class);

        builder
                .givenMeasurePoint()
                    .thenMeasuringPointId(c50_1.getId())
                    .thenMeasuringPointId(c50_2.getId())
                .givenDataTypes()
                .withDateTimeFrom("2024-05-20 00:00:00")
                .withDateTimeTo("2024-05-21 00:00:00");

        if (reportType.equals("interval")) {
            builder
                    .thenDataTypeInterval(true)
                    .thenDataTypeTransient(true)
                    .thenDataTypeBlast(true)
                    .thenDataTypeMonon(false);
        } else if (reportType.equals("transient")) {
            builder
                    .thenDataTypeInterval(false)
                    .thenDataTypeTransient(true)
                    .thenDataTypeBlast(true)
                    .thenDataTypeMonon(false);
        }
        builder.build();

        Search search = SearchApi.createSearchAndWaitForFinished(context().getProject().getId(), builder.buildJson(), 60);
        context().addSearch(search);

        String jsonBody = "{\"name\":\"ReportName\"}";
        SearchApi.updateSearch(context().getProject().getId(), search.getId(), jsonBody);
    }

    @When("I close the report")
    public void iCloseTheReport() {
        drPO.closeReport();
    }

    @Then("intervals with {string} should display {string} benchmark {string}")
    public void intervalsWithSBRAShouldDisplayMandatoryBenchmark(String expectedStandard, String expectedStatus, String expectedBenchmark) {
//        IntervalsChartView icv = drPO.getDataReport(INTERVALS_CHART);
//        System.out.println(icv);
//
//        boolean reportShowExpectedStandard = icv.getMeasuringPointDataList().getFirst().getMpChartSection().getStandardSelector().getText().equals(expectedStandard);
//        // If report do not show expected standard
//        if (!reportShowExpectedStandard) {
//            // Select other standard in report
//            drPO.selectStandardInIntervalChartReport(expectedStandard);
//            // Now read the datareport again, to see that the report show expected standard
//            icv = drPO.getDataReport(INTERVALS_CHART);
//            reportShowExpectedStandard = icv.getMeasuringPointDataList().getFirst().getMpChartSection().getStandardSelector().getText().equals(expectedStandard);
//
//            // If not, abort test
//            if (!reportShowExpectedStandard) {
//                throw new IllegalStateException("Expected standard not in report.");
//            }
//        }
//
//        // Get the one and only mp in the report
//        String actualFreqDataBenchmarkDropdownText = icv.getMeasuringPointDataList().getFirst().getFreqDataExpansionHeader().getFrequencyDataBenchmarkSelector().getText();
//
//        // Assert that the benchmark dropdown has the text of expectedStandard
//        assertEquals(expectedBenchmark, actualFreqDataBenchmarkDropdownText);
//
//        // Assert that the benchmark dropdown is as expected
//        Status actualStatus = icv.getMeasuringPointDataList().getFirst().getFreqDataExpansionHeader().getFrequencyDataBenchmarkSelector().getStatus();
//        switch (expectedStatus) {
//            case "mandatory" -> assertEquals(UNCLICKABLE, actualStatus);
//            case "clickable" -> assertEquals(CLICKABLE, actualStatus);
//            default -> throw new IllegalStateException("Unexpected expectedStatus: " + expectedStatus);
//        }
    }

    // SDR create
    @When("I verify report formats:")
    public void verifyReportFormats(DataTable table) {
        // rows: ReportType | Formats
        table.asMaps().forEach(row -> {
            String type = row.get("ReportType");
            drPO.selectDropdownByHeader("Type", type);

            List<String> expectedFormats = Randomizer.parse(row.get("Formats"));       // with delimiter ;
            List<String> actualFormats = drPO.getDropdownContent("Format");

            AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedFormats, actualFormats);
        });
    }

    // SDR create
    @And("I verify report content:")
    public void iVerifyReportContent(DataTable table) {
        table.asMaps().forEach(row -> {
            // Set report type
            String type = row.get("ReportType");
            drPO.selectDropdownByHeader("Type", type);

            // Set format export
            String format = row.get("Format");
            drPO.selectDropdownByHeader("Format", format);

            List<String> expectedContent = Randomizer.parse(row.get("Content"));
            // Read the content of Content dropdown
            List<String> actualContent = drPO.getDropdownContent("Content");

            boolean listsEqual = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedContent, actualContent);
            assertTrue(listsEqual);
        });
    }

    // SDR create
    @And("I verify schedule options:")
    public void iVerifyScheduleOptions(DataTable table) {
        table.asMaps().forEach(row -> {
            // Set report type
            String repeat = row.get("Repeat");
            drPO.selectDropdownByHeader("Repeat", repeat);

            List<String> expectedContent = Randomizer.parse(row.get("Send on"));
            // Read the content of Content dropdown
            List<String> actualContent = drPO.getDropdownContent("Send on");

            boolean listsEqual = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedContent, actualContent);
            assertTrue(listsEqual);
        });
    }

    @And("select SDR tab {string}")
    public void selectSDRTab(String tabName) {
        drPO.clickOnItem("//form //*[text()='"+tabName+"']");

    }

    // SDR create, on General tab
    @And("SDR have limit on {int} measuring points")
    public void sdrHaveLimitOnMeasuringPoints(int maxMpInSdr) {
        drPO.setValueToInput("//input[@data-qa-id='name']", Randomizer.randomString(4));
        selectSDRTab("Measuring points");
        PlaywrightActions.sleep(3);

        // Check the first maxMpInSdr measuring points
        for (int c = 1; c <= maxMpInSdr; c++) {
            String checkboxPath = "//form//tbody/tr["+c+"] //div[@role='checkbox']";
            drPO.tickCheckbox(checkboxPath);
        }

        ScheduledReportMpPanel sdr = srPO.getScheduledReportMpPanel();

        // Fetch the 26th row of Mp. It should be disabled as the previous 25 is checked
        Table.TableRow rowOfFirstDisabled = sdr.getMpTable().getContent().get(maxMpInSdr);
//        Icon checkbox = (Icon) rowOfFirstDisabled.getObjects().getFirst();
        Checkbox checkbox = (Checkbox) rowOfFirstDisabled.getObjects().getFirst();
        Status status = checkbox.getStatus();

        assertEquals(DISABLED, status);
    }

    @Then("I expect aside {string} to show correct status colour")
    public void iExpectAsideToShowCorrectStatusColour(String asideSize) {
        List<ScheduledReport> expectedSDR = ReportApi.getSDRs(context().getProject().getId());

        Aside aside = asidePO.getAside();

        if (asideSize.equals("COMPACT")) {

            int expectedSdrCount = expectedSDR.size();
            int actualSdrCount = aside.getAsideItems().size();
            assertEquals(expectedSdrCount, actualSdrCount);

            expectedSDR.forEach(sdr -> {

                // Locate the asideItem with same name as expectedSDR
                ScheduledReportItem itemMatchingOnName = aside.getScheduleReportItems().stream()
                        .filter(actualSdr -> actualSdr.getMainText().equals(sdr.getName()))
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalStateException("No ScheduledReportItem found matching: " + sdr.getName()));

                boolean isDisabled = sdr.getDisabled();
                ColourSchema leftIconColour = itemMatchingOnName.getLeftIcon().getColour();
                if (isDisabled) {
                    assertEquals(ColourSchema.DISABLED, leftIconColour);
                } else {
                    assertEquals(ColourSchema.PRIMARY, leftIconColour);
                }
            });

        } else if (asideSize.equals("MEDIUM") || asideSize.equals("FULL")) {

            int expectedSdrCount = expectedSDR.size();
            int actualSdrCount = aside.getTable().getContent().size();
            assertEquals(expectedSdrCount, actualSdrCount);

            expectedSDR.forEach(sdr -> {
                String sdrName = sdr.getName();

                Table.TableRow headerRow = aside.getTable().getHeader();

                // Locate the tableRow with same name as expectedSDR
                Table.TableRow rowMatchingOnName = aside.getTable().getContent().stream()
                        .filter(actualSdr -> actualSdr.getTableCellByTableHeader(headerRow, "Name").getCellText().equals(sdrName))  //todo: header är "Name\narrow_upward",
                        .findFirst()
                        .orElseThrow(
                                () -> new IllegalStateException("No ScheduledReportItem found matching: " + sdr.getName()));

                boolean isDisabled = sdr.getDisabled();
                ColourSchema leftIconColour = rowMatchingOnName.getTableCellByTableHeader(headerRow, "Name").getCellIcon().getColour();
                if (isDisabled) {
                    assertEquals(ColourSchema.DISABLED, leftIconColour);
                } else {
                    assertEquals(ColourSchema.PRIMARY, leftIconColour);

                }
            });
        } else {
            throw new IllegalArgumentException("Unsupported aside size: " + asideSize);
        }
    }

    @Then("it is possible to open a {string} transient")
//    public void itIsPossibleToOpenATransient(String type) {
//
//        openTransientForThisType(type);
//
//        // Make sure the TDA/FDA can be read
//        TransientAnalysisReport transientChart = tcPO.getTransientView();
//    }
    public void itIsPossibleToOpenATransient(String type) {
        int projectId = context().getProject().getId();
        String searchId = context().getLastSearch().getId();

        // First navigate to /view page so we reset browser cache
        Navigate.project(projectId)
                .view(searchId)
                .get();

        String measuringPointId = context().getMeasuringPointIdForThisType(type);

        // Find the first transient that contain the mp-id, and then get the first channel first analysis url
        DataReport report = context().getReports().getFirst();
        String analysisId = getFirstTransientAnalysisIdForThisMp(report, measuringPointId);

        // Use the url to navigate to the analysis page
        Navigate.project(projectId)
                .view(searchId)
                .transients(Integer.parseInt(measuringPointId), analysisId)
                .get();

        // Make sure the TDA/FDA can be read
        TransientAnalysisReport transientChart = tcPO.getTransientView();

        String transientChartHeader = transientChart.getTda().getChartsMetadata().getFirst();
        if (! transientChartHeader.contains("Time:")) {
            throw new IllegalStateException("Transient metadata has to contain millisecond time, but was: " + transientChartHeader); // from SSD-2477, eg. Time: 2025-11-04 00:52:25.273
        }
    }

    /**
     *
     * @return The first channels transientUrl in the oldest transient found in the report for this measuringPoint.
     */
    private String getFirstTransientAnalysisIdForThisMp(final DataReport report, final String measuringPointId) {

        return report.getTransients().stream()
                .map(tr -> tr.getMeasuringPoints().get(measuringPointId))
                .filter(Objects::nonNull)
                .flatMap(inner -> inner.getTransients().stream())
                .map(Transient.TransientData::getUrl)
                .filter(url -> url != null && !url.isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No url found for mpId: " + measuringPointId));
    }

    private void openTransientForThisType(String type) {
        int projectId = context().getProject().getId();
        List<MeasuringPoint> mps = MeasuringPointApi.getMeasuringPoints(projectId);

        // Find the Mp that uses 'type' as sensor
        MeasuringPoint mpWithThisSensor = mps.stream()
                .filter(mp -> mp.getSensorType().equals(type))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("No matching mp found for " + type)
                );

        String measuringPointId = String.valueOf(mpWithThisSensor.getId());

        String searchId = context().getLastSearch().getId();

        // Find the first transient that contain the mp-id, and then get the first channel first analysis url
        DataReport report = ReportApi.getData(projectId, searchId);
        String analysisId = report.getTransients().stream()
                .map(tr -> tr.getMeasuringPoints().get(measuringPointId))
                .filter(Objects::nonNull)
                .flatMap(inner -> inner.getTransients().stream())
                .map(Transient.TransientData::getUrl)
                .filter(url -> url != null && !url.isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No url found for mpId: " + measuringPointId));

        // First navigate to /views page so we clear browser cache
        Navigate.project(projectId)
                .view(searchId)
                .get();

        // Use the url to navigate to the analysis page
        Navigate.project(projectId)
                .view(searchId)
                .transients(Integer.parseInt(measuringPointId), analysisId)
                .get();
    }

    @When("I open the vibration report")
    public void iOpenTheVibrationReport() {
        Navigate.project(context().getProject().getId())
                .view(context().getSearches().getFirst().getId())
                .vibReport()
                .get();
    }

    @Then("Vmax and Vper is as expected")
    public void vmaxAndVperIsAsExpected() {
        VibrationReportView report = drPO.getVibrationReportView();
    }

    @Then("meatball menu gives")
    public void meatballMenuGives(DataTable table) {
        List<String> expectedMenuOptions = table.row(0);

        drPO.clickButton("meatball");

        List<String> actualMenuOptions = drPO.getMenuOptions().stream()
                .map(MenuOption::getText)
                .toList();

        boolean isAlike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMenuOptions, actualMenuOptions);
        assertTrue(isAlike);
    }

    @Then("max- and vper calculations are correct")
    public void maxAndVperCalculationsAreCorrect() {
        VibrationReportView report = drPO.getVibrationReportView();

        // Max
        List<String> expectedMaxValuesTs1 = List.of("0.22", "0.18", "0.12", "0.22");

        Table dateTable = report.getMpChartSectionBody().getVibrationReportDateTable();

        List<String> tuesdayRowValues = dateTable.getContent().get(1).getAllValuesAsString();
        List<String> actualMaxValuesTs1 = List.of(
                tuesdayRowValues.get(2),    // Vmax
                tuesdayRowValues.get(3),    // V
                tuesdayRowValues.get(4),    // L
                tuesdayRowValues.get(5));   // T

        boolean vmaxAlike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMaxValuesTs1, actualMaxValuesTs1);
        assertTrue(vmaxAlike);

        // Vper
        List<String> expectedTs1Values = List.of("0.03", "0.01", "0.05");

        Table timeslotTable = report.getMpChartSectionBody().getVibrationReportTimeslotTable();
        List<String> ts1Values = timeslotTable.getContent().getFirst().getAllValuesAsString();

        List<String> actualTs1Values = List.of(
                ts1Values.get(4),    // Vper V
                ts1Values.get(5),    // Vper L
                ts1Values.get(6));   // Vper T

        boolean vperAlike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedTs1Values, actualTs1Values);
        assertTrue(vperAlike);
    }

    @Then("the report contains channels")
    public void theReportContainsChannels(DataTable table) {
        List<String> expectedChannels = table.row(0);

        IntervalsChartView report = drPO.getIntervalChartReport();

        List<String> actualChannels = report.getMeasuringPointDataList().getFirst().getMpChartSectionBody().getChartToggles().stream()
                .map(ToggleField::getSideText)
                .toList();

        boolean isAlike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedChannels, actualChannels);
        assertTrue(isAlike);
    }
}
