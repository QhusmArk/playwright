package com.example.playwright.pageObjects;

import com.example.helpers.Randomizer;
import com.example.playwright.components.panels.*;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel.Channel;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel.ChannelWrapper;
import com.example.playwright.components.panels.device.DeviceConnectionHistoryPanel;
import com.example.playwright.components.panels.device.DeviceSettingsPanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.helpers.enums.IconType;
import com.example.playwright.helpers.enums.StandardType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.playwright.helpers.enums.DeviceType.*;

public class DevicePO extends CommonPO {

    // .../details for legacy sensors
    private static final String BUTTON_SETTINGS = "//span[text()='Settings']";


    // .../settings/sensor/{sensor_id}
    private static final String STANDARD = "//*[@data-qa-id='standard_id'] //span";

    public void setNonC50RecordingTimeNoSave(String newRecordingTime) {
        if (actions().elementExistAndVisible("//input[@data-qa-id='post_trig_time']")) {
            actions().clearAndType("//input[@data-qa-id='post_trig_time']", newRecordingTime);
        } else {
            throw new IllegalStateException("Input field needed to make input.");
        }
    }

    public String getNonC50PreRecordingTime() {
        return actions().findOneElementsValueAttribute("//input[@data-qa-id='pre_trig_time']");
    }

    public void setC50RecordingTime(int newRecordingTime) {
        actions().clearAndType("//input[@data-qa-id='record_time']", String.valueOf(newRecordingTime));
        actions().makeClick("//button[@type='submit']");
    }

    public int getC50RecordingTime() {
        if (actions().elementExistAndVisible("//input[@data-qa-id='record_time']", false, 0)) {
            String recordTime2 = actions().findOneElementsText("//input[@data-qa-id='record_time']");
            System.out.println("recordTime2: " + recordTime2);
        }
        String recordTime = actions().findOneElementsValueAttribute("//input[@data-qa-id='record_time']");
        return Integer.parseInt(recordTime);
    }

    public void setLocation() {
//        actions().makeClickOnSomeElements(FORM_ARROWS, 1);
        actions().makeClick(FORM_ARROWS);
        actions().makeClick("//div[text()='Manual position']");
        actions().clearAndType("//input[@aria-label='Latitude']", Integer.toString(Randomizer.randomInt(0, 90)));
        actions().clearAndType("//input[@aria-label='Longitude *']", Integer.toString(Randomizer.randomInt(0, 180)));
        actions().clearAndType("//input[@aria-label='Altitude']", Integer.toString(Randomizer.randomInt(0, 15)));
    }

    public void setTime() {
//        actions().makeClickOnSomeElements(FORM_ARROWS, 1);
        actions().makeClick(FORM_ARROWS);
        actions().makeClickOnSomeElements("//div[@class='q-virtual-scroll__content'] //div[@role='option']", Randomizer.randomInt(0, 29));
    }

    // callable from .../devices/{sensor_type}/{sensor_id}/details
    public void goToLegacySensorSettingsPage() {
        actions().makeClick(BUTTON_SETTINGS);
    }

    // Is part of future SensorDetailPanel
    // callable from .../devices/sensor/{sensor_id}/details
    public String getSensorStandard() {
        return actions().findOneElementsText(STANDARD);
    }

    public boolean checkIfPropertiesChipExist() {
        return actions().elementExistAndVisible("//div[contains(text(), 'Properties')]/following-sibling::i");
    }

    public List<String> getBatteryIndicator() {
        String batteryIndicatorPath = "//i[contains(text(), 'brightness')]";
        return actions().findManyElementsAttribute(batteryIndicatorPath, "class");
    }

    // .../C50/100206/settings/description
    public String readDeviceDescription() {
        PlaywrightActions.sleep(1);
        return actions().findOneElementsValueAttribute("//input[@data-qa-id='description']");
    }

    // .../C50/100206/settings/description
    public String readDeviceNotes() {
        return actions().findOneElementsValueAttribute("//textarea[@data-qa-id='notes']");
    }

    public void clickDeviceDetails(String detail) {
        actions().makeClick("(//div[@data-qa-id='panel-body'] //div[@class='col-xs-12 col-sm-6'])[5]");
    }

    // .../device/details
    public String getConnectedProjectToolTipText() {
        actions().hoverAboveElement("(//div[@data-qa-id='panel-body'] //div[@class='col-xs-12 col-sm-6'])[5] //i");
        return actions().findOneElementsText("//div[contains(@id,'tooltip')]");
    }

    public DeviceConnectionHistoryPanel getDeviceConnectionHistoryPanel() {
        DeviceConnectionHistoryPanel panel = new DeviceConnectionHistoryPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        Table deviceConnectionTable = getDeviceConnectionTable();
        panel.setDeviceConnectionTable(deviceConnectionTable);

        return panel;
    }

    private Table getDeviceConnectionTable() {
        Table table = new Table();

        Table.TableRow headerRow = getDeviceConnectionTableHeaderRow();
        table.setHeader(headerRow);

        List<Table.TableRow> tableRows = getDeviceConnectionTableContentRows();
        table.setContent(tableRows);

        return table;
    }

