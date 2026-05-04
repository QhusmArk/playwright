package com.example.playwright.steps;

import com.example.api.endpoints.DeviceApi;
import com.example.api.glue.ApiChangeGlue;
import com.example.api.glue.ApiDeviceGlue;
import com.example.api.models.device.Device;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.Randomizer;
import com.example.helpers.TimeConverter;
import com.example.helpers.testData.Context;
import com.example.playwright.components.aside.Aside;
import com.example.playwright.components.aside.asideItems.listItems.DeviceItem;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel.Channel;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel.ChannelWrapper;
import com.example.playwright.components.panels.device.DeviceConnectionHistoryPanel;
import com.example.playwright.components.panels.device.DeviceDetailsPanel;
import com.example.playwright.components.panels.device.DeviceSettingsPanel;
import com.example.playwright.components.parts.Banner;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.NoticeItem;
import com.example.playwright.components.parts.Table;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.helpers.enums.IconType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.example.api.glue.ApiDeviceGlue.setStdAndTriggerForC22;
import static com.example.playwright.helpers.enums.DeviceType.*;
import static com.example.playwright.helpers.enums.IconType.*;
import static org.junit.jupiter.api.Assertions.*;

public class DeviceGlue extends BaseGlue {

    // device details
    @When("I click on {string}")
    public void iClickOn(String detail) {
        devicePO.clickDeviceDetails(detail);
        PlaywrightActions.sleep(2);
    }

    private final Context context;

    public DeviceGlue(Context context) {
        this.context = context;
    }

    @When("I search for a Device")
    public void searchDevice() {
        String serial = context().getMeasuringPoints().getFirst().getSensors().getFirst().getSerial();
        asidePO.makeSearchInAside(serial);
    }

    @Then("Left menu contains {string} devices")    //all, searched
    public void deviceList(final String searchTerm) {
        // Device list shall contain no duplicates, ie Set
        Set<Device> expectedDevices = context().getDevices();

        if (searchTerm.equals("searched")) {
            // Get same serial as test searched for
            int serial = Integer.parseInt(context().getMeasuringPoints().getFirst().getSensors().getFirst().getSerial());
            expectedDevices.removeIf(device -> device.getSerial() != serial);
        }

        // Get all objects from the list.
        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

        // Both collections need to be of same size
        assertEquals(expectedDevices.size(), actualDevices.size(), "The number of devices do not match");

        // All devices in expected need to be in actual
        boolean allMatch = expectedDevices.stream()
                .allMatch(expected -> actualDevices.stream()
                        .anyMatch(actual -> actual.getSerialNumber() == expected.getSerial()
                        ));

        assertTrue(allMatch);
    }

    @Then("details for {string} shall show icons")
    public void detailsForShallShow(String type, DataTable detailIcons) {
        // Map the Strings to IconTypes
        List<IconType> expectedIconTypes = detailIcons.row(0).stream()
                .map(s -> "BATTERY".equals(s)   // If "BATTERY", convert to IconType.BATTERY_XX
                        ? IconType.getBatteryLevelIconType(type).name()
                        : s)
                .map(s -> "GSM".equals(s)   // If "GSM", convert to IconType.GSM_XX
                        ? IconType.getGSMIconType(type).name()
                        : s)
                .map(IconType::valueOf)
                .toList();

        // Get the actual IconTypes
        DeviceDetailsPanel deviceDetailsPanel = ddPO.getDeviceDetailsPanel(type);
        List<IconType> actualIconTypes = deviceDetailsPanel.getDeviceDetails().stream()
                .map(detailsButton -> detailsButton.getIcon().getType())
                .toList();

        // Check that the lists of IconTypes matches
        assertEquals(
                new HashSet<>(expectedIconTypes),
                new HashSet<>(actualIconTypes),
                "IconType lists do not match (ignoring order)"
        );
    }

    @When("I filter on {string}")
    public void filter(final String filterText) {
        Navigate.company()
                .devices()
                .get();

        filterPO.changeFilter(filterText);
    }

    @Then("this filter make aside only contain devices with this banner")
    public void thisFilterMakeAsideOnlyContainDevicesWithThisBanner(DataTable table) {
        List<String> filterAndExpectedResult = table.row(0);

        filterAndExpectedResult.forEach(part -> {

            String filterText = getFirstPart(part);
            String expectedBanner = getSecondPart(part);

            // Set filter
            filterPO.changeFilter(filterText);

            // Get all objects from the list.
//            Aside aside = asidePO.getAside();
            Aside aside = asidePO.getAside(15);

            if (aside.isCompact()) {
                List<DeviceItem> actualDevices = aside.getDeviceItems();

                // Assert each listItem contains a device with expectedBanner
                actualDevices.forEach(deviceItem -> {

                    List<Banner> actualBanners = deviceItem.getBanners();

                    assertTrue(
                            actualBanners.stream().anyMatch(b -> b.getText().contains(expectedBanner)) ||
                                    actualBanners.stream().anyMatch(b -> b.getText().contains("Committed changes")),
                            "Expected at least one string to contain either expectedBanner or 'Committed changes'"
                    );
                });

            } else {

                Table.TableRow headerRow = aside.getTable().getHeader();
                List<Table.TableRow> deviceRows = aside.getTable().getContent();

                deviceRows.forEach(row -> {
                    List<String> actualBanners = row.getStringListByTableHeader(headerRow, "Status");

                    assertTrue(
                            actualBanners.stream().anyMatch(s -> s.contains(expectedBanner)) ||
                                    actualBanners.stream().anyMatch(s -> s.contains("Multiple issues")),
                            "Expected at least one string to contain either expectedBanner or 'Multiple issues'"
                    );
                });
            }
        });
    }

    @Then("the list only contains communicating devices with one of the following texts")
    public void listDevicesAlternative(final DataTable table) {
        List<String> expectedTimes = table.row(0);

//        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();
        List<DeviceItem> actualDevices = asidePO.getAside(15).getDeviceItems();

        // Verify that device lastComm text is any of the ones we expect. If there is a lastCommunication that is. Brand new devices do not have text.
        actualDevices.forEach(device -> {
            String lastRead = device.getLastRead();

            if (lastRead != null) {
                boolean deviceHasOneOfTimes = expectedTimes.stream()
                        .anyMatch(lastRead::contains);

                assertTrue(deviceHasOneOfTimes,
                        "Device '" + device.getMainText() + "' is lacking last communication.");
            } else {
                assertEquals(MONITORING_OFF, device.getListItemIcon().getType());
            }
        });
    }

