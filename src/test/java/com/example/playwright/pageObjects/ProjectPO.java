package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.CompanyProjectDetailsPanel;
import com.example.playwright.components.panels.ProjectSettingsDetailsPanel;
import com.example.playwright.components.panels.project.ProjectSettingPanel;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectPO extends CommonPO {

    //project details item
    private static final String PROJECT_TOGGLE = "//div[text()='Inactive' or text()='Active']";
    private static final String PROJECT_CLOSE = "//*[text() = 'close']";

    //project settings
    private static final String SETTINGS_MENU_PROJECT_DETAILS = "//*[@data-qa-id='project_project_settings_general']";

    //project settings/general
    private static final String SETTINGS_GENERAL_INPUT_NAME = "//*[@data-qa-id='name']";
    private static final String SETTINGS_GENERAL_INPUT_DESCRIPTION = "//*[@data-qa-id='description']";

    // .../company/projects + plus button
    public void createProject(String projectName, String projectID, String location) {
        // Click create button
        clickButton("list header plus");
        setProjectName(projectName);
        setProjectId(projectID);
        setLocation(location);
        // Click create button
        clickButton("create");
    }

    // .../company/overview + plus button
    public void setProjectName(String projectName) {
        // Type project name
        actions().clearAndType("//*[@data-qa-id='name']", projectName);
    }

    // .../company/overview + plus button
    public void setProjectId(String projectID) {
        // Type project id
        actions().clearAndType("//*[@data-qa-id='project_id']", projectID);
    }

    // .../company/overview + plus button
    public void setLocation(String location) {
        actions().elementExistAndVisible("//*[@data-qa-id='find-location-field']//input", true, 4);
        // Type location and select the first row of addresses suggested String OpenStreetMap
        actions().clearAndType("//*[@data-qa-id='find-location-field']//input", location);
        actions().makeClickOnSomeElements("//*[@data-qa-id='find_location_result_item']", 1);
    }

    //  ../company/devices/create_project
    public void setBulkActionProjectName(String projectName) {
        // Type project name
        actions().clearAndType("//form //input[contains(@aria-label, 'Project name *')]", projectName);
    }

    //  ../company/devices/create_project
    public void setBulkActionProjectId(String projectID) {
        // Type project id
        actions().clearAndType("//form //input[contains(@aria-label, 'Project ID *')]", projectID);
    }

    //  ../company/devices/create_project
    public void setBulkActionLocation(String location) {
        // Type location and select the first row of addresses suggested String OpenStreetMap
        actions().clearAndType("//form //input[contains(@aria-label, 'Find location')]", location);
        actions().makeClickOnSomeElements("//*[@data-qa-id='find_location_result_item']", 1);
    }

    //    ../project/118744/settings
    public void deleteProject() {
        clickProjectSettings("Delete");

        // todo: the clicks belong to MenuPanelPO or PopupMenuPO
        actions().makeClick("//*[text()='I understand']");
        actions().makeClick("//span[contains(text(),'Delete')]");
        // Give GIU some time to catch up
        PlaywrightActions.sleep(2);
    }

    // todo: interaction with project settings belongs to SettingsPO
    public void clickProjectSettings(final String link) {
        if (link.equals("Delete")) {
            actions().makeClick("//form //div[contains(text(),'" + link + "')]");
        } else {
            actions().makeClick("//form //a //div[contains(text(),'" + link + "')]");
        }
    }

    // todo: this pbly belongs to MapPO
    public boolean existOnMap(final String projectName) {
        String projectMapMarker = "//img[@src='svg/project/map-marker-project-on.svg'][@title='"
                + projectName + "']";
        return actions().elementExistAndVisible(projectMapMarker, true);
    }

    /**
     * Method starts from url .../projectId/settings
     * @param description
     */
    public void saveNewDescription(final String description) {
        actions().makeClick(SETTINGS_MENU_PROJECT_DETAILS);
        actions().clearAndType(SETTINGS_GENERAL_INPUT_DESCRIPTION, description);
        PlaywrightActions.sleep(1);
        if (actions().elementExistAndVisible(SUBMIT, true, 0)) {
            actions().makeClick(SUBMIT);
        }
        PlaywrightActions.sleep(1);
    }

    public void addComment(String comment) {
        actions().clearAndType("//div[@class='q-card q-pa-sm'] //textarea", comment);

        actions().makeClick("//span[text()='Save']");
    }

    /**
     * Method starts from url .../projectId/settings
     * @param name
     */
    public void changeName(final String name) {
        //click on Project Details
        actions().makeClick(SETTINGS_MENU_PROJECT_DETAILS);
        //input project name
        actions().clearAndType(SETTINGS_GENERAL_INPUT_NAME, name);
        //Save the input
        actions().makeClick("//form //button[@type='submit']");

        //validate that we are redirected to /settings-page
        isPanelHeaderText("Project settings");
    }

    /**
     * Gets the project name from page header drop down.
     */
    public String getTextInPageHeaderDropdown() {
        return actions().findOneElementsText("((//header //button)[2] //span)[2] //div");
    }

    public void changeProjectActivityToggleFromDetailsView() {
        //click on toggle
        actions().makeClick(PROJECT_TOGGLE);
        //close the panel
        actions().makeClick(PROJECT_CLOSE);
        //give some room for GUI to catch up
        PlaywrightActions.sleep();
    }

    public String getCommentFromPanel(int place) {
        String commentsPath = "(//div[@data-qa-id='panel-body'] //div[@class='q-item q-item-type row no-wrap col-12'])["+ place +"] //div[2] //div[1]";
        return actions().findOneElementsText(commentsPath);
    }

    // comments panel is open
    public void deleteTopComment() {
        String topCommentMenuButtonPath = "//div[@data-qa-id='panel'] //i[text()='more_vert']";
        actions().makeClick(topCommentMenuButtonPath);

        actions().makeClick("//div[@role='menu'] //div[text()='Delete']");
        actions().makeClick("//div[@role='dialog'] //span[text()='Delete']");
    }

    /**
     * Method that works for positive and negative tests.
     * @return true if the action was possible.
     */
    public boolean setDefaultPrice(String price) {
        PlaywrightActions.sleep(2);
        actions().clearAndType("//input[@data-qa-id='default_price']", price);
        actions().makeClick("//button[@type='submit']");
        PlaywrightActions.sleep(3);
        return true;
    }

    public void clickOnHeaderDropdown() {
        actions().makeClick("(//header //button)[2]");
    }

    public boolean findShowActiveOnlyCheckbox() {
        return actions().elementExistAndVisible("//div[@role='menu'] //div[contains(text(), 'Show active only')]", false, 1);
    }

    /**
     *  .../company/devices/create_project
     * @return a list of devices in the create-measuring-points-table
     */
    public List<String> getBulkActionMpList() {
        List<String> actualMpDevices = new ArrayList<>();

        int mpRows = actions().countHowManyElements("//form //table //tr[@class='q-tr ']");
        for (int row = 1; row <= mpRows; row++) {
            String deviceText = actions().findOneElementsText("((//form //table //tr[@class='q-tr '])["+row+"] //td)[2]");
            actualMpDevices.add(deviceText);
        }
        return actualMpDevices;
    }

    // ...project/id/settings
    public ProjectSettingPanel getProjectSettingsPanel() {
        ProjectSettingPanel psp = new ProjectSettingPanel();

        psp.setPanelHeader(getPanelHeader());
         psp.setPreface(getPreface());

        SettingsItem projectDetails = getSettingsItemByDataQaId("settings_general");
        psp.setProjectDetails(projectDetails);

        SettingsItem location = getSettingsItemByDataQaId("settings_location");
        psp.setLocation(location);

        SettingsItem mapSettings = getSettingsItemByDataQaId("settings_map");
        psp.setMapSettings(mapSettings);

        SettingsItem agendas = getSettingsItemByDataQaId("settings_agendas");
        psp.setAgendas(agendas);

        SettingsItem delete = getSettingsItemByDataQaId("delete");
        psp.setDelete(delete);

        return psp;
    }

    // ...project/id/details
    public CompanyProjectDetailsPanel getProjectDetailsPanel() {
        CompanyProjectDetailsPanel pd = new CompanyProjectDetailsPanel();

        pd.setPanelHeader(getPanelHeader());

        Map<String, String> summaryPanel = getProjectSummaryPanel();
        pd.setSummaryPanel(summaryPanel);

         pd.setPreface(getPreface());

        SettingsItem comments = getSettingsItemByDataQaId("company_project_comments");
        pd.setComments(comments);

        SettingsItem settings = getSettingsItemByDataQaId("company_project_settings");
        pd.setSettings(settings);

        return pd;
    }

    private Map<String, String> getProjectSummaryPanel() {
        Map<String, String> summaryPanel = new HashMap<>();
        List<String> fields = List.of(
                "Project ID",
                "Description",
                "Time zone",
                "Active from",
                "Active to",
                "Blast standard"
        );

        for (String field : fields) {
            summaryPanel.put(field, actions().findOneElementsText("(//form //label[.//div[text()='"+field+"']] //div)[5]"));
        }

        return summaryPanel;
    }

    public ProjectSettingsDetailsPanel getProjectSettingsGeneralPanel() {
        ProjectSettingsDetailsPanel panel = new ProjectSettingsDetailsPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        FieldWrapper general = getFieldWrapperCommonPartsByHeader("General");
        panel.setGeneralWrapper(general);
        general.addContent(getInputFieldByHeader("Name *"));
        general.addContent(getInputFieldByHeader("Project ID *"));
        general.addContent(getDropdownByName("Time zone"));
        general.addContent(getDropdownByName("Project maintainer"));
        general.addContent(getInputFieldByHeader("Description"));
        boolean mpPriceExist = actions().elementExistAndVisible(" //label[.//div[text()='Default measuring point price']]", false, 0);
        if (mpPriceExist) {
            general.addContent(getInputFieldByHeader("Default measuring point price"));
        }

        FieldWrapper customerSettings = getFieldWrapperCommonPartsByHeader("Customer settings");
        panel.setCustomerSettingsWrapper(customerSettings);
        customerSettings.addContent( getDropdownByName("Customer"));
        customerSettings.addContent(getDropdownByName("Customer contact"));

        FieldWrapper timeFrame = getFieldWrapperCommonPartsByHeader("Time frame");
        panel.setTimeFrameWrapper(timeFrame);
        timeFrame.addContent(getTimeFrame());

        FieldWrapper blastPart = getFieldWrapperCommonPartsByHeader("Activate blast part");
        panel.setActiveBlastPartWrapper(blastPart);
        blastPart.addContent(getDropdownByName("Blast standard"));

        return panel;
    }


}
