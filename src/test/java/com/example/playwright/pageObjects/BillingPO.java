package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.CompanyBillingReportCreatePanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.Table.TableRow;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.view.billingReports.AccountDevicesBillingReport;
import com.example.playwright.components.view.billingReports.AccountProjectsBillingReport;
import com.example.playwright.components.view.billingReports.ProjectMeasuringPointsBillingReport;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.ArrayList;
import java.util.List;

public class BillingPO extends CommonPO {

    // Account level
    private static final String PANEL_BODY_ACCOUNT = "//*[@data-qa-id='create-billing-report-panel-body']";
    private static final String CREATE_REPORT_BUTTON_ACCOUNT = "//*[@data-qa-id='create-billing-report-panel-btn-save']";

    // Project level
    private static final String PANEL_BODY_PROJECT = "//*[@data-qa-id='create-billing-report-project-panel-body']";
    private static final String DROP_DOWN_REPORT_PERIOD = PANEL_BODY_PROJECT + " //i[contains(text(), 'arrow_drop_down')]";
    private static final String CREATE_REPORT_BUTTON_PROJECT = "//*[@data-qa-id='create-billing-report-project-panel-btn-save']";

    public AccountProjectsBillingReport getAccountProjectsBillingReport() {
        AccountProjectsBillingReport report = new AccountProjectsBillingReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        String reportDuration = actions().findOneElementsText("((//div[@class='q-page-container'] //div[@class='fit'] //div //div)[1]/div)[2]/div[1]");
        report.setReportDuration(reportDuration);

        Button exportButton = getButton("((//div[@class='q-page-container'] //div[@class='fit'] //div //div)[1]/div)[2] //button");
        report.setExportButton(exportButton);

        Button columnFilter = getButton("//div[@class='q-page-container'] //div[@class='fit'] //div[@name='ProjectReportDataGrid'] //button");
        report.setColumnFilter(columnFilter);

        Table reportContent = getProjectBillingReportTable();
        report.setReportContent(reportContent);

        return report;
    }

    public AccountDevicesBillingReport getAccountDeviceBillingReport() {
        AccountDevicesBillingReport report = new AccountDevicesBillingReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        String reportDuration = actions().findOneElementsText("((//div[@class='q-page-container'] //div[@class='fit'] //div //div)[1]/div)[2] //div");
        report.setReportDuration(reportDuration);

        Button exportButton = getButton("((//div[@class='q-page-container'] //div[@class='fit'] //div //div)[1]/div)[2] //button");
        report.setExportButton(exportButton);

        Table table = getDeviceBillingReportTable();
        report.setReportContent(table);

        return report;
    }

    public ProjectMeasuringPointsBillingReport getProjectMeasuringPointsBillingReport() {
        ProjectMeasuringPointsBillingReport report = new ProjectMeasuringPointsBillingReport();

        PanelHeader panelHeader = getPanelHeader("report");
        report.setPanelHeader(panelHeader);

        String reportDuration = actions().findOneElementsText("((//div[@class='q-page-container'] //div[@class='fit'] //div //div)[1]/div)[2] //div");
        report.setReportDuration(reportDuration);

        Button exportButton = getButton("((//div[@class='q-page-container'] //div[@class='fit'] //div //div)[1]/div)[2] //button");
        report.setExportButton(exportButton);

        Table reportContent = getMeasuringPointBillingReportTable();
        report.setReportContent(reportContent);

        return report;
    }

    /**
     * Works for both AccountDevicesBillingReport and ProjectMeasuringPointsBillingReport
     */
    public void waitForBillingReportCompletion() {

        System.out.println("Waiting for Billing Report creation to be finished.");
        // Give some space for creation to begin.
        PlaywrightActions.sleep(3);

        boolean isCreating = actions().elementExistAndVisible("//div[@class='load-mask-inner row bg-white text-infra-primary absolute-center']", false, 0);
        int counter = 0;
        int waitMax = 120;
        while (isCreating && counter < waitMax){
            PlaywrightActions.sleep(1);
            counter++;
            System.out.println("Billing report in creation. Waiting " + counter + " of " + waitMax);

            isCreating = actions().elementExistAndVisible("//div[@class='load-mask-inner row bg-white text-infra-primary absolute-center']", false, 0);
        }

        if (isCreating) {
            throw new IllegalStateException("Billing report still in creation after 120 seconds.");
        } else {
            System.out.println("Report is done creating");

            // If the creation is done, then we shall be able to get the id from url, and
        }
    }

