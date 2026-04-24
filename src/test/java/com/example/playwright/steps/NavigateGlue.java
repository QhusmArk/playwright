package com.example.playwright.steps;

import com.example.playwright.navigation.Navigate;
import io.cucumber.java.en.And;

public class NavigateGlue {

    @And("I navigate to account {string}")
    public void iNavigateToAccount(final String endpoint) {
        switch (endpoint) {
            case "devices" -> Navigate.company().devices().get();
//            case "projects" -> navigate().company().projects().get();
//            case "users" -> navigate().company().users().get();
//            case "overview" -> navigate().company().overview().get();
            default -> throw new IllegalArgumentException("Unknown endpoint");
        }
    }

//    @And("I navigate to project {int} {string}")
//    public void iNavigateToProject(final int projectId, final String endpoint) {
//        switch (endpoint) {
//            case "devices" -> navigate().project(projectId).devices().get();
//            case "users" -> navigate().project(projectId).users().get();
//            case "overview" -> navigate().project(projectId).overview().get();
//            default -> throw new IllegalArgumentException("Unknown endpoint");
//        }
//    }
}