    // .../settings/sensor/sensor_id
    @Then("the standard is visible")
    public void standardIsVisible() {
        // Sensor Standard shall not be empty.
        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();
        assertFalse(monSettings.getStandardDropdown().getText().isEmpty());
    }

    @Then("the map properties dropdown have a chip")
    public void theMapPropertiesDropdownHaveAChip() {
        assertTrue(devicePO.checkIfPropertiesChipExist());
    }

    // .../company/devices/C50/111591/status/battery
    @Then("all devices in the list has red battery indicator")
    public void allDevicesInTheListHasRedBatteryIndicator() {
        //get actual
//        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();
        List<DeviceItem> actualDevices = asidePO.getAside(10).getDeviceItems();

        // For each device in the list (with low battery indicator warning), navigate to it's page and check the indicator for red.
        actualDevices.forEach(device -> {
            DeviceType deviceType = device.getDeviceType();
            int serial = device.getSerialNumber();

            Navigate.company()
                    .device(deviceType, serial)
                    .status()
                    .battery()
                    .get();

            // 1 to 2 batteries, else fail the test
            List<String> batteryIndicators = devicePO.getBatteryIndicator();

            if (batteryIndicators.isEmpty()) {
                throw new IllegalStateException("No battery indicator was found on device " + serial);
            }
            batteryIndicators.forEach(batteryIndicator -> {
                assertTrue(batteryIndicator.contains("text-negative"));
            });
        });
    }

    @Then("the list should show device with that description")
    public void theListShouldShowDeviceWithThatDescription() {
        //get devices in list
        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

        actualDevices.forEach(device -> {
            DeviceType type = device.getDeviceType();
            int serial = device.getSerialNumber();
            Navigate.company()
                    .device(type, serial)
                    .settings()
                    .description()
                    .get();

            String description = devicePO.readDeviceDescription();
            assertTrue(description.contains("QAs device"));
        });
    }

    @Then("the list should show device with that note")
    public void theListShouldShowDeviceWithThatNote() {
        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

        if (actualDevices.isEmpty()) {
            throw new IllegalStateException("No device was found on device");
        }

        actualDevices.forEach(device -> {
            DeviceType type = device.getDeviceType();
            int serial = device.getSerialNumber();

            Navigate.company()
                    .device(type, serial)
                    .settings()
                    .description()
                    .get();

            String notes = devicePO.readDeviceNotes();
            assertTrue(notes.contains("@Jenkins"));
        });
    }

    @Then("I see the POINT in the list")
    public void iSeeThePOINTInTheList() {
        // Get devices in list
        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

        assertFalse(actualDevices.isEmpty(),
                "There should be a POINT in the project list.");

        // There should only be one POINT in the project, but to be on the safe side
        actualDevices.forEach(deviceItem -> {
            assertEquals(IconType.MASTER, deviceItem.getLeftIcon().getType(),
                    () -> "expected/actual: " + "MASTER" + "/" + deviceItem.getLeftIcon().getType());
            assertEquals(POINT, deviceItem.getDeviceType(),
                    () -> "expected/actual: " + "POINT" + "/" + deviceItem.getDeviceType());
        });
    }

    /**
     * NB. This method can expand standard-drop down even though a commit exist.
     * But selecting a standard will not change the content of MonitoringSettingsPanel.
     */
    @When("I change standard for {string}")
    public void iChangeStandard(String type) {
        // First make sure the device is not occupied with un/committed change
        ApiChangeGlue.clearChangeFromC22();

        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();
        String currentStandard = monSettings.getStandardDropdown().getText();

        // Open dropdown to capture all possible selections
        List<String> selectableStandards = devicePO.getSelectableStandards();
        // Open dropdown to select one of the possible selections
        devicePO.selectRandomNewStandard(currentStandard, selectableStandards);
    }

    // C22, adapt if other sensors should use mtd
    @When("I change to a standard with freq.weighing")
    public void iChangeToAStandardWithFreqWeighing() {
        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();
        String currentStandard = monSettings.getStandardDropdown().getText();
        List<String> selectableStandards = devicePO.getSelectableStandards();

        devicePO.selectRandomStandard(currentStandard, selectableStandards);
    }

    @Then("Resultant channel are not included for any other standards than these")
    public void resultantChannelAreNotIncludedForAllOtherStandards(DataTable table) {
        List<String> standardsWithResultant = table.row(0);

        // Get how mon.settings panel look like for each avaliable standard.
        List<DeviceSettingsMonitoringPanel> allStandardsPanels = devicePO.selectAllStandards();

        // todo: vad händer om devicen redan står i std 1A?
        allStandardsPanels.forEach(msp -> {
            // Get number 1A from '(1A) SS 4604866 Spräng 250mm/s 5-300Hz'
            String standardNumber = msp.getStandardNumber();

            List<ChannelWrapper> channelWrappers = msp.getChannelWrappers();
            List<Channel> channels = channelWrappers.stream()
                    .flatMap(wrapper -> wrapper.getChannels().stream())
                    .toList();

            if (standardsWithResultant.contains(standardNumber)) {
                boolean hasR = channels.stream()
                        .anyMatch(channel -> "R".equals(channel.getInputField().getHeader()));
                assertTrue(hasR, "No R channel was found in " + standardNumber);
            } else {
                boolean hasNotR = channels.stream()
                        .noneMatch(channel -> "R".equals(channel.getInputField().getHeader()));
                assertTrue(hasNotR, "R channel was found in " + standardNumber);
            }
        });
    }

