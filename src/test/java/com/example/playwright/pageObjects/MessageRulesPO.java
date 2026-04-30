package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.message_rule.*;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.helpers.StatusAssesser.Status.ACTIVE;


public class MessageRulesPO extends CommonPO {

    // .../create & .../settings/general
    private static final String PANEL_BODY = "//*[@data-qa-id='panel_body']";
    private static final String INPUT_NAME = "//input[@data-qa-id='name']";

    private static final String SLIDER_EMAIL = "//div[text()='E-mail peak values']/following::div[contains(@class, 'q-toggle__inner relative-position non-selectable q-toggle__inner--falsy')]";
    private static final String SLIDER_SMS = "//div[text()='SMS peak values']/following::div[contains(@class, 'q-toggle__inner relative-position non-selectable q-toggle__inner--falsy')]";
    private static final String SLIDER_REPORT = "//div[text()='E-mail transient report']/following::div[contains(@class, 'q-toggle__inner relative-position non-selectable q-toggle__inner--falsy')]";


    // .../details
    private static final String SETTINGS = "//*[@data-qa-id='project_message_rule_settings']";

    // .../settings
    private static final String DELETE = "//*[text()='Delete']";

    // .../settings, delete dialog
    private static final String CONFIRM_CHECKBOX = "//*[text()='I understand']";
    private static final String DO2_DELETE = "//*[@class='block'][contains(text(),'Delete')]";

//    protected final SeleniumApi selenium;
//
//    public MessageRulesPO() {
//        this.selenium = PlaywrightActions.getInstance();
//    }

    public void createMessageRule(String name, String trigTypes, Optional<List<String>> content, String triggerType, Optional<String> channel, String value, Optional<String> recipient) {
        // General
        setGeneral(name, trigTypes);

        // Content
        if (content.isPresent()) {
            clickOnTab("Content");
            // ...
        }

        // Select Sending threshold tab
        clickOnTab("Sending thresholds");
        setSendingThresholds("top", triggerType, channel, value);

        // Recipients
        if (recipient.isPresent()) {
            clickOnTab("Recipients");
            // ...
        }

        clickNamedButton("Save");
        PlaywrightActions.sleep(2);
    }

    private void setSendingThresholds(String mpName, String triggerType, Optional<String> channel, String value) {
        // Select mp
        //Todo: addera stöd för flera mp
        if (mpName.equals("top")) {
            actions().makeClick("(//div[@role='checkbox'])[2]");
        } else {
            actions().makeClick("//div[text()='"+mpName+"']/ancestor::tr //div[@role='checkbox']");
        }

        // Select dropdown if we want absolute value
        if (triggerType.equals("absolute") && channel.isPresent()) {
            PlaywrightActions.sleep(1);
            actions().makeClick("//div[@class='table-select']");
            PlaywrightActions.sleep(1);
            actions().makeClick("//span[text()='Absolute values']");

            // Open values dialog
            actions().hoverAboveElementAndClickOnTheNoMoreHiddenElement("//form //div[@class='ellipsis']", "//form //i[text()='edit']");
            actions().clearAndType("//div[contains(text(),'"+channel.get()+"')]/ancestor::div[contains(@class,'q-item q-item-type row no-wrap col-12 q-pa-none')]/following-sibling::div//input", value);

            clickNamedButton("Apply");
        }
    }

    public void setGeneral(String name, String trigTypes) {
        actions().clearAndType(INPUT_NAME, name);

        switch (trigTypes) {
            case "0100" ->
                    actions().makeClick(SLIDER_EMAIL);
            case "1100" -> {
                actions().makeClick(SLIDER_EMAIL);
                actions().makeClick(SLIDER_SMS);
            }
            case "1001" -> {
                actions().makeClick(SLIDER_EMAIL);
                actions().makeClick(SLIDER_SMS);
                actions().makeClick(SLIDER_REPORT);
            }
            case "0001" -> {
                actions().makeClick(SLIDER_REPORT);
            }
                default -> throw new IllegalArgumentException("Unexpected trigTypes: " + trigTypes);
        }
    }


    public void updateMessageRuleName(final String name) {
        actions().clearAndType(INPUT_NAME, name);
        clickNamedButton("Save");
        PlaywrightActions.sleep(2);
    }

    public void deleteMessageRule() {
        // Click on Delete
        actions().makeClick(DELETE);

        // Tick confirmation box
        actions().makeClick(CONFIRM_CHECKBOX);

        // Click on Delete (confirm) button.
        actions().makeClick(DO2_DELETE);
        PlaywrightActions.sleep(2);
    }

