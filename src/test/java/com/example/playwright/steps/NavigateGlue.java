package com.example.playwright.steps;

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
        int projectId = context.getProject().getId();
        switch (endpoint) {
            case "overview" -> Navigate.project(context.getProject().getId()).overview().get();
            case "measuring points" -> Navigate.project(context.getProject().getId()).measurePoints().get();
            case "measuring point create" -> Navigate.project(context.getProject().getId()).measurePoints().create().get();
            case "blasts" -> Navigate.project(context.getProject().getId()).blasts().get();
            case "devices" -> Navigate.project(context.getProject().getId()).devices().get();
            case "settings" -> Navigate.project(context.getProject().getId()).settings().get();
            case "settings general" -> Navigate.project(context.getProject().getId()).settings().general().get();
            case "data reports" -> Navigate.project(context.getProject().getId()).views().get();
            case "data reports create" -> Navigate.project(context.getProject().getId()).views().create().get();
            case "scheduled_reports" -> Navigate.project(projectId).scheduledReports().get();
            case "scheduled_reports create" -> Navigate.project(projectId).scheduledReports().create().get();
            case "message rules" -> Navigate.project(context.getProject().getId()).messageRules().get();
            case "users" -> Navigate.project(context.getProject().getId()).users().get();
            case "users create" -> Navigate.project(context.getProject().getId()).users().create().get();
            case "users manage" -> Navigate.project(context.getProject().getId()).users().manage().get();
            case "billing report create" -> Navigate.project(context.getProject().getId()).billingReports().create().get();
            case "agendas", "settings agendas" -> {
                Navigate.project(context.getProject().getId()).overview().get(); // We need to go here first so that the gui loads all projects, which web client needs for agendas
                Navigate.project(context.getProject().getId()).settings().agendas().get();
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

}