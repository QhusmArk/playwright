package com.example.playwright.hooks;

import com.example.api.endpoints.ProjectApi;
import com.example.api.models.project.Project;
import com.example.helpers.testData.TestContextHolder;
import com.example.playwright.hooks.testUsers.TestUserPool;
import com.example.playwright.steps.BaseGlue;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @After(order = 100)
    public void clearScenarioContext() {
        deleteProject();
        ScenarioContext.clear();
        TestContextHolder.clear();
    }

    /**
     * Removing the project used in the test.
     */
    public void deleteProject() {
        // Abort the entire test if the prerequisite is not met
//        if (skipScenario.get()) {
//            return;
//        }

        Project project = context().getProject();

        if (project == null) {
            System.out.println("No project in context(). No deleting to be done.");
        } else {
            int projectId = project.getId();
            System.out.println("Deleting test project " + projectId + ".");
            ProjectApi.deleteProject(projectId);
        }

        System.out.println("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("************************** Test end **************************\n");
    }

}