    /**
     * There are two toggles at recipients tab on a Message Rule
     * @return true or false, but fails if there is no toggle at all.
     */
    public boolean getToggleState(String toggleText) {
        String togglePath = switch (toggleText) {
            case "Show project's users" -> "(//form //div[@role='switch'])[2]";
            case "Show selected" -> "(//form //div[@role='switch'])[1]";
            default -> throw new IllegalStateException("Unexpected case value: " + toggleText);
        };

        boolean toggleExist = actions().elementExistAndVisible(togglePath, false, 0);

        String toggleState = null;
        if (toggleExist) {
            toggleState = actions().findOneElementsAttribute(togglePath, "aria-checked");
        } else {
            throw new IllegalStateException("Toggle 'Show project's users' could not be found.");
        }
        return toggleState.equals("true");
    }

    public void setMRRecipientToggle(String toggleText) {
        String togglePath = switch (toggleText) {
            case "Show project's users" -> "(//form //div[@role='switch'])[2]";
            case "Show selected" -> "(//form //div[@role='switch'])[1]";
            default -> throw new IllegalStateException("Unexpected case value: " + toggleText);
        };
        actions().makeClick(togglePath);
    }

    public List<String> getMessageRuleRecipientUserList() {
        return actions().findManyElementsTexts("(//tbody //tr) //td[2]");
    }

    public MessageRuleCreatePanel getMessageRuleCreatePanel() {
        MessageRuleCreatePanel panel = new MessageRuleCreatePanel();

        List<Tab> tabs = new ArrayList<>();
        int tabCount = 4;
        for (int t = 1; t <= tabCount; t++) {
            String tabPath = "//div[@role='tablist']/div/div[" + t + "]";
            Tab tab = getTab("message_rule", tabPath);
            tabs.add(tab);
        }
        panel.setTabs(tabs);

        Tab activeTab = tabs.stream()
                .filter(tab -> tab.getStatus().equals(ACTIVE))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active tab was found."));


        // Create panel have one of these active
        if (activeTab.getText().equals("General")) {
            MessageRuleGeneralPanel subPanel = getMessageRuleGeneralPanel();
            panel.setMessageRuleGeneralPanel(subPanel);

        } else if (activeTab.getText().equals("Content")) {
            MessageRuleContentPanel subPanel = getMessageRuleContentPanel();
            panel.setMessageRuleContentPanel(subPanel);

        } else if (activeTab.getText().equals("Sending thresholds")) {
            MessageRuleSendingThresholdPanel subPanel = getMessageRuleSendingThresholdsPanel();
            panel.setMessageRuleSendingThresholdPanel(subPanel);

        } else if (activeTab.getText().equals("Recipients")) {
            MessageRuleRecipientsPanel subPanel = getMessageRuleRecipientPanel();
            panel.setMessageRuleRecipientsPanel(subPanel);
        }

        return panel;
    }

    private MessageRuleGeneralPanel getMessageRuleGeneralPanel() {
        MessageRuleGeneralPanel panel = new MessageRuleGeneralPanel();

        boolean hasBgWarning = actions().elementExistAndVisible("//form //div[contains(@class,'bg-warning')]", false, 0);
        if (hasBgWarning) {
            NoticeItem warning = getNoticeItem("//form //div[contains(@class,'bg-warning')]");
            panel.setTransientReportWarningMsg(warning);
        }

        FieldWrapper general = getFieldWrapperCommonPartsByHeader("General");
        panel.setGeneralWrapper(general);

        InputField name = getInputFieldByHeader("Name *");
        general.addContent(name);

        ToggleField emailPeakValue = completeGetToggleField("none", "//form //div[text()='E-mail peak values']/ancestor::label");
        general.addContent(emailPeakValue);

        ToggleField emailTransientReportTab = completeGetToggleField("none", "//form //div[text()='E-mail transient report']/ancestor::label");
        general.addContent(emailTransientReportTab);

        ToggleField smsPeakValuesTab = completeGetToggleField("none", "//form //div[text()='SMS peak values']/ancestor::label");
        general.addContent(smsPeakValuesTab);

        ToggleField blastManagerTab = completeGetToggleField("none", "//form //div[text()='Blast manager']/ancestor::label");
        general.addContent(blastManagerTab);

        return panel;
    }