    private Table.TableRow getDeviceConnectionTableHeaderRow() {
        Table.TableRow headerRow = new Table.TableRow();
        int headersCount = 3;

        for (int h = 1; h <= headersCount; h++) {
            String cellPath = "//thead //th[" + h + "]";
            String cellText = actions().findOneElementsText(cellPath);

            headerRow.addContent(cellText);
        }

        return headerRow;
    }

    private List<Table.TableRow> getDeviceConnectionTableContentRows() {
        List<Table.TableRow> tableRows = new ArrayList<>();

        int rowCount = actions().countHowManyElements("//tbody //tr");

        for (int r = 1; r <= rowCount; r++) {
            Table.TableRow row = new Table.TableRow();

            String rowpath = "//tbody //tr[" + r + "]";

            int columnCount = 3;
            for (int c = 1; c <= columnCount; c++) {
                String cellPath = rowpath + "/td[" + c + "]";

                String cellValue = actions().findOneElementsText(cellPath);
                row.addContent(cellValue);
            }

            tableRows.add(row);
        }

        return tableRows;
    }

    // .../devices/type/serial/status/projects
    public List<Map<String, String>> getDeviceConnectionList() {
        List<Map<String, String>> projectList = new ArrayList<>();

        // For each row
        int rows = actions().countHowManyElements("//tbody //tr");
        for (int r = 1; r <= rows; r++) {
            Map<String, String> row = new HashMap<>();

            // For each column
            int columns = actions().findManyElementsTexts("//thead //th").size();
            for (int c = 1; c <= columns; c++) {
                String headerValue = actions().findOneElementsText("(//thead //th)["+c+"]");
                String cellValue = actions().findOneElementsText("((//tbody //tr)["+r+"] //td)["+c+"]");

                switch (headerValue) {
                    case "Project name" -> {
                        row.put("Project name", cellValue.substring(0, cellValue.indexOf("\n")));
                        row.put("Project id", cellValue.substring(cellValue.indexOf("ID: ") + 4));
                    }
                    case "Measuring point name" -> {
                        row.put("Measuring point name", cellValue);
                    }
                    case "Connected device date" -> {
                        row.put("Start date", cellValue.substring(cellValue.indexOf("Start date: ") + 12, cellValue.indexOf("\n")));
                        row.put("End date", cellValue.substring(cellValue.indexOf("End date: ") + 10));
                    }
                    default -> throw new IllegalStateException("Unexpected header value: " + headerValue);
                }

            }
            projectList.add(row);
        }
        return projectList;
    }

    // from monitoring settings panel
    public List<String> getSelectableStandards() {
        openOrCloseStandardsDropdown();

        // todo: ersätt med funktion för att ladda dropdown content typ:
        //  Dropdown dropdown = getStandardDropdown()
        // List<String> selections = dropdown.getSelections()

        List<String> selectableStandards = actions().findManyElementsTexts("//div[@role='menu'] //div[@class='cursor-pointer']");
        openOrCloseStandardsDropdown();

        return selectableStandards;
    }

    /**
     * Make a click on a random standard, but not the one already in the device.
     */
    public void selectRandomNewStandard(String currentStandard, List<String> selectableStandards) {
        openOrCloseStandardsDropdown();

        int random = Randomizer.getRandomIndexExcludingCurrent(selectableStandards, currentStandard);
        System.out.println("random standard position in dropdown: " + random);

        actions().makeClick("(//div[@role='menu']//div[@class='cursor-pointer'])["+ (random + 1) +"]");
        PlaywrightActions.sleep(1);
    }

    public void selectRandomStandard(String currentStandard, List<String> selectableStandards) {
        openOrCloseStandardsDropdown();

        boolean stdCanBeUsed = false;
        Integer random = null;

        while (!stdCanBeUsed) {
            random = Randomizer.getRandomIndexExcludingCurrent(selectableStandards, currentStandard);
            String newStandardsText = actions().findOneElementsText("(//div[@role='menu']//div[@class='cursor-pointer' ])["+ (random + 1) +"]");
            System.out.println("newStandardsText: " + newStandardsText);

            boolean hasFrequencyWeighting = StandardType.hasFrequencyWeighing(newStandardsText);
            System.out.println("hasFrequencyWeighting: " + hasFrequencyWeighting);

            stdCanBeUsed = hasFrequencyWeighting;

        }

        actions().makeClick("(//div[@role='menu']//div[@class='cursor-pointer' ])["+ (random + 1) +"]");
        PlaywrightActions.sleep(1);
    }

    /**
     * Make a click on a random standard, but not the one already in the device.
     */
    public void selectNewStandard(int newStandardDropDownPosition) {
        openOrCloseStandardsDropdown();

        // Add one bc xpath dont start with 0 as lists to.
        actions().makeClick("(//div[@role='menu']//div[@class='cursor-pointer' ])["+ newStandardDropDownPosition +"]");
        PlaywrightActions.sleep(1);
    }