    @And("Resultant channel are NOT included for these standards when freq.weighting:ON")
    public void resultantChannelAreNOTIncludedForTheseStandardsWhenFreqWeightingON(DataTable table) {
        List<String> standardsWithFreqWeightingSelection = table.row(0);

        standardsWithFreqWeightingSelection.forEach(standard -> {
            String standardNumber = getFirstPart(standard);
            String frequencyWeighting = getSecondPart(standard);
            // Select new standard in dropdown but do not save
            devicePO.selectStandard("("+standardNumber+")");
            // Select new freq.weighting in dropdown but do not save
            devicePO.setFreqWeighting(frequencyWeighting);

            // Get default settings for that standard
            DeviceSettingsMonitoringPanel panel = devicePO.getMonitoringSettingsPanel();

            List<ChannelWrapper> channelWrappers = panel.getChannelWrappers();
            List<Channel> channels = channelWrappers.stream()
                    .flatMap(wrapper -> wrapper.getChannels().stream())
                    .toList();

            boolean hasNotR = channels.stream()
                    .noneMatch(channel -> "R".equals(channel.getInputField().getHeader()));
            assertTrue(hasNotR, "R channel was found in " + panel);
        });
    }

    @Then("freq.weighing dropdown is {string}")
    public void freqWeighingDropdownIs(String expectedState) {
        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();
        String newStandard = monSettings.getStandardDropdown().getText();

        String dropdownText = monSettings.getFrequencyWeightingDropdown().getText();

        assertEquals(expectedState, dropdownText);
    }

    @When("I change standard for {string} and then back to original standard")
    public void iChangeStandardAndThenBackToOriginalStandard(String type) {
        // First make sure the device is not occupied with un/committed change
        ApiChangeGlue.clearChangeFromC22();

        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();
        String currentStandard = monSettings.getStandardDropdown().getText();

        // Make the first change
        List<String> selectableStandards = devicePO.getSelectableStandards();
        devicePO.selectRandomNewStandard(currentStandard, selectableStandards);

        // Then make the change back to original standard
        int currentIndex = selectableStandards.indexOf(currentStandard);
        devicePO.selectNewStandard(currentIndex + 1);
    }

    @Then("I can save record time in range 1-20 seconds")
    public void iCanSaveC50RecordTimeInRange() {
        boolean browserRefreshNeeded = ApiDeviceGlue.freeDeviceFromChangeIfPossible("C50");
        if (browserRefreshNeeded) {
            Navigate.refreshBrowser();
        }

        // Get current record_time
        DeviceSettingsMonitoringPanel postMsp = devicePO.getMonitoringSettingsPanel();

        int currentRecordingTime = Integer.parseInt(postMsp.getPostTrigTimeInputField().getText());

        // Get a random record_time, but not same as is used
        int newRecordingTime = Randomizer.randomInt(1, 20, currentRecordingTime);

        devicePO.setC50RecordingTime(newRecordingTime);
        PlaywrightActions.sleep(3);

        // Now load the mon settings page again
        Navigate.company()
                .device(DeviceType.valueOf("C50"), DeviceProperties.getConnectedSerial("C50"))
                .settings()
                .monitoring()
                .get();

        DeviceSettingsMonitoringPanel preMsp = devicePO.getMonitoringSettingsPanel();
        int savedRecordingTime = Integer.parseInt(preMsp.getPostTrigTimeInputField().getText());

        // We'll need to remove the change so we don't block the device mon settings
        DeviceApi.clearChange("C50", DeviceProperties.getConnectedSerial("C50"), "{ \"action\":\"clear\" }");

        assertEquals(newRecordingTime, savedRecordingTime,
                () -> "newRecordingTime/savedRecordingTime:" + newRecordingTime + "/" + savedRecordingTime);
    }

    @Then("this record_time yield this pre-trig")
    public void thisRecordTimeYieldThisPreTrig(DataTable table) {
        List<String> list = table.row(0);

        list.forEach(part -> {
            // Split the parts
            String postRecordTime = getFirstPart(part);
            String preRecordTime = getSecondPart(part);
            // Input the new postRecordTime, but do not save the input
            devicePO.setNonC50RecordingTimeNoSave(postRecordTime);
            // Make sure the new postRecordTime affects the preTrig as expected
            assertEquals(preRecordTime, devicePO.getNonC50PreRecordingTime(), "Pre-trig not as expected.");
        });
    }

    /**
     * @param firstAndSecondPart e.g., "20:3" or "Low battery:Battery"
     * @return '20' or 'Low battery' from example above
     */
    public String getFirstPart(String firstAndSecondPart) {
        return firstAndSecondPart.substring(0, firstAndSecondPart.indexOf(":"));
    }

    /**
     * @param firstAndSecondPart e.g., "20:3" or "Low battery:Battery"
     * @return '3' or 'Battery' from example above
     */
    public String getSecondPart(String firstAndSecondPart) {
        return firstAndSecondPart.substring(firstAndSecondPart.indexOf(":") + 1);
    }

    @Then("there is a project name in {string} connection history")
    public void thereIsAProjectNameInDeviceConnectionHistory(String deviceType) {
        DeviceDetailsPanel deviceDetailsPanel = ddPO.getDeviceDetailsPanel(deviceType);

        Button connectedProjectButton = deviceDetailsPanel.getDeviceDetails().stream()
                .filter(button -> button.getIcon().getType().equals(PROJECT))
                .toList().getFirst();

        assertFalse(connectedProjectButton.getText().isEmpty());
    }

    @And("tooltip say {string}")
    public void tooltipSay(String expectedTooltipText) {
        String actualTooltipText = null;
        switch (expectedTooltipText) {
            case "Device connection history can't be displayed for this logger type" -> actualTooltipText = devicePO.getConnectedProjectToolTipText();
        }
        assertEquals(expectedTooltipText, actualTooltipText);
    }

    @And("list contains old or current connected devices")
    public void listContainsOldOrCurrentConnectedDevices() {
        List<Map<String, String>> connectedProjects = devicePO.getDeviceConnectionList();
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Assert that sorting is on Start time and descending
        // Each row start date is later or same as the next
        for (int p = 0; p < connectedProjects.size() - 1; p++) { // use -1 to avoid checking the last item in the list
            String startDateThisDevice = connectedProjects.get(p).get("Start date");
            String startDateNextRowDevice = connectedProjects.get(p + 1).get("Start date");

            assertFalse(TimeConverter.isSecondDateAfter(startDateThisDevice, startDateNextRowDevice));
        }

        // Assert that all devices has Start day before now
        boolean hasInvalidStartDate = connectedProjects.stream()
                .map(map -> map.get("Start date"))
                .anyMatch(startDate -> TimeConverter.isSecondDateAfter(
                        nowTime, startDate));

        assertFalse(hasInvalidStartDate,
                "At least one connected device has a start date after now.");

        // Assert that the list show devices with passed End day
        boolean listHasDeviceWithOldEndDate = connectedProjects.stream()
                .map(map -> map.get("End date"))
                .anyMatch(endDate -> TimeConverter.isSecondDateAfter(
                        endDate, nowTime));

        assertTrue(listHasDeviceWithOldEndDate,
                "There should be at least one device with old end date.");
    }

