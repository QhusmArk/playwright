package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.DataReportCreatePanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.view.*;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.ReportType;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.playwright.helpers.enums.IconType.EXPANDED;
import static com.example.playwright.helpers.enums.ReportType.*;
import static com.example.playwright.pageObjects.AsidePO.*;

public class DataReportPO extends CommonPO {

    AsidePO asidePO =  new AsidePO();

    // Create data report
    static final String DROP_DOWN_REPORT_TYPE = "(//form //i[text()='arrow_drop_down'])[1]";
    static final String DROP_DOWN_REPORT_TIME = "(//form //i[text()='arrow_drop_down'])[2]";
    static final String SWITCH_ON_ACTIVE = "(//form //div[@role='switch'])[1]";
    static final String SWITCH_SELECT_ALL = "(//form //div[@role='switch'])[2]";
    static final String BUTTON_CREATE = "//form//*[text()='Create']";

    //  Dialog
    private static final String DIALOG_I_UNDERSTAND = "//div[@role='checkbox']";
    private static final String DIALOG_CANCEL = "//*[@data-qa-id='cancel']";
    private static final String DIALOG_CONFIRM = "//*[@data-qa-id='confirm']";
    private static final String DIALOG_NAME_INPUT = "//div[@class='q-card size q-pt-sm']//input[@type='text']";

    // Interval Tab Table elements
    private static final String INTERVAL_TABLE_DISPLAY_COUNTER = "//div[@class='q-table__bottom row items-center']";

    /**
     * Starts at url .../project/id/views/create and ends when report creation is finished.
     * @param reportDuration If 'static' then reportDuration will be 'present day 00:00 to next day 00:00'.
     */
    public void createIntervalReportForAllMeasuringPoints(final String reportDuration) {
        // Click drop down for report duration
        actions().makeClick(DROP_DOWN_REPORT_TIME);

        switch (reportDuration) {
            case "24 hours" -> actions().makeClick("//*[@data-qa-id='Rolling 24 hours']");
            case "7 days" -> actions().makeClick("//*[@data-qa-id='Rolling 7 days']");
            case "28 days" -> actions().makeClick("//*[@data-qa-id='Rolling 28 days']");
            case "static" -> actions().makeClick("//*[@data-qa-id='Static time frame']");
            case "live" -> actions().makeClick("//*[@data-qa-id='live']");
        }

        // Click slider so all Measuring Points get selected
        actions().makeClick(SWITCH_SELECT_ALL);

        // Click on create button
        actions().makeClick(BUTTON_CREATE);

        // Instead of spinner not there, we wait for spinner to be replaced String interval report icon
        asidePO.validateNoSpinner();
    }

    public boolean checkListFor(String reportType) {
        switch (reportType) {
            case "interval" -> {
                return actions().elementExistAndVisible("//div[@data-qa-id='list-item']//i[contains(@class, 'interval')]", false, 10);
            }
            case "transient" -> {
                return actions().elementExistAndVisible("//div[@data-qa-id='list-item']//i[contains(@class, 'transient')]", false, 10);
            }
//            case "aborted" ->
//            case "spinner" ->
            }
        return false;
    }

    public void deleteThisManyReports(int assumedReports) {
        for (int i = 0; i < assumedReports; i++) {
            deleteUppermostReportInList();
            PlaywrightActions.sleep(3);
        }
    }

    public void deleteUppermostReportInList() {
        actions().makeClick(ASIDE_ITEM_THREE_DOT_BUTTON);

        // Update the report
        actions().makeClick(ASIDE_ITEM_MENU_DELETE);

        // Tick 'I understand'
        actions().makeClick(DIALOG_I_UNDERSTAND);

        // Confirm that we want to delete the report
        actions().makeClick(DIALOG_CONFIRM);
    }

    // todo: create a MenuPO-class for all interaction with menu's?
    /**
     * Compact list item menu needs to be open for this method to work.
     */
    public void selectCompactListItemMenu(String menu) {
        switch (menu) {
            case "Update" -> actions().makeClick(ASIDE_ITEM_MENU_UPDATE);
            case "Save" -> actions().makeClick(ASIDE_ITEM_MENU_SAVE);
            case "Copy" -> actions().makeClick(ASIDE_ITEM_MENU_COPY);
            case "Delete" -> actions().makeClick(ASIDE_ITEM_MENU_DELETE);
        }
    }

    /**
     * Method works on a already opened compact list item menu.
     * @param name
     */
    public void selectSaveMenuAndSaveDataReport(final String name) {
        // Select Save menu
        actions().makeClick(ASIDE_ITEM_MENU_SAVE);

        actions().clearAndType(DIALOG_NAME_INPUT, name);

        // Confirm that we want to save the report
        actions().makeClick(DIALOG_CONFIRM);

        // It takes a sec to update the report. Pause here...
        PlaywrightActions.sleep(2);
    }

    // todo: perhaps this belong in a MenuPanelPO?
    /**
     * Method works on a already opened compact list item menu.
     */
    public void shareDataReport() {
        // Select Share menu
        actions().makeClick(ASIDE_ITEM_MENU_SHARE);

        // Confirm that we want to save the report
        actions().makeClick(DIALOG_CONFIRM);

        // It takes a sec to update the report. Pause here...
        PlaywrightActions.sleep(2);
    }

    public void clickFirstMeasuringPointsShowButtonToGoToTransientAnalysis() {
//        actions().makeClickOnSomeElements("//table //span[text()='Show']", 1);
        actions().makeClick("//table //span[text()='Show']");
        PlaywrightActions.sleep(3);
    }

    public boolean validateAsideItemsHasFilterText(String filterText) {
        // Get a list of all listItems text
        List<String> dataReportTexts = actions().findManyElementsTexts(COMPACT_ASIDE_LIST_ITEMS);

        Predicate<String> temporary = text -> text.contains("Temporary report");
        Predicate<String> shared = text -> text.contains("people");
        Predicate<String> saved = text -> !text.contains("Temporary report");

        return switch (filterText) {
            case "Temporary" -> dataReportTexts.stream().allMatch(temporary);
            case "Shared" -> dataReportTexts.stream().allMatch(shared);
            case "Saved" ->
                    // reports shared:true are also saved
                    dataReportTexts.stream().allMatch(shared.or(saved));
            default -> false;
        };
    }

    public int getCounter() {
        String counterText = actions().findOneElementsText(INTERVAL_TABLE_DISPLAY_COUNTER);
        // Return the last index from "Displaying 10079 of 10079"
        return Integer.parseInt(counterText.substring(counterText.indexOf("of") + 3));
    }

//******************************************************************

    public BlastJournalView getBlastJournal() {

        BlastJournalView journal = new BlastJournalView();

        PanelHeader panelHeader = getPanelHeader("report");
        journal.setPanelHeader(panelHeader);

        BlastJournalView.Settings blastSettings = getBlastSettings("Blast settings");
        journal.setBlastSettings(blastSettings);

        // Only get the other settings if they are shown by expanded
        if (blastSettings.getArrowUpDownIcon().getType().equals(EXPANDED)) {

            BlastJournalView.Settings holesSettings = getBlastSettings("Holes");
            journal.setHolesSettings(holesSettings);

            BlastJournalView.Settings chargeSettings = getBlastSettings("Charge");
            journal.setChargeSettings(chargeSettings);

            BlastJournalView.Settings valuesSettings = getBlastSettings("Calculated values");
            journal.setCalculatedValuesSettings(valuesSettings);
        }

        Table measuringPointTable = getBlastJournalMpTable();
        journal.setMpTable(measuringPointTable);

        return journal;
    }

