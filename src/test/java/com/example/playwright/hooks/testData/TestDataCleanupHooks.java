package com.example.playwright.hooks.testData;

import com.example.helpers.testData.TestContextHolder;
import com.example.playwright.config.TestUserLoader;
import com.example.playwright.hooks.ScenarioContext;
import com.example.playwright.hooks.testUsers.TestUser;
import com.example.playwright.steps.BaseGlue;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class TestDataCleanupHooks extends BaseGlue {

    private static boolean cleanupHasRun = false;

    @BeforeAll
    public static void cleanOldAutomationProjects() {
        System.out.println("cleanupHasRun: " +  cleanupHasRun);
        if (cleanupHasRun) {
            return;
        }

        cleanupHasRun = true;

        TestUser cleanupUser = TestUserLoader.loadCleanupApiUser();
        TestProjectCleanupService.deleteOldAutomationProjects(cleanupUser);
    }

    /**
     * NB. @After runs in descending order.
     */
    @After(order = 100)
    public void deleteTestProject(Scenario scenario) {
        System.out.println("After: " + 100);
        if (context().getProject() != null) {
            TestProjectCleanupService.deleteProject(context().getProject());
        } else {
            System.out.println("No project in context(). No deleting to be done.");
        }
    }

    @After(order = 80)
    public void clearScenarioContext() {
        System.out.println("After: " + 80);
        ScenarioContext.clear();
        TestContextHolder.clear();
    }
}
