package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.scheduled_report.*;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;

import java.util.ArrayList;
import java.util.List;

import static com.example.helpers.StatusAssesser.Status.ACTIVE;
import static com.example.playwright.helpers.enums.IconType.EXPANDED;

public class ScheduleReportsPO extends CommonPO {

    /**
     * The only difference btw ScheduledReportCreatePanel with activated tab, and e.g., ScheduledReportGeneralPanel,
     * is the row of tabs.
     * Therefore we can use ScheduledReportGeneralPanel in combination with ScheduledReportCreatePanel, or by itself.
     */
    public ScheduledReportCreatePanel getScheduledReportCreatePanel() {
        ScheduledReportCreatePanel panel = new ScheduledReportCreatePanel();

        // Tabs
        String tabsPath = "//div[@role='tablist'] //div[@role='tab']";
        Tab generalTab = getTab("scheduled_report", tabsPath + "[1]");
        panel.setGeneralTab(generalTab);

        Tab measuringPointTab = getTab("scheduled_report", tabsPath + "[2]");
        panel.setMeasuringPointsTab(measuringPointTab);

        Tab recipientTab = getTab("scheduled_report", tabsPath + "[3]");
        panel.setRecipientsTab(recipientTab);

        if (generalTab.getStatus().equals(ACTIVE)) {
            ScheduledReportGeneralPanel generalPanel = getScheduledReportGeneralPanel();
            panel.setGeneralPanel(generalPanel);
        } else if (measuringPointTab.getStatus().equals(ACTIVE)) {
            ScheduledReportMpPanel mpPanel = getScheduledReportMpPanel();
            panel.setMpPanel(mpPanel);
        } else if (recipientTab.getStatus().equals(ACTIVE)) {
            ScheduledReportRecipientsPanel recipientsPanel = getScheduledReportRecipientsPanel();
            panel.setRecipientsPanel(recipientsPanel);
        } else {
            throw new IllegalStateException("At least one tab must be active.");
        }

        return panel;
    }

    /**
     * /project/10523/scheduled_reports/9/settings/recipients
     * or /project/10523/scheduled_reports/create w Tab Recipients active
     */
    public ScheduledReportRecipientsPanel getScheduledReportRecipientsPanel() {
        ScheduledReportRecipientsPanel panel = new ScheduledReportRecipientsPanel();

        PanelHeader header = getPanelHeader("panel");
        panel.setPanelHeader(header);

         Preface preface = getPreface();
         panel.setPreface(preface);

        String recipientPath = "(//form //div[contains(@data-qa-id,'panel-body')] //div[@role='listitem'])[2]";

        Icon searchIcon = completeGetIcon(recipientPath + "/div[1]");
        panel.setSearchIcon(searchIcon);

        InputField searchField = completeGetInputField(recipientPath + "/div[2]/label");
        panel.setSearchField(searchField);

        ToggleField activeToggle = getToggle("right", recipientPath + "/div[3]");
        panel.setActiveToggle(activeToggle);

        // *********************

        Table recipientTable = new Table();

        String tablePath = "//form //table";

        // Get header row
        Table.TableRow headerRow = getScheduleReportRecipientTableHeader(tablePath);
        recipientTable.setHeader(headerRow);

        // Get mp rows
        List<Table.TableRow> tableRows = getScheduleReportRecipientTableContent(tablePath);
        recipientTable.setContent(tableRows);

        panel.setRecipientTable(recipientTable);

        return panel;
    }

