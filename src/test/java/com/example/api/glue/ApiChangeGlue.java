package com.example.api.glue;

import com.example.api.endpoints.DeviceApi;
import com.example.api.endpoints.UserApi;
import com.example.api.models.device.Change;
import com.example.api.models.device.Device;
import com.example.api.models.device.Logger;
import com.example.helpers.hardware.JenkinsDeviceSSHConnector;
import com.example.helpers.testData.Context;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.steps.BaseGlue;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiChangeGlue extends BaseGlue {

    private final Context context;

    public ApiChangeGlue(Context context) {
        this.context = context;
    }

    @When("there is a {string} change for {string}")
    public void thereIsACommittedChange(String changeStatus, String device) {
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial("D10"));
        Device d10 = DeviceApi.getDevice("D10", serial);

        if (d10.getChange() != null) {
            throw new IllegalStateException("Commit blocking unit.");
        }

        Change change = Change.builder()
                .hash(d10.getHash())
                .sensorsHash(d10.getSensorsHash())
                .special("force_reboot")
                .build();

        DeviceApi.updateChange(device, serial, change.buildJson());
        PlaywrightActions.sleep(2);
        DeviceApi.commitChange(device, serial, "{ \"action\":\"commit\" }");
    }

    @Then("I commit remote reboot for {string}")
    public void iCommitCommandFor(String type) {
        switch (type) {
            case "D10" -> iCommitCommandForD10("Force reboot");
            case "C22", "C50" -> iCommitCommandForCreator("Remote Reboot", type);
            default -> throw new IllegalStateException("Unknown type: " + type);
        }
    }

    private void iCommitCommandForD10(String command) {
        String commandLine = switch (command) {
            case "Server messages on" -> "msgon";
            case "Server messages off" -> "msgoff";
            case "Force reboot" -> "force_reboot";
            case "S50 clear agenda" -> "clear_agenda";
            case "S50 Connect 2x/hour" -> "s50_2x_per_hour";
            case "S50 Connect 4x/hour" -> "s50_4x_per_hour";
            case "Firmware upgrade" -> "firmware_upgrade";
            default -> throw new IllegalStateException("Not known command: " + command);
        };

        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial("D10"));
        Device d10 = DeviceApi.getDevice("D10", serial);

        String hash = d10.getHash();
        String sensorsHash = d10.getSensorsHash();

        ApiDeviceGlue.freeDeviceFromChangeIfPossible("D10");

        Change change = Change.builder()
                .hash(hash)
                .sensorsHash(sensorsHash)
                .special(commandLine)
                .build();

        Response response = DeviceApi.updateChange("D10", serial, change.buildJson());
        assertEquals(200, response.getStatusCode(),
                () -> "Did not get 200 from api when PUT /change");

        // Give BE time to catch up
        PlaywrightActions.sleep(4);

        // Commit change
        DeviceApi.commitChange("D10", serial, "{ \"action\":\"commit\" }");

        // Make sure change is committed
        if (!validateChangeEqualsIAllApi("committed", "D10")) {
            throw new IllegalStateException("Api's failed to be synced");
        } else {
            System.out.println("All api's report change is committed.");
        }
    }


    public void iCommitCommandForCreator(String command, String type) {
        int commandCode = switch (command) {
            case "Remote Firmware Upgrade" -> 1;
            case "Remote Reboot" -> 100;
            case "Remote Shut Down" -> 101;
            case "Remote Update GPS Position" -> 102;
            default -> throw new IllegalStateException("Not known command: " + command);
        };

        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial(type));
        Device creator = DeviceApi.getDevice(type, serial);
        String configID = creator.getConfigId();

        ApiDeviceGlue.freeDeviceFromChangeIfPossible(type);

        Logger logger = Logger.builder()
                .command(commandCode)
                .build();

        Change change = Change.builder()
                .configId(configID)
                .logger(logger)
                .build();

        Response response = DeviceApi.updateChange(type, serial, change.buildJson());
        assertEquals(200, response.getStatusCode(),
                () -> "Did not get 200 from api when PUT /change");

        // Give BE time to catch up
        PlaywrightActions.sleep(4);

        // Commit change
        DeviceApi.commitChange(type, serial, "{ \"action\":\"commit\" }");

        // Make sure change is committed
        if (!validateChangeEqualsIAllApi("committed", type)) {
            throw new IllegalStateException("Api's failed to be synced");
        } else {
            System.out.println("All api's report change is committed.");
        }
    }

    @And("change is consumed by {string}")
    public void changeIsConsumedByDevice(String type) {
        PlaywrightActions.sleep(3);

        boolean isChangeConsumed = validateChangeEqualsIAllApi(null, type);
        assertTrue(isChangeConsumed,
                () -> "Change was not consumed within the expected time range.");
    }

    /**
     * Uses three api's that all carries Change object. When all three return same state, or fails within time limit, method returns.
     * If this method is run while logger has loads of data to be sent to INFRA, 'timeToWait' might be insufficient.
     * NB. There has been cases where statusCode:400 is received from GET /device/all after D10 is rebooted with remote change.
     */
    public static Boolean validateChangeEqualsIAllApi(String expectedStateOfCommit, String type) {
        int serial = Integer.parseInt(DeviceProperties.getConnectedSerial(type));

        PlaywrightActions.sleep(1);
        int timeToWait = 400;

        // Start polling
        for (int s = 1; s <= timeToWait; s++) {
            System.out.println("\n" + s + ":400 Polling for change state.");

            Change change = DeviceApi.getChange(type, serial);
            String changeState = (change == null)
                    ? null
                    : change.getState();

            Change deviceChange = DeviceApi.getDevice(type, serial).getChange();
            String deviceChangeState = (deviceChange == null)
                    ? null
                    : deviceChange.getState();

            Optional<Device> deviceFromAll = DeviceApi.getDevices().stream()
                    .filter(d -> serial == d.getSerial())
                    .findFirst();

            String deviceAllChangeState;
            if (deviceFromAll.isPresent()) {

                Change deviceAllChange = deviceFromAll.get().getChange();
                deviceAllChangeState = (deviceAllChange == null)
                        ? null
                        : deviceAllChange.getState();

            } else {
                throw new IllegalStateException("Device '" + type + "' could not be found in device all list.");
            }

            if (Objects.equals(expectedStateOfCommit, changeState)
                    && Objects.equals(expectedStateOfCommit, deviceChangeState)
                        && Objects.equals(expectedStateOfCommit, deviceAllChangeState)) {

                return true;
            } else {
                PlaywrightActions.sleep(1);
            }
        }
        // If not all api's return matching result within timeToWait
        return false;
    }
    

    public static boolean validateCreatorChangeIsConsumed(String originalConfigId) {
        PlaywrightActions.sleep(1);
        int timeToWait = 200;

        // Start polling
        for (int s = 1; s <= timeToWait; s++) {
            System.out.println("\n" + s + ":200 Polling for new settings in device.");

//            Device device = DeviceApi.getAboutDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));
            Device device = DeviceApi.getDevice("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

            String currentConfigId = device.getConfigId();

            // NB. Config_id sent with Change is not stored in Device, but in Change
            Change change = DeviceApi.getChange("C22", Integer.parseInt(DeviceProperties.getConnectedSerial("C22")));

            // If deviceChange is consumed by fw, AND the deviceChange has been implemented in device; i.e., a new config_id has been created
            if (change.getState() == null) {
                if (!currentConfigId.equals(originalConfigId)) {
                    System.out.println("New config id issued.");
                    return true;
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

    public static void connect(String deviceType) {
        System.out.println("Connecting " + deviceType + " to INFRA");
        // connect using ssh
        JenkinsDeviceSSHConnector tdc = new JenkinsDeviceSSHConnector(DeviceType.fromType(deviceType));

        // connect device using cli
        tdc.syncConnect();
        tdc.closeConnections();
    }

    public static void clearChangeIfPossible(String type) {
        String serial = DeviceProperties.getConnectedSerial(type);

        // Make sure device is manageable
        Change change = DeviceApi.getChange(type, Integer.parseInt(serial));

        if (change.getState() != null) {
            String changeByUser = String.valueOf(change.getUserId());
            String currentUser = String.valueOf(UserApi.getCurrentUser().getId());

            if (change.getState().equals("committed")) {
                if (changeByUser.equals(currentUser)) {
                    System.out.println("Change is committed by this user. Trying to delete it.");
                    DeviceApi.clearChange(type, serial);
                } else {
                    throw new IllegalStateException(type + " blocked by user " + change.getUserId());
                }
            } else if (change.getState().equals("uncommitted")) {
                if (changeByUser.equals(currentUser)) {
                    DeviceApi.clearChange(type, serial);
                } else {
                    throw new IllegalStateException(type + " blocked by user " + change.getUserId());
                }
            }
            // todo? Hur länge är det rimligt att vänta på att en device inte används?

            // Try again
            Change changeAfterDiscarding = DeviceApi.getChange(type, Integer.parseInt(serial));
            if (changeAfterDiscarding.getState() != null) {
                throw new IllegalStateException(type  + " blocked change that could not be discarded.");
            }
        }
    }

    /**
     * For some reason a POST /change must be done using sessionid- and/or csrftoken-cookies,
     * which is not required for GET /change. This means that I cannot discard commit done by other user :-(
     */
    public static void clearChangeFromC22() {
        String serial = DeviceProperties.getConnectedSerial("C22");

        // Make sure device is manageable
        Change change = DeviceApi.getChange("C22", Integer.parseInt(serial));

        if (change.getState() != null) {
            String changeByUser = String.valueOf(change.getUserId());
            String currentUser = String.valueOf(UserApi.getCurrentUser().getId());

            if (change.getState().equals("committed")) {
                System.out.println("Change is committed. Trying to remove commit.");
                DeviceApi.clearChange("C22", serial);

            } else if (change.getState().equals("uncommitted")) {
                if (changeByUser.equals(currentUser)) {
                    DeviceApi.clearChange("C22", serial);
                } else {
                    throw new IllegalStateException("C22 blocked by user " + change.getUserId());
                }
            }

            // Try again
            Change changeAfterDiscarding = DeviceApi.getChange("C22", Integer.parseInt(serial));
            if (changeAfterDiscarding.getState() != null) {
                throw new IllegalStateException("C22 blocked change that could not be discarded.");
            }
        }
    }



}