    private BlastJournalView.Settings getBlastSettings(String settingsType) {
        BlastJournalView.Settings settings = new BlastJournalView.Settings();

        String settingsPath = "//div[@class='fit'] //span[text()='"+settingsType+"']/ancestor::div[4]";

        Icon leftIcon = completeGetIcon(settingsPath + " //i/parent::div");
        settings.setLeftIcon(leftIcon);

        // Icon, if present, can be either EXPANDED or COLLAPSED
        boolean hasRightIcon = actions().elementExistAndVisible("//div[@class='fit'] //span[text()='"+settingsType+"']/ancestor::div[4]/div[2] //i/parent::div", false, 0);
        if (hasRightIcon) {
            Icon expandCollapsIcon = completeGetIcon("//div[@class='fit'] //span[text()='"+settingsType+"']/ancestor::div[4]/div[2] //i/parent::div");
            settings.setArrowUpDownIcon(expandCollapsIcon);
        }

        Map<String, String > data = new HashMap<>();
        settings.setSettingsData(data);

        int labels = actions().countHowManyElements(settingsPath + " //label");

        for (int label = 1; label <= labels; label++) {
            String value = actions().findOneElementsText("((" + settingsPath + "//label)["+label+"] //div)[5]");
            String key = actions().findOneElementsText("((" + settingsPath + "//label)["+label+"] //div)[6]");
            data.put(key, value);
        }

        return settings;
    }

    private Table getBlastJournalMpTable() {
        Table table = new Table();

        String tablePath = "//table";

        Table.TableRow headerRow = getBlastJournalMpTableHeaderRow(tablePath);
        table.setHeader(headerRow);

        List<Table.TableRow> tableRows = getBlastJournalMpTableRows(tablePath, headerRow.getObjects().size());
        table.setContent(tableRows);

        return table;
    }

    private Table.TableRow getBlastJournalMpTableHeaderRow(String tablePath) {
        Table.TableRow tableRow = new Table.TableRow();

        int columnCount = actions().countHowManyElements(tablePath + "/thead/tr/th");

        for (int c = 1; c <= columnCount; c++) {
            String cellPath = tablePath + "/thead/tr/th["+c+"]";

            String cellValue = actions().findOneElementsText(cellPath);
            tableRow.addContent(cellValue);
        }
        return tableRow;
    }

    private List<Table.TableRow> getBlastJournalMpTableRows(String tablePath, int columns) {
        List<Table.TableRow> tableRows = new ArrayList<>();

        int rowCount = actions().countHowManyElements(tablePath + "/tbody/tr");

        for (int r = 1; r <= rowCount; r++) {
            Table.TableRow tableRow = new Table.TableRow();

            String rowPath = tablePath + "/tbody/tr["+r+"]";

            for (int c = 1; c <= columns; c++) {
                Table.TableCell tableCell = new Table.TableCell();

                String cellPath = rowPath + "/td["+c+"]";

                if (c == 1) {
                    Icon mpIcon = completeGetIcon(cellPath + "/div");
                    tableCell.addCellIcon(mpIcon);

                    String text = actions().findOneElementsText(cellPath + "/div/span");
                    tableCell.addCellText(text);

                } else if (c == 11) {

                    boolean hasTransientLink = actions().elementExistAndVisible(cellPath + "/div", false, 0);
                    if (hasTransientLink) {
                        Icon mpIcon = completeGetIcon(cellPath + "/div");
                        tableCell.addCellIcon(mpIcon);

                        String text = actions().findOneElementsText(cellPath + "/div/span");
                        tableCell.addCellText(text);
                    } else {
                        String text = actions().findOneElementsText(cellPath + "/span");
                        tableCell.addCellText(text);
                    }

                } else if (c == 4) {
                    List<String> channelValues = actions().findManyElementsTexts(cellPath + "/div");
                    tableCell.setCellTexts(channelValues);
                } else {
                    String text = actions().findOneElementsText(cellPath);
                    tableCell.addCellText(text);

                }
                tableRow.addContent(tableCell);

            }
            tableRows.add(tableRow);
        }
        return tableRows;
    }

    public BlastReport getBlastReport() {
        BlastReport report = new BlastReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        report.setReportTabs(tabs);

        String reportDuration = getReportDuration(BLASTS_REPORT);
        report.setReportDuration(reportDuration);

        // Get upper panel
        String upperPanelPart = "//div[@class='fit column no-wrap scroll']" + "/div[1]";  //div[@class='fit column no-wrap scroll']/div[1]

        // Get lower panel
        String lowerPanelPath = "//div[@class='fit column no-wrap scroll']" + "/div[2]";  //div[@class='fit column no-wrap scroll']/div[2]

        Table reportContent = getBlastReportContent(lowerPanelPath);
        report.setReportContent(reportContent);

        return report;
    }

    private Table getBlastReportContent(String reportContentPath) {
        Table table = new Table();
        String tablePath = reportContentPath + "//table";
        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th");

        Table.TableRow headerRow = getBlastReportHeaderRow(columns, tablePath + "/thead/tr/th");
        table.setHeader(headerRow);

        List<Table.TableRow> blastRows = getBlastReportBlastRows(columns, tablePath);
        table.setContent(blastRows);

        return table;
    }

    private Table.TableRow getBlastReportHeaderRow(int columns, String headerRowPath) {
        Table.TableRow headerRow = new Table.TableRow();

        for (int c = 1; c <= columns; c++) {

            String headerPath = headerRowPath + "["+c+"]";

            String header = actions().findOneElementsText(headerPath);
            headerRow.addContent(header);

        }
        return headerRow;
    }

    private List<Table.TableRow> getBlastReportBlastRows(int columns, String tablePath) {
        List<Table.TableRow> blastRows = new ArrayList<>();

        int rowCount = actions().countHowManyElements(tablePath + "/tbody/tr");

        for (int r = 1; r <= rowCount; r++) {
            Table.TableRow blastRow = new Table.TableRow();
            String rowPath = tablePath + "/tbody/tr["+r+"]";

            for (int c = 1; c <= columns; c++) {
                Table.TableCell tableCell = new Table.TableCell();

                String cellPath = rowPath + "/td["+c+"]";

                if (c == 1) {   // Blast name column
                    Icon blastIcon = completeGetIcon(cellPath + " //i/parent::div");
                    tableCell.addCellIcon(blastIcon);

                    String blastName = actions().findOneElementsText(cellPath + " //span");
                    tableCell.addCellText(blastName);

                } else if (c == 9) {    // Guide value column

                    String cellText = actions().findOneElementsText(cellPath);
                    tableCell.addCellText(cellText);

                    boolean hasGuideValueWarning = actions().elementExistAndVisible(cellPath + "/i", false, 0);
                    if (hasGuideValueWarning) {
//                        Icon blastIcon = completeGetIcon(cellPath + "/i");
                        Icon blastIcon = completeGetIcon(cellPath);

                        tableCell.addCellIcon(blastIcon);
                    }

                } else {
                    String cellText = actions().findOneElementsText(cellPath);
                    tableCell.addCellText(cellText);
                }
                blastRow.addContent(tableCell);

            }
            blastRows.add(blastRow);
        }
        return blastRows;
    }

    private List<Map<String, String>> getBlastViewTableContent() {
        List<Map<String, String>> tableRows = new ArrayList<>();
        int columns = actions().countHowManyElements("//thead//th");

        String[] headers = new String[columns];

        // First get all the headers, and put them ordered for later use
        for (int column = 1; column <= columns; column++ ) {
            String header = actions().findOneElementsText("(//thead //th)["+column+"]");
            // IDK why, but one header (Percentage) contain both row break + arrow_upward
            if (header.contains("\n")) { header = header.substring(0, header.indexOf("\n")); }
            headers[column - 1] = header;
        }

        // Now get each row in the report
        int rows = actions().countHowManyElements("//tbody //tr");

        for (int row = 1; row <= rows; row++ ) {
            Map<String, String> tableRowIncludingHeader = new HashMap<>();
            // First storing headers and then retrieving them saves us (columns x time-to-find value)
            for (int column = 1; column <= columns; column++ ) {
                String header = headers[column -1];
                String cellValue = actions().findOneElementsText("((//tbody //tr)["+row+"] //td)["+column+"]");
                tableRowIncludingHeader.put(header, cellValue);
            }
            tableRows.add(tableRowIncludingHeader);
        }

        return tableRows;
    }

