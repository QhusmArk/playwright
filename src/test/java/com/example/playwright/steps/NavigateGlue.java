package com.example.playwright.steps;

import com.example.playwright.navigation.Navigate;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

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
}