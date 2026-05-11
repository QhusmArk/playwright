package com.example.playwright.steps;

import com.example.api.endpoints.MeasuringPointApi;
import com.example.api.models.agenda.Label;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.JsonUtil;
import com.example.helpers.Randomizer;
import com.example.helpers.TimeConverter;
import com.example.playwright.components.aside.asideItems.listItems.MeasuringPointItem;
import com.example.playwright.components.map.MainPane;
import com.example.playwright.components.panels.MeasuringPointSettingsActiveChannelsPanel;
import com.example.playwright.components.panels.measuringPoint.MeasuringPointDetailsPanel;
import com.example.playwright.components.panels.user.MeasuringPointVibrationReportSettingsPanel;
import com.example.playwright.components.parts.TimeFrame;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.example.playwright.helpers.enums.ProviderType.MEASURING_POINT;
import static org.junit.jupiter.api.Assertions.*;

public class MeasuringPointGlue extends BaseGlue {


    @Then("I can change the measuring points name")
    public void iCanChangeTheMeasuringPointsName() {
        // Create a new name and replace the old name
        String newName = Randomizer.randomString(8);
        mpPO.changeMpName(newName);
        PlaywrightActions.sleep(2);

//        ag.thisCanBeFoundInAside("measuring point", newName);
        List<MeasuringPointItem> mps = asidePO.getAside().getMeasuringPointItems();
        assertTrue(mps.stream().anyMatch(mp -> mp.getName().contains(newName)));
    }

    @And("I can delete the measuring point")
    public void iCanDeleteTheMeasuringPoint() {
        mpPO.deleteMeasuringPoint();
        PlaywrightActions.sleep(2);

        // Get actual blasts
        List<MeasuringPointItem> mps = asidePO.getAside().getMeasuringPointItems();
        assertTrue(mps.isEmpty());
    }

    @Then("The measuring point should be visible on the map")
    public void visibleOnMap() {
        // Get the newly created MP
        MeasuringPoint measuringPoint = context().getMeasuringPoints().getFirst();
//        assertTrue(mpPO.existOnMap(measuringPoint.getName()));

        MainPane mainPane = mapPO.getMainPane();
        JsonUtil.createJsonAndSave(mainPane);

        mainPane.getMapMarkerButtons().stream()
                .filter(button -> button.getText().equals(measuringPoint.getName()))
                .findAny()
                .orElseThrow(
                        () -> new IllegalStateException("No map marker for " + measuringPoint.getName()));
    }

    @And("The measuring point should be visible in the list")
    public void theMeasuringPointShouldBeVisibleInTheList() {
        Navigate.project(context().getProject().getId()).measurePoints().get();
        // Get the newly created MP
        MeasuringPoint expectedMp = context().getMeasuringPoints().getFirst();

        List<MeasuringPointItem> actualMp = asidePO.getAside().getMeasuringPointItems();

        assertEquals(expectedMp.getName(), actualMp.getFirst().getName());
    }

    @When("I create a Measuring Point from overview plus button")
    public void createMpFromOverviewPlus() {
        asidePO.clickOnOverviewListPlus(MEASURING_POINT);

        mpPO.createMeasuringPointThroughPanel();

        // Add the new mp to context
        MeasuringPoint measuringPoint = MeasuringPointApi.getMeasuringPoints(context().getProject().getId()).getFirst();
        context().addMeasuringPoint(measuringPoint);
        Navigate.refreshBrowser();
    }

    @And("I create a Measuring Point using {string}, {string} and {string}")
    public void iCreateAMeasuringPointUsing(String mpName, String mpPrice, String sensorType) {
        mpPO.createMeasuringPointWithPrice(mpName, mpPrice, DeviceProperties.getConnectedSerial(sensorType));

        // Add the new mp to context
        MeasuringPoint measuringPoint = MeasuringPointApi.getMeasuringPoints(context().getProject().getId()).getFirst();
        context().addMeasuringPoint(measuringPoint);
    }

    @When("I set the mp toggle to OFF from mp detail panel")
    public void iSetTheMpToggleToOFFFromMpDetailPanel() {
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .details()
                .get();
        mpPO.changeMpActivitySwitchFromDetails();
    }

    @When("I click panel X button")
    @When("I click panel <- button")
    public void iClickPanelCloseButton() {
        mpPO.clickClosePanelButton();
    }

    @Then("the panel has an {string} button")
    @And("the panel closes with {string}")
    public void thePanelClosesWithButton(String expectedButton) {
        String actualButtonText = mpPO.isPanelButtonXorBack();

        switch (expectedButton) {
            case "X" -> assertEquals("close", actualButtonText);
            case "<-" -> assertEquals("arrow_back", actualButtonText);
        }
    }


    /**
     * This method is a way to solve that each step in feature-file is required to
     * have a method. Here we only need one method, albeit a rather large one.
     */
    @And("I click on Go to device")
    public void handleMultipleActions() {
       //dummy method
    }