    /**
     * Opens and makes a click on a selected standard.
     * @param standard Can be part or full standard name
     */
    public void selectStandard(String standard) {
        openOrCloseStandardsDropdown();

        // Search for e.g., '(1A)'
        actions().clearAndType("//input[@placeholder='Find standard']", standard);
        // Click on the top result (should only be one)
        actions().makeClick("(//div[@role='menu']//div[@class='cursor-pointer' ])[1]");
        PlaywrightActions.sleep(1);
    }

    public void selectFrequencyWeighting(String freqWeigh) {
        openOrCloseFreqWeightingDropdown();

        actions().makeClick("//div[@role='listbox'] //div[@data-qa-id='"+freqWeigh+"']");
    }

    /**
     * @return one MonitoringSettingsPanel for each selectable standard for the sensor.
     */
    public List<DeviceSettingsMonitoringPanel> selectAllStandards() {
        List<DeviceSettingsMonitoringPanel> allPanels = new ArrayList<>();

        openOrCloseStandardsDropdown();
        int selectableStandards = actions().findManyElementsTexts("//div[@role='menu']//div[@class='cursor-pointer']").size();

        for (int s = 1; s <= selectableStandards; s++) {
            selectNewStandard(s);
            allPanels.add(getMonitoringSettingsPanel());
        }
        return allPanels;
    }

    // todo: ersätt denna med typ expandDropdownAndReadContent()
    public void openOrCloseStandardsDropdown() {
        // Alter drop down list of standards
        System.out.println("Altering standard drop down menu.");
        String standardDropDownPath = "//div[@data-qa-id='settings-panel']  //div[@class='q-field__native row items-center']";
        actions().makeClick(standardDropDownPath);
    }

    public void inputTriggerValueAndPressKey(String deviceType, String value, String key) {
        String triggerValuePath = switch (DeviceType.fromType(deviceType)) {
            case C22, C20, POINT, C50 -> "(//*[@data-qa-id[contains(., '_trigger_value')]])[1]";
            default -> throw new IllegalStateException("Unexpected deviceType: " + deviceType);
        };

        actions().clearAndType(triggerValuePath, value);
        actions().simulateKey(triggerValuePath, key);
    }

    // todo: flytta detta till MonitoringSettingsPanelPO?
    /**
     * @return All the elements from .../settings/monitoring
     */
    public DeviceSettingsMonitoringPanel getMonitoringSettingsPanel() {
        return getMonitoringSettingsPanel(false);
    }

