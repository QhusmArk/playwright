package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.device.*;
import com.example.playwright.enums.DeviceType;
import com.example.playwright.helpers.PlaywrightActions;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class DeviceApi extends ModelApi {

    public final static String NEW_API = "v0";

    public static Device getDevice(final String deviceType, final int deviceId, final boolean withProjects) {
        Response response;
        if (withProjects) {
            response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "?include_connected_projects=true");
        } else {
            response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId);
        }
        return populateObject(response, Device.class);
    }

    public static Device getDevice(final String deviceType, final int deviceId, final boolean withProjects, final String userName, final String pw) {
        Response response;
        if (withProjects) {
            response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "?include_connected_projects=true", userName, pw);
        } else {
            response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId, userName, pw);
        }
        return populateObject(response, Device.class);
    }

    public static Device getDevice(final String deviceType, final int deviceId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "?include_connected_projects=true");
        return populateObject(response, Device.class);
    }

    public static Device getDevice(final DeviceType deviceType, final int deviceId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType.getType() + "/" + deviceId + "?include_connected_projects=true");
        return populateObject(response, Device.class);
    }

    public static Device getDevice(final DeviceType deviceType, final String deviceId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType.getType() + "/" + deviceId + "?include_connected_projects=true");
        return populateObject(response, Device.class);
    }

    public static Device getDevice(final String deviceType, final String deviceId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "?include_connected_projects=true");
        return populateObject(response, Device.class);
    }

    public static Device getDevice(final String deviceType, final int deviceId, final String userName, final String pw) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "?include_connected_projects=true", userName, pw);
        return populateObject(response, Device.class);
    }

    public static Device getSensor(final int sensorSerial) {
        return getSensor(Integer.toString(sensorSerial));
    }

    public static Device getSensor(final String sensorSerial) {
        Response response = RequestService.request(Method.GET, NEW_API + "/sensor/" + sensorSerial);
        return populateObject(response, Device.class);
    }

    public static Sensor getDeviceSensor(final int sensorSerial) {
        return getDeviceSensor(Integer.toString(sensorSerial));
    }

    public static Sensor getDeviceSensor(final String sensorSerial) {
        Response response = RequestService.request(Method.GET, NEW_API + "/sensor/" + sensorSerial);
        return populateObject(response, Sensor.class);
    }

    public static Response getDeviceResponse(final String deviceType, final int deviceId) {
        return RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "?include_connected_projects=true");
    }

    public static int checkDeviceApiStatus() {
        return RequestService.request(Method.GET, NEW_API + "/device/all").getStatusCode();
    }

    public static List<Device> getDevices() {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/all");
        return populateList(response, Device.class);
    }

    public static List<Device> getDevices(final String userName, final String pw) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/all", userName, pw);
        return populateList(response, Device.class);
    }

    public static UserInput getUserInput(final String deviceType, final int deviceId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "/user_input");
        return populateObject(response, UserInput.class);
    }

    public static void updateUserInput(final String deviceType, final int deviceId, final String body) {
        updateUserInput(deviceType, deviceId, "/user_input", body);
    }

    private static void updateUserInput(final String device, final int deviceId, final String path, final String body) {
        RequestService.request(Method.PUT, NEW_API + "/device/" + device + "/" + deviceId + "/user_input", body);
    }

    public static Response updateChange(final String device, final String deviceId, final String body) {
        return updateChange(device, Integer.parseInt(deviceId), body);
    }

    public static Response updateChange(final String device, final int deviceId, final String body) {
        return updateChange(device, deviceId, "/change", body);
    }

    public static Response updateChange(final String device, final int deviceId, final String path, final String body) {
        return RequestService.request(Method.PUT, NEW_API + "/device/" + device + "/" + deviceId + "/change", body);
    }

    public static Device commitChange(final String type, final int serial, final String body) {
        Response response =
                RequestService.request(Method.POST, NEW_API + "/device/" + type + "/" + serial + "/change", body);
        return populateObject(response, Device.class);
    }

    public static Change commitChange(final String device, final String deviceId) {
        Response response =
                RequestService.request(Method.POST, NEW_API + "/device/" + device + "/" + deviceId + "/change", "{ \"action\":\"commit\" }");
        return populateObject(response, Change.class);
    }

    public static Response commitChange(final String deviceType, final int deviceId, final String body, final String userName, final String pw) {
        Response response =
                RequestService.request(Method.POST, NEW_API + "/device/" + deviceType + "/" + deviceId + "/change", body, userName, pw);
        return response;
    }

    public static Device getCurrentChange(final String deviceType, final int deviceId) {
        Response response =
                RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "/change");
        return populateObject(response, Device.class);
    }

    public static Change getChange(final String deviceType, final String deviceId) {
        return getChange(deviceType, Integer.parseInt(deviceId));
    }

    public static Change getChange(final String deviceType, final int deviceId) {
        Response response =
                RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + deviceId + "/change");
        return populateObject(response, Change.class);
    }

    public static boolean waitForCommitToBeDone(final String deviceType, final int deviceId) {
        boolean done = false;

        int counter = 20;
        int timeToWait = 3;

        for (int i = 0; i < counter; i++) {
            if (done) {
                System.out.println("The Device has been updated\n");
                break;
            }
            System.out.print("Attemt no." + (i + 1) + " to validate that the Device has been updated... ");
            PlaywrightActions.sleep(timeToWait);

            Device device = getCurrentChange(deviceType, deviceId);

            if (device.getState() != null) {
                done = false;
                System.out.println("Not ready yet [" + device.getState() + "]");

            } else {
                done = true;
            }
        }
        if (!done) {
            System.out.println("Gave up waiting for Device to be updated after " + (counter * timeToWait) + " seconds!");
        }
        return done;
    }

    public static Change clearChange(final String device, final String deviceId) {
        Response response =
                RequestService.request(Method.POST, NEW_API + "/device/" + device + "/" + deviceId + "/change", "{ \"action\":\"clear\" }");
        return populateObject(response, Change.class);
    }

    public static Device clearChange(final String device, final int deviceId, final String body) {
        Response response =
                RequestService.request(Method.POST, NEW_API + "/device/" + device + "/" + deviceId + "/change", body);
        return populateObject(response, Device.class);
    }

    public static Device clearChange(final String device, final String deviceId, final String body) {
        Response response =
                RequestService.request(Method.POST, NEW_API + "/device/" + device + "/" + deviceId + "/change", body);
        return populateObject(response, Device.class);
    }

    public static Response clearChange(final String device, final int deviceId, final String body,
                                       final String userName, final String pw) {
        return RequestService.request(Method.POST, NEW_API + "/device/" + device + "/" + deviceId + "/change", body, userName, pw);
    }

    public static Definition getDefinition(String deviceType, int serial) {
        Response response =
                RequestService.request(Method.GET, NEW_API + "/device/" + deviceType + "/" + serial + "/definitions");
        return populateObject(response, Definition.class);
    }

}
