package com.example.playwright.helpers;

import com.example.playwright.config.TestEnvironment;
import com.example.playwright.enums.DeviceType;
import com.example.playwright.enums.ProviderType;
import com.example.playwright.hooks.BrowserHooks;
import com.microsoft.playwright.TimeoutError;

import java.util.stream.IntStream;

public class Navigate {

    private final StringBuilder path = new StringBuilder();

    private Navigate(String start) {
        path.append(start);
    }

    public void get() {
        System.out.println("\nNavigating to:\n -> " + path.toString());
        BrowserHooks.getPage().navigate(path.toString());
    }

    public static String webUrl() {
        return TestEnvironment.getWebUrl();
    }

    private static String base() {
        return "https://" + webUrl() + "/";
    }

    /***************************** Starters **************************/

    public static Navigate domain() {
        return new Navigate(base());
    }

    public static Navigate company() {
        return new Navigate(base() + "#/company");
    }

    public static Navigate project(final int projectId) {
        return new Navigate(base() + "#/project/" + projectId);
    }

    /***************************** Endpoints **************************/

    public Navigate login() {
        path.append("/login");
        return this;
    }

    public Navigate overview() {
        path.append("/overview");
        return this;
    }

    public Navigate devices() {
        path.append("/devices");
        return this;
    }

    public Navigate projects() {
        path.append("/projects");
        return this;
    }

    public Navigate project() {
        path.append("/project");
        return this;
    }

    public Navigate users() {
        path.append("/users");
        return this;
    }

    public Navigate devices(String searchId) {
        path.append("/devices").append("/").append(searchId);
        return this;
    }

    public Navigate device(final DeviceType type, final int id) {
        path.append("/devices/").append(type.getUrl()).append("/").append(id);
        return this;
    }

    public Navigate device(String type, final String id) {
        type = (type.equals("C10_LOGGER") || type.equals("C12_LOGGER"))
                ? "COMPACT"
                : type;

        path.append("/devices/").append(type).append("/").append(id);
        return this;
    }

    public Navigate device(final DeviceType type, final String id) {
        path.append("/devices/").append(type.getUrl()).append("/").append(id);
        return this;
    }

    public Navigate user(final int id) {
        path.append("/users").append("/").append(id);
        return this;
    }

    public Navigate measurePoints() {
        path.append("/measure_points");
        return this;
    }

    public Navigate measurePoints(String searchId) {
        path.append("/measure_points").append("/").append(searchId);
        return this;
    }

    public Navigate billingReports() {
        path.append("/billing_reports");
        return this;
    }

    public Navigate billingReports(final String searchId) {
        path.append("/billing_reports").append("/").append(searchId);
        return this;
    }

    public Navigate measurePoint(final int searchId) {
        path.append("/measure_points").append("/").append(searchId);
        return this;
    }

    public Navigate blasts() {
        path.append("/blasts");
        return this;
    }

    public Navigate blast(final int id) {
        path.append("/blasts").append("/").append(id);
        return this;
    }

    public Navigate views() {
        path.append("/views");
        return this;
    }

    public Navigate view(final String id) {
        path.append("/views").append("/").append(id);
        return this;
    }

    public Navigate messageRules() {
        path.append("/message_rules");
        return this;
    }

    public Navigate messageRule(final int id) {
        path.append("/message_rules").append("/").append(id);
        return this;
    }

    public Navigate chart() {
        path.append("/chart");
        return this;
    }

    public Navigate comments() {
        path.append("/comments");
        return this;
    }

    // todo: byt ut mot en helpermetod som anropar antingen projects() eller project(232342)?
//    public Navigate provider(ProviderType provider) {
//        switch (provider) {
//            // company level = projects, project level = project
//            case PROJECT -> path.append(path.toString().contains("company") ? "/projects" : "/project");
//            case MEASURING_POINT -> path.append("/measure_points");
//            case BLAST -> path.append("/blasts");
//            case DEVICE -> path.append("/devices");
//            case DATA_REPORT -> path.append("/views");
//            case MESSAGE_RULE -> path.append("/message_rules");
//            case USER -> path.append("/users");
//            case COMMENT -> path.append("/comments");
//            case OVERVIEW -> path.append("/overview");
//            case BILLING_REPORT ->path.append("/billing_reports/create");
//        }
//        return this;
//    }

    public Navigate settings() {
        path.append("/settings");
        return this;
    }

    public Navigate special() {
        path.append("/special");
        return this;
    }

    public Navigate content() {
        path.append("/content");
        return this;
    }

    public Navigate details() {
        path.append("/details");
        return this;
    }

    public Navigate preview() {
        path.append("/preview");
        return this;
    }

    public Navigate create() {
        path.append("/create");
        return this;
    }

    public Navigate changes() {
        path.append("/changes");
        return this;
    }

    public Navigate general() {
        path.append("/general");
        return this;
    }

    public Navigate agendas() {
        path.append("/agendas");
        return this;
    }

    public Navigate agenda(final int id) {
        path.append("/agendas").append("/").append(id);
        return this;
    }

    public Navigate table() {
        path.append("/table");
        return this;
    }

    public Navigate intervals() {
        path.append("/intervals");
        return this;
    }

