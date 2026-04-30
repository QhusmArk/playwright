package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.project.ProjectUsersManagePanel;
import com.example.playwright.components.panels.user.UserCreatePanel;
import com.example.playwright.components.parts.PanelListItem;
import com.example.playwright.components.parts.SearchBox;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.ToggleField;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.ArrayList;
import java.util.List;

public class UserPO extends CommonPO {
    private static final String DELETE = "//div[@data-qa-id='delete']";
    private static final String TICKBOX_CONFIRM_DELETE = "//div[@role='checkbox']";
    private static final String BUTTON_CANCEL_DELETE = "//button[@data-qa-id='cancel']";
    private static final String BUTTON_FINAL_DELETE = "//button[@data-qa-id='confirm']";

    //    .../users/{user_id}/settings
    private static final String INPUT_FIRST_NAME = "//input[@data-qa-id='first_name']";
    private static final String INPUT_LAST_NAME = "//input[@data-qa-id='last_name']";
    private static final String INPUT_E_MAIL = "//input[@data-qa-id='email']";
    private static final String DROPDOWN_COUNTRY_CODE = ""; //Not implemented
    private static final String COUNTRY_CODE_SWEDEN = ""; //Not implemented
    private static final String INPUT_COUNTRY_CODE = ""; //Not implemented
    private static final String INPUT_MOBILE_NUMBER = ""; //Not implemented
    private static final String DROPDOWN_ROLE = "//div[@data-qa-id='role']"; //Not implemented
    private static final String DROPDOWN_LANGUAGE = "//*[@data-qa-id='language']";
    private static final String BUTTON_SAVE = "//button[@type='submit']";

//    protected final SeleniumApi selenium;
//
//    public UserPO() {
//        this.selenium = PlaywrightActions.getInstance();
//    }

    //settings
    public String getSettingsCurrentLanguage() {
        return actions().findOneElementsText(DROPDOWN_LANGUAGE);
    }

    public List<String> getRoleDropdownContent() {
        actions().makeClick("//div[@data-qa-id='create-user-panel'] //div[@data-qa-id='role']");
        return actions().findManyElementsTexts("//div[@role='listbox'] //div[@role='option']");
    }

    public List<String> getSelectableLanguages() {
        actions().makeClick(DROPDOWN_LANGUAGE);
        return actions().findManyElementsTexts("//div[@role='listbox'] //div[@class='q-item__label']");
    }

    public List<String> getDropdownContent(String dropdown) {
        String dropdownTextPath = switch (dropdown) {
            case "role" -> DROPDOWN_ROLE;
            case "language" -> DROPDOWN_LANGUAGE;
            default -> throw new IllegalStateException("Unexpected dropdown: " + dropdown);
        };
        actions().makeClick(dropdownTextPath);
        PlaywrightActions.sleep(1);
        return actions().findManyElementsTexts("//div[@role='listbox'] //div[@class='q-item__label']");
    }

    // .../users/user_id/details
    public void deleteUser() {
        actions().makeClick(DELETE);
        actions().makeClick(TICKBOX_CONFIRM_DELETE);
        actions().makeClick(BUTTON_FINAL_DELETE);
    }

    // AsideFunction
    public void clickAsideHeaderPlusButton() {
        actions().makeClick(TOP_PLUS_SIGN);
    }

    // .../users/create
    public void createNewUser(String fname, String lname, String email, boolean sendEmail) {
        createNewUser(fname, lname, email, null, sendEmail, null, null);
    }

