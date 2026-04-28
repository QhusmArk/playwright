package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.blast.Blast;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class BlastApi extends ModelApi {

    public final static String NEW_API = "v0";

    public static Blast createBlast(final int projectId, final String body) {
        Response response = RequestService.request(Method.POST, NEW_API + "/project/" + projectId + "/blast", body);
        return populateObject(response, Blast.class);
    }

    public static Blast createBlast(final int projectId, final String body, final String user, final String pw) {
        Response response = RequestService.request(Method.POST, NEW_API + "/project/" + projectId + "/blast", body, user, pw);
        return populateObject(response, Blast.class);
    }

    public static Response getBlastAndResponse(final int projectId, final int blastId) {
        return RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/blast/" + blastId);
    }

    public static Blast getBlast(final int projectId, final int blastId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/blast/" + blastId);
        return populateObject(response, Blast.class);
    }

    public static Blast getBlast(final int projectId, final int blastId, final String user, final String pw) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/blast/" + blastId, user, pw);
        return populateObject(response, Blast.class);
    }

    public static List<Blast> getBlasts(final int projectId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/blast");
        return populateList(response, Blast.class);
    }

    public static Blast updateBlast(final int projectId, final int blastId, final String body) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/blast/" + blastId, body);
        return populateObject(response, Blast.class);
    }

    public static Response updateBlastAndGetResponse(final int projectId, final int blastId, final String body) {
        return RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/blast/" + blastId, body);
    }

    public static Blast updateBlast(final int projectId, final int blastId, final String body,
                                    final String user, final String pw) {
        Response response = RequestService.request(Method.PUT, NEW_API + "/project/" + projectId + "/blast/" + blastId, body, user, pw);
        return populateObject(response, Blast.class);
    }

    public static Blast deleteBlast(final int projectId, final int blastId) {
        Response response = RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/blast/" + blastId);
        return populateDelete(response, Blast.class);
    }

    public static Blast deleteBlast(final int projectId, final int blastId, final String user, final String pw) {
        Response response = RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/blast/" + blastId, user, pw);
        return populateDelete(response, Blast.class);
    }

    public static Blast getBlastByName(int projectId, String blastName) {
        return BlastApi.getBlasts(projectId).stream()
                .filter(b -> b.getBlastId().equals(blastName))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("No matching blast found."));
    }


}
