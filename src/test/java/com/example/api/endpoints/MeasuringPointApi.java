package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.measuringpoint.MeasuringPoint;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class MeasuringPointApi extends ModelApi {

    public static MeasuringPoint createMeasuringPoint(final int projectId, final String body) {
        Response response = RequestService.request(Method.POST, "project/" + projectId + "/measure_point/", body);
        return populateObject(response, MeasuringPoint.class);
    }

    public static MeasuringPoint createMeasuringPoint(final int projectId, final String body,
                                                      final String user, final String pw) {
        Response response = RequestService.request(Method.POST, "project/" + projectId + "/measure_point/", body, user, pw);
        return populateObject(response, MeasuringPoint.class);
    }

    public static MeasuringPoint getMeasuringPoint(final int projectId, final int mpId) {
        Response response = RequestService.request(Method.GET, "project/" + projectId + "/measure_point/" + mpId);
        return populateObject(response, MeasuringPoint.class);
    }

    public static List<MeasuringPoint> getMeasuringPoints(final int projectId) {
        Response response = RequestService.request(Method.GET, "project/" + projectId + "/measure_point");
        return populateList(response, MeasuringPoint.class);
    }

    public static List<MeasuringPoint> getMeasuringPoints(final int projectId, final String user, final String pw) {
        Response response = RequestService.request(Method.GET, "project/" + projectId + "/measure_point", user, pw);
        return populateList(response, MeasuringPoint.class);
    }

    public static MeasuringPoint updateMeasuringPoint(final int projectId, final int mpId, final String body) {
        Response response = RequestService.request(Method.PUT, "project/" + projectId + "/measure_point/" + mpId, body);
        return populateObject(response, MeasuringPoint.class);
    }

    public static MeasuringPoint updateMeasuringPoint(final int projectId, final int mpId, final String body,
                                                      final String user, final String pw) {
        Response response = RequestService.request(Method.PUT, "project/" + projectId + "/measure_point/" + mpId, body, user, pw);
        return populateObject(response, MeasuringPoint.class);
    }

    public static MeasuringPoint deleteMeasuringPoint(final int projectId, final int mpId) {
        Response response = RequestService.request(Method.DELETE, "project/" + projectId + "/measure_point/" + mpId);
        return populateDelete(response, MeasuringPoint.class);
    }

    public static MeasuringPoint deleteMeasuringPoint(final int projectId, final int mpId,
                                                      final String user, final String pw) {
        Response response = RequestService.request(Method.DELETE, "project/" + projectId + "/measure_point/" + mpId, user, pw);
        return populateDelete(response, MeasuringPoint.class);
    }

    /*********** Helper methods ***********/

    public static MeasuringPoint getMeasuringPointByConnectedSensorsSerial(final int projectId, String sensorSerial) {
        List<MeasuringPoint> mps = getMeasuringPoints(projectId);

        // Return the mp that have a sensor matching sensorSerial
        return mps.stream()
                .filter(mp -> mp.getSensors().stream()
                        .anyMatch(sensor -> sensor.getSerial() != null && sensor.getSerial().equals(sensorSerial)))
                .findFirst()
                .orElse(null);
    }

    public static MeasuringPoint getMeasuringPointByName(final int projectId, String mpName) {
        List<MeasuringPoint> mps = getMeasuringPoints(projectId);

        // Return the mp that have a name matching mpName
        return mps.stream()
                .filter(mp -> mp.getName().equals(mpName))
                .findFirst()
                .orElse(null);
    }
}