    public DeviceSettingsMonitoringPanel getMonitoringSettingsPanel(boolean includingPresetStandardDropdownContent) {
        String currentUrl = actions().getCurrentUrl();

        DeviceType deviceType = (currentUrl.contains("sensor"))
                ? DeviceType.getMonitoringDeviceFromCurrentUrl(currentUrl)
                : DeviceType.getCommunicatingDeviceFromCurrentUrl(currentUrl);

        DeviceSettingsMonitoringPanel msp = new DeviceSettingsMonitoringPanel();

        PanelHeader panelHeader = getPanelHeader();
        msp.setPanelHeader(panelHeader);

        // If a committed change is done
        boolean committedNoticeItemExist = actions().elementExistAndVisible("//form //div[@data-qa-id='settings-panel-body']/div[@role='listitem']", false, 0);
        if (committedNoticeItemExist) {

            NoticeItem noticeItem = new NoticeItem();

            Icon noticeIcon = completeGetIcon("(//form //div[@data-qa-id='settings-panel-body']/div[@role='listitem'] /div)[1]");
            noticeItem.setLeftIcon(noticeIcon);

            // Committed change
            String noticeText = actions().findOneElementsText("(//form //div[@data-qa-id='settings-panel-body']/div[@role='listitem'] /div)[2]");
            noticeItem.setText(noticeText);

            msp.setCommittedNotice(noticeItem);
        }

        Preface preface = getPreface();
        msp.setPreface(preface);

        // C10/C12_SENSORs, S50, etc
        if (deviceType.isMonitoringDevice()
                && deviceType.getFamily().equals("LEGACY")) {

            InputField deviceText = completeGetInputField("//label[.//div[contains(text(),'Device ID')]]");
            msp.setDeviceIdInputField(deviceText);
        }

        Dropdown intervalTime = getDropdownByPath("//label[.//div[contains(text(),'Interval')]]");
        msp.setIntervalTimeDropdown(intervalTime);

        // Only some creators has preTrigTime
        if (deviceType.equals(POINT) || deviceType.equals(C20) || deviceType.equals(C22)) {

            InputField preTrigTime = completeGetInputField("//label[.//div[text()='Pre trig *']]");
            msp.setPreTrigTimeInputField(preTrigTime);
        }

        if (deviceType.isCommunicatingDevice()) {

            InputField postTrigTime = completeGetInputField("//label[.//div[contains(text(),'Record time')]]");
            msp.setPostTrigTimeInputField(postTrigTime);

        } else if (deviceType.isMonitoringDevice()
                && deviceType.getFamily().equals("Legacy")) {

            Dropdown recordingTime = getDropdownByPath("//label[.//div[contains(text(),'Transient time')]]");
            msp.setPostTrigTimeDropdown(recordingTime);
        }

        // Get C50 specific stuff
        if (deviceType.equals(C50)) {

            Dropdown preset = getDropdownByPath("//label[.//div[text()='Preset']]", includingPresetStandardDropdownContent);
            msp.setPresetDropdown(preset);

            Dropdown octaveBand = getDropdownByPath("//label[.//div[text()='Octave band *']]");
            msp.setOctaveDropdown(octaveBand);

            Dropdown advLeqDropdown = getDropdownByPath("//label[.//div[text()='Advanced Leq settings']]");
            msp.setAdvLeqDropdown(advLeqDropdown);

            ToggleField statistics = getC50CalculatedStatisticsToggleField("//label[.//div[text()='Calculated statistics']]");
            msp.setCalculatedStatistics(statistics);

            if (statistics.getToggle().getState().equals(true)) {

                // Ln
                InputField customStatistics = completeGetInputField("//label[.//div[contains(text(),'Custom statistics')]]");
                msp.setCustomStatistics(customStatistics);

                // Ln trigger value
                InputField customStatisticsValue = completeGetInputField("(//label[.//div[contains(text(),'Trigger value')]])[1]");
                msp.setCustomStatisticsValue(customStatisticsValue);
                // Ln trigger state
                ToggleField customStatisticsTrigger = completeGetToggleField("none", "//div[@data-qa-id='trigger_enable_Ln_trigger']/div");
                msp.setCustomStatisticsTrigger(customStatisticsTrigger);

            }

            FieldWrapper timeslotHeaderWrapper = getFieldWrapperCommonPartsByHeader("Timeslots");
            Button manageTimeslotsButton = getButton("//div[@class='q-fieldset row no-wrap items-start q-my-md q-pl-sm col-12'][2] //button");
            timeslotHeaderWrapper.addButton(manageTimeslotsButton);
            msp.setTimeslotHeaderWrapper(timeslotHeaderWrapper);

            // One ChannelWrapper per TimeSlot
            List<ChannelWrapper> timeslotWrappers = getC50TimeslotWrappers();
            msp.setTimeslotWrappers(timeslotWrappers);

        } else {

            Dropdown standardDropdown = getDropdownByPath("//label[.//div[text()='Standard *']]", includingPresetStandardDropdownContent);
            msp.setStandardDropdown(standardDropdown);

            if (deviceType == C22 || deviceType == C20 ) {

                boolean hasFreqWeighDropdown = actions().elementExistAndVisible("//label[.//div[text()='Frequency weighting']]", false, 0);
                if (hasFreqWeighDropdown) {

                    Dropdown freqWeightingDropdown = getDropdownByPath("//label[.//div[text()='Frequency weighting']]");
                    msp.setFrequencyWeightingDropdown(freqWeightingDropdown);
                }
            }

            List<ChannelWrapper> channelWrappers = getAllChannelWrappers(deviceType);

            msp.setChannelWrappers(channelWrappers);
        }

        return msp;

    }

    private List<ChannelWrapper> getAllChannelWrappers(DeviceType sensorType) {
        List<ChannelWrapper> channelWrappers = new ArrayList<>();

        String channelWrappersPath = "//div[@class='q-fieldset row no-wrap items-start q-my-md q-pl-sm col-12']";
        int channelWrapperCount = actions().countHowManyElements(channelWrappersPath);

        int firstChannelWrapperAt = 2;

        for (int t = firstChannelWrapperAt; t <= channelWrapperCount; t++) {

            String channelWrapperPath = "(" + channelWrappersPath + ")["+t+"]";

            ChannelWrapper channelWrapper = getChannelWrapper(channelWrapperPath);
            channelWrappers.add(channelWrapper);
        }

        return channelWrappers;
    }

    /**
     * C50 have default + nCustom timeslots
     */
    private List<ChannelWrapper> getC50TimeslotWrappers() {
        List<ChannelWrapper> timeslotWrappers = new ArrayList<>();

        String timeslotWrappersPath = "//div[@data-qa-id='agenda_timeslot']";
        int timeslotCount = actions().countHowManyElements(timeslotWrappersPath);

        for (int t = 1; t <= timeslotCount; t++) {
            String timeSlotWrapperPath = timeslotWrappersPath + "[position()="+t+"]";

            ChannelWrapper timeslotWrapper = getC50TimeslotWrapper(timeSlotWrapperPath);
            timeslotWrappers.add(timeslotWrapper);
        }

        return timeslotWrappers;
    }