    /**
     * /project/10523/scheduled_reports/9/settings/general
     * or /project/10523/scheduled_reports/create w Tab General active
     */
    public ScheduledReportGeneralPanel getScheduledReportGeneralPanel() {
        ScheduledReportGeneralPanel panel = new ScheduledReportGeneralPanel();

        PanelHeader header = getPanelHeader("panel");
        panel.setPanelHeader(header);

         Preface preface = getPreface();
         panel.setPreface(preface);

        // FieldWrappers

        // General
        FieldWrapper general = getFieldWrapperCommonPartsByHeader("General");
        panel.setGeneralWrapper(general);

        InputField nameField = getInputFieldByHeader("Name *");
        general.addContent(nameField);

        // Data
        String dataWrappersPath = "//form //div[contains(@class,'q-fieldset')][2]";

        FieldWrapper data = getFieldWrapperCommonPartsByHeader("Data");
        panel.setDataWrapper(data);

        Dropdown reportType = getDropdownByPath(dataWrappersPath + "/div/div[2]/div[1]/label");
        data.addContent(reportType);

        Dropdown fileFormat = getDropdownByPath(dataWrappersPath + "/div/div[2]/div[2]/label");
        data.addContent(fileFormat);

        Dropdown fileContent = getDropdownByPath(dataWrappersPath + "/div/div[2]/div[3]/label");
        data.addContent(fileContent);

        // Sending time
        String timeWrappersPath = "//form //div[contains(@class,'q-fieldset')][3]";

        FieldWrapper sendingTime = getFieldWrapperCommonPartsByHeader("Schedule");
        panel.setSendingTimeWrapper(sendingTime);

        NoticeItem notice = getNoticeItem(timeWrappersPath + "/div/div[2]/div[1]"); // todo: ev fler steg
        sendingTime.setNoticeItem(notice);

        Dropdown frequency = getDropdownByPath(timeWrappersPath + "/div/div[2]/div[2]/label");
        sendingTime.addContent(frequency);

        Dropdown sendOn = getDropdownByPath(timeWrappersPath + "/div/div[2]/div[3]/label");
        sendingTime.addContent(sendOn);

        Dropdown timeOfDay = getDropdownByPath(timeWrappersPath + "/div/div[2]/label");
        sendingTime.addContent(timeOfDay);

        return panel;
    }

    /**
     * /project/10523/scheduled_reports/9/settings/measure_points
     * or /project/10523/scheduled_reports/create w Tab Measuring Points active
     */
    public ScheduledReportMpPanel getScheduledReportMpPanel() {
        ScheduledReportMpPanel panel = new ScheduledReportMpPanel();

        PanelHeader header = getPanelHeader("panel");
        panel.setPanelHeader(header);

         Preface preface = getPreface();
         panel.setPreface(preface);

        String mpPath = "(//form //div[contains(@data-qa-id,'panel-body')] //div[@role='listitem'])[2]";

        Icon searchIcon = completeGetIcon(mpPath + "/div[1]");
        panel.setSearchIcon(searchIcon);

        InputField searchField = completeGetInputField(mpPath + "/div[2]/label");
        panel.setSearchField(searchField);

        ToggleField activeToggle = getToggle("right", mpPath + "/div[3]");
        panel.setActiveToggle(activeToggle);

        // *********************

        Table mpTable = new Table();

        // Get header row
        Table.TableRow headerRow = getScheduleReportMpTableHeader("//form //table");
        mpTable.setHeader(headerRow);

        // Get mp rows
        List<Table.TableRow> tableRows = getScheduleReportMpTableContent("//form //table");
        mpTable.setContent(tableRows);

        panel.setMpTable(mpTable);

        return panel;
    }

    // todo: merge with MessageRulesPO.getMessageRuleMeasuringPointHeaderRow()?
    private Table.TableRow getScheduleReportMpTableHeader(String tablePath) {
        Table.TableRow headerRow = new Table.TableRow();

        String headerRowPath = tablePath + "/thead/tr[position()=1]";

        int columnCount = actions().countHowManyElements(headerRowPath + "/th");

        for (int c = 1; c <= columnCount; c++) {
            String cellPath = headerRowPath + "/th["+c+"]";

            // The first column has no 'select_all' checkbox
            String headerValue = (c == 1)
                    ? ""
                    : actions().findOneElementsText(cellPath);
            headerRow.addContent(headerValue);
        }

        return headerRow;
    }

    private List<Table.TableRow> getScheduleReportMpTableContent(String tablePath) {
        List<Table.TableRow> mpRows = new ArrayList<>();

        String tableBodyPath = tablePath + "/tbody";

        int rowCount = actions().countHowManyElements(tableBodyPath + "/tr");
        int columnCount = 5;

        // For each row
        for (int r = 1; r <= rowCount; r++) {
            Table.TableRow mpTableRow = new Table.TableRow();

            String rowPath = tableBodyPath + "/tr["+r+"]";

            // For each column
            for (int c = 1; c <= columnCount; c++) {

                String cellPath = rowPath + "/td["+c+"]";

                if (c == 1) {
//                    Icon checkbox = completeGetIcon(cellPath + "/div");
                    Checkbox checkbox = getCheckbox(cellPath + "/div[@role='checkbox']");
                    mpTableRow.addContent(checkbox);
                } else {
                    String cellText = actions().findOneElementsText(cellPath);
                    mpTableRow.addContent(cellText);
                }
            }

            mpRows.add(mpTableRow);
        }

        return mpRows;
    }