    /**
     * devices/C22/101915/details
     */
    @Then("the {string} header are")
    public void theHeaderAre(String domain, DataTable table) {
        List<String> expectedHeaders = table.row(0);

        DeviceConnectionHistoryPanel panel = devicePO.getDeviceConnectionHistoryPanel();
        List<String> actualHeaders = panel.getDeviceConnectionTable().getHeader().getAllValuesAsString();

        boolean allHeadersMatches = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedHeaders, actualHeaders);
        assertTrue(allHeadersMatches);

    }

    /**
     * /settings/monitoring
     * When changing standard all channels for that standard should be ON but have value Not Set.
     */
    @Then("all triggers are Not set:ON")
    public void allTriggersAreNotSetON() {
        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();

        List<ChannelWrapper> channelWrappers = monSettings.getChannelWrappers();
        List<Channel> channels = channelWrappers.stream()
                .flatMap(wrapper -> wrapper.getChannels().stream())
                .toList();

        // All toggles shall be ON
        channels.forEach(channel -> {
            assertEquals(true, channel.getToggleField().getState(),
                    () -> "expected/actual: " + "true" + "/" + channel.getToggleField().getState());
        });

        // All input field shall have placeholder text 'Not set'
        channels.forEach(channel -> {
            assertEquals("Not set", channel.getInputField().getText(),
                    () -> "expected/actual: " + "Not set" + "/" + channel.getInputField().getText());
        });
    }

    @And("I set a value to {string} top trigger")
    public void iSetValueOnTrigger(String deviceType) {
        devicePO.inputTriggerValueAndPressKey(deviceType, "1", "Tab");
    }

    @Then("all triggers should have same value")
    public void allTriggersShouldHaveSameValue() {
        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();

        // Get all channels
        List<Channel> channels = monSettings.getChannelWrappers().stream()
                .flatMap(wrapper -> wrapper.getChannels().stream())
                .toList();

        // Get trigger value for the channels with alarm toggle ON
        List<String> triggerValues = channels.stream()
                .filter(channel -> channel.getToggleField().getState() != null)
                .map(channel -> channel.getInputField().getText())
                .toList();

        // Use the first value as a benchmark
        triggerValues.forEach(str -> assertEquals(triggerValues.getFirst(), str));
    }

    /**
     * Gets all connected devices in the list, and based on the serial open DeviceDetailsPanel.
     */
    @Then("all devices in the list have details")
    public void allDevicesInTheListHaveDetails() {

        List<DeviceType> connectedDeviceTypes = List.of(
                DeviceType.C22,
                POINT,
                DeviceType.D10,
                DeviceType.C12_LOGGER
        );

        connectedDeviceTypes.forEach(type -> {
            int serial = DeviceProperties.getConnectedSerial(type);

            Navigate.company()
                    .device(type, serial)
                    .details()
                    .get();

            DeviceDetailsPanel deviceDetailsPanel = ddPO.getDeviceDetailsPanel(type.getType());
        });

    }

    @When("I navigate to device details, description is {string}")
    public void iNavigateToDeviceDetailsDescriptionIsSW_QAsDevice(String expectedDescription, DataTable table) {
        List<String> deviceTypes = table.row(0);

        deviceTypes.forEach(type -> {

            Navigate.company()
                    .device(type, DeviceProperties.getConnectedSerial(type))
                    .details()
                    .get();

            DeviceDetailsPanel deviceDetailsPanel = ddPO.getDeviceDetailsPanel(type);

            // Verify at least one button has icon type CLIPBOARD and expected description
            boolean buttonExist = deviceDetailsPanel.getDeviceDetails().stream()
                    .anyMatch(button -> button.getIcon().getType().equals(CLIPBOARD)
                            && button.getText().equals(expectedDescription));

            assertTrue(buttonExist,
                    "Expected button with CLIPBOARD icon and text: " + expectedDescription);
        });
    }

    @And("this sensor is listed as a connected sensor to {string}")
    public void thisSensorIsListedAsAConnectedSensor(String type, DataTable table) {
        List<String> expectedSensors = table.row(0);

        DeviceDetailsPanel deviceDetailsPanel = ddPO.getDeviceDetailsPanel(type);

        List<Table.TableRow> sensorRows = deviceDetailsPanel.getSensorPanel().getSensorsTable().getContent();
        Table.TableRow headerRow = deviceDetailsPanel.getSensorPanel().getSensorsTable().getHeader();

        // Assert that all expectedSensor had a match in sensorRows
        boolean allPresent = expectedSensors.stream().allMatch(sensorType ->
                sensorRows.stream()
                        .anyMatch(row -> row.getTableCellByTableHeader(headerRow, "Device").getCellText().contains(sensorType))
        );

        assertTrue(allPresent, "Not all expected sensors are present in the panel");
    }

    @When("POINT device details are correct")
    public void iCheckDeviceDetailsEachPOINTHasASensor(DataTable table) {
        List<String> expectedSensorTypes = table.row(0);

        filterPO.changeFilter("POINT");

        List<DeviceItem> deviceItems = asidePO.getAside().getDeviceItems();

        deviceItems.forEach(point -> {

            Navigate.company()
                    .device(POINT, point.getSerial())
                    .details()
                    .get();

            DeviceDetailsPanel panel = ddPO.getDeviceDetailsPanel("POINT");
            Table sensorTable = panel.getSensorPanel().getSensorsTable();

            List<NoticeItem> noticeItems = panel.getNoticeItems();

            boolean noSensorConnected = false;

            if (noticeItems != null) {

                List<String> infoAndWarningTexts = noticeItems.stream()
                        .map(NoticeItem::getText)
                        .toList();

                // This only shows up for units that have connected to INFRA
                noSensorConnected = infoAndWarningTexts != null
                        && infoAndWarningTexts.contains("No sensor connected");


            }

            String actualSensor = sensorTable.getContent().getFirst()
                    .getTableCellByTableHeader(sensorTable.getHeader(), "Device")
                    .getCellText();

            boolean isInUnboxingState = panel.getDeviceDetails().isEmpty();

            if (isInUnboxingState) {
                // Assert that sensorPanel declare no sensor connected
                assertEquals("undefined #undefined", actualSensor,
                        () -> "expected/actual: " + "undefined #undefined" + "/" + actualSensor);

            } else if (noSensorConnected) {
                System.out.println("No sensor is connected.");

                // Assert that the listItem has banner with same meaning as panel
                assertTrue(point.getBanners().contains("No sensor connected"));

                // Assert that sensorPanel declare no sensor connected
                assertEquals("undefined #undefined", actualSensor,
                        () -> "expected/actual: " + "undefined #undefined" + "/" + actualSensor);
            } else {
                System.out.println("A sensor is connected.");

                assertTrue(expectedSensorTypes.stream().anyMatch(actualSensor::contains),
                        "Expected part of actual sensor '" + actualSensor + "' to match one of the expected sensor types: " + expectedSensorTypes);
            }
        });
    }

    @When("I commit std 41:OFF and all triggers OFF")
    public void iSaveStd41OFFAndAllTriggersOff() {
        Map<String, Map<String, String>> channels = new HashMap<>();

        Map<String, String> stateAndValue = new HashMap<>();
        stateAndValue.put("state", "off");
        stateAndValue.put("value", null);
        channels.put("V", stateAndValue);
        channels.put("L", stateAndValue);
        channels.put("T", stateAndValue);

        setStdAndTriggerForC22("41", channels, true);
    }

    @Then("instead of {string} button there is {string} button")
    public void insteadOfCommittedChangesButtonThereIsSaveButton(String preChangeExpectedButton, String expectedButton) {
        DeviceSettingsMonitoringPanel monSettings = devicePO.getMonitoringSettingsPanel();

        assertEquals(expectedButton, monSettings.getPanelHeader().getRightButton().getText(), "Buttons text not " + expectedButton + " as expected.");

        // Remove the commit
        String body = "{ \"action\":\"clear\" }";
        DeviceApi.clearChange("C22", DeviceProperties.getConnectedSerial("C22"), body);
    }

    @Then("only changeable channels are displayed")
    public void onlyChangeableChannelsAreDisplayed() {
        // Go through all selectable standards, load the DeviceSettingsMonitoringPanel for each one.
        List<DeviceSettingsMonitoringPanel> settingsPanels = selectAndGetAllMonitoringSettingsPanels();

        // Assert that no of the panel's...
        settingsPanels.forEach(panel -> {
            // Collect the channels
            List<Channel> channels = panel.getChannelWrappers().stream()
                    .flatMap(wrapper -> wrapper.getChannels().stream())
                    .toList();

            // Now make sure that all channels that's displayed are channels that we can change, i.e., have a toggle.
            channels.forEach(channel -> {
                assertNotNull(channel.getToggleField().getState());
            });
        });
    }

    /**
     * Creator: .../settings/monitoring
     * Legacy: .../settings/sensor/5253
     * @return All possible MonitoringSettingsPanel combinations by iterating through all standards and, if applicable, freqWeightings.
     */
    public List<DeviceSettingsMonitoringPanel> selectAndGetAllMonitoringSettingsPanels() {
        List<DeviceSettingsMonitoringPanel> settingsPanels = new ArrayList<>();
        // Get the list of standards
        List<String> selectableStandards = devicePO.getSelectableStandards();

        // Use standard list to go through each one.
        selectableStandards.forEach(standard -> {
            devicePO.selectStandard(standard);

            // specialregel för std40. Denna if tas bort när tom dropdown är fixad
            if (devicePO.getMonitoringSettingsPanel().getFrequencyWeightingDropdown() != null
                    && standard.equals("(40) Arrêté du 1994 250mm/s 1-150Hz")) {
                // store one MonitoringSettingsPanel when dropdown is empty
                settingsPanels.add(devicePO.getMonitoringSettingsPanel());

                // store one MonitoringSettingsPanel when dropdown is YES
                List<String> selectableFreqWeigh = devicePO.getFrequencyWeightingOptions();
                selectableFreqWeigh.forEach(freqWeigh -> {
                    devicePO.selectFrequencyWeighting(freqWeigh);
                    settingsPanels.add(devicePO.getMonitoringSettingsPanel());
                });
            } else if (devicePO.getMonitoringSettingsPanel().getFrequencyWeightingDropdown() != null) {

                List<String> selectableFreqWeigh = devicePO.getFrequencyWeightingOptions();

                selectableFreqWeigh.forEach(freqWeigh -> {
                    devicePO.selectFrequencyWeighting(freqWeigh);
                    settingsPanels.add(devicePO.getMonitoringSettingsPanel());
                });
            } else {
                settingsPanels.add(devicePO.getMonitoringSettingsPanel());
            }
        });
        return settingsPanels;
    }

    @Then("connected sensor header is {string}")
    public void connectedSensorHeaderIsConnectedMeasuringDevices(String expectedHeaderText) {
        String actualHeaderText = ddPO.getDeviceDetailsPanel("POINT").getSensorPanel().getHeader();

        assertEquals(expectedHeaderText, actualHeaderText);
    }

    @Then("active legacy sensors has calibration date")
    public void activeLegacySensorsHasCalibrationDate() {
        // Prepare gui
        asidePO.selectThisColumnForAsideTable("Calibration date");
        filterPO.clearAllDeviceFilters();

        // Check that a (supposed) active sensor now has date
        filterPO.changeFilter("S50");    // To avoid finding the S50's logger
        asidePO.makeSearchInAside(DeviceProperties.getConnectedSerial("S50"));

//        Aside aside = asidePO.getAside();
        Aside aside = asidePO.getAside(10);

        Table.TableRow s50row = aside.getTable().getContent().getFirst();
        String actualCalibrationDate = s50row.getStringByTableHeader(aside.getTable().getHeader(), "Calibration date");

        assertNotEquals("-", actualCalibrationDate,
                () -> "not expected/actual: " + "-" + "/" + actualCalibrationDate);
    }

    @Then("active legacy loggers has firmware version")
    public void activeLegacyLoggersHasFirmwareVersion() {
        asidePO.selectThisColumnForAsideTable("Firmware version");

        // Check that a (supposed) active logger now has fw version
        asidePO.makeSearchInAside(DeviceProperties.getConnectedSerial("D10"));

//        Aside aside = asidePO.getAside();
        Aside aside = asidePO.getAside(10);

        Table.TableRow d10row = aside.getTable().getContent().getFirst();
        // Find the value for D10
        String cellData = d10row.getStringByTableHeader(aside.getTable().getHeader(), "Firmware version");

        assertNotEquals("-", cellData,
                () -> "unexpected/actual: " + "-" + "/" + cellData);
    }

    @When("I create uncommitted change for C22")
    public void iCreateUncommittedChangeForC22() {
        ApiChangeGlue.clearChangeIfPossible("C22");
        PlaywrightActions.sleep(1); // Give UI a sec to register the cleared change

        Navigate.company()
                .devices()
                .get();

        asidePO.clickOnThisAsideItem(DeviceProperties.getConnectedSerial("C22"));

        // click settings
        devicePO.clickPanelSelection("Settings");
        // click monitoring
        devicePO.clickPanelSelection("Monitoring");

        // Sometimes it takes a while for monSettings page to load
        PlaywrightActions.sleep(2);
        // First make sure all triggers are Not Set:off, so that we don't get silly "Some values are invalid, please correct them first!"-toast
        setAllTriggersToNotSetAndOFF();

        // select interval time
        List<String> selectableIntervalTimes = devicePO.getSelectableIntervals("C22");
        String randomNewIntervalTime = Randomizer.getRandomStringFromList(selectableIntervalTimes);

        // click Save
        devicePO.selectIntervalTime("C22", randomNewIntervalTime);
        devicePO.clickButton("save");

        PlaywrightActions.sleep(2);
    }

    @And("I clear {string} of any change")
    public void iClearC22OfAnyChange(final String type) {
        ApiChangeGlue.clearChangeIfPossible(type);
    }

    @When("I create uncommitted change for S50")
    public void iCreateUncommittedChangeForS50() {
        ApiChangeGlue.clearChangeIfPossible("D10");

        Navigate.company()
                .devices()
                .get();

        asidePO.makeSearchInAside(DeviceProperties.getConnectedSerial("D10"));

        asidePO.clickOnThisAsideItem(DeviceProperties.getConnectedSerial("D10"));

        // click connected sensor S50
        devicePO.clickConnectedDevice("S50");

        https://sigicom.test.indev.sigicom.net/#/company/devices/D10/103748/settings/sensor/5307
        Navigate.company()
                .device(D10, DeviceProperties.getConnectedSerial("D10"))
                .settings()
                .sensor(DeviceProperties.getConnectedSerial("S50"))
                .get();

        // First make sure all triggers are Not Set:off, so that we don't get silly "Some values are invalid, please correct them first!"-toast
        setAllTriggersToNotSetAndOFF();

        // select interval time
        List<String> selectableIntervalTimes = devicePO.getSelectableIntervals("S50");
        String randomNewIntervalTime = Randomizer.getRandomStringFromList(selectableIntervalTimes);

        // click Save
        devicePO.selectIntervalTime("S50", randomNewIntervalTime);
        devicePO.clickButton("save");

        PlaywrightActions.sleep(2);
        asidePO.closeSearchInAside();
    }

    private void setAllTriggersToNotSetAndOFF() {
        DeviceSettingsMonitoringPanel msp = devicePO.getMonitoringSettingsPanel();

        List<ChannelWrapper> channelWrappers = msp.getChannelWrappers();

        // For each channel (wrapper)
        channelWrappers.forEach(channelWrapper -> {
            String header = channelWrapper.getHeader();
            // All but C50 has only one channel per wrapper
            channelWrapper.getChannels().forEach(channel -> {
                // Channel name is always null for non-C50
                String channelName = (channel.getChannelName() != null)
                        ? channel.getChannelName()
                        : header;

                if (channel.getToggleField().getState().equals(true)) {
                    devicePO.setTriggerValue(channelName, "");
                    devicePO.changeTriggerState(channel.getInputField().getHeader());
                }
            });
        });
    }

    @When("I create committed change for C22")
    public void iCreateCommittedChangeForC22() {
        iCreateUncommittedChangeForC22();

        Navigate.company()
                .device("C22", DeviceProperties.getConnectedSerial("C22"))
                .settings()
                .changes()
                .get();

        devicePO.clickButton("Commit");
        PlaywrightActions.sleep(2);
    }

    @When("I create committed change for S50")
    public void iCreateCommittedChangeForS50() {
        iCreateUncommittedChangeForS50();

        Navigate.company()
                .device("D10", DeviceProperties.getConnectedSerial("D10"))
                .settings()
                .changes()
                .get();

        devicePO.clickButton("Commit");
        PlaywrightActions.sleep(2);
    }

    @Then("I can save {string}")
    public void iCanSaveCommand(String command) {
        String deviceType = DeviceType.getCommunicatingDeviceFromCurrentUrl(Navigate.getCurrentUrl()).getType();
        boolean browserRefreshNeeded = ApiDeviceGlue.freeDeviceFromChangeIfPossible(deviceType);
        if (browserRefreshNeeded) Navigate.refreshBrowser();

        devicePO.selectOverrideCommand(command);
        devicePO.clickButton("Save");

        // Assert that no toast was caught, which can indicate trouble.
        List<String> toasts = devicePO.getToasts();
        if (!toasts.isEmpty()) {
            throw new IllegalStateException("We got toast, which was not expected. \n"
                    + String.join("\n", toasts));
        }

        // Assert that we're redirected to /settings
        String currentUrl = Navigate.getCurrentUrl();
        if (!currentUrl.contains("/settings")) {
            throw new IllegalStateException("We're not redirected to /settings");
        }

        browserRefreshNeeded = ApiDeviceGlue.freeDeviceFromChangeIfPossible(deviceType);
        if (browserRefreshNeeded) Navigate.refreshBrowser();
    }

    @Then("the calibration date is yesterdays date")
    public void theCalibrationDateIsYesterdaysDate() {
        String calibrationInfo = DeviceApi.getDevice("S50", DeviceProperties.getConnectedSerial("S50")).getCalibrationDate();

        LocalDateTime actualCalibrationDate = TimeConverter.parseDate(calibrationInfo);
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        LocalDateTime expectedDate;
        // The sync in SSD-3307 only runs at weekdays
        // Check if today is Tuesday, Wednesday, Thursday, or Friday
        if (today == DayOfWeek.TUESDAY ||
                today == DayOfWeek.WEDNESDAY ||
                today == DayOfWeek.THURSDAY ||
                today == DayOfWeek.FRIDAY) {
            expectedDate = LocalDate.now().minusDays(1).atStartOfDay();
        } else {
            // Get last Friday's date
            expectedDate = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)).atStartOfDay();
        }

        assertEquals(expectedDate, actualCalibrationDate,
                () -> "Calibration date not as expected");
    }

    @Then("selected interval time regulates which rolling_window that can be selected")
    public void selectedIntervalTimeRegulatesWhichRolling_windowThatCanBeSelected() {
        List<List<String>> testcases = Stream.of(
                List.of("15 seconds", "1", "5", "15", "60"),
                List.of("1 minute", "5", "15", "60"),
                List.of("2 minutes", "60"),
                List.of("5 minutes", "15", "60")
        ).toList();

        ApiDeviceGlue.freeDeviceFromChangeIfPossible("C50");

        DeviceSettingsMonitoringPanel msp = devicePO.getMonitoringSettingsPanel();

        if (!msp.getAdvLeqDropdown().getText().equals("Rolling")) {
            // Select Rolling
            devicePO.selectDropdownByHeader("Advanced Leq settings", "Rolling");
        } else {
            // Rolling is already selected
        }

        testcases.forEach(testcase -> {
            String intervalTime = testcase.getFirst();

            List<String> expectedOptions = new ArrayList<>();
            expectedOptions.addAll(testcase);
            expectedOptions.remove(0);

            // Select interval time
            devicePO.selectDropdownByHeader("Interval time *", intervalTime);

            // Click on Default timeslot Rolling Window dropdown
            List<String> actualOptions = devicePO.getDropdownContent("Rolling window *");

            boolean alike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedOptions, actualOptions);
            assertTrue(alike);
        });
    }

    @When("I discard change for {string}")
    public void iDiscardChangeFor(String type) {
        switch (type) {
            case "C22" -> iCreateCommittedChangeForC22();
            case "S50" -> iCreateCommittedChangeForS50();
        }

        // We have to be at /details with filtered aside
        devicePO.clickButton("Discard");
        devicePO.clickButton("Remove");
        PlaywrightActions.sleep(2);
    }

    /**
     * We cannot be sure that there is no commit notice for scn_no,
     * or how the commit notice will look like for scn_uncommitted.
     */
    @Then("aside menu show {string} change")
    public void asideMenuShowChange(String scenario) {
        // As we're interested in Aside Menu, lets not waste time in getting all DeviceItems
        Aside aside = asidePO.getAside(1);

        // Null, or "X uncommitted change/s"
        String expectedNoticeText = deductExpectedNoticeText();

        // Null, or "X uncommitted change"
        String actualNoticeText = (aside.getMenu().getCommitNotice() != null)
                ? aside.getMenu().getCommitNotice().getText()
                : null;

        assertEquals(expectedNoticeText, actualNoticeText,
                () -> "expectedNoticeText/actualNoticeText:" + expectedNoticeText + "/" + actualNoticeText);
    }

    /**
     * Fetches all devices to read if any of them have uncommitted change.
     * We cannot use aside bc we can only read the devices in the DOM.
     * @return "X uncommitted change"
     */
    private String deductExpectedNoticeText() {
        long count = DeviceApi.getDevices().stream()
                .filter(device -> device.getChange() != null)
                .filter(device -> "uncommitted".equals(device.getChange().getState()))
                .count();

        return (count == 0)
                ? null
                : (count == 1)
                ? count + " uncommitted change"
                : count + " uncommitted changes";
    }

    @Then("{string} list item show {string} change")
    public void listItemShowChange(String type, String scenario) {
        String deviceSerial = DeviceProperties.getConnectedSerial(type);

        // Make search in aside to limit units we've need to process
        asidePO.makeSearchInAside(deviceSerial);

        Aside aside = asidePO.getAside();
        IntStream.range(0, aside.getAsideItems().size())
                .forEach(d -> System.out.println(d + ": " + aside.getAsideItems().get(d).getMainText()));

        DeviceItem deviceItem = aside.getDeviceItems().stream()
                .filter(item -> item.getMainText().contains(deviceSerial))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("Expected match, but got none"));

        List<Banner> banners = deviceItem.getBanners();

        switch (scenario) {
            // At least one li
            case "uncommitted" -> {
                Optional.ofNullable(banners)
                        .orElseThrow(() -> new AssertionError("Banners list is null"))
                        .stream()
                        .filter(banner -> "Uncommitted changes".equals(banner.getText()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("No banner with text 'Committed changes' found"));
            }
            case "committed" -> {
                Optional.ofNullable(banners)
                        .orElseThrow(() -> new AssertionError("Banners list is null"))
                        .stream()
                        .filter(banner -> "Committed changes".equals(banner.getText()))
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("No banner with text 'Committed changes' found"));
            }
            case "no" -> {
                // If there is a banner, at least the banner should not be a un/commit banner
                if (banners != null && !banners.isEmpty()) {
                    assertTrue(
                            banners.stream().noneMatch(banner ->
                                    banner.getText().contains("Committed changes") ||
                                            banner.getText().contains("Uncommitted changes")
                            ),
                            "Expected no banner to contain committed or uncommitted changes"
                    );
                } else {
                    assertNull(banners);
                }
            }
        }
    }

    @And("settings panel show {string} change")
    public void settingsPanelShowChange(String scenario) {
        DeviceSettingsPanel panel = devicePO.getDeviceSettingsPanel();

        switch (scenario) {
            case "uncommitted" ->
                // Check that /settings panel has banner
                    assertNotNull(panel.getNoticeItem("Manage uncommitted changes"),
                            "No NoticeItem found.");
            case "committed" ->
                // Device details panel
                    assertNotNull(panel.getNoticeItem("Changes sent"),
                            "No NoticeItem found.");
            case "no" ->
                    assertNull(panel.getNoticeItem(FLAG),
                            "An un/commit NoticeItem found: " + panel.getNoticeItem(FLAG));
        }
    }

    @And("details panel show {string} change")
    public void detailsPanelShowChange(String scenario) {
        DeviceDetailsPanel panel = ddPO.getDeviceDetailsPanel();

        switch (scenario) {
            case "uncommitted" ->
                // Check that /settings panel has banner
                    assertNotNull(panel.getNoticeItem("Manage uncommitted changes"),
                            "No NoticeItem found.");
            case "committed" ->
                // Device details panel
                    assertNotNull(panel.getNoticeItem("Changes sent"),
                            "No NoticeItem found.");
            case "no" -> {
                assertNull(panel.getNoticeItem(FLAG),
                        "An un/commit NoticeItem found: " + panel.getNoticeItem(FLAG));
            }
        }
    }

    @Then("selectable {string} for {string} are")
    public void selectablePresetsAre(final String content, final String type) {
        List<String> expectedContent = getStringArray(content, ";");
        DeviceSettingsMonitoringPanel panel = devicePO.getMonitoringSettingsPanel(true);

        List<String> actualContent = switch (type) {
            case "C22" -> panel.getStandardDropdown().getExpandedDropdownContent();
            case "C50" -> panel.getPresetDropdown().getExpandedDropdownContent();
            default -> throw new IllegalArgumentException("Unrecognized type: " + type);
        };

        boolean alike = AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedContent, actualContent);
        assertTrue(alike);
    }

    public List<String> getStringArray(String toBeSplitted, String delimeter) {
        List<String> list = new ArrayList<>();
        try {
            String[] split = toBeSplitted.split(delimeter);
            for (String s : split) {
                list.add(s.trim());
            }
        } catch (NullPointerException e) {
            // This is Ok, variable has not been set
        }
        return list;
    }

    @When("site object then preset exist")
    public void siteObjectThenPresetExist() {
        List<Device> c50s = DeviceApi.getDevices().stream()
                .filter(device -> "C50".equals(device.getType()))
                .toList();

        String presetGuessingReleaseDate = getReleaseDateForPresetGuessingDeploy();

        c50s.forEach(c50 -> {
            Integer serial = c50.getSerial();
            System.out.println(serial);

            String lastCommunication = c50.getLastCommunication();

            if (lastCommunication == null) {
                Assertions.assertNull(c50.getPreset());
            } else {

                boolean newConnection = TimeConverter.firstIsAfterSecond(lastCommunication, presetGuessingReleaseDate);

                if (!newConnection) {
                    // Do not check these devices, as a new connection is required to 100 % know that preset exist
                } else {
                    // A C50 read after preset release should have preset
                    Assertions.assertNotNull(
                            c50.getPreset(),
                            () -> "Expected preset for C50 '" + serial + "' (lastCommunication=" + lastCommunication + ", presetRelease=" + presetGuessingReleaseDate + ")"
                    );
                }
            }
        });
    }

    // SSD-3837
    public static String getReleaseDateForPresetGuessingDeploy() {
        String url = Navigate.getCurrentUrl();
        return url.contains("test")
                ? "2026-02-14 00:00"
                : "2026-03-10 00:00";
    }

    @When("no agenda then no default timeslot")
    public void noAgendaThenNoDefaultTimeslot() {
        int c50Serial = DeviceProperties.getConnectedSerial(C50);

        Device c50 = DeviceApi.getDevice("C50", c50Serial);

        boolean hasAgenda = c50.getC50Agenda() != null;
        if (hasAgenda) {
            Navigate.company()
                    .device(C50, c50Serial)
                    .settings()
                    .monitoring()
                    .get();

            // Default timeslot is not included in the agenda, but is a timeslot nevertheless
            int expectedTimeslots = c50.getC50Agenda().getTimeslots().size() + 1;

            DeviceSettingsMonitoringPanel panel =  devicePO.getMonitoringSettingsPanel();
            int actualTimeslots = panel.getTimeslotWrappers().size();

            assertEquals(expectedTimeslots, actualTimeslots,
                    () -> "expectedTimeslots/actualTimeslots: " + expectedTimeslots + "/" + actualTimeslots);
        }
    }

    @And("api preset matches mon.settings preset")
    public void apiPresetMatchesMonSettingsPreset() {
        List<Device> c50s = DeviceApi.getDevices().stream()
                .filter(device -> "C50".equals(device.getType()))
                .toList();

        c50s.forEach(c50 -> {

            Integer serial = c50.getSerial();
            Integer expectedPresetPosition = c50.getPreset();

            // A device in pre-first-connect shall not be checked
            String lastCommunication = c50.getLastCommunication();
            if (lastCommunication == null) {
                return;
            }

            String presetGuessingReleaseDate = getReleaseDateForPresetGuessingDeploy();

            // A device with last communication before preset guessing shall not be checked
            boolean newConnection = TimeConverter.firstIsAfterSecond(lastCommunication, presetGuessingReleaseDate);
            if (!newConnection) {
                return;
            }

            Navigate.company()
                    .device(C50, serial)
                    .settings()
                    .monitoring()
                    .get();

            DeviceSettingsMonitoringPanel panel =  devicePO.getMonitoringSettingsPanel(true);

            // No preset should yield an empty preset selector
            if (expectedPresetPosition == null) {
                // null preset should yield null selectedPreset
                Integer actualPresetPosition = (panel.getPresetDropdown() == null)
                        ? null
                        : -1;

                assertEquals(expectedPresetPosition, actualPresetPosition,
                        () -> "expectedPresetPosition/actualPresetPosition: " + expectedPresetPosition + "/" + actualPresetPosition);
                return;
            }

            String selectedPreset = panel.getPresetDropdown().getText();

            List<String> presetChoices = panel.getPresetDropdown().getExpandedDropdownContent();
            int actualPresetPosition = presetChoices.indexOf(selectedPreset) + 1; // +1 is to match selenium counter with array elements

            assertEquals(expectedPresetPosition, actualPresetPosition,
                    () -> "expectedPresetPosition/actualPresetPosition: " + expectedPresetPosition + "/" + actualPresetPosition);
        });

    }


}