    private ChannelWrapper getC50TimeslotWrapper(String timeslotWrappersPath) {
        ChannelWrapper timeslot = new ChannelWrapper();

        Icon timeslotIcon = completeGetIcon(timeslotWrappersPath + " //i/parent::*");
        timeslot.setLeftIcon(timeslotIcon);

        String timeslotWrapperHeader = actions().findOneElementsText(timeslotWrappersPath + " //div[@class='row col']/div");    // aka timeslotName
        timeslot.setHeader(timeslotWrapperHeader);

        // All but the first timeslot (default) have time and days
        if (!timeslotWrappersPath.endsWith("[position()=1]")) {
            List<String> durationElements = actions().findManyElementsTexts(timeslotWrappersPath + " //div[@class='ellipsis']");
            timeslot.setTimeslotDuration(durationElements);
        }

        // Get Lmax
        Channel lmaxChannel = getChannel(timeslotWrappersPath + "/div/div[2]/div[2]");
        timeslot.addChannel(lmaxChannel);

        // Get Leq
        Channel leqChannel = getChannel(timeslotWrappersPath + "/div/div[2]/div[3]/div");
        timeslot.addChannel(leqChannel);

        boolean hasBaselineInputField = actions().elementExistAndVisible("//label[.//div[text()='Baseline *']]", false, 0);
        if (hasBaselineInputField) {
            InputField baselineInputField = completeGetInputField("//label[.//div[text()='Baseline *']]");
            timeslot.setBaseline(baselineInputField);
        }

        boolean hasAccumulationDropdown = actions().elementExistAndVisible("//label[.//div[text()='Accumulation span *']]", false, 0);
        if (hasAccumulationDropdown) {
            Dropdown accumulationSpanDropdown = getDropdownByPath("//label[.//div[text()='Accumulation span *']]");
            timeslot.setAccumulationSpan(accumulationSpanDropdown);
        }

        boolean hasRollingWindowDropdown = actions().elementExistAndVisible("//label[.//div[text()='Rolling window *']]", false, 0);
        if (hasRollingWindowDropdown) {
            Dropdown rollingWindowDropdown = getDropdownByPath("//label[.//div[text()='Rolling window *']]");
            timeslot.setRollingWindow(rollingWindowDropdown);
        }

        return timeslot;
    }

    private ChannelWrapper getChannelWrapper(String channelWrapperPath) {
        ChannelWrapper channelWrapper = new ChannelWrapper();

        Icon channelWrapperIcon = completeGetIcon(channelWrapperPath + " //i/parent::*");
        channelWrapper.setLeftIcon(channelWrapperIcon);

        String channelWrapperHeader = actions().findOneElementsText(channelWrapperPath + " //div[@class='row col']/div");    // aka timeslotName
        channelWrapper.setHeader(channelWrapperHeader);

        String channelsPath = channelWrapperPath + " //div[@class='col-xs-12 col-sm-6 row no-wrap']";

        // Normally a wrapper only holds one Channel, but for C50 there can be many channels
        List<Channel> channels = getChannels(channelsPath);
        channelWrapper.setChannels(channels);

        return channelWrapper;
    }

    private List<Channel> getChannels(String channelsPath) {
        List<Channel> channels = new ArrayList<>();

        int channelCount = 1;

        for (int c = 1; c <= channelCount; c++) {
            String channelPath = "(" + channelsPath + ")["+c+"]";

            Channel channel = getChannel(channelPath);
            channels.add(channel);
        }

        return channels;
    }

    /**
     * Path ends with <div>
     */
    private Channel getChannel(String channelPath) {
        Channel channel = new Channel();

        // This is unique for C50, as other Channels have their name in ChannelWrapperName
        boolean isC50Channel = actions().elementExistAndVisible(channelPath + "/div[@class='text-weight-medium text-infra-primary']", false, 0);
        if (isC50Channel) {
            String channelName = actions().findOneElementsText(channelPath + "/div[@class='text-weight-medium text-infra-primary']");
            channel.setChannelName(channelName);
        }

        ToggleField triggerToggleField = getToggleWithoutText(channelPath + " //div[@role='switch']");
        channel.setToggleField(triggerToggleField);

        // Always get trigger input field for C50 channels
        if (isC50Channel) {

            InputField triggerValue = completeGetInputField(channelPath + " //label");
            channel.setInputField(triggerValue);

        } else {

            // Only get trigger input field for non-C50 channels if toggle:ON
            if (triggerToggleField.getState().equals(true)) {

                InputField triggerValue = completeGetInputField("(" + channelPath + " //label)[1]");
                channel.setInputField(triggerValue);
            }
        }

        return channel;
    }

    public List<String> getFrequencyWeightingOptions() {
        openOrCloseFreqWeightingDropdown();
        List<String> freqWeighOptions = actions().findManyElementsTexts("//div[@role='listbox'] //div[@class='q-item__label']");
        openOrCloseFreqWeightingDropdown();
        return freqWeighOptions;
    }

    private void openOrCloseFreqWeightingDropdown() {
        actions().makeClick("//div[@data-qa-id='frequency_weighting']");
    }

    private String getIntervalTime(DeviceType type) {
        String intervalTimeDropDownPath = getIntervalTimeDropdownPath(type);
        return actions().findOneElementsText(intervalTimeDropDownPath);
    }

