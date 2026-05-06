package com.example.playwright.steps;

import com.example.api.models.report.Search;
import com.example.helpers.testData.Context;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.helpers.enums.ProviderType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static com.example.playwright.helpers.enums.DeviceType.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigateGlue extends BaseGlue {
    Context context = context();

    @And("I navigate to account {string}")
    public void iNavigateToAccount(final String endpoint) {
        switch (endpoint) {
            case "devices" -> Navigate.company().devices().get();
            case "projects" -> Navigate.company().projects().get();
            case "users" -> Navigate.company().users().get();
            case "overview" -> Navigate.company().overview().get();
            default -> throw new IllegalArgumentException("Unknown endpoint");
        }
    }

    @And("I navigate to project {int} {string}")
    public void iNavigateToProject(final int projectId, final String endpoint) {
        switch (endpoint) {
            case "devices" -> Navigate.project(projectId).devices().get();
            case "users" -> Navigate.project(projectId).users().get();
            case "overview" -> Navigate.project(projectId).overview().get();
            default -> throw new IllegalArgumentException("Unknown endpoint");
        }
    }

    @And("I am at project {string}")
    @When("I navigate to project {string}")
    public void iNavigateToProject(String endpoint) {
        int projectId = context().getProject().getId();
        switch (endpoint) {
            case "overview" -> Navigate.project(context().getProject().getId()).overview().get();
            case "measuring points" -> Navigate.project(context().getProject().getId()).measurePoints().get();
            case "measuring point create" -> Navigate.project(context().getProject().getId()).measurePoints().create().get();
            case "blasts" -> Navigate.project(context().getProject().getId()).blasts().get();
            case "devices" -> Navigate.project(context().getProject().getId()).devices().get();
            case "settings" -> Navigate.project(context().getProject().getId()).settings().get();
            case "settings general" -> Navigate.project(context().getProject().getId()).settings().general().get();
            case "data reports" -> Navigate.project(context().getProject().getId()).views().get();
            case "data reports create" -> Navigate.project(context().getProject().getId()).views().create().get();
            case "scheduled_reports" -> Navigate.project(projectId).scheduledReports().get();
            case "scheduled_reports create" -> Navigate.project(projectId).scheduledReports().create().get();
            case "message rules" -> Navigate.project(context().getProject().getId()).messageRules().get();
            case "users" -> Navigate.project(context().getProject().getId()).users().get();
            case "users create" -> Navigate.project(context().getProject().getId()).users().create().get();
            case "users manage" -> Navigate.project(context().getProject().getId()).users().manage().get();
            case "billing report create" -> Navigate.project(context().getProject().getId()).billingReports().create().get();
            case "agendas", "settings agendas" -> {
                Navigate.project(context().getProject().getId()).overview().get(); // We need to go here first so that the gui loads all projects, which web client needs for agendas
                Navigate.project(context().getProject().getId()).settings().agendas().get();
            }
            default -> throw new IllegalStateException("Unexpected endpoint value: " + endpoint);
        }
    }
    

//    ***************************************************************************
    

    @Then("I validate url contains {string}")
    public void iValidateUrlContainsOverview(String endpoint) {
        Navigate.validateUrlContains(endpoint);
    }

    @When("I navigate to the {string} details")
    public void iNavigateToDeviceDetails(String deviceType) {
        DeviceType type = DeviceType.valueOf(deviceType);
        String serial = DeviceProperties.getConnectedSerial(deviceType);

        Navigate.company()
                .device(type, serial)
                .details()
                .get();
    }

    @Then("I am redirected to {string}")
    public void validateCorrectUrlEndpoint(String endpoint) {

        // Sometimes we use an endpoint that easier to read than the one used in the url
        String expectedEndpoint = switch (endpoint) {
            case "measuring points" -> "measure_points";
            case "vibration report" -> "vib_report?goBackTo=project_measure_points";
            default -> endpoint;
        };

        // redirection to vibration reports is done from /views, and we need to wait for the creation to be complete
        if (endpoint.equals("vibration report")) {
            PlaywrightActions.sleep(6);
//            asidePO.validateNoSpinner();
        }

        String actualUrl = Navigate.getCurrentUrl();

        assertTrue(actualUrl.contains(expectedEndpoint),
                () -> "expectedEndpoint vs. actualUrl: " + expectedEndpoint + " vs. " + actualUrl);
    }

    @Given("I am in account {string}")
    public void iAmInAccountProvider(String provider) {
        ProviderType type = ProviderType.fromEndpoint(provider);
        Navigate.company()
                .provider(type)
                .get();
    }

    // todo: merge with iNavigateToProject() in some way?
    // Typ låta några av top-level provider anrop hamna här. Och låta sub-level-anropen vara kvar i andra metoden?
    @Given("I am in project {string}")
    public void iAmInProjectProvider(String provider) {
        if (provider.equals("measuring points")) {
            provider = "measure_points";
        }
        ProviderType type = ProviderType.fromEndpoint(provider);
        Navigate.project(context.getProject().getId())
                .provider(type)
                .get();
    }

    @And("I am at message rule settings {string}")
    @And("I am at project message rule {string}")
    @And("I am at project message rules settings {string}")
    public void openMessageRuleSettings(String endpoint) {
        switch (endpoint) {
            case "recipients" -> Navigate.project(context.getProject().getId()).messageRule(context.getMessageRules().getFirst().getId()).settings().recipients().get();
            case "settings general" -> Navigate.project(context.getProject().getId()).messageRule(context.getMessageRules().getFirst().getId()).settings().general().get();
            case "content" -> Navigate.project(context.getProject().getId()).messageRule(context.getMessageRules().getFirst().getId()).settings().content().get();
            default -> throw new IllegalStateException("Unexpected endpoint value: " + endpoint);
        }
    }

    @And("I am at measuring point {string}")
    public void iAmAtMeasuringPointCoordinates(String endpoint) {
        switch (endpoint) {
            case "coordinates" -> Navigate.project(context.getProject().getId()).measurePoint(context.getMeasuringPoints().getFirst().getId()).settings().coordinates().get();
            default -> throw new IllegalStateException("Unexpected endpoint value: " + endpoint);
        }
    }

    @And("I am at project blast {string}")
    public void openBlast(String endpoint) {
        switch (endpoint) {
            case "create" -> Navigate.project(context.getProject().getId()).blasts().create().get();
            case "settings general" -> Navigate.project(context.getProject().getId()).blast(context.getBlasts().getFirst().getId()).settings().general().get();
            default -> throw new IllegalStateException("Unexpected endpoint value: " + endpoint);
        }
    }

    @And("I navigate to {string} Billing Report create")
    public void iNavigateToBillingReportCreate(String level) {
        switch (level) {
            case "Account" -> Navigate.company()
                    .billingReports()
                    .create()
                    .get();
            case "Project" -> Navigate.project(context.getProject().getId())
                    .billingReports()
                    .create()
                    .get();
        }
    }

    @Given("I am in account {string} Connection History")
    public void iAmInAccountCConnectionHistory(String deviceType) {
        DeviceType type = DeviceType.valueOf(deviceType);
        String serial = DeviceProperties.getConnectedSerial(deviceType);

        Navigate.company()
                .device(type, serial)
                .status()
                .projects()
                .get();
    }

    @Given("I am in account {string} monitoring settings")
    public void iAmInAccountMonitoringSettings(String deviceType) {
        DeviceType type = DeviceType.valueOf(deviceType);
        String serial = DeviceProperties.getConnectedSerial(deviceType);

        Navigate.company()
                .device(type, serial)
                .settings()
                .monitoring()
                .get();
    }

    @When("I navigate to account {string} device {string}")
    public void iNavigateToAccountDeviceSettingsMonitoring(String type, String endpoint) {
        DeviceType deviceType = DeviceType.fromType(type);
        String serial = DeviceProperties.getConnectedSerial(type);

        switch (endpoint) {
            case "settings monitoring" -> Navigate.company().device(deviceType, serial).settings().monitoring().get();
            default ->  throw new IllegalStateException("Unexpected value: " + endpoint);
        }
    }

    @When("{string} view is opened")
    public void iNavigateToProjectReports(String viewPoint) {
        Search search = context.getLastSearch();

        switch (viewPoint) {
            case "intervals chart" -> Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .intervals()
                    .chart()
                    .get();
            case "intervals table" -> Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .intervals()
                    .table()
                    .get();
            case "transients", "transients table" -> Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .transients()
                    .get();
            case "measuring report" -> Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .measuringReport()
                    .get();
            case "blasts" -> Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .blasts()
                    .get();
            case "blast journal" -> { Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .blast(context.getLastBlast().getId())
                    .get();
                PlaywrightActions.sleep(3);
            }
            case "regression report" ->         Navigate.project(context.getProject().getId())
                    .view(search.getId())
                    .regression()
                    .get();
            default ->  throw new IllegalStateException("Unexpected value: " + viewPoint);
        }
    }

    @Given("I am at login page")
    public void navigateToLoginPage() {
        Navigate.domain()
                .login()
                .get();
    }

    @Given("I am at passreset page")
    public void navigateToPassresetPage() {
        Navigate.domain()
                .passreset()
                .get();
    }

    @Given("I navigate to {string} mon_settings")
    public void iNavigateToDeviceMonSettings(String type) {
        DeviceType deviceType = DeviceType.fromType(type);
        String serial = DeviceProperties.getConnectedSerial(type);

        if (deviceType.equals(C22) || deviceType.equals(C50)) {

            Navigate.company()
                    .device(deviceType, serial)
                    .settings()
                    .monitoring()
                    .get();

        } else if (deviceType.equals(S50)) {
            DeviceType loggerType = DeviceType.fromType("D10");
            String loggerSerial = DeviceProperties.getConnectedSerial("D10");

            Navigate.company()
                    .device(loggerType, loggerSerial)
                    .settings()
                    .sensor(serial)
                    .get();
        }
    }

    @Given("I navigate to {string} {string} mon_settings")
    public void iNavigateToDeviceMonSettings(String logger, String sensor) {
        DeviceType loggerType = DeviceType.fromType(logger);
        String loggerSerial = DeviceProperties.getConnectedSerial(logger);

        String sensorSerial = DeviceProperties.getConnectedSerial(sensor);

        Navigate.company()
                .device(loggerType, loggerSerial)
                .settings()
                .sensor(sensorSerial)
                .get();
    }

    @And("I am in the clients settings page")
    public void iAmInTheClientsSettingsPage() {
        Navigate.company()
                .user(context.getUsers().getFirst().getId())
                .settings()
                .get();
    }

    @Then("I remain at {string}")
    public void iRemainAtDetails(String urlContains) {
        String currentUrl = Navigate.getCurrentUrl();
        assertTrue(currentUrl.contains(urlContains),
                "currentUrl: " + currentUrl);
    }

    @When("I navigate to project measuring point {string}")
    public void iNavigateToProjectMeasuringPoint(String endpoint) {
        switch (endpoint) {
            case "vibration-report" -> Navigate.project(context.getProject().getId()).measurePoint(context.getMeasuringPoints().getFirst().getId()).settings().vibrationReport().get();
            case "settings" -> Navigate.project(context.getProject().getId()).measurePoint(context.getMeasuringPoints().getFirst().getId()).settings().get();
            case "settings general" -> Navigate.project(context.getProject().getId()).measurePoint(context.getMeasuringPoints().getFirst().getId()).settings().general().get();
            case "details" -> Navigate.project(context.getProject().getId()).measurePoint(context.getMeasuringPoints().getFirst().getId()).details().get();
            case "create" -> Navigate.project(context.getProject().getId()).measurePoints().create().get();
            case "active channels" -> Navigate.project(context.getProject().getId()).measurePoint(context.getMeasuringPoints().getFirst().getId()).settings().activeChannels().get();
        }
    }

    @When("I navigate to project message rules {string}")
    public void iNavigateToProjectMessageRulesSettingsGeneral(String endpoint) {
        switch (endpoint) {
//            case "settings" ->
            case "settings general" -> Navigate.project(context.getProject().getId()).messageRule(context.getMessageRules().getFirst().getId()).settings().general().get();
//            case "details" ->
            case "settings thresholds" -> Navigate.project(context.getProject().getId()).messageRule(context.getMessageRules().getFirst().getId()).settings().thresholds().get();
            case "create" -> Navigate.project(context.getProject().getId()).messageRules().create().get();
        }
    }

    @Given("I navigate to {string} remote override")
    public void iNavigateToCRemoteOverride(String type) {
        DeviceType deviceType = DeviceType.fromType(type);
        String serial = DeviceProperties.getConnectedSerial(type);
        Navigate.company()
                .device(deviceType, serial)
                .settings()
                .special()
                .get();
    }

}