    private MessageRuleRecipientsPanel getMessageRuleRecipientPanel() {
        MessageRuleRecipientsPanel panel = new MessageRuleRecipientsPanel();
        return null;
    }

    // /message_rules/36491/settings/content
    public MessageRuleContentPanel getMessageRuleContentPanel() {
        MessageRuleContentPanel mrcp = new MessageRuleContentPanel();
        String panelPath = "//div[@data-qa-id='message-rules-content-panel']";

        mrcp.setPanelHeader(getPanelHeader());
        mrcp.setPreface(getPreface());

        FieldWrapper projectInfo = getFieldWrapperCommonPartsByHeader("Project info");
        mrcp.setProjectInfoWrapper(projectInfo);

        projectInfo.addContent(getFormToggleByName("above", "Company name"));
        projectInfo.addContent(getFormToggleByName("above", "Project name"));
        projectInfo.addContent(getFormToggleByName("above", "Message name"));

        FieldWrapper measuringPointInfo = getFieldWrapperCommonPartsByHeader("Measuring point info");
        mrcp.setMeasuringPointInfoWrapper(measuringPointInfo);

        measuringPointInfo.addContent(getFormToggleByName("above", "Measuring point name"));
        measuringPointInfo.addContent(getFormToggleByName("above", "Measuring point description"));
        measuringPointInfo.addContent(getFormToggleByName("above", "Monitoring device name"));
        measuringPointInfo.addContent(getFormToggleByName("above", "Monitoring device id #"));

        FieldWrapper measuredValue = getFieldWrapperCommonPartsByHeader("Measured value");
        mrcp.setMeasuredValueWrapper(measuredValue);

        measuredValue.addContent(getFormToggleByName("above", "Value"));
        measuredValue.addContent(getFormToggleByName("above", "Date"));
        measuredValue.addContent(getFormToggleByName("above", "Time"));
        measuredValue.addContent(getFormToggleByName("above", "Channel"));
        measuredValue.addContent(getFormToggleByName("above", "Standard"));

        return mrcp;
    }

    // .../project/10523/message_rules/39520/details
    public MessageRuleDetailsPanel getMessageRuleDetailsPanel() {
        MessageRuleDetailsPanel panel = new MessageRuleDetailsPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

//        Map<String, Object> mpSummary = getMessageRuleSummary("(//form //a[@role='listitem'])[1]");
//        panel.setMpSummary(mpSummary);

        Button mpSummaryButton = getButton("(//form //a[@role='listitem'])[1]");
        panel.setMpSummaryButton(mpSummaryButton);

//        Map<String, Object> recipientSummary = getMessageRuleSummary("(//form //a[@role='listitem'])[2]");
//        panel.setRecipientSummary(recipientSummary);

        Button recipientSummaryButton = getButton("(//form //a[@role='listitem'])[2]");
        panel.setRecipientSummaryButton(recipientSummaryButton);

        List<String> content = actions().findManyElementsTexts( "//form //div[text()='Content']/preceding-sibling::div //div[@class='ellipsis']");
        panel.setContent(content);

        SettingsItem settings = getSettingsItemByDataQaId("project_message_rule_settings");
        panel.setSettings(settings);

        return panel;
    }

//    /**
//     * @return A map of the icon and text above 'content' in /message_rules/mr_id/details
//     */
//    private Map<String, Object> getMessageRuleSummary(String summaryPath) {
//        Map<String, Object> summaryMap = new HashMap<>();
//
//        IconType icon = getIconType(summaryPath + " //i");
//        summaryMap.put("icon", icon);
//
//        String text = actions().findOneElementsText("(" + summaryPath + "  //div)[last()]");
//        summaryMap.put("text", text);
//
//        return summaryMap;
//    }

    public MessageRuleSendingThresholdPanel getMessageRuleSendingThresholdsPanel() {
        MessageRuleSendingThresholdPanel panel = new MessageRuleSendingThresholdPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        boolean hasBgWarning = actions().elementExistAndVisible("//form //div[contains(@class,'bg-warning')]", false, 0);
        if (hasBgWarning) {
            NoticeItem warning = getNoticeItem("//form //div[contains(@class,'bg-warning')]");
            panel.setTransientReportWarningMsg(warning);
        }

        FieldWrapper measuringPoint = getFieldWrapperCommonPartsByHeader("Measuring points");
        panel.setMeasuringPointWrapper(measuringPoint);

        Table mpTable = getMrMeasuringPointsTable();
        panel.setMpTable(mpTable);

        return panel;
    }

