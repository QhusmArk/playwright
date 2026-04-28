package com.example.playwright.hooks;

import com.example.playwright.testUsers.TestUserPool;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.ArrayList;

public class UserHooks {

    @Before(order = 1)
    public void acquireUserWithRole(Scenario scenario) {

        String requiredRole = resolveRequiredRole(scenario);

        var user = (requiredRole != null)
                ? TestUserPool.acquireUserWithRole(requiredRole)
                : TestUserPool.acquireUser()
                .orElseThrow(() -> new RuntimeException("No available test users in pool"));
        System.out.println("user: " + user.email());
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
        }

        return roles.isEmpty()
                ? null
                : roles.getFirst();
    }

    @After
    public void releaseTestUser(Scenario scenario) {
        TestUserPool.releaseCurrentUser();
    }
}