    // .../users/create
    public void createNewUser(String fname, String lname, String email, String role, boolean sendEmail, List<String> projectAccessNames, String messageRuleAccessName) {
        //enter mandatory data
        actions().clearAndType(INPUT_FIRST_NAME, fname);
        actions().clearAndType(INPUT_LAST_NAME, lname);

        if (email != null) {
            actions().clearAndType(INPUT_E_MAIL, email);
        }
        if (role != null) {
            selectDropdownByHeader("Role *",  role);
        }

        if (!sendEmail) {
            actions().makeClick("//div[@aria-label='Send e-mail invite']");
        }

        // To be implemented
//        if (projectAccessNames != null) {}

        if (messageRuleAccessName != null) {
            String mrAccessPath = "//div[@data-qa-id='create-user-panel-body']/div/div[3] //div[@data-qa-id='list-item' and .//span[contains(text(),'"+messageRuleAccessName+"')]] ";
            // Assume the message rule access toggle is in expected state
            actions().makeClick(mrAccessPath);
        }

        //click save
        actions().makeClick(BUTTON_SAVE);

        // It takes a while to create a user and redirect to /details.
        PlaywrightActions.sleep(2);
    }

    // ..company/users/id/settings
    public void searchForAProjectFromUserSettings(String projectName) {
        // Make search for project
        String searchPath = "//input[@placeholder='Search...']";
        actions().clearAndType(searchPath, projectName);
        PlaywrightActions.sleep(1); // to get search result time to adjust

        String searchResultPath = "//div[@data-qa-id='user-settings-panel-body'] //div[@data-qa-id='list-item']";
        int foundProjects = actions().countHowManyElements(searchResultPath);

        if (foundProjects == 1) {
            //Click on the matching project toggle slider
            actions().makeClick("//div[@data-qa-id='list-item'] //div[@role='switch']");
            actions().makeClick("//button[@data-qa-id='user-settings-panel-btn-save']");
        } else {
            throw new IllegalStateException("Too many search hits to know which one to save to user.");
        }
        PlaywrightActions.sleep(2);
    }

    // .../company/users/1891/settings
    public String checkTextOfShowSelectedToggle() {
        return actions().findOneElementsText("//div[@role='switch' and contains(@aria-label, 'Show selected')]");
    }

    // todo: fixa dynamisk listhantering
    // .../company/users/1891/settings
    public List<String> getShowSelectedToggleProjectNames() {
        if (actions().elementExistAndVisible("//div[@data-qa-id='user-settings-panel-body'] //div[@data-qa-id='list-item']", false,0)) {
            return actions().findManyElementsTexts("//div[@data-qa-id='user-settings-panel-body'] //div[@data-qa-id='list-item']");
        } else {
            return new ArrayList<>();
        }
    }

    public void changeShowSelectedToggleState() {
        actions().makeClick("//div[@role='switch' and contains(@aria-label, 'Show selected')]");
    }

    // .../users/user_id/settings
    // .../users/create
    public List<String> getProjectNamesForProjectsUserHasAccessTo() {
        // toggle Show Selected to ON (default is OFF)
        actions().makeClick("(//form //div[@data-qa-id='create-user-panel-body']/div/div)[2] //div[@role='switch']");
        // läs in listan

        int projectsUserHasAccessTo = actions().countHowManyElements("//form //div[@data-qa-id='list-item']");

        List<String> projectNames = new ArrayList<>();

        for (int i = 1; i <= projectsUserHasAccessTo; i++) {
            String projectName = actions().findOneElementsText("(//form //div[@data-qa-id='list-item'])["+i+"] //span");
            projectNames.add(projectName);
        }

        return projectNames;
    }