    public MeasuringReportTableReport getMeasuringReportTableReport() {
        MeasuringReportTableReport report = new MeasuringReportTableReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        report.setReportTabs(tabs);

        String reportDuration = getReportDuration(MEASURING_REPORT);
        report.setReportDuration(reportDuration);

        Icon filterIcon = completeGetIcon("//div[@class='fit']  //button //i[contains(text(), 'tune')]/ancestor::button");
        report.setFilterIcon(filterIcon);

        Button exportButton = getButton("//div[@class='fit']  //button //div[contains(text(), 'Export')]/ancestor::button");
        report.setExportButton(exportButton);

        // Expanded filter
        //div[@class='fit'] //form[1]

        Table table = getMeasuringReportTable();
        report.setTable(table);

        return report;
    }

    private Table getMeasuringReportTable() {
        Table table = new Table();

        String tablePath = "//table";

        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th");

        Table.TableRow headerRow = getMeasuringReportTableHeaderRow(tablePath, columns);
        table.setHeader(headerRow);

        List<Table.TableRow> reportContent = getMeasuringReportTransientRows(tablePath, columns);
        table.setContent(reportContent);

        return table;
    }

    private Table.TableRow getMeasuringReportTableHeaderRow(String tablePath, int columns) {
        Table.TableRow headerRow = new Table.TableRow();

        for (int c = 1; c <= columns; c++) {
            String headerRowPath = tablePath + "/thead/tr[1]/th["+c+"]";

            String headerText = actions().findOneElementsText(headerRowPath);
            if (headerText.contains("arrow_upward")) {
                headerText = headerText.replace("arrow_upward", "");
            }
            headerRow.addContent(headerText);
        }

        return headerRow;
    }

    private List<Table.TableRow> getMeasuringReportTransientRows(String tablePath, int columns) {
        List<Table.TableRow> transientRows = new ArrayList<>();

        int rowCount = actions().countHowManyElements(tablePath + "/tbody[@class='q-virtual-scroll__content']/tr");

        // todo: dynamic DOM... :-( ?
        for (int r = 1; r <= rowCount; r++) {
            Table.TableRow transientRow = new Table.TableRow();

            String rowPath = tablePath + "/tbody[@class='q-virtual-scroll__content']/tr["+r+"]";

            for (int c = 1; c <= columns; c++) {
                Table.TableCell tableCell = new Table.TableCell();

                String cellPath = rowPath + "/td["+c+"]";

                if (c == 1 || c == 10) {
                    Icon mpIcon = completeGetIcon(cellPath + "/div");
                    tableCell.addCellIcon(mpIcon);

                    String text = actions().findOneElementsText(cellPath + "/div/span");
                    tableCell.addCellText(text);

                    transientRow.addContent(tableCell);

                } else if (c == 4) {    // Measured value
                    List<String> channelValues = actions().findManyElementsTexts(cellPath + "/div/div");
                    tableCell.setCellTexts(channelValues);

                    transientRow.addContent(tableCell);

                }
                else if (c == 7) {    // Percentage
                    List<String> percentages = actions().findManyElementsTexts(cellPath);
                    tableCell.setCellTexts(percentages);

                    transientRow.addContent(tableCell);
                }
                else {
                    String text = actions().findOneElementsText(cellPath);
                    transientRow.addContent(text);
                }
            }
            transientRows.add(transientRow);
        }
        return transientRows;
    }

    /**
     * .../views/id/intervals/table
     */
    public IntervalTableReport getIntervalTableReport() {
        IntervalTableReport report = new IntervalTableReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        report.setReportTabs(tabs);

        String reportDuration = getReportDuration(INTERVALS_TABLE);
        report.setReportDuration(reportDuration);

        Icon filterIcon = completeGetIcon("//div[@class='fit']  //button //i[contains(text(), 'tune')]/ancestor::button");
        report.setFilterIcon(filterIcon);

        Icon settingsIcon = completeGetIcon("//div[@class='fit'] //button //i[contains(@class, 'q-icon icon-settings')]/ancestor::button");
        report.setSettingsIcon(settingsIcon);

        Icon chartIcon = completeGetIcon("//div[@class='fit'] //a //i[contains(@class, 'q-icon icon-interval-data')]/ancestor::a");
        report.setChartIcon(chartIcon);

        // todo: dropdown Export
        Button exportButton = getButton("//div[@class='fit']  //button //div[contains(text(), 'Export')]/ancestor::button");
        report.setExportButton(exportButton);

        // Expanded filter
        //div[@class='fit'] //form[1]


        Table table = getIntervalTable();
        report.setTable(table);

        return report;
    }

    private Table getIntervalTable() {
        Table table = new Table();

        String tablePath = "//table";

        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th");

        Table.TableRow headerRow = getIntervalTableHeaderRow(tablePath, columns);
        table.setHeader(headerRow);

        List<Table.TableRow> metaDataRows = getIntervalTableMetadataRows(tablePath, columns);
        table.setMetaData(metaDataRows);

        List<Table.TableRow> reportContent = getIntervalTableIntervalRows(tablePath, columns);
        table.setContent(reportContent);

        if (reportContent == null) {
            Icon noDataIcon = completeGetIcon(tablePath + "/parent::div/parent::div" + "/div[contains(@class,'bottom row')]");
            table.setFooterIcon(noDataIcon);
            String noDataText = actions().findOneElementsText(tablePath + "/parent::div/parent::div" + "/div[contains(@class,'bottom row')]");
            table.setFooterText(noDataText);
        } else {
            String tableFooter = actions().findOneElementsText(tablePath + "/parent::div/parent::div" + "/div[contains(@class,'bottom row')]/div");
            table.setFooterText(tableFooter);
        }

        return table;
    }

    private Table.TableRow getIntervalTableHeaderRow(String tablePath, int columns) {
        Table.TableRow headerRow = new Table.TableRow();

        String headerRowPath = tablePath + "/thead/tr/th";

        for (int c = 1; c <= columns; c++) {
            String cellPath = headerRowPath + "["+c+"]";

            if (c == 1) {
                // First get the key
                Table.TableCell tableCell = new Table.TableCell();
                String firstColumnText = actions().findOneElementsText(cellPath);
                tableCell.addCellText(firstColumnText);
                headerRow.addContent(tableCell);

            } else {
                Table.TableCell tableCell = new Table.TableCell();

                Icon icon = completeGetIcon(cellPath + "/div/div");
                tableCell.addCellIcon(icon);

                String mpName = actions().findOneElementsText(cellPath + "/div/div[2]/div[1]");
                tableCell.addCellText(mpName);

                String mpSensor = actions().findOneElementsText(cellPath + "/div/div[2]/div[2]");
                tableCell.addCellText(mpSensor);

                boolean hasBanner = actions().elementExistAndVisible(cellPath + "/div/div[2]/div[3]", false, 0);
                if (hasBanner) {
                    List<String> banners = actions().findManyElementsTexts(cellPath + "/div/div[2]/div[3]/div");
                    banners.forEach(tableCell::addCellText);
                }

                headerRow.addContent(tableCell);
            }
        }

        return headerRow;
    }

