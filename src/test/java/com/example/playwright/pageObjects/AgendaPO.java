package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.project.AgendaAddTimeslotPanel;
import com.example.playwright.components.panels.project.AgendaSettingsPanel;
import com.example.playwright.components.panels.project.ProjectSettingsAgendaPanel;
import com.example.playwright.components.panels.project.ProjectSettingsAgendasPanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.components.parts.popups.SelectAgendaPopup;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.playwright.helpers.enums.ColourSchema.DARK;
import static com.example.playwright.helpers.enums.ColourSchema.POSITIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgendaPO extends CommonPO {

    // agenda-settings
    private static final String AGENDA_DROPDOWN = "//div[@class='col-xs-12 col-lg-9'] //*[@data-qa-id='agenda_id']";
    private static final String ADD_TIMESLOT_BUTTON = "//form //*[text()='Add time slot']";

    public void clickOnAgendaPopupItem(String projectName, String agendaName) {
        actions().makeClick("//div[@data-qa-id='list-item'][.//span[text()='"+projectName+"'] and .//div[text()='"+agendaName+"']]");
    }
    private static final String INFO = "(//form//div[@class='q-item__section column q-item__section--main justify-center'])[1]";
    private static final String SAVE_BUTTON = "//*[text()='Save']";

    private static final String DELETE_BUTTON = "//span[text()='Delete']";

    public String getMessage() {
        return actions().findOneElementsText(INFO);
    }

    /**
     * Click links that navigate to popup Create agenda.
     * Starts at url: .../project/id/settings
     */
    public void openCreateAgendaPanel() {
        // Navigate to .../project/id/settings/agendas
        actions().makeClick("//span[text()='Project settings']");
        // Navigate to .../project/id/settings/agendas
        actions().makeClick("//a //div[text()='Agendas']");
        // Click create Agenda button
        actions().makeClick("//form //*[text()='Create agenda']");
    }

    /**
     * Don't need projectId as we came here through URL.
     */
    public void createAgenda(final String agendaName) {
        actions().makeClick("//form //*[text()='Create agenda']");
        actions().clearAndType("//div[@class='q-card size q-pt-sm'] //input", agendaName);
        clickSaveButton();
    }

    public void renameAgenda(final String value) {
        actions().makeClick("//div[text()='Agenda name']");
        createAgendaPopup(value);
    }

    /**
     * Popup to write Agenda name and click Save.
     */
    public void createAgendaPopup(final String name) {
        actions().clearAndType("//div[@class='q-card size q-pt-sm'] //input", name);
        clickSaveButton();
    }

    /**
     * url = .../project/id/settings/agendas/id
     */
    public void deleteAgenda() {
        actions().makeClick("//form //*[text()='Delete']");
        actions().makeClick("//*[text()='I understand']");
        actions().makeClick("//span[text()='Delete']");
    }

    /**
     * url = .../project/id/settings/agendas/id
     */
    public void selectTimeslot(String name) {
        // Click on the timeslot we want to update
        actions().makeClick("//form //table//tr//td[contains(text(),'" + name + "')]");
    }

    /**
     * url = .../project/id/settings/agendas/id
     */
    public void editTimeSlotToAndFromTime(final String from, final String to) {
        // Validate that panel has Edit Time Slot in header
        String panelHeader = "//*[@data-qa-id='project-add-timeslot-panel']";
        actions().elementExistAndVisible(panelHeader);

        // Set to- and from time
        setTimeslotTimes(from, to);

        // Save new to- and from time
        clickSaveButton();
    }

    /**
     * Starts at url: .../project/id/settings/agendas/id
     */
    public void addTimeslotFromId(final String name, final String from, final String to, final String[] days) {
        addTimeslot(name, from, to, days);
    }

    /**
     * Starts at url: .../project/id/settings/agendas/id
     */
    public void addTimeslot(final String name, final String from, final String to, final String[] days) {
        actions().makeClick(ADD_TIMESLOT_BUTTON);

        // Type timeslot name
        actions().clearAndType("//*[@data-qa-id='name']", name);

        // Set to- and from time
        setTimeslotTime("time_from", from);

        PlaywrightActions.sleep(2);

        // todo: Type timeslot name again to see if the problem have to do with state of element
        actions().clearAndType("//*[@data-qa-id='name']", name);

        setTimeslotTime("time_to", to);

        AgendaAddTimeslotPanel panel = getAgendaAddTimeslotPanel();

        // Set the days that the timeslot should be active on
        assertDaysAreSelected(panel.getRepeatWrapper(), days);

        clickSaveButton();
    }

    private void setTimeslotTime(String type, String value) {
        String dropDownControllerPath = "//form //div[@data-qa-id='"+type+"']/ancestor::label";

        PlaywrightActions.sleep(2);
        System.out.println("Setting '"+type+"' to '" + value + "'");

        boolean dropdownExist = actions().elementExistAndVisible(dropDownControllerPath, false, 3);
        System.out.println("Dropdown '" + type + "' found");

        // Make the dropdown expand
        actions().makeClick(dropDownControllerPath);

//        actions().makeJavaScriptClick("//div[@role='listbox'] //div[@data-qa-id='"+value+"']/ancestor::div[@role='option']");
        actions().makeClick("//div[@role='listbox'] //div[@data-qa-id='"+value+"']/ancestor::div[@role='option']");
    }

    /**
     * Method make sure that the days in the array will be selected when method ends.
     * The buttons not in the days-array will be deselected.
     */
    private void assertDaysAreSelected(FieldWrapper dayWrapper, String[] days) {
        // 7 day buttons, make sure each button has state
        dayWrapper.getButtons().forEach(button -> {

            boolean buttonShouldBeSelected = Arrays.stream(days).toList().contains(button.getText());
            if (buttonShouldBeSelected) {

                boolean buttonIsNotSelected = button.getBackgroundColour().equals(DARK);
                if (buttonIsNotSelected) {
                    // If button is in the list, and not selected -> select it
                    clickButton(button.getText());
                }

            } else {

                boolean buttonIsSelected = button.getBackgroundColour().equals(POSITIVE);
                if (buttonIsSelected) {
                    // If a button is not in the list, and is selected -> deselect it
                    clickButton(button.getText());
                }
            }
        });
    }

    // .../project/10523/settings/agendas/28495
    public AgendaSettingsPanel getAgendaSettingsPanel() {
        AgendaSettingsPanel panel = new AgendaSettingsPanel();

        PanelHeader panelHeader = getPanelHeader();
        panel.setPanelHeader(panelHeader);

        Preface preface = getPreface();
        panel.setPreface(preface);

        return panel;
    }

    //  .../project/10523/settings/agendas/25942/timeslot/add
    public AgendaAddTimeslotPanel getAgendaAddTimeslotPanel() {
        AgendaAddTimeslotPanel aatp = new AgendaAddTimeslotPanel();

        PanelHeader panelHeader = getPanelHeader();
        aatp.setPanelHeader(panelHeader);

        String panelBodyPath = "//div[@data-qa-id='project-add-timeslot-panel-body']";

        boolean hasNoticeItem = actions().countHowManyElements(panelBodyPath + "/div") == 3;
        if (hasNoticeItem) {
            NoticeItem overlapMessage = getNoticeItem(panelBodyPath + "/div[1]/div");
            aatp.setOverlapMessage(overlapMessage);
        }

        Preface preface = getPreface();
        aatp.setPreface(preface);

        FieldWrapper timeslotWrapper = getTimeslotFieldWrapper();
        aatp.setTimeSlotWrapper(timeslotWrapper);

        FieldWrapper daysWrapper = getDaysFieldWrapper();
        aatp.setRepeatWrapper(daysWrapper);

        return aatp;
    }

    private FieldWrapper getTimeslotFieldWrapper() {
        FieldWrapper timeslotWrapper = getFieldWrapperCommonPartsByHeader("Time slot");

        InputField inputField = getInputFieldByPath("(//form //label)[1]");
        timeslotWrapper.addContent(inputField);

        Dropdown fromTime = getDropdownByName("From *");
        timeslotWrapper.addContent(fromTime);

        Dropdown toTime = getDropdownByName("To *");
        timeslotWrapper.addContent(toTime);

        return timeslotWrapper;
    }

    private FieldWrapper getDaysFieldWrapper() {
        FieldWrapper dayWrapper = getFieldWrapperCommonPartsByHeader("Repeat");

        List<Button> dayButtons = new ArrayList<>();
        int buttonCount = actions().countHowManyElements("//form //i[text()='autorenew']/parent::div //button");

        for (int i = 1; i <= buttonCount; i++) {
            Button button = getButton("(//div[@data-qa-id='subset-picker'] //button)["+i+"]");
            dayButtons.add(button);
        }
        dayWrapper.setButtons(dayButtons);

        return dayWrapper;
    }

    /**
     * In own method because these clicks are done in multiple methods.
     * url = .../project/id/settings/agendas/id/timeslot/edit/'timeslot number'
     */
    private void setTimeslotTimes(final String from, final String to) {
        // Click From drop down
        PlaywrightActions.sleep(1);
        actions().makeClick("//*[@data-qa-id='time_from']");
        PlaywrightActions.sleep(1);

        // Select matching time in drop down list
        actions().makeClick("//div[@role='listbox'] //div[@data-qa-id='"+ from +"']");

        // Click To drop down and set time
        actions().makeClick("//*[@data-qa-id='time_to']");
        PlaywrightActions.sleep(1);

        boolean toListBoxExist = actions().elementExistAndVisible("//div[@role='listbox']");
        System.out.println("toListBoxExist: " + toListBoxExist);

        // Select matching time in drop down list
        actions().makeClick("//div[@role='listbox'] //div[@data-qa-id='"+ to +"']");
    }

    public void deleteTimeSlot(final String timeslotName) {
        actions().makeClick("//form //table//tr//td[contains(text(),'" + timeslotName + "')]");
        actions().makeClick("//span[text()='Delete time slot']");
        clickDeleteButton();
    }

    // ...measure_points/103570/settings/agenda-settings
    private void selectAgendaForS50Mp(String agendaName) {
        // Open the drop down with available Agendas.
        actions().makeClick(AGENDA_DROPDOWN);

        // Select the agenda
        actions().makeClick("//*[@data-qa-id='" + agendaName + "']");
    }

    // ...measure_points/103570/settings/agenda-settings
    private void setTimeslotValues(String timeslotName, String accSpanValue, String baselineValue, String triggerValue) {
        String timeslotPath = "//div[@class='q-fieldset row no-wrap items-start q-my-md q-pl-sm col-12' and .//div[contains(text(), '"+timeslotName+"')]]";

        actions().clearAndType(timeslotPath + " //input[@data-qa-id='accumulation_span']", accSpanValue);
        actions().clearAndType(timeslotPath + " //input[@data-qa-id='baseline']", baselineValue);
        actions().clearAndType(timeslotPath + " //input[@data-qa-id='trig_value']", triggerValue);
    }

    /**
     * Adds an Agenda to MP from url .../project/id/measure_points/id/settings/agenda-settings
     * @param agendaName Agenda to be clicked on in drop down.
     */
    public void addAgendaToMeasuringPointAndSetTimeSlotValues(final String agendaName,
                                                              final List<String> timeslotNames,
                                                              final String accSpanValue,
                                                              final String baselineValue,
                                                              final String triggerValue) {
        // Open the drop down with available Agendas, and set the agenda
        selectAgendaForS50Mp(agendaName);

        // Set time slot values
        timeslotNames.forEach(timeslotName -> {
            String timeslotNameWithCapitalLetterFirst = timeslotName.substring(0, 1).toUpperCase() + timeslotName.substring(1);
            setTimeslotValues(timeslotNameWithCapitalLetterFirst, accSpanValue, baselineValue, triggerValue);
        });

        // Save the agenda to the MP
        clickSaveButton();
    }

//    /**
//     * url: .../project/id/measure_points/id/settings/agenda-settings
//     * Get name of selected Agenda in an MP.
//     * @return Agenda name if MP has an agenda.
//     */
//    public String getAgendaNameFromMeasuringPoint() {
//        if (actions().elementExistWithRefresh(AGENDA_DROPDOWN, false, 20)) {
//            return actions().findOneElementsText(AGENDA_DROPDOWN);
//        } else {
//            return null;
//        }
//    }

//    public int getNumberOfTimeSlotsFromMeasuringPoint() {
//        // Validate we're in the right place
//        isPanelHeaderVisible("mp-agenda-panel-header");
//        return actions().countHowManyElements("//*[@data-qa-id='accumulation_span']");
//    }

    /**
     * url: .../project/id/measure_points/id/settings/agenda-settings
     * @param mpName Name of MP that we want to copy agenda settings from.
     */
    public void copyAgendaSettings(final String mpName) {
        // Validate we're in the right place
        isPanelHeaderVisible("mp-agenda-panel-header");

        // Click on Copy Settings Button
        actions().makeClick("//*[text()='Copy settings']");

        // Click on MP with 'measuringPointName'
        actions().makeClick("//*[@data-qa-id='select_measuring_point_popup']//*[text()='" + mpName + "']");
        // Click save button. NB. Save button will not exist if other MP already has same Agenda
        clickSaveButton();
    }

    private void clickSaveButton() {
        actions().makeClick(SAVE_BUTTON);
        //We need to wait a bit until the save process is completed
        PlaywrightActions.sleep(2);
    }

    private void clickDeleteButton() {
        actions().makeClick(DELETE_BUTTON);
        //We need to wait a bit until the delete process is completed
        PlaywrightActions.sleep(2);
    }

    // .../project/10523/settings/agendas
    public ProjectSettingsAgendasPanel getAgendasPanel() {
        ProjectSettingsAgendasPanel panel = new ProjectSettingsAgendasPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        boolean copyAgendaButtonExist = actions().elementExistAndVisible("//button[.//span[text()='Copy agenda']]", false, 0);
        if (copyAgendaButtonExist) {
            Button copyAgendaButton = getButtonByText("Copy agenda");
            panel.setCopyAgendaButton(copyAgendaButton);
        }

        boolean createAgendaButtonExist = actions().elementExistAndVisible("//button[.//span[text()='Create agenda']]", false, 0);
        if (createAgendaButtonExist) {
            Button createAgendaButton = getButtonByText("Create agenda");
            panel.setCreateAgendaButton(createAgendaButton);
        }

        Table agendasTable = getAgendasTable();
        panel.setAgendasTable(agendasTable);

        return panel;
    }

    private Table getAgendasTable() {
        Table table = new Table();
        String tablePath = "//table";

        int tableColumns = 2;
        Table.TableRow header = new Table.TableRow();
        String headerPath = tablePath + "/thead";

        String nameColumn = actions().findOneElementsText(headerPath + "/tr/th[2]")
                .replace("arrow_upward", "");
        header.addContent(nameColumn);
        header.addContent(actions().findOneElementsText(headerPath + "/tr/th[3]"));
        table.setHeader(header);

        boolean hasRows = actions().elementExistAndVisible(tablePath + "/tbody/tr", false, 0);
        if (hasRows) {
            List<Table.TableRow> rows = new ArrayList<>();
            int rowsCount = actions().countHowManyElements(tablePath + "/tbody/tr");

            for (int row = 1; row <= rowsCount; row++) {
                Table.TableRow rowItem = new Table.TableRow();
                String rowPath = tablePath + "/tbody/tr["+row+"]";

                for (int column = 2; column <= tableColumns + 1; column++) {
                    // todo: paketera i TableCell, som jag gör i DataReportPO?

                    if (column == 2) {  // Name
                        String cellText = actions().findOneElementsText(rowPath + "/td["+column+"]");
                        rowItem.addContent(cellText);
                    } else if (column == 3) { // Timeslots
                        List<String> timeslots =  new ArrayList<>();
                        int timeSlotCount = actions().countHowManyElements(rowPath + "/td["+column+"]" + "/div");

                        // e.g., ts_1, 18:00 - 06:00 (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
                        for (int timeslot = 1; timeslot <= timeSlotCount; timeslot++) {
                            String timeslotText = actions().findOneElementsText(rowPath + "/td["+column+"]" + "/div["+timeslot+"]");
                            timeslots.add(timeslotText);
                        }

                        rowItem.addContent(timeslots);
                    }
                }
                rows.add(rowItem);
            }
            table.setContent(rows);
        } else {
            String noAgendaMessage = actions().findOneElementsText("//tbody //div[@staticclass]");   // 'No agendas available'
            table.setNoRowsText(noAgendaMessage);
        }

        return table;
    }

    public ProjectSettingsAgendaPanel getProjectSettingsAgendaPanel() {
        ProjectSettingsAgendaPanel panel = new ProjectSettingsAgendaPanel();

        panel.setPanelHeader(getPanelHeader());
        panel.setPreface(getPreface());

        // Get the table
        Table timeslotTable = new Table();

        // Get the table headers
        Table.TableRow header = new Table.TableRow();
        int columnCount = actions().countHowManyElements("//form //thead //th");
        for (int c = 1; c <= columnCount; c++) {
            String columnHeaderText = actions().findOneElementsText("//form //thead //th[position()="+c+"]");
            header.addContent(columnHeaderText);
        }
        timeslotTable.setHeader(header);

        // Get each rows values
        List<Table.TableRow> timeslotRows = new ArrayList<>();

        boolean hasRows = actions().elementExistAndVisible("//tbody //tr", false, 0);
        if (hasRows) {
            int rows = actions().countHowManyElements("//tbody //tr");

            for (int r = 1; r <= rows; r++) {
                Table.TableRow timeslotRow = new Table.TableRow();

                for (int c = 1; c <= columnCount; c++) {

                    if (c == 1) {   // Timeslot name
                        String cellText = actions().findOneElementsText("//form //tbody //tr[position()="+r+"]/td["+c+"]");
                        timeslotRow.addContent(cellText);
                    } else {
                        // todo; fixa stöd för att läsa av cell värdet grafiskt
                    }

                }
                timeslotRows.add(timeslotRow);
            }
            timeslotTable.setContent(timeslotRows);

            // Get the subtitles
            List<String> subtitles = actions().findManyElementsTexts("//form //div[contains(@class,'subtitle')]/div");
            timeslotTable.setTableSubtitle(subtitles);

        } else {
            String noTimeSlotMessage = actions().findOneElementsText("//tbody //div[@staticclass]");
            timeslotTable.setNoRowsText(noTimeSlotMessage);
        }
        panel.setTimeslotTable(timeslotTable);

        Button addTimeslotButton = getButtonByText("Add time slot");
        panel.setAddTimeSlot(addTimeslotButton);

        SettingsItem agendaName = getSettingsItemByPath("//div[@data-qa-id='panel-body'] //div[contains(text(),'Agenda name')]/ancestor::div[@role='listitem']");
        panel.setAgendaName(agendaName);

        SettingsItem delete = getSettingsItemByPath("//div[@data-qa-id='panel-body'] //div[contains(text(),'Delete')]/ancestor::div[@role='listitem']");
        panel.setDelete(delete);

        return panel;
    }

    public void searchCopyAgendaPopupFor(String searchPhrase) {
        if (actions().elementExistAndVisible("//div[@role='menu'] //button[text()='cancel']", false, 0)) {
            actions().makeClick("//div[@role='menu'] //button[text()='cancel']");
        }
        actions().clearAndType("//input[@placeholder='Find agenda']", searchPhrase);
        PlaywrightActions.sleep(1);
    }

    public SelectAgendaPopup copyAgendaPopup() {
        String headerText = actions().findOneElementsText("//div[@role='menu'] //div[@class='q-item__label']");

        List<SelectAgendaPopup.SelectAgendaPopupItem> agendaItems = getAgendaPopupList();

        return new SelectAgendaPopup(headerText, agendaItems);
    }

    private List<SelectAgendaPopup.SelectAgendaPopupItem> getAgendaPopupList() {
        List<SelectAgendaPopup.SelectAgendaPopupItem> agendaList = new ArrayList<>();

        int agendaCount = actions().countHowManyElements("//div[@role='menu'] //div[@data-qa-id='list-item']");

        for (int a = 1; a <= agendaCount; a++) {
            String iconText = actions().findOneElementsText("(//div[@role='menu'] //div[@data-qa-id='list-item'])["+a+"] //i");
            String projectName = actions().findOneElementsText("(//div[@role='menu'] //div[@data-qa-id='list-item'])["+a+"] //span");
            String agendaName = actions().findOneElementsText("((//div[@role='menu'] //div[@data-qa-id='list-item'])["+a+"] //div)[last()]");

            agendaList.add(new SelectAgendaPopup.SelectAgendaPopupItem(iconText, projectName, agendaName));
        }

        assertEquals(agendaCount, agendaList.size(),
                ()-> "agendaCount/agendaList.size(): " + agendaCount + "/" + agendaList.size());
        return agendaList;
    }
}
