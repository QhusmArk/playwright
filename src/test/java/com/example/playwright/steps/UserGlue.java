package com.example.playwright.steps;

import com.example.api.endpoints.MessageRuleApi;
import com.example.api.endpoints.UserApi;
import com.example.api.models.user.User;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.builders.BuilderFactory;
import com.example.helpers.builders.UserBuilder;
import com.example.playwright.components.aside.asideItems.listItems.UserItem;
import com.example.playwright.components.panels.user.UserCreatePanel;
import com.example.playwright.components.parts.PanelListItem;
import com.example.playwright.helpers.Navigate;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserGlue extends BaseGlue {

    private User userBeforeChange;

    @And("I open UserProfile")
    public void userProfile() {
        menuPO.openUserMenuAndSelectMenuItem("USER_PROFILE");
    }

    @When("I try to edit my first name")
    public void editUserFirstName() {
        this.userBeforeChange = new User();

        // store name before changing it
        userBeforeChange.setFirstName(upPO.getFirstName());

        // create a new name, enter it and save it
        String value = "AutoTest_" + (int) (Math.random() * 100);
        upPO.enterUserFirstName(value);
        userPO.clickButtonText("Save");
    }

    @Then("the name change request was a {string}")
    public void validateChangedFirstName(String expected) {
        //get the old name
        String previousFirstName = userBeforeChange.getFirstName();
        System.out.println("previousFirstName: " + previousFirstName);
        // Go to UserProfile to grab current data
        menuPO.openUserMenuAndSelectMenuItem("USER_PROFILE");

        //get the new name
        String currentFirstName = upPO.getFirstName();
        System.out.println("currentFirstName: " + currentFirstName);
        // successful namechange = true
        assertNotEquals(previousFirstName, currentFirstName);
    }


    @When("I create a user with email sent")
    public void createUserAndSendEmail() {
        createUser(true);
    }

    @When("I create a user without email sent")
    public void createUserAndDoNotSendEmail() {
        createUser(false);
    }

    public void createUser(boolean sendingEmail) {
        String fname = "qa_fname";
        String lname = "lname";
        String email = "qa_fname.lname@sigicom.com";

        User u = UserApi.getUserByMail(email);
        if (u != null) {
            System.out.println("User already exist. Deleting the user");
            UserApi.deleteUser(u.getId());
        }

        userPO.createNewUser(fname, lname, email, sendingEmail);

        // Update context
        context().setUsers(UserApi.getUsers());
    }

    @And("I create a {string} from project with access to")
    public void iCreateAUserFromProjectWithAccessTo(String role, DataTable dataTable) {
        List<String> mrAccess = dataTable.row(0);

        String fname = "qa_fname";
        String lname = "lname";
        String email = "qa_fname.lname@sigicom.com";

        User u = UserApi.getUserByMail(email);
        if (u != null) {
            System.out.println("User already exist. Deleting the user");
            UserApi.deleteUser(u.getId());
        }

        userPO.createNewUser(fname, lname, email, role, false, null, mrAccess.getFirst());

        // Update context
        context().setUsers(UserApi.getUsers());
        context().setMessageRules(MessageRuleApi.getMessageRules(context().getProject().getId()));
    }

    @When("I create a user without email")
    public void iCreateAUserWithoutEmail() {
        //  go to .../company/users
        Navigate.company()
                .users()
                .get();

        //click on the +-symbol
        userPO.clickAsideHeaderPlusButton();

        userPO.createNewUser("qa_fname", "lname", null, false);
    }

    @When("I delete the new user")
    @And("I can delete the new user")
    public void deleteUserByClicking() {
        //delete the user in the url
        userPO.deleteUser();
    }

    @Then("aside contain the new user")
    public void findUserInAside() {
        //get expected user
        User expectedUser = context().getUsers().getFirst();
        String expectedUserFullName = expectedUser.getFirstName() + " " + expectedUser.getLastName();

        //make aside search to find match
        asidePO.makeSearchInAside(expectedUserFullName);
        // Get all users in list
        List<UserItem> users = asidePO.getAside().getUserItems();

        // Make sure we've only got one user in the list
        assertEquals(1, users.size(),
                () -> "Too many users with same name.");

        String actualFullName = users.getFirst().getName();
        assertEquals(expectedUserFullName, actualFullName,
                () -> "expectedUserFullName/actualFullName" + expectedUserFullName + "/" + actualFullName);
    }

    @And("I am at logged in user's settings page")
    public void goToUserSettingsPage() {
        int userId = getLoggedInUsersId();

        // Navigate to .../company/users/{userId}/settings
        Navigate.company()
                .user(userId)
                .settings()
                .get();
    }

    private int getLoggedInUsersId() {
        //open support dialog and get the current users Id
        userPO.openUserMenuAndSelectMenuItem("SUPPORT");

        return sPO.getUserNumber();
    }

    @And("I change password")
    public void iChangePassword() {
        upPO.changePassword("test", "test");
    }

    @When("I choose a different language")
    public void chooseLanguageSetting() {
        String currentLanguage = upPO.getCurrentLanguage();

        //store language for later comparison
        userBeforeChange.setLanguage(currentLanguage);

        upPO.changeLanguage(currentLanguage);
    }

    @When("I log out")
    public void logOut() {
        userPO.openUserMenuAndSelectMenuItem("LOGOUT");
        // todo: hoppas denna inte behövs i Playwright
//        PropertyUtil.deleteCookieFile();
    }

    @Then("my language has changed")
    public void validateChangedLanguage() {
        //get the previous language
        String previousLanguage = userBeforeChange.getLanguage();

        // Go to UserProfile to grab current data
        userPO.openUserMenuAndSelectMenuItem("USER_PROFILE");

        //get the current language
        String currentLanguage = userPO.getSettingsCurrentLanguage();
        assertNotEquals(previousLanguage, currentLanguage);
    }

    @Then("my language shall not be the same as before")
    public void validateChangeLanguageResult() {
        //get the previous language
        String previousLanguage = userBeforeChange.getLanguage();

        // Go to UserProfile to grab current data
        userPO.openUserMenuAndSelectMenuItem("USER_PROFILE");

        //get the current language
        String currentLanguage = upPO.getCurrentLanguage();

        assertNotEquals(previousLanguage, currentLanguage);

        // Now revert back to English
        if (currentLanguage.equals("Svenska") || currentLanguage.equals("Swedish")) {
            upPO.changeLanguage(currentLanguage);
        }
    }

    @Then("the name change request was a {result}")
    public void validateChangedFirstName(boolean expected) {
        //get the old name
        String previousFirstName = userBeforeChange.getFirstName();

        // Go to UserProfile to grab current data
        userPO.openUserMenuAndSelectMenuItem("USER_PROFILE");

        //get the new name
        String currentFirstName = upPO.getFirstName();

        // successful namechange = true
        assertNotEquals(previousFirstName.equals(currentFirstName), expected);
    }

    @And("I navigate to Support")
    public void support() {
        userPO.openUserMenuAndSelectMenuItem("SUPPORT");
    }

    @When("user language is {string}")
    public void userLanguageIs(String language) {
        User currentUser = UserApi.getCurrentUser();

        String newLanguage = switch (language) {
            case "English" -> "en";
            case "Swedish" -> "sv_SE";
            case "French" -> "fr";
            case "German" -> "de";
            case "Norwegian" -> "nb";
            default -> throw new IllegalArgumentException("Unknown language " + language);
        };

        changeLanguageTo(currentUser, newLanguage);
    }

    /**
     * Uses api to change the language of a user.
     */
    private void changeLanguageTo(User currentUser, String newLanguage) {
//        System.out.println("lang before set: " + currentUser.getLanguage());
        currentUser.setLanguage(newLanguage);
//        System.out.println("lang after set: " + currentUser.getLanguage());

        UserBuilder builder = BuilderFactory.getBuilder(
                BuilderFactory.Providers.USER,
                UserBuilder.class);
        builder.setProvider(currentUser);

        String updatedLanguage = UserApi.updateUser(currentUser.getId(), builder.buildJson()).getLanguage();
//        System.out.println("lang after update: " + updatedLanguage);
//        System.out.println("GET /user: " + UserApi.getUser(currentUser.getId()).getLanguage());
        assertEquals(newLanguage, updatedLanguage,
                () -> "newLanguage/updatedLanguage: " + newLanguage + "/" + updatedLanguage);
    }

    @Then("week starts with {string}")
    public void weekStartsWith(String expectedFirstDayOfWeek) {

        // Closest calendar on account level are Billing Report
        Navigate.company()
                .billingReports()
                .create()
                .get();

        // refresh so new language is loaded
        Navigate.refreshBrowser();
        // select the third drop down selector
        billingPO.selectReportPeriodByPlaceInDropdown(3);

        String actualFirstDayOfWeek = billingPO.openCalendarAndGetFirstDayOfWeek();

        assertEquals(expectedFirstDayOfWeek, actualFirstDayOfWeek,
                () -> "expectedFirstDayOfWeek/actualFirstDayOfWeek: " + expectedFirstDayOfWeek + "/" + actualFirstDayOfWeek);
    }

    @When("I remove the project from the user")
    public void iRemoveTheProjectFromTheUser () {
        Navigate.project(context().getProject().getId())
                .user(context().getUsers().getFirst().getId())
                .settings()
                .get();

        userPO.searchForAProjectFromUserSettings(context().getProject().getName());
    }

    @Then("I am to remain at user details page")
    public void iAmToRemainAtUserDetailsPage() {
        String url = Navigate.getCurrentUrl();
        assertTrue(url.contains("details"));
    }

    /**
     * .../company/users/1891/settings
     */
    @Then("the list of active projects contains {string} active project")
    public void theListOfActiveProjectsContainsActiveProject(String expectedProjects) {
        int expectedProjectsCount = Integer.parseInt(expectedProjects);
        // Flip the toggle (to ON)
        userPO.changeShowSelectedToggleState();

        // Get the string containing active projects user is added to. Eg. 'Show selected (1)'
        String toggleText = userPO.checkTextOfShowSelectedToggle();
        int projectNumberFromShowSelectedToggle = Integer.parseInt(toggleText.substring(toggleText.indexOf("(") + 1, toggleText.indexOf(")")));

        // Get projects name from the list of active projects
        List<String> actualProjectsName = userPO.getShowSelectedToggleProjectNames();
        int actualProjectsCount = actualProjectsName.isEmpty() ? 0 : actualProjectsName.size();

        assertEquals(expectedProjectsCount, projectNumberFromShowSelectedToggle,
                () -> "projectNumberFromShowSelectedToggle: " + projectNumberFromShowSelectedToggle);

        assertEquals(expectedProjectsCount, actualProjectsCount,
                () -> "expectedProjectsCount/actualProjectsCount: " + expectedProjectsCount +  "/" + actualProjectsCount);
    }

    //.../users/manage
    @And("current project is pre-selected for access")
    public void currentProjectIsPreSelectedForAccess() {
        String expectedProjectName = context().getProject().getName();

        List<String> projects = userPO.getProjectNamesForProjectsUserHasAccessTo();
        String actualProjectNameInPreSelectedList = projects.getFirst();

        assertEquals(expectedProjectName, actualProjectNameInPreSelectedList,
                () -> "expectedProjectName/actualProjectNameInPreSelectedList: " + expectedProjectName + "/" + actualProjectNameInPreSelectedList);
    }

    @Then("dropdown contains {string}")
    public void iCanSelectTheseLanguage(String dropdownText, DataTable table) {
        List<String> expected = table.row(0);
        List<String> actual = userPO.getDropdownContent(dropdownText);

        boolean areListsEqual = AssertionHelpers.areTrimmedAndSortedListsIdentical(expected, actual);
        assertTrue(areListsEqual,
                () -> "Lists are not equal."
                        + "\n" + expected
                        + "\n" + actual);
    }

    @Then("the create user panel has a list of projects")
    public void theCreateUserPanelHasAListOfProjects() {
        UserCreatePanel ucp = userPO.getUsersCreatePanel();

        // As the list is dynamic we cannot match all projects in the account with the numbers of projects in the list
        assertNotNull(ucp.getProjectAccessWrapper().getListOfType(PanelListItem.class));
        assertFalse(ucp.getProjectAccessWrapper().getListOfType(PanelListItem.class).isEmpty());
    }

    @Then("the create user panel has a list of message rules")
    public void theCreateUserPanelHasAListOfMessageRules() {
        UserCreatePanel ucp = userPO.getUsersCreatePanel();

        // The 'select all toggle' is part of list-items together with projects. So even if we can read all projects, we need to add +1 to expected count.
        int expectedMrCount = MessageRuleApi.getMessageRules(context().getProject().getId()).size() + 1;
        int actualMrCount = ucp.getMessageRuleAccessWrapper().getListOfType(PanelListItem.class).size();

        assertEquals(expectedMrCount, actualMrCount,
                () -> "expectedMrCount/actualMrCount: " + expectedMrCount + "/" + actualMrCount);
    }
}