    private List<Table.TableRow> getIntervalTableMetadataRows(String tablePath, int columns) {
        List<Table.TableRow> metaDataRows = new ArrayList<>();

        int metaDataRowCount = actions().countHowManyElements(tablePath + "/tbody[1]/tr/td[1]/div/div");

        for (int r = 1; r <= metaDataRowCount; r++) {
            Table.TableRow metaDataRow = new Table.TableRow();

            String metaDataKey = "";

            for (int c = 1; c <= columns; c++) {

                // First column have the key
                if (c == 1) {
                    metaDataKey = actions().findOneElementsText(tablePath + "/tbody[1]/tr/td["+c+"]/div/div["+r+"]");
                    metaDataRow.addContent(metaDataKey);

                } else {

                    // Interval metadata values are combined in a /div, but not 'dBCorr'...
                    if (metaDataKey.equals("dBCorr")) {
                        String cellValuePath = tablePath + "/tbody[1]/tr/td[" + c + "]/div/div[2]";

                        String cellValue = actions().findOneElementsText(cellValuePath);
                        metaDataRow.addContent(cellValue);

                    } else {
                        String cellValuePath = tablePath + "/tbody[1]/tr/td[" + c + "]/div/div[1]/div/div[" + r + "]";

                        boolean isDropdown = actions().elementExistAndVisible(cellValuePath + "/label", false, 0);
                        if (isDropdown) {
                            Dropdown dropdown = getDropdownByPath(cellValuePath + "/label");
                            metaDataRow.addContent(dropdown);
                        } else {
                            String cellValue = actions().findOneElementsText(cellValuePath);
                            metaDataRow.addContent(cellValue);
                        }
                    }
                }
            }
            metaDataRows.add(metaDataRow);
        }
        return metaDataRows;
    }

    private List<Table.TableRow> getIntervalTableIntervalRows(String tablePath, int columns) {
        String contentPath = tablePath + "/tbody[@class='q-virtual-scroll__content']";

        boolean hasIntervalContent = actions().elementExistAndVisible(contentPath + "/tr", false, 0);
        if (hasIntervalContent) {
            List<Table.TableRow> intervalRows = new ArrayList<>();

            int intervalRowCount = actions().countHowManyElements(contentPath + "/tr");

            // Get all the rows in the DOM
            for (int r = 1; r <= intervalRowCount; r++) {
                Table.TableRow intervalRow = new Table.TableRow();
                String intervalRowPath = contentPath + "/tr["+r+"]";

                // Get all columns
                for (int c = 1; c <= columns; c++) {
                    String cellValuePath = intervalRowPath + "/td["+c+"]";

                    // First column is timestamp
                    if (c == 1) {
                        String timestamp = actions().findOneElementsText(cellValuePath);
                        intervalRow.addContent(timestamp);

                    } else {
                        Table.TableCell intervalCell = new Table.TableCell();

                        // Get all text based information in the interval
                        boolean hasChannels = actions().elementExistAndVisible(cellValuePath + "/div", false, 0);
                        if (hasChannels) {
                            List<String> intervalChannels = actions().findManyElementsTexts(cellValuePath + "/div");
                            intervalCell.setCellTexts(intervalChannels);
                        }

                        // Get all icons in the interval
                        boolean hasIcon = actions().elementExistAndVisible(cellValuePath + "/i", false, 0);
                        if (hasIcon) {
                            List<Icon> intervalIcons = new ArrayList<>();
                            int iconCount = actions().countHowManyElements(cellValuePath + "/i");

                            // As the icon can have siblings we cannot send parent to completeGetIcon().
                            for (int i = 1; i <= iconCount; i++) {
                                String iconPath = cellValuePath + "/i["+i+"]/parent::td";

                                Icon intervalIcon = completeGetIcon(iconPath);
                                intervalIcons.add(intervalIcon);

                            }
                            intervalCell.setCellIcons(intervalIcons);

                            // Make sure we've got all the icons
                            if (iconCount != intervalIcons.size()) {
                                throw new IllegalStateException("Not all icons was accounted for: " + "\n"
                                        + "iconCount/actualIconsFound: " + iconCount + "/" + intervalIcons.size());
                            }
                        }
                        intervalRow.addContent(intervalCell);
                    }
                }
                intervalRows.add(intervalRow);
            }
            return intervalRows;

        } else {
            // Intervals table without intervals has no noRowsText, but instead a footer with icon and 'No data available'
            return null;
        }
    }

    public TransientTableReport getTransientTableReport() {
        TransientTableReport report = new TransientTableReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        report.setReportTabs(tabs);

        String reportDuration = getReportDuration(TRANSIENTS_TABLE);
        report.setReportDuration(reportDuration);

        Button exportButton = getButton("//div[@class='fit column']/descendant::button");
        report.setExportButton(exportButton);

        DataViewStatus dataViewStatus = getDataViewStatus();
        report.setDataViewStatus(dataViewStatus);

        Table table = getTransientTable();
        report.setReportContent(table);

        return report;
    }

    private Table getTransientTable() {
        Table table = new Table();

        String tablePath = "//table";
        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th[*]");

        // Get header row
        Table.TableRow headerRow = getTransientTableTableHeaderRow(columns, tablePath + "/thead/tr");
        table.setHeader(headerRow);

        // Get meta data rows
        List<Table.TableRow> metaDataRows = getTransientTableTableMetaDataRows(tablePath, columns);
        table.setMetaData(metaDataRows);

        boolean configHasTransients = actions().elementExistAndVisible(tablePath + "/tbody[@class='q-virtual-scroll__content'][*]", false, 0);
        if (configHasTransients) {
            // Get all transient rows
            List<Table.TableRow> transientRows = getTransientTableTransientRows(tablePath, columns);
            table.setContent(transientRows);

            Dropdown footerDropdown = getDropdownByPath(tablePath + "/parent::div/parent::div" + "/div[contains(@class,'bottom row')]/div[2]/label");
            table.setFooterDropdown(footerDropdown);
            String footerCounter = actions().findOneElementsText(tablePath + "/parent::div/parent::div" + "/div[contains(@class,'bottom row')]/div[3]/span");
            table.setFooterText(footerCounter);
        } else {
            // No transients has been found in the selected time interval
            String noTransientMessage = actions().findOneElementsText("//tbody //div[@staticclass]");
            table.setNoRowsText(noTransientMessage);
        }

        return table;
    }

    private Table.TableRow getTransientTableTableHeaderRow(int columns, String headerRowPath) {
        Table.TableRow headerRow = new Table.TableRow();

        for (int c = 1; c <= columns; c++) {
            // First column is 'Timestamp'
            if (c == 1) {

                Table.TableCell headerCell = getTableHeaderCell(headerRowPath + "/th[position()="+c+"]");
                headerRow.addContent(headerCell);

            // Following columns are icon + mpName+\n+mpDescription
            } else {

                Table.TableCell headerCell = getTableHeaderCell(headerRowPath + "/th[position()="+c+"]/div");
                headerRow.addContent(headerCell);
            }
        }
        return headerRow;
    }

    /**
     * To get the rows in MetaData we first need to get each column, and store the values (String or Dropdown) to a List<Object>.
     * When we have as many List<Object> then we loop through them from the top and from every iteration we get a TableRow.
     */
    private List<Table.TableRow> getTransientTableTableMetaDataRows(String tablePath, int columns) {
        String metadataPath = tablePath + "/tbody[1]/tr[1]";

        // Get MetaData column first
        List<Object> metaDataKeys = getTransientTableMetaDataKeyColumn(metadataPath, columns);

        // Then All measuring point columns
        List<List<Object>> measuringPointColumns = getTransientTableMetaDataColumns(metadataPath, columns, metaDataKeys.size());

        // Now map the columns into rows
        List<Table.TableRow> metaDataRows = createMetaDataTableRows(metaDataKeys, measuringPointColumns);

        return metaDataRows;
    }

    /**
     * Uses the metadata List + each columnList to build rows
     */
    private List<Table.TableRow> createMetaDataTableRows(List<Object> metaDataKeys, List<List<Object>> columnsList) {
        System.out.println("expected rowCount: " + metaDataKeys.size());
        metaDataKeys.forEach(System.out::println);
        System.out.println("****");
        columnsList.forEach(System.out::println);
        System.out.println("****");

        // metaDataKeys.size has to match the size of each inner <List> in columnsList
        columnsList.forEach(column -> {
            if (metaDataKeys.size() != column.size()) {
                throw new IllegalStateException("Metadata key size and a column are not matching in size (rows)");
            }
        });

        List<Table.TableRow> metaDataRows = new ArrayList<>();

        int rowCount = metaDataKeys.size();

        for (int r = 0; r < rowCount; r++) {
            Table.TableRow metaDataRow = new Table.TableRow();

            Object metaDataKey = metaDataKeys.get(r);
            metaDataRow.addContent(metaDataKey);

            int columnCount = columnsList.size();

            for (int c = 0; c < columnCount; c++) {
                Object o = columnsList.get(c).get(r);
                metaDataRow.addContent(o);
            }

            metaDataRows.add(metaDataRow);
        }
        return metaDataRows;
    }

