package com.example.playwright.steps;

import com.example.api.endpoints.ProjectApi;
import com.example.api.models.comment.Comment;
import com.example.api.models.project.Project;
import com.example.helpers.Randomizer;
import com.example.helpers.builders.BuilderFactory;
import com.example.helpers.builders.CommentBuilder;
import com.example.helpers.builders.ProjectBuilder;
import com.example.playwright.components.aside.Aside;
import com.example.playwright.components.aside.asideItems.listItems.OverviewItem;
import com.example.playwright.components.aside.asideItems.listItems.ProjectItem;
import com.example.playwright.components.panels.CompanyProjectDetailsPanel;
import com.example.playwright.components.panels.ProjectSettingsDetailsPanel;
import com.example.playwright.components.panels.project.ProjectSettingPanel;
import com.example.playwright.components.parts.Table;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.IconType;
import com.example.playwright.helpers.enums.ProviderType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;

import static com.example.playwright.helpers.enums.ProviderType.COMMENT;
import static com.example.playwright.helpers.enums.ProviderType.OVERVIEW;
import static org.junit.jupiter.api.Assertions.*;

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
            throw new IllegalStateException("Project could not be put to context().");
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

    @When("I rename the project")
    public void changeName() {
        String newName = "test-auto-" + Randomizer.randomString(6);
        projectPO.changeName(newName);
        context().getProject().setName(newName);
    }

    @Then("the new project name will be visible in settings panel")
    public void validatePanelHeaderContent() {
        // Remove project description bc otherwise description will show in panel header instead of project name
        projectPO.saveNewDescription("");

        String projectName = context().getProject().getName();
        String actualPanelHeader = projectPO.getPanelBodyHeader();
        // Panel body
        assertEquals(projectName, actualPanelHeader);
    }

    @And("project name and id are visible in header dropdown")
    public void validateHeaderDropDownContent() {
        String textInPageHeaderDropdown = projectPO.getTextInPageHeaderDropdown();

        String projectName = context().getProject().getName();
        assertTrue(textInPageHeaderDropdown.contains(projectName));

        String projectsId = String.valueOf(context().getProject().getProjectId());
        assertTrue(textInPageHeaderDropdown.contains(projectsId));
    }

    @When("I change the project description")
    public void addDescription() {
        String newDescription = "Detta är en text";
        projectPO.saveNewDescription(newDescription);
    }

    @Then("the new project description will be visible in settings panel")
    public void theNewProjectDescriptionWillBeVisibleInSettingsPanel() {
        // Check that the new description is set to panel header
        String actualPanelHeader = projectPO.getPanelBodyHeader();
        assertEquals("Detta är en text", actualPanelHeader);
    }


    @When("A comment is added")
    public void addComment() {
        asidePO.clickOnOverviewListPlus(COMMENT);
        projectPO.addComment("What you really should know about this project is...");
    }

    @Then("in list there's {int} comment")
    public void commentListMainText(final int numberOfComments) {
        Aside aside = asidePO.getAside(OVERVIEW);

        String expectedCommentItemText = numberOfComments + " Comments";
        String actualCommentItemText = aside.getOverviewItemByIconType(IconType.COMMENTS)
                .getText();

        assertEquals(expectedCommentItemText, actualCommentItemText,
                () -> "expectedCommentItemText/actualCommentItemText: " + expectedCommentItemText + "/" + actualCommentItemText);
    }

    @When("I set the project toggle to Inactive from project map icon")
    public void changeProjectToggleFromMapPanel() {
        //remove all filters so inactive projects are shown on map
        filterPO.changeFilter("All projects");

        mapPO.changeProjectActivityToggleFromMapIcon(context().getProject());
    }

    @When("I set the project toggle to Inactive from project list")
    public void iSetTheProjectToggleToInactiveFromDetailsUrl() {
        //Navigate to projects
        Navigate.company().projects().get();

        //remove all filters so inactive projects are shown on map
        filterPO.changeFilter("All projects");

        //make search for the project, because the list has dynamic DOM
        asidePO.makeSearchInAside(context().getProject().getName());
        //find the item we want to click on
        asidePO.clickOnThisAsideItem(context().getProject().getName());

        //as we are in .../company/projects/{projectID}}/details we don't need to supply projectId.
        projectPO.changeProjectActivityToggleFromDetailsView();
    }

    @When("I add a comment")
    public void iAddAComment() {
        asidePO.clickOnOverviewListPlus(COMMENT);

        String c = Randomizer.randomString(8);
        projectPO.addComment(c);

        CommentBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.COMMENT,
                CommentBuilder.class);
        builder.withComment(c).build();
        Comment newComment = builder.getProvider();
        context().addComment(newComment);

        PlaywrightActions.sleep(2);
    }

    @Then("the new comment should be visible in Comment list and in Comments panel")
    public void theNewCommentShouldBeVisibleInCommentListAndInCommentsPanel() {
        String expectedTopCommentList = context().getLastComment().getComment();

        List<OverviewItem> listItems = asidePO.getAside(OVERVIEW).getOverviewItems();
        OverviewItem commentItem = listItems.stream().filter(item -> item.getText().contains("Comments")).findFirst().get();

        String actualTopCommentList = commentItem.getSubText();

        assertEquals(expectedTopCommentList,actualTopCommentList,
                () -> "expectedTopCommentList/actualTopCommentList:" + expectedTopCommentList + "/" + actualTopCommentList);

        // Now check that the same comment is in the panel
        String actualTopCommentPanel = projectPO.getCommentFromPanel(1);

        assertEquals(expectedTopCommentList,actualTopCommentPanel,
                () -> "expectedTopCommentList/actualTopCommentPanel:" + expectedTopCommentList + "/" + actualTopCommentPanel);
    }

    @And("when I remove the new comment the previous comment should appear in list and panel")
    public void whenIRemoveTheNewCommentThePreviousCommentShouldAppearInListAndPanel() {
        String secondTopCommentInPanel = projectPO.getCommentFromPanel(2);

        projectPO.deleteTopComment();

        List<OverviewItem> listItems = asidePO.getAside(OVERVIEW).getOverviewItems();
        OverviewItem commentItem = listItems.stream().filter(item -> item.getText().contains("Comments")).findFirst().get();

        String actualTopCommentList = commentItem.getSubText();

        assertEquals(secondTopCommentInPanel, actualTopCommentList,
                () -> "secondTopCommentInPanel/actualTopCommentList:" + secondTopCommentInPanel + "/" + actualTopCommentList);


        String actualTopCommentPanel = projectPO.getCommentFromPanel(1);
        assertEquals(secondTopCommentInPanel, actualTopCommentPanel,
                () -> "secondTopCommentInPanel/actualTopCommentPanel:" + secondTopCommentInPanel + "/" + actualTopCommentPanel);
    }

    @Then("a panel with all devices should appear")
    public void aPanelWithAllDevicesShouldAppear() {
        // Check if menu pops up from in .../project/10523/measure_points/create
        assertTrue(projectPO.isMenuPanelPresent(),
                "Connectable devices menu did not pop up.");
    }

    @When("I search for {string}")
    @When("I make a search with {string}")
    public void iMakeASearch(String searchPhrase) {
        asidePO.makeSearchInAside(searchPhrase);
    }

    @When("I input search {string}")
    public void inputSearch(String searchPhrase) {
        asidePO.inputSearchPhrase(searchPhrase);
    }

    @And("I set blast standard to project")
    public void iSetBlastStandardToProject() {
        ProjectBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.PROJECT,
                ProjectBuilder.class);
        builder.setProvider(context().getProject());
        builder.withBlastStandard("SS4604866").build();

        ProjectApi.updateProject(context().getProject().getId(), builder.buildJson());

        Navigate.refreshBrowser();
        PlaywrightActions.sleep(2);
    }


    @Then("project dropdown menu {string} have Show active only checkbox")
    public void projectDropdownMenuShallNotHaveShowActiveOnlyCheckbox(String expectedOutcome) {
        projectPO.clickOnHeaderDropdown();
        boolean checkboxExist = projectPO.findShowActiveOnlyCheckbox();

        switch (expectedOutcome) {
            case "shall not" -> assertFalse(checkboxExist);
            case "shall" -> assertTrue(checkboxExist);
            default -> throw new IllegalStateException("Unexpected value: " + expectedOutcome);
        }
    }

    @Then("I shall not be able to see Create project button")
    public void iShallNotBeAbleToSeeCreateProjectButton() {
        List<OverviewItem> listItems = asidePO.getAside().getOverviewItems();

        OverviewItem projectItem = listItems.stream()
                .filter(item -> item.getText().contains("Projects"))
                .findFirst().get();
        assertNull(projectItem.getRightButton());
    }

    // Create project - Bulk action
    @Then("selected devices are listed in Create measuring points")
    public void selectedDevicesAreListedInCreateMeasuringPoints(DataTable table) {
        PlaywrightActions.sleep(1);
        String currentUrl = Navigate.getCurrentUrl();
        assertTrue(currentUrl.contains("company/devices/create_project"),
                "Not at company/devices/create_project but '" + currentUrl + "'");

        List<String> expectedMpDevices = new ArrayList<>();

        // Adapt expected device so that we can compare with actual
        //todo: när jag ändrat i listPO.getAllListItems så den klarar checkbox, byt till den då.
        table.row(0).forEach(item -> {
            expectedMpDevices.add(item + " #" + DeviceProperties.getConnectedSerial(item));
        });

        // Make sure the body header has correct counter
        String bodyHeader = projectPO.getPanelBodyHeader();
        // Get '2' from '2 devices'
        int deviceCount = Integer.parseInt(bodyHeader.substring(0, bodyHeader.indexOf(" ")));
        assertEquals(expectedMpDevices.size(), deviceCount);

        // Make sure the actualMpDevices contains the correct devices
        List<String> actualMpDevices = projectPO.getBulkActionMpList();
        assertTrue(actualMpDevices.containsAll(expectedMpDevices));
    }

    @When("I create the bulk action project")
    public void iCreateTheBulkActionProject() {
        projectPO.setBulkActionProjectName("qa-bulk-action");
        projectPO.setBulkActionProjectId(Randomizer.randomString(6));
        projectPO.clickButton("create");

        // Give api and UI time to create and navigate to new project
        PlaywrightActions.sleep(6);

        // Add the project to context, so it's auto-deleted after test is done
        int projectId = getProjectIdFromUrl();
        context().setProject(ProjectApi.getProject(projectId));
    }

    /**
     * @return A projectId, eg '179095' from 'https://sigicom.test.indev.sigicom.net/#/project/179095/devices'
     */
    public int getProjectIdFromUrl() {
        String currentUrl = Navigate.getCurrentUrl();
        String projectPrefix = "project/";

        if (!currentUrl.contains(projectPrefix)) {
            throw new IllegalStateException("Url did not contain 'project/'");
        }

        int startIndex = currentUrl.indexOf(projectPrefix) + projectPrefix.length();
        int endIndex = currentUrl.indexOf('/', startIndex);

        return Integer.parseInt(currentUrl.substring(startIndex, endIndex));
    }


    // .../company/overview
    @When("I start creating a project from account overview")
    public void iStartCreatingAProjectFromAccountOverview() {
        // Give api a chance to send the new project to FE
        PlaywrightActions.sleep(2);
        Navigate.refreshBrowser();
        asidePO.clickOnOverviewListPlus(ProviderType.PROJECT);
    }

    @And("for new {string} I use {string} name and non-unique id")
    public void forNewProjectIUseSameNameAndIdAsAnExistingProject(String origin, String nameRule) {
        String projectName = (nameRule.equals("non-unique")) ? context().getProject().getName() : "sdlfjasdö";
        String projectId = context().getProject().getProjectId();

        switch (origin) {
            case "project" -> {
                projectPO.setProjectName(projectName);
                PlaywrightActions.sleep(1);
                projectPO.setProjectId(projectId);
                PlaywrightActions.sleep(1);
                projectPO.setLocation("Älvsjö");
            }
            case "bulk project" ->  {
                projectPO.setBulkActionProjectName(projectName);
                PlaywrightActions.sleep(1);
                projectPO.setBulkActionProjectId(projectId);
                PlaywrightActions.sleep(1);
                projectPO.setBulkActionLocation("Älvsjö");
            }
        }
        // To trigger toast for unique project name we must click Create button
        if (nameRule.equals("unique")) {
            projectPO.clickButton("create");
        }
    }

    @Then("show project {string} in project.tz")
    public void showProjectAsideTimeInProjectTz(String testCase) {
        switch (testCase) {
            case "aside time" -> {
                asidePO.makeSearchInAside("test-auto-project");
                Aside aside = asidePO.getAside();
                Table.TableRow header = aside.getTable().getHeader();

                String expectedFromTime = "2025-01-01 00:00";
                String actualFromTime = aside.getTable().getContent().getFirst().getStringByTableHeader(header, "Active from");

                assertEquals(expectedFromTime, actualFromTime,
                        () -> "aside time: expectedFromTime/actualFromTime: " + expectedFromTime + "/" + actualFromTime);
            }
            case "project details time" -> {
                CompanyProjectDetailsPanel panel = projectPO.getProjectDetailsPanel();

                String expectedFromTime = "2025-01-01 00:00";
                String actualFromTime = panel.getSummaryPanel().get("Active from");

                assertEquals(expectedFromTime, actualFromTime,
                        () -> "project details time: expectedFromTime/actualFromTime: " + expectedFromTime + "/" + actualFromTime);
            }
            case "project settings time" -> {
                ProjectSettingPanel panel = projectPO.getProjectSettingsPanel();

                String expectedFromTime = "2025-01-01";
                // Eg. "Active from: 2020-01-03, Active to: 2034-08-22 15:45, Time zone: Europe/Stockholm, Blast standard: SS4604866"
                String projectDetailsText = panel.getProjectDetails().getSubText();

                assertTrue(projectDetailsText.contains(expectedFromTime),
                        () -> "project settings time: projectDetailsText: " + projectDetailsText);
            }
            case "project settings general time" -> {
                ProjectSettingsDetailsPanel panel = projectPO.getProjectSettingsGeneralPanel();

                String expectedFromTime = "2025-01-01 00:00";
                String actualDate = panel.getTimeFrameWrapper().getTimeFrame().getFromDate().getValue();
                String actualTime = panel.getTimeFrameWrapper().getTimeFrame().getFromTime().getValue();
                String actualFromTime = actualDate + " " + actualTime;

                assertEquals(expectedFromTime, actualFromTime,
                        () -> "project settings general time, expectedFromTime/actualFromTime: " + expectedFromTime + "/" + actualFromTime);
            }
            default -> throw new IllegalArgumentException("Unsupported testCase: " + testCase);
        }
    }
}
