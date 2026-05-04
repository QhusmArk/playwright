package com.example.playwright.steps;

import com.example.api.endpoints.DeviceApi;
import com.example.api.endpoints.UserApi;
import com.example.api.models.device.Change;
import com.example.api.models.device.Device;
import com.example.api.models.device.Trigger;
import com.example.helpers.StatusAssesser;
import com.example.helpers.StatusAssesser.Status;
import com.example.helpers.builders.BuilderFactory;
import com.example.helpers.builders.ChangeBuilder;
import com.example.helpers.hardware.JenkinsDeviceSSHConnector;
import com.example.helpers.testData.TestDataBuilder;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel.Channel;
import com.example.playwright.components.panels.DeviceSettingsMonitoringPanel.ChannelWrapper;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.steps.testdata.TestDataMonSettingsC22;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;

import static com.example.helpers.AssertionHelpers.areTrimmedAndSortedListsIdentical;
import static com.example.helpers.StatusAssesser.getDeviceCurrentMonStatus;
import static com.example.playwright.helpers.enums.DeviceType.C22;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SystemGlue extends BaseGlue {

    private List<Map<String, String>> originalTriggers;
    private String originalConfigId;

    @When("I trigger a {string}")
    @Then("I can trigger a {string}")
    public void iCanTrigger(String type) {
        DeviceType dt = DeviceType.fromType(type);

        JenkinsDeviceSSHConnector tdc = new JenkinsDeviceSSHConnector(dt);
        tdc.createManualTrigger();
        tdc.closeConnections();
    }

    @When("I reboot a {string}")
    public void iRebootAD(String type) {
        DeviceType dt = DeviceType.fromType(type);

        JenkinsDeviceSSHConnector tdc = new JenkinsDeviceSSHConnector(dt);
        tdc.rebootDevice();
        tdc.closeConnections();
    }


    @And("make C22 connect")
    public static void connectC22() {
        // connect using ssh
        JenkinsDeviceSSHConnector tdc = new JenkinsDeviceSSHConnector(C22);

        // connect device using cli
        tdc.syncConnect();
        tdc.closeConnections();
    }

    @And("make {string} connect")
    public static void connectLogger(String type) {
        DeviceType loggerType = DeviceType.fromType(type);

        if (!loggerType.isCommunicatingDevice()) {
            throw new IllegalStateException("Type '" + loggerType + "' is not approved logger.");
        }

        // connect using ssh
        JenkinsDeviceSSHConnector tdc = new JenkinsDeviceSSHConnector(loggerType);

        // connect device using cli
        tdc.syncConnect();
        tdc.closeConnections();
    }

    @Given("C22 has {string} settings")
    public void prepareC22ForTest(String preTestTestCase) {
        // First make sure the device is not occupied with un/committed change
        clearChange(C22);

        // Spara ner trigger values så att vi kan jämföra bättre
//        Device currentDevice = DeviceApi.getAboutDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
        Device currentDevice = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

        this.originalTriggers = parseApiTriggersToMap(currentDevice.getTriggers());
        printTestcase("current settings", currentDevice.getStandard(), currentDevice.getFrequencyWeighting(), parseApiTriggersToMap(currentDevice.getTriggers()));
        this.originalConfigId = currentDevice.getConfigId();
        System.out.println("originalConfigId: " + originalConfigId);

        String expectedStandard = TestDataMonSettingsC22.getStandard("api", preTestTestCase);
        String expectedFreqWeighting = TestDataMonSettingsC22.getFrequencyWeighting("api", preTestTestCase);
        List<Map<String, String>> expectedTriggers = TestDataMonSettingsC22.getTestTriggers(preTestTestCase);

        printTestcase("sending " + preTestTestCase, expectedStandard, expectedFreqWeighting, expectedTriggers);

        String changeBody = buildChange(expectedStandard, expectedFreqWeighting, expectedTriggers);
        // todo: Skulle det gå att fixa så jag inte gör change om enheten redan är i rätt state? Tex:
        commitChange(changeBody);

        connectC22();

        boolean apiShowPreTestTestCase = validateC22HasTheseSettingsInApi(expectedStandard, expectedFreqWeighting, expectedTriggers);

        if (apiShowPreTestTestCase) {
            System.out.println("\n" + preTestTestCase + " settings in C22. Proceeding with test.");
            // todo: kan detta räcka för att göra så att Change från GUI inte använder en gammal configId?
            // 5 funkade bra, funkar 3?
            PlaywrightActions.sleep(3);
        } else {
            throw new IllegalStateException("\n" + preTestTestCase + " not in device after 200 seconds. Aborting test.");
        }

    }

    private void printTestcase(String testCase, String expectedStandard, String expectedFreqWeighting, List<Map<String, String>> expectedTriggers) {
        System.out.println("******** " + testCase + " **************");
        System.out.println(expectedStandard + ":" + expectedFreqWeighting);
        expectedTriggers.forEach(trigger -> {
            System.out.println(trigger.get("name") + "-> " + trigger.get("value") + ": " + trigger.get("state"));
        });
        System.out.println("**********************");

    }

    private boolean validateC22HasTheseSettingsInApi(String expectedStandard, String expectedFreqWeighting, List<Map<String, String>> expectedTriggers) {
        PlaywrightActions.sleep(1);
        int timeToWait = 200;

        // Start polling
        for (int s = 1; s <= timeToWait; s++) {
            System.out.println("\n" + s + ": Polling for new settings in device.");

//            Device device = DeviceApi.getAboutDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
            Device device = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

            //        System.out.println("\n" + device + "\n");
            String currentConfigId = device.getConfigId();
            // NB. Config_id sent with Change is not stored in Device, but in Change
            Change change = DeviceApi.getChange("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
    //        System.out.println("\n" + change + "\n");

            // If deviceChange is consumed by fw, AND the deviceChange has been implemented in device; i.e., a new config_id has been created
            if (change.getState() == null) {
                System.out.println("Change is null");
                if (!currentConfigId.equals(originalConfigId)) {
                    System.out.println("New config id");
                    String currentStandard = device.getStandard();
                    String currentFreqWeighting = device.getFrequencyWeighting();
                    List<Map<String, String>> currentTriggers = parseApiTriggersToMap(device.getTriggers());

                    boolean correctStandard = expectedStandard.equals(currentStandard);
                    System.out.println("correctStandard: " + correctStandard);

                    boolean correctFreqWeighting;
                    if (expectedFreqWeighting == null) {
                        correctFreqWeighting = (currentFreqWeighting == null);
                    } else {
                        correctFreqWeighting = expectedFreqWeighting.equals(currentFreqWeighting);
                    }
                    System.out.println("correctFreqWeighting: " + correctFreqWeighting);

                    boolean correctTriggers = compareTriggersAfterApiChange(expectedTriggers, currentTriggers);
                    System.out.println("correctTriggers: " + correctTriggers);

                    if (correctStandard && correctFreqWeighting && correctTriggers) {
                        return true;
                    }
                } else {
                    System.out.println("Commit consumed, but not yet implemented by device.");
                    PlaywrightActions.sleep(1);
                }

            } else if (change.getState().equals("uncommitted")) {
                throw new IllegalStateException("Change should not be in state uncommitted.");
            } else if (change.getState().equals("committed")) {
                PlaywrightActions.sleep(1);
            }
        }
        return false;
    }

    @When("I change to {string} settings")
    @When("I commit {string} settings")
    public void iCommitExpResSettings(String testcase) {
        // First make sure the device is not occupied with un/committed change
        clearChange(C22);

        // Build the testcase
        String expectedStandard = TestDataMonSettingsC22.getStandard("gui", testcase);
        String expectedFreqWeighting = TestDataMonSettingsC22.getFrequencyWeighting("gui", testcase);
        List<Map<String, String>> expectedTriggers = TestDataMonSettingsC22.getTestTriggers(testcase);
        printTestcase("testcase " + testcase, expectedStandard, expectedFreqWeighting, expectedTriggers);

        // Get current from the mon settings panel
        DeviceSettingsMonitoringPanel msp = devicePO.getMonitoringSettingsPanel();
//        List<Map<String, String>> currentTriggers = parseGuiTriggersToMap(msp.getChannels());

        List<ChannelWrapper> channelWrappers = msp.getChannelWrappers();
        List<Channel> channels = channelWrappers.stream()
                .flatMap(wrapper -> wrapper.getChannels().stream())
                .toList();
        List<Map<String, String>> currentTriggers = parseGuiTriggersToMap(channels);

        String currentStandard = msp.getStandardDropdown().getText();

//        Optional<String> optCurrentFreqWeighting = msp.getFrequencyWeighting();
        Optional<String> optCurrentFreqWeighting = Optional.of(msp.getFrequencyWeightingDropdown().getText());

        printTestcase("current settings", currentStandard, optCurrentFreqWeighting.orElse("OFF"), currentTriggers);

        // todo: här behöver jag spara ner api-triggers.
        //  Eller spara det för trigger values = null.
        //  Använd sedan värdet när jag assertar.
        //  För om jag har null på trigger value, då kommer expected resultat vara det värde som finns i enheten nu.
        //  För C2x/Point i alla fall. Annat är det nog för legacy o C50 (med det tar jag en annan gång). Behåll dock den här kommentaren
        //  Så spara api-triggers så jag kan hämta dem i min assertion.
        //  Kan det sparas i en Map<String, Double>?.

        boolean correctStandard = expectedStandard.equals(currentStandard);

        boolean correctFreqWeighting = (expectedFreqWeighting == null)
                ? optCurrentFreqWeighting.isEmpty()
                : optCurrentFreqWeighting.isPresent() && expectedFreqWeighting.equals(optCurrentFreqWeighting.get());

        boolean correctTriggers = validateTriggerEquals(expectedTriggers, currentTriggers);

        System.out.println("\tcorrectStandard: " + correctStandard);
        System.out.println("\tcorrectFreqWeighting: " + correctFreqWeighting);
        System.out.println("\tcorrectTriggers: " + correctTriggers);

        // Do not change standard if not necessary

        // Do not change freqWeighing if not necessary

        // Always change triggers if changed standard and/or freqWeighing

        // Do not change triggers if standard and/or freqWeighing has not been changed

        if (correctStandard && correctFreqWeighting && correctTriggers) {
            throw new IllegalStateException("ExpRes already in device!");
        }

        if ((!correctStandard && correctFreqWeighting)
            || (!correctStandard && !correctFreqWeighting)) {
            System.out.println("Changing standard, freqW and triggers");
            // Always change/set freqWeighting and triggers if standard is changed
            devicePO.setStandard(expectedStandard);
            devicePO.setFrequencyWeighting(expectedFreqWeighting);   // if freqW is null, is no problem, method contains null check
            devicePO.setTriggersAfterStdOrFreqWChange(expectedTriggers);
        } else if (correctStandard && !correctFreqWeighting) {
            System.out.println("Changing standard and freqW");
            // Always change/set triggers if freqWeighting is changed
            devicePO.setFrequencyWeighting(expectedFreqWeighting);
            devicePO.setTriggersAfterStdOrFreqWChange(expectedTriggers);
        } else {
            System.out.println("Changing triggers");
            // Only change the triggers that needs to be changed
            setTriggersWithoutStdOrFreqWChange(expectedTriggers);
        }

        System.out.println("\nMonitoringSettingsPanel after new settings inputed, but before save.");

        boolean connectNeeded = saveAndCommitChange();

        // Make C22 poll for change
        if (connectNeeded) {
            connectC22();
        }
    }

    /**
     * Method used for triggers unaltered by previous change.
     * Use this method when no manual or standard/freqWeighting has changed trigger value or state.
     * I.e., when both preReq and ExpRes have the same Testsuite (e.g., A -> A)
     */
    private void setTriggersWithoutStdOrFreqWChange(List<Map<String, String>> expectedTriggers) {
        System.out.println("\nSetting each trigger according to channelsValuesAndStates");

        expectedTriggers.forEach((trigger) -> {
            String triggerName = trigger.get("name");
            System.out.println("Processing trigger: " + triggerName);

            String valueToSet = trigger.get("value");
            String stateToSet = trigger.get("state");

            String currentToggleState = devicePO.getTriggerState(triggerName);
            currentToggleState = currentToggleState.equals("true") ? "ON" : "OFF";

            String currentToggleValue = "";
            // toggles in ON has values
            if (currentToggleState.equals("ON")) {
                currentToggleValue = devicePO.getTriggerValue(triggerName);
            }

            if (currentToggleState.equals("ON") && stateToSet.equals("ON")) {
                // Then check if we need to change the value
                if (currentToggleValue.equals(valueToSet)) {
                    // no need to do anything
                } else {
                    devicePO.setTriggerValue(triggerName, valueToSet);
                }
            } else if (currentToggleState.equals("OFF") && stateToSet.equals("OFF")) {
                // set toggle to ON
                devicePO.changeTriggerState(triggerName);

                if (valueToSet == null) {
                    // nullify the value field
                    devicePO.setTriggerValue(triggerName, "");
                    // set toggle to OFF
                    devicePO.changeTriggerState(triggerName);
                } else {
                    // read the previously hidden value
                    currentToggleValue = devicePO.getTriggerValue(triggerName);

                    if (currentToggleValue.equals(valueToSet)) {
                        // no need to do anything but set toggle to OFF
                        devicePO.changeTriggerState(triggerName);
                    } else {
                        // set the new value
                        devicePO.setTriggerValue(triggerName, valueToSet);
                        // set toggle to OFF
                        devicePO.changeTriggerState(triggerName);
                    }
                }
                // no need to do anything
            } else if (currentToggleState.equals("OFF") && stateToSet.equals("ON")) {
                // set toggle to ON
                devicePO.changeTriggerState(triggerName);
                // Now we need to get the value
                currentToggleValue = devicePO.getTriggerValue(triggerName);
                if (currentToggleValue.equals(valueToSet)) {
                    // no need to do anything
                } else {
                    devicePO.setTriggerValue(triggerName, valueToSet);
                }
            } else if (currentToggleState.equals("ON") && stateToSet.equals("OFF")) {
                // Then check if we need to change the value
                if (currentToggleValue.equals(valueToSet)) {
                    // no need to do anything
                } else {
                    if (valueToSet == null) {
                        devicePO.setTriggerValue(triggerName, "");
                    } else {
                        devicePO.setTriggerValue(triggerName, valueToSet);
                    }
                }
                // set toggle to OFF
                devicePO.changeTriggerState(triggerName);
            }
        });

    }

    @And("{string} is found in Monitoring settings panel")
    public void isFoundInMonitoringSettingsPanel(String testcase) {
        // Get current from the mon settings panel
        DeviceSettingsMonitoringPanel msp = devicePO.getMonitoringSettingsPanel();

        String currentStandard = msp.getStandardDropdown().getText();
//        Optional<String> currentFreqWeighting = msp.getFrequencyWeighting();
        Optional<String> currentFreqWeighting = Optional.of(msp.getFrequencyWeightingDropdown().getText());

//        List<Map<String, String>> currentGuiTriggers = parseGuiTriggersToMap(msp.getChannels());
        List<ChannelWrapper> channelWrappers = msp.getChannelWrappers();
        List<Channel> channels = channelWrappers.stream()
                .flatMap(wrapper -> wrapper.getChannels().stream())
                .toList();
        List<Map<String, String>> currentGuiTriggers = parseGuiTriggersToMap(channels);

        String expectedStandard = TestDataMonSettingsC22.getStandard("gui", testcase);
        String expectedFreqWeighting = TestDataMonSettingsC22.getFrequencyWeighting("gui", testcase);
        List<Map<String, String>> expectedTriggers = TestDataMonSettingsC22.getTestTriggers(testcase);

        boolean correctStandard = expectedStandard.equals(currentStandard);

        boolean correctFreqWeighting = (expectedFreqWeighting == null)
                ? currentFreqWeighting.isEmpty()
                : currentFreqWeighting.isPresent() && expectedFreqWeighting.equals(currentFreqWeighting.get());

        boolean correctTriggers = compareTriggersAfterGuiChange(expectedTriggers, currentGuiTriggers);

        assertTrue(correctStandard && correctFreqWeighting && correctTriggers,
                () -> "Expected settings not in C22 .../monitoring/settings");
    }

    private List<Map<String, String>> parseGuiTriggersToMap(List<Channel> channels) {
        List<Map<String, String>> parsedTriggers = new ArrayList<>();

        channels.forEach(channel -> {
            Map<String, String> triggerMap = new HashMap<>();

            triggerMap.put("name", channel.getInputField().getHeader());
            triggerMap.put("value", channel.getInputField().getText());
            String stateToSet = (channel.getToggleField().getState()) ? "ON" : "OFF";
            triggerMap.put("state", stateToSet);

            parsedTriggers.add(triggerMap);
        });
        return parsedTriggers;
    }

    private List<Map<String, String>> parseApiTriggersToMap(List<Trigger> triggers) {
        List<Map<String, String>> parsedTriggers = new ArrayList<>();

        triggers.forEach(trigger -> {
            Map<String, String> triggerMap = new HashMap<>();

            triggerMap.put("name", trigger.getName());
            triggerMap.put("value", String.valueOf(trigger.getTrig_recording_value()));
            String stateToSet = (trigger.getTrig_recording_enable()) ? "ON" : "OFF";
            triggerMap.put("state", stateToSet);

            parsedTriggers.add(triggerMap);
        });
        return parsedTriggers;
    }

    private String buildChange(String expectedStandard, String expectedFreqWeighting, List<Map<String, String>> expectedTriggers) {
//        Device device = DeviceApi.getAboutDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
        Device device = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

        System.out.println("buildChange config_id: " + device.getConfigId());

        ChangeBuilder builder = BuilderFactory.getBuilder(
            BuilderFactory.Providers.DEVICE,
            ChangeBuilder.class);
        builder
                .givenVib()
                .addVibChannel()
                .thenStandard(expectedStandard)
                .withConfigId(device.getConfigId());

        expectedTriggers.forEach((trigger) -> {
            String triggerName = trigger.get("name");
            String valueToSet = trigger.get("value");
            Boolean stateToSet = trigger.get("state").equals("ON");

            if (valueToSet == null) {
                builder
                        .thenChannelNameAndTriggerValue(triggerName, null, stateToSet);
            } else {
                builder
                        .thenChannelNameAndTriggerValue(triggerName, Double.valueOf(valueToSet), stateToSet);
            }
        });

        if (expectedFreqWeighting != null) {
            builder
                .thenFrequencyWeighting(expectedFreqWeighting);
        }

        builder.build();
        return builder.buildJson();
    }

    private Change commitChange(final String body) {
        DeviceApi.updateChange("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")), body);

        Change change = DeviceApi.getChange("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
//        System.out.println(change);
        if (change.getState() == null) {
            throw new IllegalStateException("No change in Test. Aborting test.");
        }
        System.out.println("\nCommitting change to C22.");
        return DeviceApi.commitChange("C22", DeviceProperties.getConnectedSerial("C22"));
    }

    public boolean saveAndCommitChange() {
//        Device currentDevice = DeviceApi.getAboutDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
        Device currentDevice = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

        this.originalConfigId = currentDevice.getConfigId();

        //todo: börja avlyssna trafiken här:
//        Chrome.listenForPutChangeRequests(); // tex {"config_id":"pFoWUCeZEc6o1SVH3youog==","vib":{"channels":[{"name":"V","trigger_enable":false},{"name":"L","trigger_value":22,"trigger_enable":false},{"name":"T","trigger_enable":false}]}}
//        Chrome.monitorPutRequests();
        // todo: Jag borde kunna använda detta för att testa att FE skickar "rätt" grejer till api'et.

        PlaywrightActions.sleep(1);
        // Save settings
        devicePO.clickButton("save");
        PlaywrightActions.sleep(3);

        // Sometimes a change is consumed at once, and then a connect is not needed.
//        Device device = DeviceApi.getAboutDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
        Device device = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

//        System.out.println("\n" + device);
        Change change = device.getChange();
//        String currentConfigId = device.getConfigId();
//        System.out.println("currentConfigId: " + currentConfigId);

        if (change == null) {   // Neither uncommitted nor uncommitted, i.e., we should not be here after Saved change. :-(
            throw new PendingException("saveAndCommitChange(): Skipping test as Change was null despite being Saved.");
        } else {
            if (change.getState().equals("uncommitted")) {   // Expected state
                System.out.println("Committing change");
                // To save time, commit change by api instead of gui
                DeviceApi.commitChange("C22", Integer.parseInt( DeviceProperties.getConnectedSerial("C22")), "{ \"action\":\"commit\" }");
                return true;
            } else if (change.getState().equals("committed")) { // We should not be here after Saved change, but before Commit. :-(
                throw new PendingException("saveAndCommitChange(): Skipping test as Change was committed after Save.");
            }
            return true;
        }
    }

    private boolean compareTriggersAfterApiChange(List<Map<String, String>> expectedTriggers, List<Map<String, String>> currentTriggers) {

        boolean triggerNamesMatches = compareTriggerNames(expectedTriggers, currentTriggers);

        if (!triggerNamesMatches) {
            System.out.println("Trigger names did not match.");
            return false;
        }

        for (Map<String, String> expectedTrigger : expectedTriggers) {
            String expectedTriggerName = expectedTrigger.get("name");
            String expectedTriggerState = expectedTrigger.get("state");
            String expectedTriggerValue = expectedTrigger.get("value");
            System.out.println("\t" + expectedTriggerName + "-> " + expectedTriggerValue + ": " + expectedTriggerState);

            // Compare expectedTrigger state
            boolean triggerStateMatches = compareTriggerStates(expectedTrigger, currentTriggers);

            if (!triggerStateMatches) {
                System.out.println("Not matching triggerState for channel " + expectedTriggerName);
                return false;
            }

            boolean triggerValueMatches;
            // If we sent no value to api/device, then the old value is kept in the C22 (not so for legacy sensors). Use that to compare.
            if (expectedTrigger.get("value") == null) {
                System.out.println("\tUsing original value to compare with current value");
                System.out.println("originalTriggerValues: " + originalTriggers);

                triggerValueMatches = compareTriggerValue(expectedTriggerName, originalTriggers, currentTriggers);

            } else {
                System.out.println("\tUsing expected value to compare with current value");

                triggerValueMatches = compareTriggerValue(expectedTriggerName, expectedTriggers, currentTriggers);
            }

            if (!triggerValueMatches) {
                System.out.println("Not matching triggerValue for channel " + expectedTriggerName);
                return false;
            }

        }
        return true;
    }

    /**
     * When swapping between different standards
     * Compares the trigger value for a given range of acceptable trigger names between expected and current triggers.
     * Ensures no ArrayIndexOutOfBoundsException is thrown.
     *
     * @param triggerName         A name we use to get a list of acceptable trigger names to check.
     * @param expectedTriggers    The list of expected triggers, where each trigger is a map of name-value pairs.
     * @param currentTriggers      The list of current triggers, where each trigger is a map of name-value pairs.
     * @return True if the expected and current values for the matched trigger name are equal; false otherwise.
     */
    // todo: kontrollera den här metoden en gång till, tänk om den är helt feltänk?
    private boolean compareTriggerValue(String triggerName, List<Map<String, String>> expectedTriggers, List<Map<String, String>> currentTriggers) {
        if (triggerName.equals("R")) {
            System.out.println("Not comparing R channel.");
            // ATM we cannot compare the value in R-channel, as it's an odd bird.
            return true;
        } else {
            List<String> triggerNames = switch (triggerName) {
                case "V", "rV", "VDV-V accu" -> List.of("V", "rV", "VDV-V accu");
                case "L", "rL", "VDV-L accu" -> List.of("L", "rL", "VDV-L accu");
                case "T", "rT", "VDV-T accu" -> List.of("T", "rT", "VDV-T accu");
//                case "R" -> List.of("R");
                default -> throw new IllegalStateException("Unexpected triggerName: " + triggerName);
            };

            String expectedValue = expectedTriggers.stream()
                    .filter(map -> triggerNames.contains(map.get("name")))  // We do not know which channel name was used, when originalTriggers was saved.
                    .map(map -> map.get("value"))
                    .findFirst()
                    .orElse(null); // Avoids ArrayIndexOutOfBoundsException

            String currentValue = currentTriggers.stream()
                    .filter(map -> map.get("name").equals(triggerName))
                    .map(map -> map.get("value"))
                    .map(this::appendDotZeroIfInteger)
                    .toList().getFirst();
            System.out.println("expectedValue/currentValue: " + expectedValue + "/" + currentValue);
            return expectedValue.equals(currentValue);
        }
    }

    private boolean compareTriggerStates(Map<String, String> expectedTrigger, List<Map<String, String>> currentTriggers) {
        String expectedState = expectedTrigger.get("state");

        String currentState = currentTriggers.stream()
                .filter(map -> map.get("name").equals(expectedTrigger.get("name")))
                .map(map -> map.get("state"))
                .toList().getFirst();

        return expectedState.equals(currentState);
    }



    private boolean compareTriggerNames(List<Map<String, String>> expectedTriggers, List<Map<String, String>> currentTriggers) {
        return areTrimmedAndSortedListsIdentical(
                expectedTriggers.stream().map(map -> map.get("name")).toList(),
                currentTriggers.stream().map(map -> map.get("name")).toList());
    }

    private boolean compareTriggersAfterGuiChange(List<Map<String, String>> expectedTriggers, List<Map<String, String>> currentTriggers) {

        boolean triggerNamesMatches = compareTriggerNames(expectedTriggers, currentTriggers);

        if (!triggerNamesMatches) {
            System.out.println("Trigger names did not match.");
            return false;
        }

        for (Map<String, String> expectedTrigger : expectedTriggers) {
            String expectedTriggerName = expectedTrigger.get("name");
            String expectedTriggerState = expectedTrigger.get("state");
            String expectedTriggerValue = expectedTrigger.get("value");
            System.out.println("\t" + expectedTriggerName + "-> " + expectedTriggerValue + ": " + expectedTriggerState);

            // Compare expectedTrigger state
            boolean triggerStateMatches = compareTriggerStates(expectedTrigger, currentTriggers);

            if (!triggerStateMatches) {
                System.out.println("No matching triggerState for channel " + expectedTriggerName);
                return false;
            }

            boolean triggerValueMatches;
            if (expectedTriggerState.equals("OFF")) {   // do not check value, as GUI do not show value when expectedTrigger is OFF
                triggerValueMatches = compareTriggerValueWhenOFF(expectedTriggerName, expectedTriggers, currentTriggers);
            } else {
                triggerValueMatches = compareTriggerValue(expectedTriggerName, expectedTriggers, currentTriggers);
            }

            if (!triggerValueMatches) {
                System.out.println("No matching expectedTrigger value for channel " + expectedTriggerName);
                return false;
            }

        }

        return true;
    }

    // todo: göra om till compareTriggersGUI? Och sen kombinera den andra compareTrigger med denna, och ändra namnet till compareTriggersAPI
    private boolean compareTriggerValueWhenOFF(String expectedTriggerName, List<Map<String, String>> expectedTriggers, List<Map<String, String>> currentTriggers) {
        System.out.println("expectedTriggerName: " + expectedTriggerName);
        expectedTriggers.forEach(System.out::println);
        System.out.println("*******");
        currentTriggers.forEach(System.out::println);

        String expectedValue = null;

        String currentValue = currentTriggers.stream()
                .filter(map -> map.get("name").equals(expectedTriggerName))
                .map(map -> map.get("value"))
                .toList().getFirst();
        System.out.println("currentValue: " + currentValue);

        return currentValue == null;
    }


// todo: om expected trigger value = null, då har jag inte sparat ner ett trigger värde tidigare i testet.
//  apiet har då inte skickat något nytt triggervärde till C22, som i sin tur använder ett gammalt värde.
//  Det värdet har jag sparat ner i apiTriggerValues. Använd det
    // api
    private boolean validateTriggerEquals(List<Map<String, String>> expectedTriggers, List<Map<String, String>> currentTriggers) {
        // todo: flytta till metoder
        // Save all channel names to list
        List<String> expectedTriggerNames = expectedTriggers.stream().map(map -> map.get("name")).toList();
        List<String> currentTriggerNames = currentTriggers.stream().map(map -> map.get("name")).toList();

        boolean triggerNamesMatches = areTrimmedAndSortedListsIdentical(expectedTriggerNames, currentTriggerNames);
        System.out.println("triggerNamesMatches: " + triggerNamesMatches);

        // Only proceed if triggerNamesMatches is true
        if (triggerNamesMatches) {
            // Save all channel state to list
            List<String> expectedTriggerStates = expectedTriggers.stream().map(map -> map.get("state")).toList();
            List<String> currentTriggerStates = currentTriggers.stream().map(map -> map.get("state")).toList();

            boolean triggerStatesMatches = areTrimmedAndSortedListsIdentical(expectedTriggerStates, currentTriggerStates);
            System.out.println("triggerStatesMatches: " + triggerStatesMatches);

            // Only proceed if triggerStatesMatches is true
            if (triggerStatesMatches) {
                // Save all channel values to list
                List<String> expectedTriggerValues = expectedTriggers.stream().map(map -> map.get("value")).toList();

                System.out.println("expectedTriggerValues");
                expectedTriggerValues.forEach(System.out::println);
                //todo: om ett expectedValue = null, då behöver jag byta ut det mot ett lagrat värde

                // IN parses 22.0 to 22, so we need to add '.0' to be able to compare trigger values
                List<String> currentTriggerValues = currentTriggers.stream()
                        .map(map -> map.get("value"))
                        .map(this::appendDotZeroIfInteger)
                        .toList();

                System.out.println("currentTriggerValues");
                currentTriggerValues.forEach(System.out::println);

                boolean triggerValueMatches = areTrimmedAndSortedListsIdentical(expectedTriggerValues, currentTriggerValues);
                System.out.println("triggerValueMatches: " + triggerValueMatches);

                return triggerValueMatches;
            }
        }
        return false;
    }

    /**
     * If a String has value that can be parsed to an Integer, then a '.0' is added,
     * so that we can compare string-int with string-double.
     */
    private String appendDotZeroIfInteger(String value) {
        try {
            // Attempt to parse the string as an integer
            Integer.parseInt(value);
            // If parsing succeeds, append ".0" to the string
            return value + ".0";
        } catch (NumberFormatException e) {
            // If parsing fails, return the original value
            return value;
        }
    }

    /**
     * From API triggers always has a value, but in GUI/UX triggers "only" has value if trigger-toggle is ON.
     * This method tries to resemble that.
     * This means that if we want to start testing what trigger value that "show up" when we set a toggle OFF -> ON, this method have to be changed.
     * As goes for the method(s) that uses the list this method returns.
     * @param triggers
     * @return
     */
    private List<Map<String, String>> parseApiTriggersToResembleGuiTriggers(List<Trigger> triggers) {
        List<Map<String, String>> currentTriggers = new ArrayList<>();
        triggers.forEach(trigger -> {
            String name = trigger.getName();
            String state = trigger.getTrig_recording_enable().equals(true) ? "ON" : "OFF";

            String value = (state.equals("ON")) ? String.valueOf(trigger.getTrig_recording_value()) : null;

            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("state", state);
            map.put("value", value);
            currentTriggers.add(map);

        });
        return currentTriggers;
    }

    public static void clearChange(DeviceType deviceType) {
        String serial = DeviceProperties.getConnectedSerial(deviceType.getType());

        // Make sure device is manageable
        Change change = DeviceApi.getChange(deviceType.getType(), Integer.parseInt(serial));

        if (change.getState() != null) {
            String changeByUser = String.valueOf(change.getUserId());
            String currentUser = String.valueOf(UserApi.getCurrentUser().getId());

            if (change.getState().equals("committed")) {
                DeviceApi.clearChange(deviceType.getType(), serial);
            } else if (change.getState().equals("uncommitted")) {
                if (changeByUser.equals(currentUser)) {
                    DeviceApi.clearChange(deviceType.getType(), serial);
                } else {
                    throw new IllegalStateException(deviceType.getType() + " blocked by user " + change.getUserId());
                }
            }

            // Try again
            Change changeAfterDiscarding = DeviceApi.getChange(deviceType.getType(), Integer.parseInt(serial));
            if (changeAfterDiscarding.getState() != null) {
                throw new IllegalStateException(deviceType.getType() + " blocked change that could not be discarded.");
            }
        }
    }

    @And("C22 are ready to trigger")
    public void c22ReadyToTrigger() {
        Device c22 = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

        // Check if device is up and running
        Status connectedDeviceMonStatus = getDeviceCurrentMonStatus(c22);  // MONON, MONOFF
        Status connectedDeviceConnectionStatus = StatusAssesser.deductDeviceCommunicationStatus(c22);   // NO_WARNING, WARNING

        if (connectedDeviceMonStatus.equals(Status.MONOFF) ||
                connectedDeviceConnectionStatus.equals(Status.WARNING)) {
            throw new IllegalStateException("C22 is not ready." +
                    "\n\t connectedDeviceMonStatus: " + connectedDeviceMonStatus +
                    "\n\t connectedDeviceConnectionStatus: " + connectedDeviceConnectionStatus);
        }

        // Now check if device settings is acceptable
        List<Trigger> triggers = c22.getTriggers();
        boolean atLeastOneTriggerON = triggers.stream()
                .anyMatch(trigger -> trigger.getTrig_recording_enable().equals(true));

        if (!atLeastOneTriggerON) {
            // Create a change in C22, so that we get a trigger ON
            TestDataBuilder builder = new TestDataBuilder(context(), "templates/change");
            builder
                    .addTestData("change-" + "C22" + "_" + "A1")
                    .build();
        }
    }
}