    private List<Object> getTransientTableMetaDataKeyColumn(String metadataPath, int columns) {
        String columnPath =  metadataPath + "/td[1]";

        List<String> metaDataColumnKeys = actions().findManyElementsTexts(columnPath + "/div/div");
        return  metaDataColumnKeys.stream()
                .map(s -> (Object) s)
                .toList();
    }

    private List<List<Object>> getTransientTableMetaDataColumns(String metadataPath, int columns, int expectedRowsInMetaDataRow) {
        List<List<Object>> mpColumns = new ArrayList<>();

        // Skip the first column and go for each measuring point column
        for (int c = 2; c <= columns; c++) {
            List<Object> mpColumnObjects = new ArrayList<>();

            String columnPath =  metadataPath + "/td["+c+"]";

            int nodesInFirstDiv = actions().countHowManyElements(columnPath + "/div/div[1]/div/div");  // 2 or 3

            // The 1-3 elements in the first <div> can be either String or Dropdown (Standard, Configuration, Interval_time)
            for (int n = 1; n <= nodesInFirstDiv; n++) {
                String nodePath = columnPath + "/div/div[1]/div/div["+n+"]";

                boolean isDropdown = actions().elementExistAndVisible(nodePath + "/label", false, 0);
                if (isDropdown) {
                    Dropdown dropdown = getDropdownByPath(nodePath + "/label");
                    mpColumnObjects.add(dropdown);
                } else {
                    String cellValue = actions().findOneElementsText(nodePath);
                    mpColumnObjects.add(cellValue);
                }
            }

            // Get the first list in the list, and get how many rows the metadata should consist of
            int nodesLeft = expectedRowsInMetaDataRow - nodesInFirstDiv;

            // Add '1' to nodesLeft because we start at '2'
            for (int l = 2; l <= nodesLeft + 1; l++) {

                String cellValue = actions().findOneElementsText(columnPath + "/div/div["+l+"]");
                mpColumnObjects.add(cellValue);
            }

            mpColumns.add(mpColumnObjects);
        }
        return mpColumns;
    }

    private List<Table.TableRow> getTransientTableTransientRows(String tablePath, int columns) {
        List<Table.TableRow> transientRows = new ArrayList<>();

        String transientsRowsPath = tablePath + "/tbody[@class='q-virtual-scroll__content']";

        int transientRowCount = actions().countHowManyElements(transientsRowsPath + "/tr");

        for (int r = 1; r <= transientRowCount; r++) {
            Table.TableRow transientRow = new Table.TableRow();

            // First get the timeStamp
            String timeStamp = actions().findOneElementsText(transientsRowsPath + "/tr["+r+"]/td[1]");
            transientRow.addContent(timeStamp);

            // For each following mp column
            for (int c = 2; c <= columns; c++) {
                String transientText = actions().findOneElementsText(transientsRowsPath + "/tr["+r+"]/td["+c+"]");
                transientRow.addContent(transientText);
            }
            transientRows.add(transientRow);
        }

        return transientRows;
    }

    /**
     * .../views/id/intervals/chart
     */
    public IntervalsChartView getIntervalChartReport() {
        IntervalsChartView intervalsChartView = new IntervalsChartView();

        PanelHeader panelHeader = getPanelHeader("report");
        intervalsChartView.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        intervalsChartView.setReportTabs(tabs);

        String chartHeaderPath = "(//div[@class='fit'] //div[contains(@class, 'text-body1')])[1]";

        String reportDuration = getReportDuration(INTERVALS_CHART);
        intervalsChartView.setReportDuration(reportDuration);

        Icon copyIcon = completeGetIcon(chartHeaderPath + " //i[contains(@class, 'icon-content-copy')]/ancestor::button");
        intervalsChartView.setCopyIcon(copyIcon);

        Icon gridIcon = completeGetIcon(chartHeaderPath + " //i[text()='grid_on']/ancestor::a");
        intervalsChartView.setGridIcon(gridIcon);

        Button exportButton = getButton(chartHeaderPath + " //div[text()='Export']/ancestor::button");
        intervalsChartView.setExportButton(exportButton);

        // Get the intervals chart header
        DataViewStatus intervalChartHeader = getDataViewStatus();
        intervalsChartView.setDataViewStatus(intervalChartHeader);

        // Get all mp charts in the report view
        List<IntervalsChartView.MeasuringPointData> mpCharts = getMeasuringPointDataList();
        intervalsChartView.setMeasuringPointDataList(mpCharts);

        return intervalsChartView;
    }

    /**
     * .../views/id/intervals/chart
     * @return the charts in intervals chart
     */
    private List<IntervalsChartView.MeasuringPointData> getMeasuringPointDataList() {
        List<IntervalsChartView.MeasuringPointData> mpCharts = new ArrayList<>();

        // Every measuring point has one MeasuringPointChart
        boolean hasMeasuringPointChart =  actions().elementExistAndVisible("//div[@class='fit scroll'] //div[@data-qa-id='mp_data']", false, 0);

        if (hasMeasuringPointChart) {
            int numberOfMpChartInReport = actions().countHowManyElements("//div[@class='fit scroll'] //div[@data-qa-id='mp_data']");
            for (int mp = 1; mp <= numberOfMpChartInReport; mp++) {
                mpCharts.add(getMeasuringPointData(mp));
            }
        }
        return mpCharts;
    }

    /**
     * Three possible states:
     * Collapsed mpExpansionHeader, nothing else visible
     * Expanded mpExpansionHeader, visible mpChartSection, collapsed freqDataExpansionHeader
     * Expanded mpExpansionHeader, visible mpChartSection, Expanded freqDataExpansionHeader, visible freqDataChartSection
     */
    private IntervalsChartView.MeasuringPointData getMeasuringPointData(int mp) {
        String mpPath = "(//div[@class='fit scroll'] //div[@data-qa-id='mp_data'])["+mp+"]";

        IntervalsChartView.MeasuringPointData mpd = new IntervalsChartView.MeasuringPointData();

        // Get warning
        String mpWarningPath = mpPath + " //div[@role='list']";
        boolean chartHasAlert = actions().elementExistAndVisible(mpWarningPath, false, 0);

        if (chartHasAlert) {
            Icon warningIcon = completeGetIcon(mpWarningPath + " //i/parent::div");
            mpd.setWarningIcon(warningIcon);

            String warningText = actions().findOneElementsText(mpWarningPath + " //div[2]");
            mpd.setWarningText(warningText);
        }

        // Get the ever present mpExpansionHeader
        ChartSectionHeader mpChartSectionHeader = getExpansionHeaderIntervalsChartMpData("(" + mpPath + " //div[@role='button'])[1]");
        mpd.setMpChartSectionHeader(mpChartSectionHeader);

        // Get CharSection if mpExpansionHeader is expanded
        if (mpChartSectionHeader.getExpansionIcon().getType().equals(EXPANDED)) {
            ChartSectionBody mpData = getMeasuringPointChartSection(mpPath);
            mpd.setMpChartSectionBody(mpData);

            // NB. Even though FrequencyData (Header, ChartSection) is within Mp.ChartSection in the DOM, we treat them as on same level.
            // But only look for FreqData when we also look for mpData.ChartSection

            // Get the optional freqDataExpansionHeader or Octave_graph
            boolean hasFrequencyData = actions().elementExistAndVisible(mpPath + " //div[@data-qa-id='frequency_data']", false, 0);
            if (hasFrequencyData) {
                ChartSectionHeader freqDataChartSectionHeader = getExpansionHeaderFreqData(mpPath + " //div[@data-qa-id='frequency_data']" + " //div[@role='button']");
                mpd.setFreqDataChartSectionHeader(freqDataChartSectionHeader);

                // Get CharSection if freqDataExpansionHeader is expanded
                if (freqDataChartSectionHeader.getExpansionIcon().getType().equals(EXPANDED)) {
                    ChartSectionBody freqDataChartSectionBody = getFrequencyDataChartSection(mpPath + " //div[@data-qa-id='frequency_data']");
                    mpd.setFreqDataChartSectionBody(freqDataChartSectionBody);
                }
            } else {
                String octaveGraphPath = "(" + mpPath + " //div[contains(@class,'q-expansion-item') and .//div[contains(text(),'Octave')]])[5]";
                boolean hasOctaveGraph = actions().elementExistAndVisible(octaveGraphPath, false, 0);

                if (hasOctaveGraph) {
                    ChartSectionHeader octaveGraphSectionHeader = getExpansionHeaderOctaveGraph(octaveGraphPath);
                    mpd.setOctaveDataChartSectionHeader(octaveGraphSectionHeader);

                    // No way to capture the sciChart-graphs found :-(
                }
            }
        }

        return mpd;
    }

