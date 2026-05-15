package com.example.playwright.hooks.testData;

import com.example.api.RequestService;
import com.example.api.endpoints.ProjectApi;
import com.example.api.models.project.Project;
import com.example.playwright.hooks.testUsers.TestUser;

import java.util.List;

public class TestProjectCleanupService {

    private static final String TEST_PROJECT_NAME_PREFIX = "test-auto-project-";
    private static final long FIVE_HOURS_IN_MILLIS = 5L * 60 * 60 * 1000;

    /**
     * Removing the project used in the test.
     */
    public static void deleteProject(Project project) {
        // Abort the entire test if the prerequisite is not met
//        if (skipScenario.get()) {
//            return;
//        }
        int projectId = project.getId();
        System.out.println("Deleting test project " + projectId + ".");
        ProjectApi.deleteProject(projectId);
    }

    public static void deleteOldAutomationProjects(TestUser cleanupUser) {
        RequestService.setUp();

        List<Project> projects = ProjectApi.getProjects(cleanupUser.email(), cleanupUser.password());

        List<Project> testAutoProjects = projects.stream()
                .filter(TestProjectCleanupService::isAutomationProject)
                        .toList();

        List<Project> oldTestAutoProjects = testAutoProjects.stream()
                .filter(TestProjectCleanupService::isOlderThanFiveHours)
                .toList();

        if (oldTestAutoProjects.isEmpty()) {
            System.out.println("No >5h old test-auto-projects-* in context().");
        } else {
            System.out.println("Found " + oldTestAutoProjects.size() + " >5h old project in context(). Deleting to be done for:");
            oldTestAutoProjects.forEach(project -> System.out.println(project.getProjectId()));

            oldTestAutoProjects
                    .forEach(project -> ProjectApi.deleteProject(project.getId(), cleanupUser.email(), cleanupUser.password()));
        }
    }

    private static boolean isAutomationProject(Project project) {
        return project.getName() != null
                && project.getName().startsWith(TEST_PROJECT_NAME_PREFIX);
    }

    private static boolean isOlderThanFiveHours(Project project) {
        long projectCreatedTimeMillis = Long.parseLong(project.getProjectId());
        return System.currentTimeMillis() - projectCreatedTimeMillis > FIVE_HOURS_IN_MILLIS;
    }
}
