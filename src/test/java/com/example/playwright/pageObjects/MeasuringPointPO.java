package com.example.playwright.pageObjects;

import com.example.helpers.Randomizer;
import com.example.playwright.components.panels.MeasuringPointCreateVibrationReportPanel;
import com.example.playwright.components.panels.MeasuringPointSettingsActiveChannelsPanel;
import com.example.playwright.components.panels.MeasuringPointSettingsGeneralPanel;
import com.example.playwright.components.panels.MeasuringPointSettingsPanel;
import com.example.playwright.components.panels.measuringPoint.MeasurePointSettingsAgendaSettingsPanel;
import com.example.playwright.components.panels.measuringPoint.MeasuringPointDetailsPanel;
import com.example.playwright.components.panels.user.MeasuringPointVibrationReportSettingsPanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeasuringPointPO extends CommonPO {

    private static final String MP_NAME = "//input[@data-qa-id='name']";
    private static final String MP_DEVICE = "//input[@data-qa-id='device']";
    private static final String MP_DESCRIPTION = "//input[@data-qa-id='description']";
    private static final String MP_ACTIVITY_SWITCH = "//div[@role='switch']";
    private static final String MP_DETAILS_CLOSE = "//*[text() = 'close']";


    public void editSettingsGeneral() {
        isPanelHeaderText("mp-general-settings-panel-header", "General settings");

        actions().clearAndType(MP_NAME, "New name");
        actions().clearAndType(MP_DESCRIPTION, "New description");
        actions().makeClick("//button[@type='submit']");
    }

    public void changeMpName(final String newName) {
        isPanelHeaderText("mp-general-settings-panel-header", "General settings");

        actions().clearAndType(MP_NAME, newName);
        actions().makeClick("//button[@type='submit']");
    }

    public void changeMpActivitySwitchFromDetails() {
            //click on toggle
            actions().makeClick(MP_ACTIVITY_SWITCH);
            //close the panel
            actions().makeClick(MP_DETAILS_CLOSE);
            //give some room for GUI to catch up
            PlaywrightActions.sleep();
    }

    /**
     * Method that works for positive and negative tests.
     * @return true if the action was possible.
     */
    public boolean setPrice(String price) {
        actions().clearAndType("//input[@data-qa-id='price']", price);
        actions().makeClick("//button[@type='submit']");
        return true;
    }

    public void clickClosePanelButton() {
        actions().makeClick("//div[@data-qa-id='panel'] //button //i");
    }

    public void clickGoToDeviceButton() {
        actions().makeClick("//div[@data-qa-id='mp-connected-devices-panel'] //*[text()='Go to device']");
    }

    public void tabToDeviceList() {
        // Select vibration as new MP type
        actions().makeClick("//img[@src='svg/mp/map-marker-mp-vib-created.svg']");

        //enter name of MP
        actions().clearAndType(MP_NAME, Randomizer.randomString(10));

        // Now tab to device list field
        actions().simulateKey(MP_NAME, "Tab");

        //enter any digit so device list pops up
        actions().clearAndType(MP_DEVICE, String.valueOf(Randomizer.randomInt(1,5)));
    }

    // prereq: Create mp panel open
    public void createMeasuringPointThroughPanel() {
        // Select vibration as new MP type
        actions().makeClick("//img[@src='svg/mp/map-marker-mp-vib-created.svg']");

        //enter name of MP
        actions().clearAndType(MP_NAME, Randomizer.randomString(10));
        //click in field to get list of devices
        actions().makeClick(MP_DEVICE);
        //select any device
        actions().makeClick("//div[@class='selection_list']//div[@class='q-virtual-scroll__content']//div");

        //enter description data
        actions().clearAndType(MP_DESCRIPTION, Randomizer.randomString(10));
        //submit creation of MP
        actions().makeClick("//button[@type='submit']");

        // Wait a while so the http-requests can be processed by BE
        PlaywrightActions.sleep(2);
    }

    // from .../measure_points/create
    public void createMeasuringPoint(String mpName, String sensorToConnectToMp) {
        createMeasuringPointWithPrice(mpName, "", sensorToConnectToMp);
    }

    // from .../measure_points/create
    public void createMeasuringPointWithPrice(String mpName, String mpPrice, String sensorToConnectToMp) {
        actions().clearAndType(MP_NAME, mpName);
        if (!mpPrice.isEmpty()) {
            actions().clearAndType("//input[@data-qa-id='price']", mpPrice);
        }

        actions().makeClick(MP_DEVICE);
        // Menu
        actions().clearAndType("//input[@placeholder='Find device']", sensorToConnectToMp);
        PlaywrightActions.sleep(1);
        actions().makeClick("//div[@role='menu'] //div[@data-qa-id='list-item']");
        actions().makeClick("//button[@type='submit']");
    }


    // from .../measure_points/create
    public void searchForDeviceInMpCreate(String serial) {
        actions().makeClick(MP_DEVICE);
        // Menu
        actions().clearAndType("//input[@placeholder='Find device']", serial);
    }

    // from device search menu in .../measure_points/create
    public String getMenuText() {
        return actions().findOneElementsText("//div[@class='selection_list']");
    }

    // from .../measure_points/187446/settings/data-presentation
    public void setDbCorrection(String dBCorrectionValue) {
        actions().clearAndType("//input[@data-qa-id='property_dbcorr']", dBCorrectionValue);
        actions().makeClick("//button[@type='submit']");
    }

    // graph settings
    public void setShowTransientPPVZX() {
        actions().makeClick("//div[contains(text(), 'Show Transient ppvzx')]/../div/div[@role='switch']");
        actions().makeClick("//span[text()='Save']");
    }

    // ...project/10523/measure_points/222722/settings
    public void deleteMeasuringPoint() {
        actions().makeClick("//div[@class='q-item__label'][contains(text(),'Advanced settings')]");
        //todo: detta borde ligga i MenuPO
        actions().makeClick("//div[@class='q-item__label'][contains(text(),'Delete')]");
        actions().makeClick("//div[contains(text(),'I understand')]");
        actions().makeClick("//*[@class='block'][contains(text(),'Delete')]");
    }

    /**
     * .../project/10523/measure_points/edit-time-frame
     * @return e.g., value "3" from "3 measure points selected"
     */
    public String getEditTimeFrameCount() {
        String timeFrameHeader = actions().findOneElementsText("(//form //span)[7]");
        System.out.println("timeFrameHeader: " + timeFrameHeader);
        // 3 measure points selected
        return timeFrameHeader.substring(0, timeFrameHeader.indexOf(" "));
    }

    /**
     * .../project/10523/measure_points/226872/details
     */
    public MeasuringPointDetailsPanel getMpDetailsPanel() {
        MeasuringPointDetailsPanel panel = new MeasuringPointDetailsPanel();

        PanelHeader panelHeader = getPanelHeader();
        panel.setPanelHeader(panelHeader);

        Preface preface = getPreface();
        panel.setPreface(preface);

        Map<String, String> summaryPanel = getMeasuringPointSummaryMap();
        panel.setSummaryMap(summaryPanel);

        SettingsItem connectedSensor = getSettingsItemByDataQaId("project_mp_manage_devices");
        panel.setConnectedSensor(connectedSensor);

        SettingsItem settingsItem = getSettingsItemByDataQaId("project_mp_settings");
        panel.setSettings(settingsItem);

        return panel;
    }

    /**
     * /project/id/blasts/id/details
     */
    private Map<String, String> getMeasuringPointSummaryMap() {
        // Only the three top fields are mandatory. The rest may be displayed on set.
        List<String> fields = List.of(
                "Active from",
                "Active to",
                "Sensor type",
                "Guide value (V10)",
                "Uncorrected velocity",
                "Alarm",
                "Alert"
        );

        return getSummaryMap(fields);
    }

    // todo: är det värt det kan denna returnera en MeasuringPointGeneralSettingsPanel
    public String getMpGeneralSettingsPrice() {
        return actions().findOneElementsValueAttribute("//input[@data-qa-id='price']");
    }

    // .../project/project_id/measure_points/create
    public void expandCreateMpDeviceMenu() {
        actions().makeClick(MP_DEVICE);
    }

    // .../project/36265/measure_points/360786/settings/coordinates
    public void changeLocation(double lat, double lng) {
        actions().clearAndType("//form //input[@data-qa-id='latitude']", String.valueOf(lat));
        actions().clearAndType("//form //input[@data-qa-id='longitude']", String.valueOf(lng));

        actions().makeClick("//button[@type='submit']");
    }

    public MeasuringPointSettingsPanel getMeasuringPointSettingsPanel() {
        MeasuringPointSettingsPanel panel = new MeasuringPointSettingsPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        SettingsItem general = null;
        SettingsItem timeFrame = null;
        SettingsItem location = null;
        SettingsItem activeChannels = null;
        SettingsItem connectedDevices = null;
        SettingsItem graphSettings = null;
        SettingsItem dataPresentation = null;
        SettingsItem vibrationReport = getSettingsItemByDataQaId("project_mp_vibration_report");
        panel.setVibrationReport(vibrationReport);
        SettingsItem blastSettings = null;
        SettingsItem advancedSettings = null;
        SettingsItem delete = null;

        return panel;
    }


    public MeasuringPointVibrationReportSettingsPanel getMeasuringPointVibrationReportSettingsPanel(List<String> timeslotNames) {
        MeasuringPointVibrationReportSettingsPanel panel = new MeasuringPointVibrationReportSettingsPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        FieldWrapper vibrationReport = getFieldWrapperCommonPartsByHeader("Vibration agenda report");
        Dropdown agendaDropdown = getDropdownByName("Agenda");
        vibrationReport.setDropdown(agendaDropdown);
        panel.setVibrationReport(vibrationReport);

        List<FieldWrapper> timeslotWrappers = getAllTimeslotWrappers(timeslotNames);
        panel.setTimeslots(timeslotWrappers);

        return panel;
    }

    private List<FieldWrapper> getAllTimeslotWrappers(List<String> timeslotNames) {
        List<FieldWrapper> timeslotWrappers = new ArrayList<>();

        int timeslotCount = timeslotNames.size();

        for (int i = 1; i <= timeslotCount; i++) {
            System.out.println("name: " + timeslotNames.get(i-1));
            FieldWrapper timeslot = getFieldWrapperCommonPartsByHeader(timeslotNames.get(i-1));

            // Not yet implemented
            // timeslot duration information

            InputField vperInput = getInputFieldByHeader("Vper threshold *");
            timeslot.setInputField(vperInput);

            timeslotWrappers.add(timeslot);

        }

        return timeslotWrappers;
    }

    // /project/238567/measure_points/313584/settings/vibration-report
    public void setVibrationReportAgendaTimeslots(String inputValue) {
        String inputPath = "//input[@data-qa-id='vper_threshold']";

        int inputFields = actions().countHowManyElements(inputPath);

        for (int i = 1; i <= inputFields; i++) {
            String inputFieldPath = "(" + inputPath + ")["+i+"]";
            actions().clearAndType(inputFieldPath, inputValue);
        }
    }


    public void createVibrationReport(String date, String time, String duration) {

        // Set date

        // Set time

        // Set duration
        selectDropdownByHeader("Duration", "7 days");
    }

    public MeasuringPointCreateVibrationReportPanel getMeasuringPointCreateVibrationReportPanel() {
        MeasuringPointCreateVibrationReportPanel panel = new MeasuringPointCreateVibrationReportPanel();

        panel.setPanelHeader(getPanelHeader());

        FieldWrapper dateWrapper = getFieldWrapperCommonPartsByHeader("Date");
        TimeFrame timeFrame = getTimeFrame();
        dateWrapper.setTimeFrame(timeFrame);

        Dropdown durationDropdown = getDropdownByName("Duration");
        dateWrapper.setDropdown(durationDropdown);

        return panel;
    }

    public MeasuringPointSettingsActiveChannelsPanel getMeasuringPointSettingsActiveChannelsPanel() {
        MeasuringPointSettingsActiveChannelsPanel panel = new MeasuringPointSettingsActiveChannelsPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        String panelPath = "//div[@data-qa-id='mp-active-channels-panel']";

        List<FieldWrapper> channelWrappers = new ArrayList<>();

        String channelsWrapperPath = panelPath + "/div[2]/div[2]/div";

        int channelWrapperCount = actions().countHowManyElements(channelsWrapperPath);

        for (int i = 1; i <= channelWrapperCount; i++) {
            String channelWrapperPath = channelsWrapperPath + "["+i+"]";

            FieldWrapper channelWrapper = getFieldWrapperByPath(channelWrapperPath);

            // Each channelWrapper has toggleFields, get each one and add them to channelWrapper
            List<ToggleField> channelToggles = getChannelToggles(channelWrapperPath);
            channelToggles.forEach(channelWrapper::addContent);

            channelWrappers.add(channelWrapper);
        }
        panel.setChannelWrappers(channelWrappers);

        return panel;
    }

    private List<ToggleField> getChannelToggles(String channelWrapperPath) {
        List<ToggleField> channelToggles = new ArrayList<>();

        // Each channelWrapper has a control toggle
        String controlTogglePath = channelWrapperPath + "/div/div[2]/label";
        ToggleField controlToggle = completeGetToggleField("none", controlTogglePath);
        channelToggles.add(controlToggle);

        String channelTogglesPath = channelWrapperPath + "/div/div[2]/div";
        int channelToggleCount = actions().countHowManyElements(channelTogglesPath);

        for (int i = 1; i <= channelToggleCount; i++) {
            String toggleFieldPath = channelTogglesPath + "["+i+"]/label";

            ToggleField channelToggleField = completeGetToggleField("none",  toggleFieldPath);
            channelToggles.add(channelToggleField);
        }

        return channelToggles;
    }

    public MeasurePointSettingsAgendaSettingsPanel getMeasurePointSettingsAgendaSettingsPanel() {
        MeasurePointSettingsAgendaSettingsPanel panel = new MeasurePointSettingsAgendaSettingsPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        FieldWrapper agendaFieldWrapper = getAgendaFieldWrapper();
        panel.setAgendaFieldWrapper(agendaFieldWrapper);

        // If no agenda is selected, then do not bother getting timeslots
        if (!agendaFieldWrapper.getDropdown().getText().isEmpty()) {
            List<FieldWrapper> timeslots = getAgendaTimeslotFieldWrappers();
            panel.setTimeslotFieldWrappers(timeslots);
        }

        return panel;
    }

    private FieldWrapper getAgendaFieldWrapper() {
        FieldWrapper agendaFieldWrapper = getFieldWrapperCommonPartsByHeader("Agenda");

        Button copySettings = getButtonByText("Copy settings");
        agendaFieldWrapper.addButton(copySettings);

        Dropdown agendaDropdown = getDropdownByName("Agenda");
        agendaFieldWrapper.setDropdown(agendaDropdown);

        return agendaFieldWrapper;
    }

    private List<FieldWrapper> getAgendaTimeslotFieldWrappers() {
        List<FieldWrapper>  timeslotFieldWrappers = new ArrayList<>();

        // At least one default timeslotWrapper
        int timeslotCount = actions().countHowManyElements("//form //div[@data-qa-id='agenda_timeslot']");

        for (int timeslot = 1; timeslot <= timeslotCount; timeslot++) {
            String timeslotPath = "//form //div[@data-qa-id='agenda_timeslot']["+timeslot+"]";

            FieldWrapper timeslotWrapper = getFieldWrapperByPath(timeslotPath);

            InputField accSpan = getInputFieldByPath("(" + timeslotPath + " //label)[1]");
            timeslotWrapper.addContent(accSpan);

            InputField baseLine = getInputFieldByPath("(" + timeslotPath + " //label)[2]");
            timeslotWrapper.addContent(baseLine);

            InputField triggerValue = getInputFieldByPath("(" + timeslotPath + " //label)[3]");
            timeslotWrapper.addContent(triggerValue);

            timeslotFieldWrappers.add(timeslotWrapper);
        }
        return timeslotFieldWrappers;
    }

    public MeasuringPointSettingsGeneralPanel getMeasuringPointSettingsGeneralPanel() {
        MeasuringPointSettingsGeneralPanel panel = new MeasuringPointSettingsGeneralPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        FieldWrapper generalWrapper = getFieldWrapperCommonPartsByHeader("General settings");
        panel.setGeneralSettings(generalWrapper);

        InputField name = getInputFieldByHeader("Name *");
        generalWrapper.addContent(name);

        InputField description = getInputFieldByHeader("Description");
        generalWrapper.addContent(description);

        InputField price = getInputFieldByHeader("Price");
        generalWrapper.addContent(price);

        return panel;
    }
}
