package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.report.BillingReportWrapper;
import io.restassured.http.Method;
import io.restassured.response.Response;

/**
 * BillingReportApi used solely for AccountProjectBillingReport
 * <p>
 * Billing_report api is bonkers, as it do not work as Search.
 * POST /search return a redirect to an unfinished Search.
 * Then browser polls GET /search until state:finished
 * <p>
 * POST /billing_report do not return anything until the request is fulfilled.
 * Then a redirect and Response.Header(location) is returned.
 * Location is used to send GET /billing_report
 * <p>
 * But if POST /billing_report can yield status_code:500 if the request is too complex.
 */
public class BillingReportApi extends ModelApi {

    public final static String NEW_API = "v0";

    public static String createAndGetBillingReportId(final String body) {
        Response response = RequestService.requestWithoutRedirect(Method.POST, NEW_API + "/billing_report", body);

        if (response.getStatusCode() == 500) {
            throw new IllegalStateException(" GET /billing_report returned 500");
        } else if (response.getStatusCode() == 400) {
            throw new IllegalStateException(" GET /billing_report returned 400, perhaps due to malformed json.");
        } else if (response.getStatusCode() == 303) {

            if (response.getHeader("location") == null) {
                throw new IllegalStateException("No location was found in headers.");
            }

            String locationHeader = response.getHeader("location");
            return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
        }
        throw new IllegalStateException("GET /billing_report returned unsupported status code: " + response.getStatusCode());
    }

    public static BillingReportWrapper createBillingReport(final String body) {
        Response response = RequestService.request(Method.POST, NEW_API + "/billing_report", body);
        return populateObject(response, BillingReportWrapper.class);
    }

    public static BillingReportWrapper getBillingReport(final String searchId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/billing_report/" + searchId);
        return populateObject(response, BillingReportWrapper.class);
    }
}