    private ChartSectionBody getFrequencyDataChartSection(String frequencyDataChartPath) {
        ChartSectionBody freqData = new ChartSectionBody();

        // to be evolved

        return freqData;
    }

    private ChartSectionBody getMeasuringPointChartSection(String chartPath) {
        ChartSectionBody mpData = new ChartSectionBody();

        // Get the metaData
        Map<String, String> infoFields = new LinkedHashMap<>();
        String mpMetaDataPath = chartPath + " //div[@class='row q-gutter-xs']";
        int numberOfInfoFields = actions().countHowManyElements(chartPath + " //div[@class= 'row no-wrap']");
        for (int i = 1; i <= numberOfInfoFields; i++) {
            String keyPath = "(" + mpMetaDataPath + " //div[@class='info-field-label q-mr-xs']" + ")["+i+"]";
            String valuePath = "(" + mpMetaDataPath + " //div[@class='info-field-value'])["+i+"]";

            infoFields.put(
                    actions().findOneElementsText( keyPath).replace(":", "").trim(),
                    actions().findOneElementsText(valuePath).trim());
        }
        mpData.setMetaData(infoFields);

        // Get dropdown selector text if present
        boolean chartHasStandardSelector = actions().elementExistAndVisible(chartPath + " //div[@class='dropdown'] //label", false, 0);

        if (chartHasStandardSelector) {
            Map<String, String> standardDropdown = getDropdownParts(chartPath + " //div[@class='dropdown'] //label");
            mpData.addDropdown(standardDropdown);
//            Dropdown standardDropdown = getDropdown(chartPath + " //div[@class='dropdown'] //label");
//            mpData.setStandardSelector(standardDropdown);

            // Get intvTime selector text if present
            boolean chartHasIntervalTimeSelector = actions().elementExistAndVisible(chartPath + " //div[@class='metadata_values dropdown'] //label", false, 0);

            if (chartHasIntervalTimeSelector) {
                Map<String, String> intvTimeDropdown = getDropdownParts(chartPath + " //div[@class='metadata_values dropdown'] //label");
                mpData.addDropdown(intvTimeDropdown);
//                Dropdown intvTimeDropdown = getDropdown(chartPath + " //div[@class='metadata_values dropdown'] //label");
//                mpData.setIntervalTimeSelector(intvTimeDropdown);
            }
        }

        List<ToggleField> mpChannels = getChartChannels(chartPath + " //div[@class='q-px-lg relative-position']");
        mpData.setChartToggles(mpChannels);

        return mpData;
    }

    private List<ToggleField> getChartChannels(String chartPath) {
        List<ToggleField> channels = new ArrayList<>();
        String toggleFieldHolderPath = chartPath + "/div/div/span";

        int toggleFieldCount = actions().countHowManyElements(toggleFieldHolderPath + "/div");

        for (int i = 1; i <= toggleFieldCount; i++) {
            String toggleFieldPath = toggleFieldHolderPath + "/div["+i+"]";

            ToggleField toggleField = completeGetToggleField("right", toggleFieldPath);
            channels.add(toggleField);
        }

        return channels;
    }

    public ChartSectionBody getExpansionBodyVibrationReport(String bodyPath) {
        ChartSectionBody vibReportBody = new ChartSectionBody();

        // metadata
        String metadataPath = bodyPath + "/div[1]";
        Map<String, String> infoFields = getReportMetadata(metadataPath);
        vibReportBody.setMetaData(infoFields);

        // Frequency filter
        // From
        InputField frequencyFilterFrom = getInputFieldByPath("//form //div/following::label[1]");
        vibReportBody.addInputField(frequencyFilterFrom);

        // To
        InputField frequencyFilterTo = getInputFieldByPath("//form //div/following::label[2]");
        vibReportBody.addInputField(frequencyFilterTo);

        // Velocity filter
        // From
        InputField velocityFilterFrom = getInputFieldByPath("//form //div/following::label[3]");
        vibReportBody.addInputField(velocityFilterFrom);

        // To
        InputField velocityFilterTo = getInputFieldByPath("//form //div/following::label[4]");
        vibReportBody.addInputField(velocityFilterTo);

        Button reset = getButtonByText("Reset");
        vibReportBody.setReset(reset);

        Button apply = getButtonByText("Apply");
        vibReportBody.setApply(apply);

        // Date table
        String tablePath = bodyPath + "/div[2]/div[2]";

        String dateTablePath = tablePath + "/div[1]";
        Table dateTable = getVibrationReportTable(dateTablePath + " //table");
        vibReportBody.setVibrationReportDateTable(dateTable);

        // Timeslot table
        String timeslotPath = tablePath + "/div[2]";
        Table timeslotTable = getVibrationReportTable(timeslotPath + " //table");
        vibReportBody.setVibrationReportTimeslotTable(timeslotTable);

        return vibReportBody;
    }

    // todo: might also work for Noise report
    private Table getVibrationReportTable(String tablePath) {
        Table table = new Table();

        Table.TableRow headerRow = getDualHeaderTableRow(tablePath + "/thead");
        table.setHeader(headerRow);

        String firstColumnText = headerRow.getStringAtPosition(0);
//        System.out.println("firstColumnText: " + firstColumnText);

        // Add one column if the first column has day+date
        int expectedColumns = firstColumnText.contains("Date")
                ? headerRow.getObjects().size() + 1
                : firstColumnText.contains("Timeslot")
                    ? headerRow.getObjects().size()
                    : 0;

//        System.out.println("expectedColumns: " + expectedColumns);

        List<Table.TableRow> dateRows = getVibrationReportDateRows(tablePath + "/tbody", expectedColumns);
        table.setContent(dateRows);

        return table;
    }

    private List<Table.TableRow> getVibrationReportDateRows(String tableContentPath, int columns) {
        List<Table.TableRow> dateRows = new ArrayList<>();

        int rows = actions().countHowManyElements(tableContentPath + "/tr");

        for (int i = 1; i <= rows; i++) {
            Table.TableRow dateRow = new Table.TableRow();

            String rowPath = tableContentPath + "/tr[" + i + "]";

            for (int c = 1; c <= columns; c++) {
                String cellPath = rowPath + "/td["+c+"]";

                String cellValue = actions().findOneElementsText(cellPath);
                dateRow.addContent(cellValue);
            }

            dateRows.add(dateRow);
        }

        return dateRows;
    }

    //div[@class='fit scroll']/div/div/div/div/div[2]/div/div/div/div[2]/div[2]/div[1] //table/thead
    private Table.TableRow getDualHeaderTableRow(String tablePath) {
        // First row
        Table.TableRow headerRowFirst = getVibrationReportHeaderRow(tablePath + "/tr[1]");

        // Second row
        Table.TableRow headerRowSecond = getVibrationReportHeaderRow(tablePath + "/tr[2]");

        return combineHeaderRows(headerRowFirst, headerRowSecond);
    }