    private static String getIntervalTimeDropdownPath(DeviceType type) {
        return switch (type) {
            case C50 -> "//div[@data-qa-id='intv_time'] //span";
            case C22, C20, POINT -> "//div[@data-qa-id='interval'] //span";
            default -> "//div[@data-qa-id='interval_time']";
        };
    }

    public void setFreqWeighting(String freqWeightingSelection) {
        // Open freq.weighting drop down (if existing)
        if (actions().elementExistAndVisible("//div[@data-qa-id='frequency_weighting']", true, 0)) {
            actions().makeClick("//div[@data-qa-id='frequency_weighting']");
            // Select freq.weighting
            actions().makeClick("//div[@data-qa-id='"+freqWeightingSelection+"']");
        }
    }

    public void setStandard(String standard) {
        openOrCloseStandardsDropdown();
        actions().makeClick("(//div[@role='menu']//div[@class='cursor-pointer'])[.//div[text()='"+standard+"']]");
    }

    public void setFrequencyWeighting(String frequencyWeighting) {
        if (frequencyWeighting != null) {
            setC22FreqWeighting(frequencyWeighting);
        }
    }

    public void setC22FreqWeighting(String freqWeighting) {
        // expand dropdown
        actions().makeClick("//div[@data-qa-id='frequency_weighting']");
        // Select freq.weighting
        actions().makeClick("//div[@data-qa-id='"+freqWeighting+"']");
    }

    // todo: om vi byter standard kommer alla triggers bli ON automatiskt
    // .../company/devices/C22/101915/settings/monitoring
    public void setTriggers(List<Map<String, String>> channelsValuesAndStates) {
        // Set all triggers to ON
        System.out.println("\nSetting all triggers to ON");
        int triggerCount = actions().countHowManyElements("//div[@data-qa-id='settings-panel-body']/div[2] //div[@role='switch']");
        for (int trigger = 1; trigger <= triggerCount; trigger++) {
            boolean isTriggerON = actions().findOneElementsAttribute("(//div[@data-qa-id='settings-panel-body']/div[2] //div[@role='switch'])["+trigger+"]", "aria-checked").equals("true");

            if (!isTriggerON) {
                System.out.println("\nTurning on trigger");
                actions().makeClick("(//div[@data-qa-id='settings-panel-body']/div[2] //div[@role='switch'])["+trigger+"]");
            }
        }

        PlaywrightActions.sleep(1);
        System.out.println("\nSetting each trigger according to channelsValuesAndStates");
        // For every trigger
        channelsValuesAndStates.forEach((trigger) -> {
            String name = trigger.get("name");
            String value = trigger.get("value");
            boolean setToOff = trigger.get("state").equals("OFF");

            // Erase trigger, so that we do not get autocompleted trigger values
            actions().clearAndType("//input[@data-qa-id='"+name+"_trigger_value']", "");
            // Set trigger values, if value is not empty
            if (value != null) {
                // Set trigger value
                actions().clearAndType("//input[@data-qa-id='"+name+"_trigger_value']", value);
            }

            // Set trigger state (assume trigger=true after changing standard)
            if (setToOff) {
                actions().makeClick("//div[@data-qa-id='settings-panel-body']/div[2] //div[.//div[text()='"+name+"']] //div[@role='switch']");
            }
        });
    }

    /**
     * Puts all triggers to 'not set':ON
     * No need to call this if
     */
    public void resetTriggers() {
        // Set all triggers to ON
        System.out.println("\nSetting all triggers to not_set:ON");
        int triggerCount = actions().countHowManyElements("//div[@data-qa-id='settings-panel-body']/div[2] //div[@role='switch']");
        for (int trigger = 1; trigger <= triggerCount; trigger++) {
            boolean isTriggerON = actions().findOneElementsAttribute("(//div[@data-qa-id='settings-panel-body']/div[2] //div[@role='switch'])["+trigger+"]", "aria-checked").equals("true");

            if (!isTriggerON) {
                System.out.println("\nTurning on trigger");
                actions().makeClick("(//div[@data-qa-id='settings-panel-body']/div[2] //div[@role='switch'])["+trigger+"]");
            }
        }
        PlaywrightActions.sleep(1);
    }


    // .../company/devices/C22/101915/settings/monitoring

    /**
     * Triggers reach this case either manually or when standard and/or freqWeighting has been altered.
     */
    public void setTriggersAfterStdOrFreqWChange(List<Map<String, String>> channelsValuesAndStates) {
        System.out.println("\nSetting each trigger according to channelsValuesAndStates");
        // For every trigger
        channelsValuesAndStates.forEach((trigger) -> {
            String name = trigger.get("name");
            String value = trigger.get("value");
            boolean setToOff = trigger.get("state").equals("OFF");

            // Set trigger values, if value is not empty
            if (value != null) {
                // Erase trigger value, so that we do not get autocompleted trigger values
                actions().clearAndType("//input[@data-qa-id='"+name+"_trigger_value']", "");
                // Set trigger value
                actions().clearAndType("//input[@data-qa-id='"+name+"_trigger_value']", value);
            } else {
                // A null value mean that nothing should change
            }

            // Set trigger state (assume trigger=true after changing standard)
            if (setToOff) {
                actions().makeClick("//div[@data-qa-id='settings-panel-body']/div[2] //div[.//div[text()='"+name+"']] //div[@role='switch']");
            }
        });
    }

