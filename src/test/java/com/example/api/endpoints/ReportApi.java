package com.example.api.endpoints;

import com.example.api.RequestService;
import com.example.api.models.ScheduledReport;
import com.example.api.models.report.DataReport;
import com.example.api.models.report.analysis.Analysis;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

public class ReportApi extends ModelApi {


    /**
     * The "/search" endpoint creates a report and returns the report's id and some other information.
     * However, it does not contain information about Transients, Intervals and others. To get a report
     * with this information, the endpoint "/data" must be used.
     * <p>
     * When a request to create a report is received, the status flag is set to "running". When the
     * report is ready, the status changes to "finished". How long our tests want to wait for the report to
     * be ready is specified with the timeToWait variable. This variable can be set to 0 if we are not
     * interested in which mode the report is in.
     */

    public final static String NEW_API = "v0";

    public static DataReport getData(final int projectId, final String dataReportId) {
        Response response = RequestService.request(Method.GET, NEW_API + "/project/" + projectId + "/search/" + dataReportId + "/data");
        return populateObject(response, DataReport.class);
    }

    /**
     * Analysis
     */

    // NB. We really do not need a valid projectId as the BE do not make validation of it.
    public static Analysis createAnalysis(int projectId, String transientKey, String json) {
        String template = "v0/project/{projectId}/search/transient_key/{transientKey}/analysis";

        Map<String, Object> params = Map.of(
                "projectId", projectId,
                "transientKey", transientKey
        );
        Response response = RequestService.request(Method.POST, template, params, json);
        return populateObject(response, Analysis.class);
    }

    /**
     * SDR
     */
    public static ScheduledReport createSDR(int projectId, String body) {
        Response response = RequestService.request(Method.POST, "/project/" + projectId + "/scheduled_report/", body);
        return populateObject(response, ScheduledReport.class);
    }

    public static ScheduledReport updateSDR(int projectId, String body) {
        Response response = RequestService.request(Method.PUT, "/project/" + projectId + "/scheduled_report/", body);
        return populateObject(response, ScheduledReport.class);
    }

    public static ScheduledReport getSDR(int projectId, int sdrId) {
        Response response = RequestService.request(Method.GET, "/project/" + projectId + "/scheduled_report/" + sdrId);
        return populateObject(response, ScheduledReport.class);
    }

    public static List<ScheduledReport> getSDRs(int projectId) {
        Response response = RequestService.request(Method.GET, "/project/" + projectId + "/scheduled_report/");
        return populateList(response, ScheduledReport.class);
    }
}