    private Table.TableRow combineHeaderRows(Table.TableRow headerRowFirst, Table.TableRow headerRowSecond) {
        Table.TableRow combinedHeaderRow = new Table.TableRow();

        String firstColumnText = headerRowFirst.getStringAtPosition(0);
        int expectedColumnsInSecondRow = firstColumnText.contains("Date")   // Vmax,V,L,T
                ? 4
                : firstColumnText.contains("Timeslot")  //V,L,T
                    ? 3
                    : 0;

        // Remove "Date" or "Timeslot" from the count
        int dynamicColumnsCount = headerRowFirst.getObjects().size() - 1;

        // Add "Date" or "Timeslot" to the first column
        combinedHeaderRow.addContent(headerRowFirst.getObjects().getFirst());

        for (int i = 1; i <= dynamicColumnsCount; i++) {
            String timeslotHeader = headerRowFirst.getStringAtPosition(i);

            // Each timeslot has four channels (Vmax,V,L,T)
            for (int c = 0; c < expectedColumnsInSecondRow; c++) {
                String channelHeader = headerRowSecond.getStringAtPosition(c);
                if (c == 0) {
                    String combinedHeader = timeslotHeader + "\n" + channelHeader;
                    combinedHeaderRow.addContent(combinedHeader);
                } else {
                    combinedHeaderRow.addContent(channelHeader);
                }
            }
        }

        return combinedHeaderRow;
    }

    private Table.TableRow getVibrationReportHeaderRow(String headerRowPath) {
        Table.TableRow headerRow = new Table.TableRow();

        int columnCount = actions().countHowManyElements(headerRowPath + "/th");

        for (int i = 1; i <= columnCount; i++) {
            String columnPath = headerRowPath + "/th["+i+"]";
            String cellValue = actions().findOneElementsText(columnPath);
            headerRow.addContent(cellValue);
        }

        return headerRow;
    }

    private Map<String, String> getReportMetadata(String metadataPath) {
        Map<String, String> infoFields = new LinkedHashMap<>();
        String mpMetaDataPath = metadataPath + " //div[@class='row q-gutter-xs']";
        int numberOfInfoFields = actions().countHowManyElements(metadataPath + " //div[@class= 'row no-wrap']");
        for (int i = 1; i <= numberOfInfoFields; i++) {
            String keyPath = "(" + mpMetaDataPath + " //div[@class='info-field-label q-mr-xs']" + ")["+i+"]";
            String valuePath = "(" + mpMetaDataPath + " //div[@class='info-field-value'])["+i+"]";

            infoFields.put(
                    actions().findOneElementsText( keyPath).replace(":", "").trim(),
                    actions().findOneElementsText(valuePath).trim());
        }
        return infoFields;
    }

    /**
     * All reportTypes contain data about report duration, but not all reportTypes has the data at same place in DOM.
     */
    String getReportDuration(ReportType reportType) {
        switch (reportType) {
            case INTERVALS_CHART, ANALYSIS, MEASURING_REPORT, BLASTS_REPORT, REGRESSION_REPORT -> {
                return actions().findOneElementsText("(//div[@class='fit'] //div[contains(@class, 'text-body1')])[1] //div");
            }
            case TRANSIENTS_TABLE, TRANSIENT -> {
                return actions().findOneElementsText("(//div[@class='fit'] //div[contains(@class, 'text-body1')])[1] ");
            }
            case INTERVALS_TABLE -> {
                return actions().findOneElementsText("//div[@class='fit'] //div[@class='q-item__section column q-item__section--main justify-center q-py-none text-infra-primary']");
            }
            default -> throw new IllegalArgumentException("Unsupported reportType: " + reportType);
        }
    }

    public List<Tab> getReportTabs() {
        List<Tab> tabs = new ArrayList<>();

        // Intervals tab always present
        Tab intervalsTab = getTab("data_report", "//*[@name='intervals']");
        tabs.add(intervalsTab);

        // Transients tab always present
        Tab transientsTab = getTab("data_report", "//*[@name='transients_report']");
        tabs.add(transientsTab);

        boolean hasNoiseReportTab = actions().elementExistAndVisible("//*[@name='noise_report']", false, 0);
        if (hasNoiseReportTab) {
            Tab noiseReportTab = getTab("data_report", "//*[@name='noise_report']");
            tabs.add(noiseReportTab);
        }

        boolean hasVibrationReportTab = actions().elementExistAndVisible("//*[@name='vib_report']", false, 0);
        if (hasVibrationReportTab) {
            Tab vibReportTab = getTab("data_report", "//*[@name='vib_report']");
            tabs.add(vibReportTab);
        }

        boolean hasMeasuringReportTab = actions().elementExistAndVisible("//*[@name='measuring_report']", false, 0);
        if (hasMeasuringReportTab) {
            Tab measuringReportTab = getTab("data_report", "//*[@name='measuring_report']");
            tabs.add(measuringReportTab);
        }

        boolean hasBlastTab = actions().elementExistAndVisible("//*[@name='blasts_report']", false, 0);
        if (hasBlastTab) {
            Tab blastsTab = getTab("data_report", "//*[@name='blasts_report']");
            tabs.add(blastsTab);
        }

        boolean hasRegressionTab = actions().elementExistAndVisible("//*[@name='regression']", false, 0);
        if (hasRegressionTab) {
            Tab regressionTab = getTab("data_report", "//*[@name='regression']");
            tabs.add(regressionTab);
        }

        return tabs;
    }

    /**
     * From the table in .../report_id/transients
     * @param row   Each row has a recording for one of the measuring points
     * @param column Each column in the table is a measuring point
     */
    public void openTransient(int row, int column) {
        // Check if update spinner is still spinning
        long startTime = System.currentTimeMillis();
        long timeout = 60_000; // 60 seconds

        do {
            PlaywrightActions.sleep(1);

            if (System.currentTimeMillis() - startTime > timeout) {
                throw new IllegalStateException("Timeout exceeded: Spinner still exists after 60 seconds.");
            }
        } while (actions().elementExistAndVisible("//*[@staticClass='sigi-spinner']", false, 0));
        System.out.println("Waited " + (timeout - startTime) + " milliseconds for transient report to open.");

        actions().makeClick("((//tbody[@class='q-virtual-scroll__content'] //tr)["+row+"] //td[@class='q-td cursor-pointer'])["+column+"]");

        // Opening a transient could take quite a while
        actions().elementExistAndVisible("//span[contains(text(), 'Time domain analysis')]", true, 60);
    }

    // Require open transient report with FDA
    public void selectFrequencyDomainOperator(String operator) {
        // Open the dropdown
        actions().makeClick("(//div[@class='fit scroll']/div/div/div)[2] //div[@data-qa-id='freq_op']/ancestor::label");

        PlaywrightActions.sleep(1);

        // Select choice
        actions().makeClick("//div[@role='listbox'] //div[@data-qa-id='"+operator+"']");
    }

    public void updateReportFromTransientsView() {
        // Open three button menu
        actions().makeClick("//div[@role='toolbar'] //i[contains(text(), 'more_vert')]");
        PlaywrightActions.sleep(1);
        actions().makeClick("//div[@role='menu'] //div[contains(text(), 'Update')]");
    }

    public String getSaveReportButtonOpacity() {
        return actions().findOneElementsCssValue("//div[@role='dialog'] //button[@data-qa-id='confirm']", "opacity");
    }

    public void closeReport() {
        actions().makeClick("//i[contains(text(), 'close')]");
    }

    public void useReturnArrow() {
        actions().makeClick("//i[contains(text(), 'arrow_back')]");
    }

//      .../project/196958/views/9e2d3fb9-a436-4cfe-af89-71b6f6c15955/measuring_report
    public void followShowLinkInMpReport(String mpName, String time) {
        String showButtonPath = "//tbody//tr[.//span[text()='"+mpName+"'] and .//td[contains(text(), '"+time+"')]][1] //td[.//span[text()='Show']]";

        boolean showButtonExist = actions().elementExistAndVisible(showButtonPath, false, 5);
        if (showButtonExist) {
            // Click on 'Show' on the same row as mpName and time
            actions().makeClick(showButtonPath);
        } else {
            throw new IllegalStateException("No show button for measuring point '" + mpName + "' and transient '" + time +"' found.");
        }
    }