    public String getTriggerState(String triggerName) {
        return actions().findOneElementsAttribute("//div[text()='"+triggerName+"']/ancestor::div[contains(@class, 'q-fieldset')] //div[@role='switch']", "aria-checked");
    }

    public String getTriggerValue(String triggerName) {
        return actions().findOneElementsAttribute("//div[text()='"+triggerName+"']/ancestor::div[contains(@class, 'q-fieldset')] //input", "value");
    }

    /**
     * V_trigger_value for C22
     * Lmax_max_threshold_value for S50
     * LAeq_0_value for C50 (the digit being which timeslot)
     */
    public void setTriggerValue(String triggerName, String valueToSet) {
        switch (triggerName) {
            case "Lmax", "Leq" -> actions().clearAndType("//input[@data-qa-id='"+triggerName+"_max_threshold_value']", valueToSet);
            case "LAFmax", "LASmax", "LAeq" -> actions().clearAndType("//input[@data-qa-id='"+triggerName+"_value']", valueToSet);
            default -> actions().clearAndType("//input[@data-qa-id='"+triggerName+"_trigger_value']", valueToSet);
        }
    }

    // todo: denna slog av V-kanalens toggle ist för L-kanalen...
    public void changeTriggerState(String triggerName) {
        actions().makeClick("//div[@data-qa-id='settings-panel-body']/div[2] //div[.//div[text()='"+triggerName+"']] //div[@role='switch']");
    }

    // .../monitoring or .../settings/sensor/id
    public List<String> getSelectableIntervals(String type) {
        // Get current interval
        String currentIntervalTime = getIntervalTime(DeviceType.fromType(type));

        // Expand interval dropdown
        String intervalTimeDropDownPath = getIntervalTimeDropdownPath(DeviceType.fromType(type));
        actions().makeClick(intervalTimeDropDownPath);
        // Read all interval time options
        List<String> selectableIntervalTimes = actions().findManyElementsTexts("//div[@role='listbox'] //div[@class='q-item__label']");

        // Close dropdown
        actions().makeClick(intervalTimeDropDownPath);

        // Remove current
        selectableIntervalTimes.remove(currentIntervalTime);
        // Return the rest
        return selectableIntervalTimes;
    }

    // .../monitoring or .../settings/sensor/id
    public void selectIntervalTime(String type, String randomNewIntervalTime) {
        String intervalTimeDropDownPath = getIntervalTimeDropdownPath(DeviceType.fromType(type));
        actions().makeClick(intervalTimeDropDownPath);

        actions().makeClick("//div[@role='listbox'] //*[text()='"+randomNewIntervalTime+"']");
    }

    // Legacy .../details
    public void clickConnectedDevice(String connectedDeviceType) {
        actions().makeClick("(//div[@data-qa-id='panel'])[2] //tbody //div[contains(text(),'"+connectedDeviceType+"')]");
    }

    public DeviceSettingsPanel getDeviceSettingsPanel() {
        DeviceSettingsPanel panel = new DeviceSettingsPanel();

        PanelHeader panelHeader = getPanelHeader();
        panel.setPanelHeader(panelHeader);

        // PanelBody
        String panelPartPath = "//form[1]/div[@data-qa-id='panel'] //div[@data-qa-id='panel-body']/div";

        // First find out if the panel have notices like 'Manage uncommitted changes' or 'Device using an external power source'
        int panelPartsCount = actions().countHowManyElements(panelPartPath);
        boolean hasNoticeItem = panelPartsCount == 2;

        // NoticeItems
        if (hasNoticeItem) {
            List<NoticeItem> noticeItems = getDeviceDetailNoticeItems(panelPartPath + "[1]");
            panel.setNoticeItems(noticeItems);
        }

        Preface preface = getPreface();
        panel.setPreface(preface);

        SettingsItem monitoring = getSettingsItemByDataQaId("company_monitoring_settings");
        panel.setMonitoring(monitoring);

        SettingsItem uploadSchedule = getSettingsItemByDataQaId("company_upload_settings");
        panel.setUploadSchedule(uploadSchedule);

        SettingsItem serviceMessage = getSettingsItemByDataQaId("company_service_messages");
        panel.setServiceMessage(serviceMessage);

        SettingsItem location = getSettingsItemByDataQaId("company_gps_coordinates");
        panel.setLocation(location);

        SettingsItem timeZone = getSettingsItemByDataQaId("company_time_settings");
        panel.setTimeZone(timeZone);

        SettingsItem advancedSettings = getSettingsItemByPath("//div[contains(@aria-label,'Advanced settings')]", true);
        panel.setAdvancedSettings(advancedSettings);

        if (advancedSettings.getExpansionIcon().getType().equals(IconType.EXPANDED)) {
            SettingsItem remoteOverrides = getSettingsItemByDataQaId("company_special");
            panel.setRemoteOverrides(remoteOverrides);

            SettingsItem disableDevice = getSettingsItemByDataQaId("company_disable");
            panel.setDisableDevice(disableDevice);
        }

        return panel;
    }

//    .../settings/special
    public void selectOverrideCommand(String command) {
        // Open dropdown
        actions().makeClick("//div[@data-qa-id='settings-panel-body'] //div[@data-qa-id='special_command']");
        // Select dropdown item
        actions().makeClick("//div[@role='listbox'] //div[@data-qa-id='"+command+"']");
    }

