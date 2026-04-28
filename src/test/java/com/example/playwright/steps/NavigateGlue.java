package com.example.playwright.steps;

import com.example.playwright.enums.DeviceType;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.testDevices.DeviceProperties;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigateGlue {

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

}