    // ../project/10523/views/c2fcabe1-6a69-4272-904f-6b6fc5d92d98/intervals/chart
    public void selectStandardInIntervalChartReport(String standard) {
        // Open standard dropdown for the first mp_chart
        actions().makeClick("((//div[@class='relative-position']/div[@class='q-mr-xs'])[1] //div[@class='q-expansion-item__container relative-position'])[1] //label");

        if (actions().elementExistAndVisible("//div[@role='listbox'] //span[text()='"+standard+"']", true, 1)) {
            // Select the standard
            actions().makeClick("//div[@role='listbox'] //span[text()='"+standard+"']");
        }
    }

    public DataReportCreatePanel getCreateDataReportPanel() {
        DataReportCreatePanel rdrp = new DataReportCreatePanel();

        PanelHeader panelHeader = getPanelHeader();
        rdrp.setPanelHeader(panelHeader);

        FieldWrapper dataWrapper = getFieldWrapperCommonPartsByHeader("Data");
        rdrp.setDataWrapper(dataWrapper);

        dataWrapper.addContent(getDropdownByName("Type"));
        dataWrapper.addContent(getDropdownByPath("(//form //label)[2]"));    // no header, B25-134

        TimeFrame timeFrame = getTimeFrame();
        dataWrapper.addContent(timeFrame);

        FieldWrapper mpWrapper = getFieldWrapperCommonPartsByHeader("Measuring points");
        rdrp.setMeasuringPointsWrapper(mpWrapper);

        SearchBox mpSearch = getSearchBox("//form //label[.//input[contains(@placeholder,'Search...')]]");
        mpWrapper.addContent(mpSearch);

        ToggleField onlyActiveToggleField = getToggle("right", "//form //div[@role='switch' and @aria-label='Only Active']/parent::div");
        mpWrapper.addContent(onlyActiveToggleField);

        ToggleField selectAllMpToggleField = getToggleWithoutText("((//form //div[contains(@class,'q-fieldset')])[2] //div[@role='switch'])[2]");
        mpWrapper.addContent(selectAllMpToggleField);

        List<PanelListItem> mpList = getPanelListItems(true,"//form //div[contains(@class,'mp-list scroll')]/div");
        mpWrapper.addContent(mpList);

        return rdrp;
    }

    /**
     * .../project/17196/views/5016629b-bbdd-4801-b9d9-9538d9c8151a/regression
     */
    public RegressionReport getRegressionReport() {
        RegressionReport report = new RegressionReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        report.setReportTabs(tabs);

        String reportDuration = getReportDuration(REGRESSION_REPORT);
        report.setReportDuration(reportDuration);

        String regressionHeaderPath = "(//div[@class='fit']/div/div[2]/div/div/div)[1]";

        ChartSectionHeader regressionChartHeader = getRegressionAnalysisChartSectionHeader(regressionHeaderPath);
        report.setChartSectionHeader(regressionChartHeader);

//        String regressionBodyPath = "(//div[@class='fit']/div/div)[6]/div";
//        ChartSectionBody regressionChartBody = getRegressionAnalysisChartBody();

        String regressionBodyChartPath = "(//div[@class='fit']/div/div)[6]/div/div[1]";

        // Regression Chart Calculations
        List<Banner> chartCalculationsResults = getRegressionAnalysisChartBanners(regressionBodyChartPath);
        report.setChartBanners(chartCalculationsResults);

        String regressionBodyListPath = "(//div[@class='fit']/div/div)[6]/div/div[2]";

        return report;
    }

    /**
     * .../project/17196/views/5016629b-bbdd-4801-b9d9-9538d9c8151a/regression
     * @return The banners above the regression analysis chart
     */
    private List<Banner> getRegressionAnalysisChartBanners(String chartPath) {
        List<Banner> chartCalculationsBanners = new ArrayList<>();

        String bannersPath = chartPath + "/div[1]";

        Banner quantityBanner = getBannerDropdown(bannersPath + "/div[1]");
        chartCalculationsBanners.add(quantityBanner);

        // These banners share <div> that has information about background
        Banner bValue = getBanner("grandparent", bannersPath + " //div[text()='B: ']/parent::div");
        chartCalculationsBanners.add(bValue);

        Banner aValue = getBanner("grandparent", bannersPath + " //div[text()='A: ']/parent::div");
        chartCalculationsBanners.add(aValue);

        Banner sValue = getBanner("grandparent", bannersPath + " //div[text()='S: ']/parent::div");
        chartCalculationsBanners.add(sValue);

        Banner rValue = getBanner("grandparent", bannersPath + " //div[text()='R: ']/parent::div");
        chartCalculationsBanners.add(rValue);

        // Statistics
        Banner a50Value = getBanner("grandparent", bannersPath + " //div[text()='A50: ']/parent::div");
        chartCalculationsBanners.add(a50Value);

        Banner a84Value = getBanner("grandparent", bannersPath + " //div[text()='A84: ']/parent::div");
        chartCalculationsBanners.add(a84Value);

        Banner a95Value = getBanner("grandparent", bannersPath + " //div[text()='A95: ']/parent::div");
        chartCalculationsBanners.add(a95Value);

        Banner a98Value = getBanner("grandparent", bannersPath + " //div[text()='A98: ']/parent::div");
        chartCalculationsBanners.add(a98Value);

        // Blast used for calculation
        Banner measuredValue = getBanner("grandparent", bannersPath + " //div[text()='Measured values: ']/parent::div");
        chartCalculationsBanners.add(measuredValue);

        return chartCalculationsBanners;
    }

    public VibrationReportView getVibrationReportView() {
        VibrationReportView report = new VibrationReportView();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        List<Tab> tabs = getReportTabs();
        report.setReportTabs(tabs);

        String reportDuration = getReportDuration(INTERVALS_CHART);
        report.setReportDuration(reportDuration);

        String chartHeaderPath = "(//div[@class='fit'] //div[contains(@class, 'text-body1')])[1]";

        Button exportButton = getButton(chartHeaderPath + " //div[text()='Export']/ancestor::button");
        report.setExportButton(exportButton);

        String mpChartSectionPath = "//div[@class='fit scroll']/div/div/div/div/div";

        ChartSectionHeader vibrationReportHeader = getExpansionHeaderVibrationReport(mpChartSectionPath + "[1]");
        report.setMpChartSectionHeader(vibrationReportHeader);

        boolean isExpanded = vibrationReportHeader.getExpansionIcon().getType().equals(EXPANDED);
        if (isExpanded) {

            ChartSectionBody vibrationReportBody = getExpansionBodyVibrationReport(mpChartSectionPath + "[2]/div/div/div");
            report.setMpChartSectionBody(vibrationReportBody);
        }

        return report;
    }

    private int getIntervalTableDisplayCounter() {
        Integer listItems = null;

        String displayCounter = actions().findOneElementsText("//*[@class='q-pa-none fit interval-grid']/div/div[2]");

        // Get the first group of digits in "Displaying XXX of XXX"
        Pattern pattern = Pattern.compile("Displaying (\\d+) of"); // match "Displaying ", followed String one or more digits (captured), followed String " of"
        Matcher matcher = pattern.matcher(displayCounter);

        if (matcher.find()) {
            String output = matcher.group(1); // extract the first captured group (the digits)
            listItems = Integer.parseInt(output);
        }
        return listItems;
    }


    public void makeClickInGraph(String chart, String mpId) {
        // todo: gör denna dynamisk
        actions().makeClick("//div[@id='intervals-"+chart+"-chart-"+mpId+"-project_intervals_report']");
    }
}
