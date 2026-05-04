package com.example.api.glue;

import com.example.api.endpoints.DeviceApi;
import com.example.api.endpoints.UserApi;
import com.example.api.helpers.AssertionHelpers;
import com.example.api.helpers.JsonHelpers;
import com.example.api.models.device.*;
import com.example.helpers.hardware.JenkinsDeviceSSHConnector;
import com.example.helpers.testData.Context;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.steps.BaseGlue;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.*;
import java.util.function.Predicate;

import static com.example.playwright.helpers.enums.DeviceType.C22;
import static java.util.Arrays.copyOfRange;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiDeviceGlue extends BaseGlue {

    private final Context context;

    public ApiDeviceGlue(Context context) {
        this.context = context;
    }

    @When("I check these loggers")
    public void iCheckEachLogger(DataTable table) {
        List<String> expectedLoggers = table.row(0);
        List<Device> allDevices = DeviceApi.getDevices();

        List<Predicate<Device>> predicateList = new ArrayList<>();
        for (String logger : expectedLoggers) {
            Predicate<Device> predicate = device -> device.getType().equals(logger);
            predicateList.add(predicate);
        }

        // Reduce the list of predicates into a single predicate with 'or' logic
        Predicate<Device> combinedPredicate = predicateList.stream()
                .reduce(x -> false, Predicate::or);

        // Use the combined predicate to filter the devices
        List<Device> loggers = allDevices.stream()
                .filter(combinedPredicate)
                .toList();

        // Now store the expectedLoggers in Context to be used in next step
        loggers.forEach(context::addDevice);
    }

    //todo: till typ ApiJsonGlue?
    @Then("device response contains only one of each key")
    public void onlyOneOfEachKey() {
        List<Device> expectedCreatorLoggers = context().getDevicesAsList();

        // Nb. This fails for C50's
        expectedCreatorLoggers.forEach(device -> {
            Response deviceResponse = DeviceApi.getDeviceResponse(device.getType(), device.getSerial());
            String json = deviceResponse.getBody().asString();

            List<String> keys = JsonHelpers.getAllKeys(json);
            // Make sure there is only one of each
            assertTrue(AssertionHelpers.noDuplicatesInList(keys), "List for " + device.getSerial() + " has duplicate: " + keys);
        });
    }

    //todo: till typ ApiJsonGlue?
    @Then("device response contains only one of these keys")
    public void deviceResponseContainsOnlyOneOfTheseKeys(DataTable table) {
        List<String> expectedKeys = table.row(0);
        List<Device> expectedCreatorLoggers = context().getDevicesAsList();

        // Nb. This fails for C50's
        expectedCreatorLoggers.forEach(device -> {
            Response deviceResponse = DeviceApi.getDeviceResponse(device.getType(), device.getSerial());
            String json = deviceResponse.getBody().asString();

            List<String> actualKeys = JsonHelpers.getAllKeys(json);
            // Make sure there is only one of each
            expectedKeys.forEach(expectedKey -> {
                assertTrue(AssertionHelpers.hasSingleInstance(expectedKey, actualKeys), "List for " + device.getSerial() + " has duplicate: " + expectedKey);
            });
        });
    }

    //todo: till typ ApiJsonGlue?
    @And("this keys is of this type")
    public void thisKeysIsOfThisType(Map<String, String> keyTypePairs) {
        List<Device> expectedCreatorLoggers = context().getDevicesAsList();

        expectedCreatorLoggers.forEach(device -> {
            Response deviceResponse = DeviceApi.getDeviceResponse(device.getType(), device.getSerial());
            String json = deviceResponse.getBody().asString();

            keyTypePairs.forEach((k,t) -> {
                JsonNode nodeForThisKey = JsonHelpers.getNode(json, k);
                if (k.equals("upload_schedule")) {
                    List<String> scheduleValues = JsonHelpers.getArrayInNode(nodeForThisKey);
                    assertTrue(AssertionHelpers.onlyIntegersInList(scheduleValues), "List for " + device.getSerial() + " contained not only Integers.");
                }

                if (k.equals("fw_version")) {
                    if (!nodeForThisKey.isObject()) {
                        throw new IllegalStateException("Fw_version is not object in " + device.getSerial());
                    }
                }
            });

        });
    }

    // todo: ändra till nya sättet att hämta inställningar från testdata/change
    @Given("C22 monitors with std 1A and all triggers off")
    public void c22MonitorsWithStdAAndAllTriggersOff() {
        freeDeviceFromChangeIfPossible("C22");
        String serial = DeviceProperties.getConnectedSerial("C22");

        Device device = DeviceApi.getDevice("C22", Integer.parseInt(serial));
        String std = device.getStandard();

        List<Trigger> triggers = device.getTriggers();
        Boolean allTriggersOff = triggers.stream().allMatch(trigger -> trigger.getTrig_recording_enable().equals(false));

        if (std.equals("1A") && allTriggersOff) {
            // do nothing
            System.out.println("Correct DeviceProperties");

        } else {
            System.out.println("Wrong DeviceProperties");

            // change to 1A
            // commit change
            Map<String, Map<String, String>> channels = new HashMap<>();

            Map<String, String> stateAndValue = new HashMap<>();
            stateAndValue.put("state", "off");
            stateAndValue.put("value", null);
            channels.put("V", stateAndValue);
            channels.put("L", stateAndValue);
            channels.put("T", stateAndValue);
            setStdAndTriggerForC22("1A", channels, true);

            // connect using ssh
            JenkinsDeviceSSHConnector tdc = new JenkinsDeviceSSHConnector(C22);

            // connect device using cli
            tdc.syncConnect();
            tdc.closeConnections();

            // poll for changed mon.DeviceProperties
            int secondsToWait = 60;
            for (int i = 0; i < secondsToWait; i++) {
                System.out.println("Polling for consumed change.");
                Device postChangeDevice = DeviceApi.getDevice("C22", Integer.parseInt(serial));
                String postStd = postChangeDevice.getStandard();
                List<Trigger> postTriggers = postChangeDevice.getTriggers();
                Boolean postAllTriggersOff = postTriggers.stream().allMatch(trigger -> trigger.getTrig_recording_enable().equals(false));

                if (postStd.equals("1A") && postAllTriggersOff) {
                    // do nothing
                    System.out.println("Correct DeviceProperties after " + i + " seconds.");
                    break;
                } else {
                    PlaywrightActions.sleep(1);
                }

            }
        }
    }

    public static boolean freeDeviceFromChangeIfPossible(String type) {
        boolean browserRefreshNeeded = false;
        String serial = DeviceProperties.getConnectedSerial(type);
        Change change = DeviceApi.getChange(type, Integer.parseInt(serial));

        if (change.getState() == null) {
            return browserRefreshNeeded; // No change, all is okay
        }

        switch (change.getState()) {
            case "committed" -> {
                DeviceApi.clearChange(type, serial);
                browserRefreshNeeded = true;
            }
            case "uncommitted" -> {
                int changeByUser = change.getUserId();
                int currentUser = UserApi.getCurrentUser().getId();
                if (Objects.equals(changeByUser, currentUser)) {
                    System.out.println("Uncommitted change done by current user. Removing the change");
                    DeviceApi.clearChange(type, serial);
                    browserRefreshNeeded = true;
                } else {
                    throw new PendingException("Skipping test as device is blocked with uncommitted change.");
                }
            }
            default ->
                // Handle unexpected state if necessary
                    throw new IllegalStateException("Unexpected change state: " + change.getState());
        }
        return browserRefreshNeeded;
    }


    public static void setStdAndTriggerForC22(String standard, Map<String, Map<String, String>> c, boolean toCommit) {
        String s = DeviceProperties.getConnectedSerial("C22");
        Integer serial = Integer.parseInt(s);
        String configID = DeviceApi.getDevice("C22", serial).getConfigId();

        List<Channel> channels = new ArrayList<>();
        c.forEach((name, map) -> {
            Channel channel = Channel.builder()
                    .name(name)
                    .triggerEnable(map.get("state").equals("on"))
                    .triggerValue(map.get("value") != null ? Double.valueOf(map.get("value")) : null)
                    .build();

            channels.add(channel);
        });

        Vib vib = Vib.builder()
                .channels(channels)
                .standard(standard)
                .build();

        Change change = Change.builder()
                .configId(configID)
                .vib(vib)
                .build();

        DeviceApi.updateChange("C22", serial, change.buildJson());
        if (toCommit) {
            DeviceApi.commitChange("C22", serial, "{ \"action\":\"commit\" }");
        }
    }

    @Then("I can change C22 upload schedule")
    public void iCanChangeCUploadSchedule() {
        freeDeviceFromChangeIfPossible("C22");

        Device c22 = DeviceApi.getDevice("C22", DeviceProperties.getConnectedSerial("C22"));
        Integer[] uploadSchedule = c22.getUploadSchedule();

        // Create a new array but without the first element
        Integer[] alteredSchedule = copyOfRange(uploadSchedule, 1, uploadSchedule.length);

        String configID = c22.getConfigId();

        Logger logger = Logger.builder()
                .uploadSchedule(alteredSchedule)
                .build();

        Change change = Change.builder()
                .configId(configID)
                .logger(logger)
                .build();

        Response response = DeviceApi.updateChange("C22", DeviceProperties.getConnectedSerial("C22"), change.buildJson());
        assertEquals(200, response.getStatusCode(),
                () -> "Did not get 200 from api when PUT /change");

        // Finish by removing the uncommitted change
        DeviceApi.clearChange("C22", DeviceProperties.getConnectedSerial("C22"));
    }
}