    private Table getMeasuringPointBillingReportTable() {
        Table table = new Table();

        String tablePath = "//div[@class='container fixed-top fullscreen'] //table";
        int columns = actions().countHowManyElements(tablePath + "/thead/tr/th");

        // Get header row
        TableRow headerRow = getMeasuringPointBillingReportTableHeaders(columns, tablePath + "/thead/tr");
        table.setHeader(headerRow);

        List<TableRow> tableRows = getMeasuringPointBillingReportTableContent(tablePath, columns);
        table.setContent(tableRows);

        return table;
    }

    // todo: merge med DeviceBillingReportTableHeaders?
    private TableRow getMeasuringPointBillingReportTableHeaders(int columns, String headerRowPath) {
        TableRow headerRow = new TableRow();

        for (int c = 1; c <= columns; c++) {
            String header = switch (c) {
                case 1, 6 -> "";    // First column for MeasuringPoint Billing Report is icon, and last column for MeasuringPoint Billing Report is expandIcon
                default -> actions().findOneElementsText(headerRowPath + "/th[position()="+c+"]");
            };

            headerRow.addContent(header);
        }

        return headerRow;
    }

    // todo: merge with getDeviceBillingReportTableContent
    private List<TableRow> getMeasuringPointBillingReportTableContent(String tablePath, int columns) {
        List<TableRow> rows = new ArrayList<>();

        String rowPath = tablePath + "/tbody/tr[not(@style='display: none;')]";

        // Rows that are can be expanded, but aren't, are still in the DOM. Don't read them.
        int visibleRows = actions().countHowManyElements(rowPath );

        for (int r = 1; r <= visibleRows; r++) {
            TableRow row = new TableRow();

            // count /td so that we know if the row is expansion row, or expanded row
            int tdChildCount = actions().countHowManyElements(rowPath + "[position()="+r+"]/td");

            switch (tdChildCount) {
                case 6 -> {     // A expansion row
                    for (int c = 1; c <= columns; c++) {
                        // First and last column value is Icon, the rest String
                        Object cellValue = switch (c) { // First column for MeasuringPoint Billing report is icon, and last column for MeasuringPoint Billing report is expandIcon
                            case 1 -> completeGetIcon(rowPath + "[position()="+r+"]" + "/td[position()="+c+"]");
                            case 6 -> getButton(rowPath + "[position()="+r+"]" + "/td[position()="+c+"]/button");
                            default -> actions().findOneElementsText(rowPath + "[position()="+r+"]" + "/td[position()="+c+"]");
                        };

                        row.addContent(cellValue);
                    }
                }
                case 4 -> {     // An expanded row, with four /td and content in three of them
                    for (int c = 1; c <= columns; c++) {
                        Object cellValue = switch (c) {
                            case 1, 2, 6 -> "";   // Expanded rows have columns 1 - 2 combined into one, the last is a non existent expansion icon
                            case 3, 4, 5 -> actions().findOneElementsText(rowPath + "[position()="+r+"]" + "/td[position()="+ (c - 1) +"]");
                            default -> throw new IllegalArgumentException("Cannot find value for column: " + c);
                        };

                        row.addContent(cellValue);
                    }
                }
                default -> throw new IllegalStateException("Only 4 or 6 td children are expected: " + tdChildCount);
            }

            rows.add(row);
        }
        return rows;
    }


    public String getHeadlines() {
        return actions().findOneElementsText("//div[@class='q-item__section column q-item__section--main justify-center text-title']", 20);
    }

    public void createProjectBillingReport(final String timeSpan) {
        // Open drop down for period
        actions().makeClick(DROP_DOWN_REPORT_PERIOD);
        // Choose for which period the report should be.
        actions().makeClick("//div[@role='listbox']//div[contains(text(), '" + timeSpan + "')]");

        // Tick the box that selects all Measuring Points
        actions().makeClick("//table //div[@role='checkbox']");

        // Create the report
        actions().makeClick(CREATE_REPORT_BUTTON_PROJECT);
    }

    public TableColumnSettingsPO openTableColumnSettings() {
        String columnSelector = "//div[@class='q-table__top relative-position row items-center']//i[@role='img']";
        actions().makeClick(columnSelector);
        return new TableColumnSettingsPO();
    }