    /**
     * Table from ...project/10523/message_rules/36491/settings/thresholds where all have checkbox.
     */
    public Table getMrMeasuringPointsTable() {
        Table table = new Table();

        String tablePath = "//form //table";

        // Get header row
        Table.TableRow headerRow = getMessageRuleMeasuringPointHeaderRow(tablePath);
        table.setHeader(headerRow);

        // Get mp rows
        List<Table.MpTableRow> tableRows = getMessageRuleMeasuringPointsTableRows(tablePath);
        table.setMessageRuleMpRows(tableRows);

        return table;
    }

    private Table.TableRow getMessageRuleMeasuringPointHeaderRow(String tablePath) {
        Table.TableRow headerRow = new Table.TableRow();

        String headerRowPath = tablePath + "/thead/tr[position()=1]";

        int columnCount = actions().countHowManyElements(headerRowPath + "/th");

        for (int c = 1; c <= columnCount; c++) {
            String cellPath = headerRowPath + "/th["+c+"]";

            if (c == 1) {
                Checkbox checkbox = getCheckbox(cellPath + "/div");
                headerRow.addContent(checkbox);

            } else {
                String headerValue = actions().findOneElementsText(cellPath);
                headerRow.addContent(headerValue);
            }
        }

        return headerRow;
    }

    /**
     * project/10523/message_rules/36491/settings/thresholds
     */
    private List<Table.MpTableRow> getMessageRuleMeasuringPointsTableRows(String tablePath) {
        List<Table.MpTableRow> tableRows = new ArrayList<>();

        String tableBodyPath = tablePath + "/tbody";

        int rowCount = actions().countHowManyElements(tableBodyPath + "/tr");
        int columnCount = 4;

        // for each row
        for (int r = 1; r <= rowCount; r++) {
            Table.MpTableRow mpTableRow = new Table.MpTableRow();

            String rowPath = tableBodyPath + "/tr["+r+"]";

            for (int c = 1; c <= columnCount; c++) {

                String cellPath = rowPath + "/td["+c+"]";

                if (c == 1) {

//                    Icon checkbox = completeGetIcon(cellPath + "/div");
                    Checkbox checkbox = getCheckbox(cellPath + "/div");
                    mpTableRow.setCheckbox(checkbox);

                } else if (c == 2) {
                    Icon mpIcon = completeGetIcon(cellPath + " //i/parent::div");
                    mpTableRow.setMpTypeIcon(mpIcon);

                    String mpName = actions().findOneElementsText(cellPath + "/div/div[2]/div[1]");
                    mpTableRow.setMpName(mpName);

                    boolean hasMpDescription = actions().elementExistAndVisible(cellPath + "/div/div[2]/div[2]", false, 0);
                    if (hasMpDescription) {
                        String mpDescription = actions().findOneElementsText(cellPath + "/div/div[2]/div[2]");
                        mpTableRow.setMpDescription(mpDescription);
                    }

                    String sensorAndSerial = actions().findOneElementsText(cellPath + "/div/div[2]/div[3]/div[1]");
                    mpTableRow.setMpSensor(sensorAndSerial);

                    String monitoringSetting = actions().findOneElementsText(cellPath + "/div/div[2]/div[3]/div[2]");
                    mpTableRow.setMpSensorSetting(monitoringSetting);

                } else if (c == 3) {

                    Dropdown triggerSelector = getDropdownByPath(cellPath + "/div[1]/label");
                    mpTableRow.setTriggerSelector(triggerSelector);

                    String lastUpdated = actions().findOneElementsText(cellPath + "/div[2]");
                    mpTableRow.setLastUpdated(lastUpdated);

                } else if (c == 4) {

                    // If no channels, then there is a red banner 'No triggers set'
                    boolean hasBanner = actions().elementExistAndVisible(cellPath + "/div/div[2]", false, 0);
                    if (hasBanner) {

                        String banner = actions().findOneElementsText(cellPath + "/div/div[2]");
                        mpTableRow.setBanner(banner);

                    } else {

                        List<String> channels = actions().findManyElementsTexts(cellPath + "/div/div");
                        mpTableRow.setChannels(channels);

                    }
                }
            }

            tableRows.add(mpTableRow);
        }

        return tableRows;
    }


    public void setToggle(String toggleHeaderText) {
        actions().makeClick("//form //div[text()='E-mail transient report']/ancestor::label //div[@role='switch']");
    }
}