    public DeviceSettingsUploadSchedulePanel getDeviceUploadSchedulePanel() {
        DeviceSettingsUploadSchedulePanel dusp = new DeviceSettingsUploadSchedulePanel();

        dusp.setPanelHeader(getPanelHeader());
        dusp.setPreface(getPreface());

        // ********************

        FieldWrapper activeHoursWrapper = getFieldWrapperCommonPartsByHeader("Active hours");
        dusp.setActiveHoursWrapper(activeHoursWrapper);

        Button mirrorSettings = getButtonByText("Mirror settings");
        activeHoursWrapper.addContent(mirrorSettings);

        // Get hour-buttons
        List<Button> uploadScheduleButtons = new ArrayList<>();
        for (int b = 1; b <= 24; b++) {
            String buttonPath = "(//form //div[contains(@class,'q-fieldset') and .//div[contains(text(),'Active hours')]] //label //button)["+b+"]";
            Button button = getButton(buttonPath);
            uploadScheduleButtons.add(button);
        }
        activeHoursWrapper.addContent(uploadScheduleButtons);

        // get control buttons
        Button selectAll = getButtonByText("Select all");
        activeHoursWrapper.addContent(selectAll);

        Button deSelectAll = getButtonByText("Deselect all");
        activeHoursWrapper.addContent(deSelectAll);

        return dusp;
    }

    public DeviceSettingsServiceMessagePanel getDeviceServiceMessagePanel() {
        DeviceSettingsServiceMessagePanel dsmp = new DeviceSettingsServiceMessagePanel();

        dsmp.setPanelHeader(getPanelHeader());
        dsmp.setPreface(getPreface());

        // ********************

        FieldWrapper recipientsWrapper = getFieldWrapperCommonPartsByHeader("Recipients");
        dsmp.setRecipientsWrapper(recipientsWrapper);

        Button mirrorSettings = getButtonByText("Mirror settings");
        recipientsWrapper.addContent(mirrorSettings);

        // todo: denna dropdown är weird C22/101915/settings/service_messages
//        Dropdown usersDropdown = getDropdownByName("Users");
//        recipientsWrapper.addContent(usersDropdown);

        return dsmp;
    }

    public DeviceSettingsLocationPanel getDeviceLocationPanel() {
        DeviceSettingsLocationPanel dlp = new DeviceSettingsLocationPanel();

        dlp.setPanelHeader(getPanelHeader());
         dlp.setPreface(getPreface());

        FieldWrapper positionSettingFieldWrapper = getFieldWrapperCommonPartsByHeader("Position settings");
        dlp.setPositionSettingsWrapper(positionSettingFieldWrapper);

        Dropdown inputTypeDropdown = getDropdownByName("Input type");
        positionSettingFieldWrapper.addContent(inputTypeDropdown);

        Button mirrorSettings = getButtonByText("Mirror settings");
        positionSettingFieldWrapper.addContent(mirrorSettings);

        if (inputTypeDropdown.getText().contains("Manual position")) {

            FieldWrapper locationWrapper = getFieldWrapperCommonPartsByHeader("Location");
            dlp.setLocationWrapper(locationWrapper);

            locationWrapper.addContent(getInputFieldByHeader("Latitude *"));
            locationWrapper.addContent(getInputFieldByHeader("Longitude *"));
            locationWrapper.addContent(getInputFieldByHeader("Altitude"));

            locationWrapper.addContent(getButtonByText("Pin on map"));
            locationWrapper.addContent(getButtonByText("Read my location"));
        }

        return dlp;
    }

    public DeviceSettingsTimezonePanel getDeviceTimezonePanel() {
        DeviceSettingsTimezonePanel dtp = new DeviceSettingsTimezonePanel();

        dtp.setPanelHeader(getPanelHeader());
        dtp.setPreface(getPreface());

        FieldWrapper timezoneWrapper = getFieldWrapperCommonPartsByHeader("Time zone");
        dtp.setTimezoneWrapper(timezoneWrapper);

        Button mirrorSettings = getButtonByText("Mirror settings");
        timezoneWrapper.addContent(mirrorSettings);

        Dropdown inputTypeDropdown = getDropdownByName("Time zone");
        timezoneWrapper.addContent(inputTypeDropdown);

        return dtp;
    }
}