    /**
     * @return List with selected columns to show in result.
     */
    public List<String> displayedColumns() {
        String columnPath = "//table//thead//th";
        if (actions().elementExistAndVisible(columnPath, false, 2)) {
            return actions().findManyElementsTexts(columnPath);
        } else {
            throw new IllegalStateException("No columns could be found.");
        }
    }

    public List<String> getAvailableExportFormats() {
        // Open Export menu
        if (actions().elementExistAndVisible("//button//*[contains(text(), 'Export')]", false, 0)) {
            actions().makeClick("//button//*[contains(text(), 'Export')]");
            PlaywrightActions.sleep(1);
        } else {
            throw new IllegalStateException("Export button not present.");
        }

        if (actions().elementExistAndVisible("//div[@role='menu']", false, 0)) {
            return actions().findManyElementsTexts("//div[@role='menu']//div[@role='listitem']");
        } else {
            throw new IllegalStateException("No menu panel present.");
        }
    }

    public void selectReportPeriod(String level, String timeSpan) {
        // Open drop down
        switch (level) {
            case "account" -> actions().makeClick("//div[@data-qa-id= 'create-billing-report-panel'] //label");
            case "project" -> actions().makeClick("//div[@data-qa-id= 'create-billing-report-project-panel'] //label");
            default -> throw new IllegalStateException("Unknown level: " + level);
        }

        // Select for which period the report should be.
        actions().makeClick("//div[@role='listbox']//div[contains(text(), '" + timeSpan + "')]");
    }

    public void selectReportPeriodByPlaceInDropdown(int place) {
        // Open drop down
        actions().makeClick("//div[@data-qa-id= 'create-billing-report-panel'] //label");

        // Select for which period the report should be.
        actions().makeClick("(//div[@role='listbox'] //div[@role='option'])[" + place + "]");
    }

    public void selectReportType(String type) {
        // Click on either tab Devices or Projects
        String buttonLabel = PANEL_BODY_ACCOUNT + " //*[contains(text(), '" + type + "')]";
        actions().makeClick(buttonLabel);

        // Wait for either text "Find device" or "Find project" to make sure we are in the right tab
        String waitForText = "Find " + type.toLowerCase();
        String searchText = PANEL_BODY_ACCOUNT + " //input[@placeholder='" + waitForText + "']";
        actions().elementExistAndVisible(searchText, true, 1);
    }

    public void selectProjectCheckboxes(int checkboxesToTick) {
        String projectPath = "(//tbody//*[@role='checkbox'])[position() <=" + checkboxesToTick + "]";
        // Tick the checkboxes
        actions().makeClickOnAllElements(projectPath);
    }

    // .../company/billing_reports/create with report type Device
    public void selectProjectCheckbox(String serial) {
        // Click on the first //td belonging to the //tr that has a //td with the serial
        // This works as well: //tbody //td[contains(text(), '111591')]/preceding-sibling::td
        actions().makeClick("//tbody //td[contains(text(), '"+serial+"')]/parent::tr//td");
    }

    /**
     * Select this many sensor checkboxes
     */
    public void selectSensorCheckboxes(int checkboxesToCheck) {
        for (int i = 1; i <= checkboxesToCheck; i++) {
            String checkboxToCheck = "(//tbody //div[@role='checkbox'])["+i+"]";
            actions().makeClick(checkboxToCheck);
        }
    }

    public void clickCreateButtonAndWait() {
        // Create the report
        actions().makeClick(CREATE_REPORT_BUTTON_ACCOUNT);

        // After create it takes while for spinner to appear, then we're redirected to ...billing_reports/devices/[id]
        PlaywrightActions.sleep(4);

        // A GIF spinner is visible while the report is being generated.
        // Wait for it to disappear to ensure the report is complete.
        actions().elementDoNotExist("//*[@staticClass='sigi-spinner']", 300);
        // todo: hitta ett sätt att gracefully faila här.

        // After create it takes while for spinner to appear, then we're redirected to ...billing_reports/devices/[id]
    }

    public String openCalendarAndGetFirstDayOfWeek() {
        // Click on first date holder
        actions().makeClick("//label[@data-qa-id='time_interval_from']");

        // Get text from calendar week headers first day
        return actions().findOneElementsText("//div[@class='q-date__calendar-item']");
    }