    public Navigate transients() {
        path.append("/transients");
        return this;
    }

    public Navigate transients(final int id, final String analysisId) {
        String escapedAnalysisId = analysisId.replace("+", "%2B");
        path.append("/transients/").append(id).append("?tr_ids=").append(escapedAnalysisId);
        return this;
    }

    public Navigate transients(final int id, final String analysisId1, final String analysisId2, final String analysisId3) {
        String escapedAnalysisId1 = analysisId1.replace("+", "%2B");
        String escapedAnalysisId2 = analysisId2.replace("+", "%2B");
        String escapedAnalysisId3 = analysisId3.replace("+", "%2B");

        path.append("/transients/").append(id)
                .append("?tr_ids=").append(escapedAnalysisId1)
                .append("&tr_ids=").append(escapedAnalysisId2)
                .append("&tr_ids=").append(escapedAnalysisId3);
        return this;
    }

    public Navigate transients(final int id, final String[] analysisIds) {
        path.append("/transients/").append(id).append("?");
        IntStream.range(0, analysisIds.length)
                .forEach(i -> {
                    if (i == 0) path.append("?tr_ids=").append(analysisIds[i]);
                    else path.append("&tr_ids=").append(analysisIds[i]);
                });
        return this;
    }

    public Navigate measuringReport() {
        path.append("/measuring_report");
        return this;
    }

    public Navigate sensor(final String id) {
        path.append("/sensor/" + id);
        return this;
    }

    public Navigate agendaSettings() {
        path.append("/agenda-settings");
        return this;
    }

    public Navigate timeslot() {
        path.append("/timeslot");
        return this;
    }

    public Navigate add() {
        path.append("/add");
        return this;
    }

    public Navigate noiseReport() {
        path.append("/noise-report");
        return this;
    }

    public Navigate graphSettings() {
        path.append("/graph-settings");
        return this;
    }

    public Navigate dataPresentation() {
        path.append("/data-presentation");
        return this;
    }

    public Navigate coordinates() {
        path.append("/coordinates");
        return this;
    }

    public Navigate manage() {
        path.append("/manage");
        return this;
    }

    public Navigate passreset() {
        path.append("/passreset");
        return this;
    }

//    public Navigate type(DeviceType type) {
//        path.append("/").append(type);
//        return this;
//    }

    public Navigate map() {
        path.append("/map");
        return this;
    }

    public Navigate location() {
        path.append("/location");
        return this;
    }

    public Navigate charge() {
        path.append("/charge");
        return this;
    }

    public Navigate holes() {
        path.append("/holes");
        return this;
    }

    public Navigate time() {
        path.append("/time");
        return this;
    }

    public Navigate units() {
        path.append("/units");
        return this;
    }

    public Navigate upload() {
        path.append("/upload");
        return this;
    }

    public Navigate serviceMessages() {
        path.append("/service_messages");
        return this;
    }

    public Navigate status() {
        path.append("/status");
        return this;
    }

    public Navigate battery() {
        path.append("/battery");
        return this;
    }

    public Navigate description() {
        path.append("/description");
        return this;
    }

    public Navigate monitoring() {
        path.append("/monitoring");
        return this;
    }

    public Navigate timeFrame() {
        path.append("/time-frame");
        return this;
    }

    public Navigate recipients() {
        path.append("/recipients");
        return this;
    }

    public Navigate scheduledReports() {
        path.append("/scheduled_reports");
        return this;
    }

    public Navigate scheduledReport(int reportId) {
        path.append("/scheduled_reports/").append(reportId);
        return this;
    }

    public Navigate thresholds() {
        path.append("/thresholds");
        return this;
    }

    public Navigate regression() {
        path.append("/regression");
        return this;
    }

    public Navigate vibrationReport() {
        path.append("/vibration-report");
        return this;
    }

    public Navigate vibReport() {
        path.append("/vib_report");
        return this;
    }

    public Navigate activeChannels() {
        path.append("/active-channels");
        return this;
    }

    public Navigate provider(ProviderType provider) {
        return switch (provider) {
            // company level = projects, project level = project
            case PROJECT -> path.toString().contains("company")
                    ? projects()
                    : project();
            case MEASURING_POINT -> measurePoints();
            case BLAST -> blasts();
            case DEVICE -> devices();
            case DATA_REPORT -> views();
            case MESSAGE_RULE -> messageRules();
            case USER -> users();
            case COMMENT -> comments();
            case OVERVIEW -> overview();
            case BILLING_REPORT -> billingReports().create();
            default -> throw new IllegalArgumentException("Unknown provider " + provider);
        };
    }


    /***************************** Helpers **************************/

    public static String getCurrentUrl() {
        return BrowserHooks.getPage().url();
    }

    public static void waitUntilUrlContains(String value) {
        BrowserHooks.getPage().waitForURL(url -> url.contains(value));
    }

    public static void validateUrlContains(String value) {
        try {
            BrowserHooks.getPage().waitForURL(url -> url.contains(value));
        } catch (TimeoutError e) {
            throw new RuntimeException("Timed out waiting for URL: " + value);
        }
    }

    public static void refreshBrowser() {
        BrowserHooks.getPage().reload();
    }
}