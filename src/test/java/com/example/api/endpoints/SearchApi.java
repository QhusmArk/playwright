package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.report.Search;
import com.example.playwright.helpers.PlaywrightActions;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;

public class SearchApi extends ModelApi {

    public final static String NEW_API = "v0";

    public static Search createSearch(final int projectId, final String body, final boolean followRedirect) {
        return (followRedirect)
                ? createSearch(projectId, body, 200)
                : createSearch(projectId, body, 303);
    }

    public static Search createSearch(final int projectId, final String body, final int expectedStatusCode) {

        switch (expectedStatusCode) {
            case 200 -> {   // implicit followRedirect:true
                return createSearchAndWaitForFinished(projectId, body, 120);
            }
            case 303 -> {   // implicit followRedirect:false
                return createSearchWithLocation(projectId, body);
            }
            case 400 -> {   // expecting 400, but the response body can contain information about why it failed
                Response response = RequestService.request(Method.POST, SearchApi.NEW_API + "/project/" + projectId + "/search", body);
                return populateObject(response, Search.class);
            }
            default -> throw new IllegalArgumentException("Unsupported expectedStatusCode: " + expectedStatusCode);
        }
    }

    public static Search createSearchAndWaitForFinished(final int projectId, final String body, final int timeToWait) {
        Response response = RequestService.request(Method.POST, SearchApi.NEW_API + "/project/" + projectId + "/search", body);
        Search search = populateObject(response, Search.class);

        waitForFinished(0, SearchApi.NEW_API + "/project/" + projectId + "/search" + "/" + search.getId(), timeToWait);
        return getSearch(projectId, search.getId());
    }

    private static Search createSearchWithLocation(final int projectId, final String body) {
        Response response = RequestService.requestWithoutRedirect(Method.POST, SearchApi.NEW_API + "/project/" + projectId + "/search", body);

        if (response.getStatusCode() == 303) {
            String location = response.getHeader("location");
            Search search = new Search();
            search.setSelfUrl(location);
            search.setId(extractSearchIdFromLocation(location));

            return search;
        } else {
            throw new IllegalStateException("ExpectedStatusCode/ActualStatusCode: " + 303 + "/" + response.getStatusCode());
        }
    }

    /**
     * @return '8ed601ee-afed-4239-979c-d47b87265c5b' from "/api/v0/project/10523/search/8ed601ee-afed-4239-979c-d47b87265c5b"
     */
    private static String extractSearchIdFromLocation(final String locationHeader) {
        try {
            return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Could not get search id from: " + locationHeader);
        }
    }

    /**
     * Used for AccountDeviceBillingReport, AccountProjectBillingReport
     */
    public static Search createSearchAndWaitForFinished(final String body, final int timeToWait) {
        Response response = RequestService.request(Method.POST, SearchApi.NEW_API + "/search", body);
        Search search = populateObject(response, Search.class);
        waitForFinished(0, SearchApi.NEW_API + "/search" + "/" + search.getId(), timeToWait);

        return getSearch(search.getId());
    }

    /**
     * Used for AccountDeviceBillingReport, AccountProjectBillingReport
     */
    private static Search getSearch(final String searchId) {
        Response response = RequestService.request(Method.GET, SearchApi.NEW_API + "/search/" + searchId);
        return populateObject(response, Search.class);
    }

    public static Search getSearch(final int projectId, final String searchId) {
        Response response = RequestService.request(Method.GET, SearchApi.NEW_API + "/project/" + projectId + "/search/" + searchId);
        return populateObject(response, Search.class);
    }

    private static boolean waitForFinished(int counter, final String path, final int timeToWait) {

        long startTime = System.currentTimeMillis() / 1000L;
        long giveUpAtTime = startTime + timeToWait;

        while ((System.currentTimeMillis() / 1000L) < giveUpAtTime) {
            PlaywrightActions.sleep(1);
            Response response = RequestService.request(Method.GET, path);
            String state = response.jsonPath().getString("state");

            long elapsedTime = System.currentTimeMillis() / 1000L - startTime;

            switch (state) {
                case "running" -> System.out.println("\t" + elapsedTime + ". Report not ready yet [" + state + "]");
                case "abort" -> {
                    throw new IllegalStateException("The search was aborted.");
                }
                case "finished" -> {
                    System.out.println("\tIt took around " + elapsedTime + " seconds to create report [finished] \n");
                    return true;
                }
            }
        }
        return false;
    }

    public static void updateSearch(final int projectId, final String searchId, final String body) {
        RequestService.request(Method.PATCH, NEW_API + "/project/" + projectId + "/search/" + searchId, body);
    }

    public static void deleteSearch(int projectId, String searchId) {
        RequestService.request(Method.DELETE, NEW_API + "/project/" + projectId + "/search/" + searchId);
    }

    public static List<Search> getSearches(int projectId) {
        Response response = RequestService.request(Method.GET, SearchApi.NEW_API + "/project/" + projectId + "/search/");
        return populateList(response, Search.class);
    }
}