    public UserCreatePanel getUsersCreatePanel() {
        UserCreatePanel ucp = new UserCreatePanel();

        ucp.setPanelHeader(getPanelHeader());

// ********************

        FieldWrapper general = getFieldWrapperCommonPartsByHeader("General");
        ucp.setGeneralWrapper(general);

        general.addContent(getInputFieldByHeader("First name *"));
        general.addContent(getInputFieldByHeader("Last name *"));
        general.addContent(getInputFieldByHeader("E-mail *"));

        general.addContent(getDropdownByName("Mobile phone"));
        general.addContent(getInputFieldByPath("//label[.//div[text()='Mobile phone']]/following::label"));

        general.addContent(getDropdownByName("Role *"));
        general.addContent(getDropdownByName("Language"));
        general.addContent(getDropdownByName("Customer company"));

        general.addContent(getAriaLabelToggle("right", "INFRA Net access"));
        general.addContent(getAriaLabelToggle("right", "Send e-mail invite"));

// ********************

        FieldWrapper projectAccess = getFieldWrapperCommonPartsByHeader("Project access");
        ucp.setProjectAccessWrapper(projectAccess);

        String projectAccessPath = "//form //div[@data-qa-id='create-user-panel-body']/div/div[position()=2]";

        SearchBox projectSearchBox = getSearchBox(projectAccessPath + " //label[.//input[contains(@placeholder,'Search...')]]");
        projectAccess.addContent(projectSearchBox);

        ToggleField showSelected = getToggle("right", projectAccessPath);
        projectAccess.addContent(showSelected);

        List<PanelListItem> projectList = getPanelListItems(false, projectAccessPath + " //div[@data-qa-id='list-item']");
        projectAccess.addContent(projectList);

// ********************

        if (actions().getCurrentUrl().contains("project")) {
            String messageRuleAccessPath = "//form //div[@data-qa-id='create-user-panel-body']/div/div[position()=3]";

            FieldWrapper messageRuleAccess = getFieldWrapperCommonPartsByHeader("Message rules in current project");
            ucp.setMessageRuleAccessWrapper(messageRuleAccess);

            SearchBox mrSearchBox = getSearchBox(messageRuleAccessPath + " //label[.//input[contains(@placeholder,'Search...')]]");
            messageRuleAccess.addContent(mrSearchBox);

            ToggleField showSelectedMessageRules = getToggle("right", messageRuleAccessPath);  // todo: texten fångades inte...
            messageRuleAccess.addContent(showSelectedMessageRules);

            List<PanelListItem> messageRuleList = new ArrayList<>();

            // This is the first row in the list, but has only the selectAllToggle
            PanelListItem selectAllPanelListItem = new PanelListItem();
            // The selectAllMrToggle has no explanation text, but is still an PanelListItem.
            ToggleField selectAllToggleField = completeGetToggleField("none", messageRuleAccessPath + " //div[@class='q-virtual-scroll__content'] //div[@role='switch']");
            selectAllPanelListItem.setToggleField(selectAllToggleField);

            // Then get all the message rules
            List<PanelListItem> selectableMessageRules = getPanelListItems(true, messageRuleAccessPath + " //div[@data-qa-id='list-item']");

            // First add the selectAllToggle
            messageRuleList.add(selectAllPanelListItem);
            // Then add all the message rules
            messageRuleList.addAll(selectableMessageRules);

            messageRuleAccess.addContent(messageRuleList);
        }

// ********************

        return ucp;
    }

    public ProjectUsersManagePanel getProjectUsersManagePanel() {
        ProjectUsersManagePanel pump = new ProjectUsersManagePanel();

        pump.setPanelHeader(getPanelHeader());
         pump.setPreface(getPreface());

        // ********************

        FieldWrapper usersWrapper = getFieldWrapperCommonPartsByHeader("Users");
        pump.setUsersWrapper(usersWrapper);

        usersWrapper.addContent(getSearchBox());

        // ********************

        FieldWrapper clientPlusWrapper = getFieldWrapperCommonPartsByHeader("Client+");
        pump.setClientPlusWrapper(clientPlusWrapper);

        Table clientPlusList = getFieldWrapperTable("Client+");
        clientPlusWrapper.addContent(clientPlusList);

        // ********************

        FieldWrapper clientWrapper = getFieldWrapperCommonPartsByHeader("Client");
        pump.setClientWrapper(clientWrapper);

        Table clientList = getFieldWrapperTable("Client");
        clientWrapper.addContent(clientList);

        // ********************

        FieldWrapper blasterWrapper = getFieldWrapperCommonPartsByHeader("Blaster");
        pump.setBlasterWrapper(blasterWrapper);

        Table blasterList = getFieldWrapperTable("Blaster");
        blasterWrapper.addContent(blasterList);

        return pump;
    }


}