    public void searchForProject(String searchText) {
        PlaywrightActions.sleep(2);
        actions().clearAndType("//input[@placeholder='Find project']", searchText);
        PlaywrightActions.sleep(1);
    }

    public List<String> getAllSelectableDevices() {
        List<String> allTextFromTr = actions().findManyElementsTexts("//tbody //tr");
        List<String> deviceTypeText = new ArrayList<>();
        allTextFromTr.forEach(device -> {
            deviceTypeText.add(device.substring(0, device.indexOf(" ")));
        });
        return deviceTypeText;
    }

    // .../company/billing_reports/create
    public void searchForProjectInBilling(String name) {
        actions().clearAndType("//input[@placeholder='Find project']", name);
    }

    // .../company/billing_reports/create
    // .../project/10523/billing_reports/create
    public CompanyBillingReportCreatePanel getBillingReportCreatePanel(boolean getCalendarContent, boolean getTimeDropdownContent) {
        CompanyBillingReportCreatePanel brcp = new CompanyBillingReportCreatePanel();

        PanelHeader panelHeader = getPanelHeader();
        brcp.setPanelHeader(panelHeader);

        Dropdown durationDropdown = getDropdownByPath("(//div[@data-qa-id='create-billing-report-panel-body'] //label)[1]");
        brcp.setReportDuration(durationDropdown);

        TimeFrame timeFrame = getTimeFrame(getCalendarContent, true);
        brcp.setTimeFrame(timeFrame);

        Tab deviceTab = getTab("billing_report", "//div[@role='tab' and .//span[text()=' Devices']]");
        brcp.setDeviceTab(deviceTab);

        Tab projectTab = getTab("billing_report", "//div[@role='tab' and .//span[text()=' Projects']]");
        brcp.setProjectTab(projectTab);

        ToggleField showActiveOnly = completeGetToggleField("right", "//form //div[@role='switch' and @aria-label='Show active only']");
        brcp.setShowActiveOnlyToggleField(showActiveOnly);

        // 'Show selected'-toggle has dynamic counter
        ToggleField showSelected = completeGetToggleField("right", "//form //div[@role='switch' and contains(@aria-label,'Show selected')]");
        brcp.setShowSelectedToggleField(showSelected);

        // Table
        Table table = getCreateBillingReportTable();
        brcp.setTable(table);

        return brcp;
    }

    private Table getCreateBillingReportTable() {
        Table table = new Table();

        String tablePath = "//form //table";

        TableRow headerRow = getCreateBillingReportHeaderRow(tablePath);
        table.setHeader(headerRow);

        List<TableRow> tableContent = getCreateBillingReportTableContent(tablePath);
        table.setContent(tableContent);

        return table;
    }

    private TableRow getCreateBillingReportHeaderRow(String tablePath) {
        TableRow headerRow = new TableRow();

        String headerRowPath = tablePath + "/thead/tr";

        int columnCount = actions().countHowManyElements(headerRowPath + "/th");

        for (int c = 1; c <= columnCount; c++) {
            String cellPath = headerRowPath + "/th["+c+"]";

            // First column have checkbox
            if (c == 1) {
                Checkbox checkbox = getCheckbox(cellPath + "/span/div");
                headerRow.addContent(checkbox);
            // The rest of columns have text
            } else {
                String headerText = actions().findOneElementsText(cellPath);
                headerRow.addContent(headerText);
            }
        }

        return headerRow;
    }

    private List<TableRow> getCreateBillingReportTableContent(String tablePath) {
        List<TableRow> tableRows = new ArrayList<>();

        String rowsPath = tablePath + "/tbody/tr";

        int rowCount = actions().countHowManyElements(rowsPath);

        for (int r = 1; r <= 2; r++) {
            TableRow tableRow = new TableRow();
            String rowPath = rowsPath + "["+r+"]";

            int columnCount = actions().countHowManyElements(rowPath + "/td");

            for (int c = 1; c <= columnCount; c++) {
                String cellPath = rowPath + "/td["+c+"]";

                // First column have checkbox
                if (c == 1) {
                    Checkbox checkbox = getCheckbox(cellPath + "/div");
                    tableRow.addContent(checkbox);

                // The rest of columns have text
                } else {
                    String headerText = actions().findOneElementsText(cellPath);
                    tableRow.addContent(headerText);
                }
            }

            tableRows.add(tableRow);
        }

        return tableRows;
    }


}
