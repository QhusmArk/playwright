package com.example.playwright.hooks.testUsers;

import com.example.playwright.hooks.ScenarioContext;
import com.example.playwright.steps.BaseGlue;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.ArrayList;

public class UserHooks extends BaseGlue {

    @Before(order = 1)
    public void acquireUserWithRole(Scenario scenario) {
        ScenarioContext.setScenarioName(scenario.getName());

        String requiredRole = resolveRequiredRole(scenario);

        var user = (requiredRole != null)
                ? TestUserPool.acquireUserWithRole(requiredRole)
                : TestUserPool.acquireUser()
                .orElseThrow(() -> new RuntimeException("No available test users in pool"));
        System.out.println("test_user: " + user.email());
    }

    private String resolveRequiredRole(Scenario scenario) {
        var tags = scenario.getSourceTagNames();

        var roles = new ArrayList<String>();

        if (tags.contains("@admin") || tags.contains("@loginWithAdmin")) {
            roles.add("ADMIN");
        }
        if (tags.contains("@user") || tags.contains("@loginWithUser")) {
            roles.add("USER");
        }
        if (tags.contains("@client") || tags.contains("@loginWithClient")) {
            roles.add("CLIENT");
        }
        if (tags.contains("@blaster") || tags.contains("@loginWithBlaster")) {
            roles.add("BLASTER");
        }

        // Not ok to use more than one role per scenario
        if (roles.size() > 1) {
            throw new RuntimeException("Scenario has multiple role tags: " + tags);
        } else if (roles.isEmpty()) {
            throw new IllegalStateException("No role found for tag.");
        } else {
            return roles.getFirst();
        }
    }

    @After(order = 90)
    public void releaseTestUser(Scenario scenario) {
        System.out.println("After: " + 90);
        TestUserPool.releaseCurrentUser();
    }
}