    private Table.TableRow getScheduleReportRecipientTableHeader(String tablePath) {
        Table.TableRow headerRow = new Table.TableRow();

        String headerRowPath = tablePath + "/thead/tr[position()=1]";

        int columnCount = actions().countHowManyElements(headerRowPath + "/th");

        for (int c = 1; c <= columnCount; c++) {
            String cellPath = headerRowPath + "/th["+c+"]";

            if (c == 1) {
//                Icon checkbox = completeGetIcon(cellPath + "/span/div");
                Checkbox checkbox = getCheckbox(cellPath + "/span/div[@role='checkbox']");

                headerRow.addContent(checkbox);
            } else {
                String headerValue = actions().findOneElementsText(cellPath);
                headerRow.addContent(headerValue);
            }
        }

        return headerRow;
    }

    private List<Table.TableRow> getScheduleReportRecipientTableContent(String tablePath) {
        List<Table.TableRow> mpRows = new ArrayList<>();

        String tableBodyPath = tablePath + "/tbody";

        int rowCount = actions().countHowManyElements(tableBodyPath + "/tr");
        int columnCount = 4;

        // For each row
        for (int r = 1; r <= rowCount; r++) {
            Table.TableRow mpTableRow = new Table.TableRow();

            String rowPath = tableBodyPath + "/tr["+r+"]";

            // For each column
            for (int c = 1; c <= columnCount; c++) {

                String cellPath = rowPath + "/td["+c+"]";

                if (c == 1) {
//                    Icon checkbox = completeGetIcon(cellPath + "/div");
                    Checkbox checkbox = getCheckbox(cellPath + "/div[@role='checkbox']");
                    mpTableRow.addContent(checkbox);
                } else {
                    String cellText = actions().findOneElementsText(cellPath);
                    mpTableRow.addContent(cellText);
                }
            }

            mpRows.add(mpTableRow);
        }

        return mpRows;
    }

    /**
     * /project/10523/scheduled_reports/9/details
     */
    public ScheduledReportDetailsPanel getScheduledReportDetailsPanel() {
        ScheduledReportDetailsPanel panel = new ScheduledReportDetailsPanel();

        PanelHeader header = getPanelHeader("panel");
        panel.setPanelHeader(header);

         Preface preface = getPreface();
         panel.setPreface(preface);

        Button mpSummary = getButtonWithTextToTheRight("(//form //div[@data-qa-id='panel-body'] //a)[1]");
        panel.setMpSummary(mpSummary);

        Button recipientSummary = getButtonWithTextToTheRight("(//form //div[@data-qa-id='panel-body'] //a)[2]");
        panel.setRecipientSummary(recipientSummary);

        SettingsItem settings = getSettingsItemByDataQaId("project_scheduled_report_settings");
        panel.setSettings(settings);

        return panel;
    }

    /**
     * /project/10523/scheduled_reports/9/settings
     */
    public ScheduledReportSettingsPanel getScheduledReportSettingsPanel() {
        ScheduledReportSettingsPanel panel = new ScheduledReportSettingsPanel();

        PanelHeader header = getPanelHeader("panel");
        panel.setPanelHeader(header);

        Preface preface = getPreface();
        panel.setPreface(preface);

        SettingsItem generalSettings = getSettingsItemByDataQaId("project_scheduled_report_general_settings");
        panel.setGeneral(generalSettings);

        SettingsItem measuringPointsSettings = getSettingsItemByDataQaId("project_scheduled_report_measure_points_settings");
        panel.setMeasuringPoints(measuringPointsSettings);

        SettingsItem recipientsSettings = getSettingsItemByDataQaId("project_scheduled_report_recipients_settings");
        panel.setRecipients(recipientsSettings);

        SettingsItem advancedSettings = getSettingsItemByPath("//div[contains(@aria-label,'Advanced settings')]", true);
        panel.setAdvancedSettings(advancedSettings);

        if (advancedSettings.getExpansionIcon().getType().equals(EXPANDED)) {
            SettingsItem delete = getSettingsItemByPath("//div[@custom-key='advanced']/div/div[2]");
            panel.setDeleteSettings(delete);
        }

        return panel;
    }
}