    // This is a test to fit all checks in one method.
    // The alternative would be to use Scenario Outline for
    // each mp in the project. OH
    @Then("I am redirected back to {string}")
    public void iAmRedirectedBackToSettingsDevices(String expectedEndpoint) {
        List<MeasuringPoint> mps = context().getMeasuringPoints();

        mps.forEach(measuringPoint -> {
            // Go to connected devices view
            Navigate.project(context().getProject().getId())
                    .measurePoint(measuringPoint.getId())
                    .settings()
                    .devices()
                    .get();

            PlaywrightActions.sleep(1);
            // Click on the GoToDevice-button
            mpPO.clickGoToDeviceButton();
            PlaywrightActions.sleep(1);
            // Make sure the button is <- and then click on it
            String actualButtonText = mpPO.isPanelButtonXorBack();
            PlaywrightActions.sleep(1);
            if (actualButtonText.equals("arrow_back")) {
                iClickPanelCloseButton();
            } else {
                fail("Button was not back, but X");
            }
            PlaywrightActions.sleep(1);
            String actualUrl = Navigate.getCurrentUrl();
            System.out.println("expectedEndpoint vs. actualUrl: " + expectedEndpoint + " vs. " + actualUrl);
            assertTrue(actualUrl.contains(expectedEndpoint));
        });
    }

    @And("tab to device name")
    public void tabToDeviceName() {
        mpPO.tabToDeviceList();
        PlaywrightActions.sleep(1);
    }

    @When("I create a Measuring Point")
    public void iCreateAMeasuringPoint() {
        String serial = DeviceProperties.getConnectedSerial("C22");
        mpPO.createMeasuringPoint("This_is_a_mp_name", serial);
    }

    @And("when I click on X then I am redirected to {string}")
    public void whenIClickOnXThenIMRedirectedTo(String endpoint) {
        // Test_env har fått query param 'mode=LIST'
        assertTrue(Navigate.getCurrentUrl().contains(endpoint));
    }

    @When("I set dBCorr {string} on mp {string}")
    public void iSetDBCorrectionOnMp(String dbCorrValue, String mpName) {
        MeasuringPoint mp = MeasuringPointApi.getMeasuringPointByName(context().getProject().getId(), mpName);

        Navigate.project(context().getProject().getId())
                .measurePoint(mp.getId())
                .settings()
                .dataPresentation()
                .get();

        mpPO.setDbCorrection(dbCorrValue);
        PlaywrightActions.sleep(2);
    }

    @And("search for a device that do not exist")
    public void searchForADeviceThatDoNotExist() {
        mpPO.searchForDeviceInMpCreate("666222");
    }

    @Then("I get menu message {string}")
    public void iGetMenuMessageNoDeviceCouldBeFound(String expectedText) {
        PlaywrightActions.sleep(1);
        String emptyMenuText = mpPO.getMenuText();
        assertEquals(expectedText, emptyMenuText, "Empty device didn't look like expected.");
    }

    // Graph settings
    @And("when I disable MP graph settings Show Transient ppvzx")
    public void whenIDisableMPGraphSettingsShowTransientPpvzx() {
        MeasuringPoint mp = context().getMeasuringPoints().getFirst();
        Navigate.project(context().getProject().getId())
                .measurePoint(mp.getId())
                .settings()
                .graphSettings()
                .get();

        mpPO.setShowTransientPPVZX();
    }

    @And("the panel has {string} measuring points, time frame is now and all toggles are OFF")
    public void thePanelHasMeasuringPointsTimeFrameIsNowAndAllTogglesAreOFF(String expectedMpCount) {
        // Make sure the panel description (below header) has the selected mp-count
        assertEquals(expectedMpCount, mpPO.getEditTimeFrameCount());

        TimeFrame timeFrame = mpPO.getTimeFrame();

        // Make sure default time showing is now +- 1 minute
        String toDate = timeFrame.getToDate().getValue();
        String toTime = timeFrame.getToTime().getValue();

        LocalDateTime toLDT = TimeConverter.parseDateAndTime(toDate + " " + toTime);
        assertTrue(TimeConverter.isNotMoreThanAMinuteBefore(toLDT, LocalDateTime.now()));

        // Make sure Until_further_notice-toggle is OFF
        assertEquals(false, timeFrame.getUntilFurtherNoticeToggle());

        // Make sure Set_measuring_point _active_ON/OFF-toggle is OFF
        Map<String, Boolean> offToggle = mpPO.getToggleInPanel("Off");
        assertEquals(false, offToggle.get("Off"));
    }

    @When("I click toggle {string} and {string}")
    public void iClickToggle(String toggleText, String save) {
        // Now set the other toggle
        mpPO.setToggle(toggleText, save.equals("save"));

        // Give api/aside some time to update all measuring points
        if (save.equals("save")) {
            PlaywrightActions.sleep(3);
        }
    }

