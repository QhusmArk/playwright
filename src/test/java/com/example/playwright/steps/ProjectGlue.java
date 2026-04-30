package com.example.playwright.steps;

import com.example.api.endpoints.ProjectApi;
import com.example.api.models.project.Project;
import com.example.helpers.Randomizer;
import com.example.playwright.components.aside.asideItems.listItems.ProjectItem;
import com.example.playwright.helpers.Navigate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectGlue extends BaseGlue {

    @Given("A project is created")
    public void createProjectByGui() {
        Navigate.company()
                .projects()
                .get();

        // First make sure no project is called "A-test-auto-tmp"
        Project p = ProjectApi.getProjectByName("A-test-auto-tmp");
        // We expect no projects with name "A-test-auto-tmp"
        if (p != null) {
            ProjectApi.deleteProject(p.getId());
            System.out.println("Project with name 'A-test-auto-tmp' already existed. Deleting it.");
            createProjectByGui();
        } else {
            System.out.println("No project with name 'A-test-auto-tmp' existed. Creating the new project.");
            projectPO.createProject("A-test-auto-tmp", Randomizer.randomString(6), "Sankt Paulsgatan");
        }
        // Fetch the newly created project and store it to context
        Project project = ProjectApi.getProjectByName("A-test-auto-tmp");
        if (project != null) {
            context().setProject(project);
        } else {
            throw new IllegalStateException("Project could not be put to context.");
        }
    }

    @Then("the project should be visible in aside")
    public void theProjectShouldBeVisibleInAside() {
        String expectedProjectsName = context().getProject().getName();

        // todo: sök i aside så det bara är ett projekt i listan
        asidePO.makeSearchInAside(expectedProjectsName);

        List<ProjectItem> asideProjects = asidePO.getAside().getProjectItems();
        boolean matchingProjectNames = asideProjects.stream()
                .anyMatch(projectItem -> projectItem.getProjectName().equals(expectedProjectsName));

        assertTrue(matchingProjectNames,
                "There was no project in aside with name '" + expectedProjectsName + "'.");
    }
}