    @Then("all measuring points time_frame and connected device date is {string}")
    public void allMeasuringPointsTimeFrameAndConnectedDeviceDateIs(String expectedToDate) {
        List<MeasuringPointItem> mps = asidePO.getAside().getMeasuringPointItems();

        // Go through each mp validate new expectedToDate
        mps.forEach(mp -> {
            int mpId = MeasuringPointApi.getMeasuringPointByName(context().getProject().getId(), mp.getName()).getId();

            // Check that the mp details page show correct date
            Navigate.project(context().getProject().getId())
                    .measurePoint(mpId)
                    .details()
                    .get();

            MeasuringPointDetailsPanel mpDetails = mpPO.getMpDetailsPanel();
            assertEquals(expectedToDate, mpDetails.getSummaryMap().get("Active to"),
                    () -> "expectedToDate/actToDate: " + expectedToDate + mpDetails.getSummaryMap().get("Active to"));

            // Check that the mp time frame settings page show correct toggle state
            Navigate.project(context().getProject().getId())
                    .measurePoint(mpId)
                    .settings()
                    .timeFrame()
                    .get();

            TimeFrame timeFrame =  mpPO.getTimeFrame();
            // Check that toggle is true i.e., 'On'
            assertEquals(true, timeFrame.getUntilFurtherNoticeToggle(),
                    () -> "expectedToggleState/actualToggleState: " + "true" + timeFrame.getUntilFurtherNoticeToggle());

            // Check that the connected sensor has correct until further notice toggle state
            Navigate.project(context().getProject().getId())
                    .measurePoint(mpId)
                    .settings()
                    .devices()
                    .get();

            TimeFrame deviceTimeFrame = mpPO.getTimeFrame();
            assertEquals(true, deviceTimeFrame.getUntilFurtherNoticeToggle(),
                    () -> "expectedToggleState/actualToggleState: " + "true" + timeFrame.getUntilFurtherNoticeToggle());
        });
    }

    @Then("Mp price is {string}")
    public void mpPriceIs(String expectedMpPrice) {
        String actualMpPrice = mpPO.getMeasuringPointSettingsGeneralPanel()
                .getGeneralSettings()
                .getInputField("Price")
                .getText();

        assertEquals(expectedMpPrice, actualMpPrice, "Actual mp price of '"+actualMpPrice+"' was not " + expectedMpPrice);
    }

    @When("I type a new mp location that is outside current zoom level")
    public void iSetANewMpLocationThatIsOutsideCurrentZoomLevel() {
        MeasuringPoint mp = context().getMeasuringPoints().getFirst();

        asidePO.clickOnThisAsideItem(mp.getName());

        mpPO.clickOnPanel("Settings");
        mpPO.clickOnPanel("Location");

        mpPO.changeLocation(2.2222, 174.4444);

        Navigate.project(context().getProject().getId())
                .overview()
                .get();
    }

    @Then("neither project- nor mp price settings are available")
    public void neitherProjectNorMpPriceSettingsAreAvailable() {
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .settings()
                .general()
                .get();

        assertFalse(mpPO.setPrice("7"));

        Navigate.project(context().getProject().getId())
                .settings()
                .general()
                .get();

        assertFalse(projectPO.setDefaultPrice("7"));
    }

    @Then("I set Vper threshold for each timeslot")
    public void iSetVperThresholdForEachTimeslot() {
        // Open dropdown and select an agenda
        String agendaName = context().getAgendas().getFirst().getName();
        mpPO.selectDropdownByHeader("Agenda", agendaName);

        mpPO.setVibrationReportAgendaTimeslots("77");

        List<String> timeslotNames = context().getAgendas().getFirst().getLabels().stream()
                .skip(1)    // the default timeslot
                .map(Label::getName)
                .toList();

        MeasuringPointVibrationReportSettingsPanel settingsPanel = mpPO.getMeasuringPointVibrationReportSettingsPanel(timeslotNames);

        if (settingsPanel.getTimeslots().stream().allMatch(timeslot -> timeslot.getInputField().getText().equals("77"))) {
            mpPO.clickButton("Save");
        } else {
            throw new IllegalStateException("Input fields did not match expected values.");
        }
    }

    @And("I select vibration report date {string}, time {string}, duration {string}")
    public void iSelectVibrationReportDateTimeDurationDays(String date, String time, String duration) {
        mpPO.createVibrationReport(date, time, duration);

        mpPO.clickButton("Create");
    }

    @Then("channel wrappers are")
    public void channelWrappersAre(DataTable table) {
        List<String> expectedWrapperTexts = table.row(0);

        MeasuringPointSettingsActiveChannelsPanel panel = mpPO.getMeasuringPointSettingsActiveChannelsPanel();

        List<String> actualChannelWrapperTexts = panel.getChannelWrappers().stream()
                .map(FieldWrapper::getHeader)
                .toList();

        boolean alike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedWrapperTexts, actualChannelWrapperTexts);
        assertTrue(alike);
    }

    @And("channel toggles are {int}")
    public void channelTogglesAre(int expectedToggleCount) {
        MeasuringPointSettingsActiveChannelsPanel panel = mpPO.getMeasuringPointSettingsActiveChannelsPanel();

        int actualToggleCount = panel.getChannelWrappers().stream()
                .mapToInt(wrapper -> wrapper.getContent().size())
                .sum();

        assertEquals(expectedToggleCount, actualToggleCount);
    }
}
