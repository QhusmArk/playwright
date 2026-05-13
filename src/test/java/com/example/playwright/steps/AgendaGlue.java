package com.example.playwright.steps;

import com.example.api.endpoints.AgendaApi;
import com.example.api.endpoints.ProjectApi;
import com.example.api.models.agenda.Agenda;
import com.example.api.models.agenda.Label;
import com.example.api.models.project.Project;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.Randomizer;
import com.example.playwright.components.panels.measuringPoint.MeasurePointSettingsAgendaSettingsPanel;
import com.example.playwright.components.panels.project.AgendaAddTimeslotPanel;
import com.example.playwright.components.panels.project.AgendaSettingsPanel;
import com.example.playwright.components.panels.project.ProjectSettingsAgendaPanel;
import com.example.playwright.components.panels.project.ProjectSettingsAgendasPanel;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.popups.SelectAgendaPopup;
import com.example.playwright.components.parts.popups.SelectAgendaPopup.SelectAgendaPopupItem;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.playwright.helpers.enums.ColourSchema.DARK;
import static com.example.playwright.helpers.enums.ColourSchema.POSITIVE;
import static org.junit.jupiter.api.Assertions.*;

public class AgendaGlue extends BaseGlue {
    
    @When("I create agenda {string}")
    public void createAgenda(final String agendaName) {
        agendaPO.openCreateAgendaPanel();

        // Type name in the popup, and then click Save
        agendaPO.createAgendaPopup(agendaName);

        Agenda agenda = AgendaApi.getAgendas(context().getProject().getId()).getFirst();
        context().addAgenda(agenda);
    }

//    .../settings/agendas/1367
    @And("timeslot {string} is created with start time {string}, end time {string} and days")
    public void timeslotWeekendIsCreatedWithStartTimeEndTimeAndDays(final String timeslotName,
                                                                    final String fromTime,
                                                                    final String toTime,
                                                                    final DataTable table) {
        String[] days = table.row(0).toArray(String[]::new);

        agendaPO.addTimeslot(timeslotName, fromTime, toTime, days);
    }

    // .../project/10523/settings/agendas/31210
    @Then("each created timeslots time is visible")
    public void displayTable(final DataTable table) {
        List<String> expectedTimeslotTimeRange = table.row(0);

        ProjectSettingsAgendaPanel panel = agendaPO.getProjectSettingsAgendaPanel();

        // Validate that timeslot duration are displayed correctly
        List<String> actualTimeslotTimeRange = panel.getTimeslotTable().getTableSubtitle();
        AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedTimeslotTimeRange, actualTimeslotTimeRange);
    }

    @When("An attempt to create an agenda with the same name as already existing")
    public void an_attempt_to_create_an_agenda_with_the_same_name_as_already_existing() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agendas()
                .get();

        String name = context().getAgendas().getFirst().getName();
        agendaPO.createAgenda(name);
    }

    @Then("only the project agendas are visible")
    @Then("Only the project agendas are visible")
    public void only_the_project_agendas_are_visible() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agendas()
                .get();

        ProjectSettingsAgendasPanel panel = agendaPO.getAgendasPanel();
        String topAgendaName = panel.getAgendasTable().getContent().getFirst()
                .getStringByTableHeader(panel.getAgendasTable().getHeader(),"Name");

        assertEquals(context().getAgendas().getFirst().getName(), topAgendaName);
    }

    @Then("Name and time is displayed")
    public void name_and_time_is_displayed() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agendas()
                .get();

        List<Label> labels = context().getAgendas().getFirst().getLabels();
        List<String> expectedTimeslotNames = labels.stream()
                .map(Label::getName)
                .filter(name -> !name.equals("default"))
                .toList();

        ProjectSettingsAgendasPanel panel = agendaPO.getAgendasPanel();
        List<String> timeSlots = panel.getAgendasTable().getContent().getFirst().getStringListByTableHeader(panel.getAgendasTable().getHeader(),"Time slots");
        List<String> actualTimeslotNames = timeSlots.stream()
                .map(s -> s.split(",", 2)[0])
                .toList();

        AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedTimeslotNames, actualTimeslotNames);
    }

    @When("An attempt to create a timeslot with the same name as already existing")
    public void duplicateTimeslot() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .get();

        String timeslotNameCopy = context().getAgendas().getFirst().getLabels().get(1).getName();
        agendaPO.addTimeslotFromId(timeslotNameCopy, "08:00", "16:00", new String[]{"Wed"});
    }

    @When("timeslot {string} is updated to start {string} and stop {string}")
    public void update(final String timeslotName, final String start, final String stop) {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .get();

        // Click on the timeslot we want to edit
        agendaPO.selectTimeslot(timeslotName);

        // Set the new time
        agendaPO.editTimeSlotToAndFromTime(start, stop);
    }

    @When("a timeslot is deleted")
    public void a_timeslot_is_deleted() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .get();

        agendaPO.deleteTimeSlot("Helg");
    }

    @Then("There is one timeslot less than before")
    public void there_is_one_timeslot_less_than_before() {
        // Get the agenda before we deleted a timeslot
        Agenda agenda = context().getAgendas().getFirst();
        // Number of slots should now be minus the deleted and minus default slot.
        int expectedTimeslots = agenda.getLabels().size() - 2;

        // Get actual
        ProjectSettingsAgendaPanel panel = agendaPO.getProjectSettingsAgendaPanel();
        int actualTimeslots = panel.getTimeslotTable().getContent().size();

        assertEquals(expectedTimeslots, actualTimeslots,
                () -> "expectedTimeslots/actualTimeslots: " + expectedTimeslots + "/" + actualTimeslots);
    }

    /**
     * Gets the timeslots of an Agenda, and validates that at least one of them has the same start/stop-time as expected.
     */
    @Then("timeslot {string} has duration {string} and stop {string}")
    public void timeslotHasDurationAndStop(String timeslotName, final String start, final String stop) {
        String expected = start + "- " + stop;

        ProjectSettingsAgendaPanel panel = agendaPO.getProjectSettingsAgendaPanel();

        int timeslotOrder = panel.getTimeslotTable().getContentRowIndexByKey(timeslotName);
        // Validate that timeslot duration are displayed correctly
        List<String> actualTimeslotTimeRange = panel.getTimeslotTable().getTableSubtitle();
        String actualTimeslotDuration = actualTimeslotTimeRange.get(timeslotOrder);

        assertEquals(expected, actualTimeslotDuration);
    }

    @When("An attempt to create a timeslot that is overlapping")
    public void overlappingTimeslot() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .timeslot()
                .add()
                .get();

        agendaPO.addTimeslot(Randomizer.randomString(8), "08:00", "16:00", new String[]{"Wed"});
    }

    @Then("The message that contains {string} is displayed")
    public void errorMessage(final String expected) {
        assertTrue(agendaPO.getMessage().contains(expected),
                "Expected: " + expected + " but was: " + agendaPO.getMessage());
    }

    @When("I delete the agenda")
    @When("The agenda is deleted")
    public void iDeleteTheAgenda() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .get();

        agendaPO.deleteAgenda();
        PlaywrightActions.sleep(1);

        ProjectSettingsAgendasPanel panel = agendaPO.getAgendasPanel();
        assertNull(panel.getAgendasTable().getContent());
    }

    @Then("the agenda is also deleted from the measuring point")
    public void the_agenda_is_also_deleted_from_the_measuring_point() {
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .settings()
                .agendaSettings()
                .get();

        MeasurePointSettingsAgendaSettingsPanel panel = mpPO.getMeasurePointSettingsAgendaSettingsPanel();
        assertTrue(panel.getAgendaFieldWrapper().getDropdown().getText().isEmpty(),
                () -> "Expected agenda dropdown to be empty.");
    }

    @Then("I can rename the agenda")
    public void iCanRenameTheAgenda() {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .get();

        String newName = Randomizer.randomString(8);
        agendaPO.renameAgenda(newName);

        ProjectSettingsAgendaPanel panel = agendaPO.getProjectSettingsAgendaPanel();
        assertEquals(newName, panel.getPanelHeader().getHeaderText(),
                () -> "New name did not appear on panel header.");
    }

    @When("The agenda is added to a measuring point")
    public void the_agenda_is_added_to_a_measuring_point() {
        String agendaName = context().getAgendas().getFirst().getName();

        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .settings()
                .agendaSettings()
                .get();

        List<String> timeslotNames = new ArrayList<>();
        context().getAgendas().getFirst().getLabels().forEach(timeslot -> {
            timeslotNames.add(timeslot.getName());
        });

        agendaPO.addAgendaToMeasuringPointAndSetTimeSlotValues(agendaName, timeslotNames, "5", "50", "60");

    }

    @Then("Validate that the measuring point has a connected agenda")
    public void validate_that_the_measuring_point_has_a_connected_agenda() {
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .settings()
                .agendaSettings()
                .get();

        MeasurePointSettingsAgendaSettingsPanel panel = mpPO.getMeasurePointSettingsAgendaSettingsPanel();

        assertEquals(context().getAgendas().getFirst().getName(), panel.getAgendaFieldWrapper().getDropdown().getText());
    }

    @Then("Validate that both MPs has the same Agenda")
    public void validateThatBothMPsHasTheSameAgenda() {
        String agendaName = context().getAgendas().getFirst().getName();

        // Go to first MP and get it's agenda name
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .settings()
                .agendaSettings()
                .get();

        MeasurePointSettingsAgendaSettingsPanel panel = mpPO.getMeasurePointSettingsAgendaSettingsPanel();
        String firstMPsAgenda = panel.getAgendaFieldWrapper().getDropdown().getText();

        // Go to second MP and get it's agenda name
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().get(1).getId())
                .settings()
                .agendaSettings()
                .get();

        MeasurePointSettingsAgendaSettingsPanel panel2 = mpPO.getMeasurePointSettingsAgendaSettingsPanel();
        String secondMPsAgenda = panel2.getAgendaFieldWrapper().getDropdown().getText();

        // Validate that both MP's has expected agenda name.
        assertEquals(agendaName, firstMPsAgenda, "First MP did not have expected agenda name.");
        assertEquals(agendaName, secondMPsAgenda, "Second MP did not have expected agenda name.");
    }

    @Then("The same number of timeslots should be in the measuring point as in the agenda")
    public void checkConnectedAgendas() {
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().getFirst().getId())
                .settings()
                .agendaSettings()
                .get();

        // Get the expectedTimeslots Agenda and how many timeslot it has.
        Agenda agenda = AgendaApi.getAgendas(context().getProject().getId()).getFirst();
        int expectedTimeslots = agenda.getLabels().size();

        // Get how many timeslot the actualTimeslots Agenda has in the MP.
        MeasurePointSettingsAgendaSettingsPanel panel = mpPO.getMeasurePointSettingsAgendaSettingsPanel();
        int actualTimeslots = panel.getTimeslotFieldWrappers().size();

        assertEquals(expectedTimeslots, actualTimeslots,
                () -> "expectedTimeslots/actualTimeslots timeslots: " + expectedTimeslots + "/" + actualTimeslots);
    }

    @When("Other Measuring Point copy Agenda settings from previous Measuring Point")
    public void copyAgendaSettings() {
        // Go to MP with no Agenda
        Navigate.project(context().getProject().getId())
                .measurePoint(context().getMeasuringPoints().get(1).getId())
                .settings()
                .agendaSettings()
                .get();

        // Get agenda name from MP with Agenda and use it to set Agenda to MP with no Agenda
        agendaPO.copyAgendaSettings(context().getMeasuringPoints().getFirst().getName());
    }

    @Then("the copy agenda popup show only active projects")
    public void theCopyAgendaPopupShowOnlyActiveProjects() {
        // Collect all active projects and trim
        Set<String> expectedActiveProjectsNames = ProjectApi.getActiveProjects().stream()
                .map(Project::getName)
                .map(String::trim)
                .collect(Collectors.toSet());

        // Collect all projects in the list, use Set to only have one of each
        List<SelectAgendaPopupItem> actualProjects = agendaPO.copyAgendaPopup().getAgendaMenuItemList();
        Set<String> actualProjectsNames = actualProjects.stream()
                .map(SelectAgendaPopupItem::getProjectName)
                .map(String::trim)
                .collect(Collectors.toSet());

        // Every projectName in SelectAgendaPopupItem, should exist in expectedActiveProjects
        assertTrue(expectedActiveProjectsNames.containsAll(actualProjectsNames),
                 "expectedActiveProjectsNames: \n" +
                         expectedActiveProjectsNames + "\n" +
                         "actualProjectsNames: \n" +
                         actualProjectsNames + "\n"
                );
    }

    @Then("I see my project in the copy agenda popup")
    public void theMenuContainAgendasInCurrentProject() {
        String currentProjectName = context().getProject().getName();

        Set<Map<String, String>> actualSet = menuPO.getCopyAgendaListItems();

        assertTrue(actualSet.stream().anyMatch(map -> map.get("pName").equals(currentProjectName)));
    }

    // ...project/10523/settings/agendas + Copy Agenda popup visible
    @And("I find {string} in copy agenda popup")
    public void iFindInCopyAgendaPopup(String searchPhrase) {
        agendaPO.searchCopyAgendaPopupFor(searchPhrase);

        SelectAgendaPopup popup = agendaPO.copyAgendaPopup();

        assertFalse(popup.getAgendaMenuItemList().isEmpty(), "No search hit was found.");
    }

    @When("I try to copy an agenda")
    public void iTryToCopyAnAgenda() {
        SelectAgendaPopup selectAgendaPopup = agendaPO.copyAgendaPopup();

        if (selectAgendaPopup.getAgendaMenuItemList().isEmpty()) {
            throw new IllegalStateException("SelectAgendaPopup list is empty. No agenda to copy.");
        }

        String projectName = selectAgendaPopup.getAgendaMenuItemList().getFirst().getProjectName();
        String agendaName = selectAgendaPopup.getAgendaMenuItemList().getFirst().getAgendaName();

        agendaPO.clickOnAgendaPopupItem(projectName, agendaName);
    }

    @When("I try to copy same agenda again")
    public void iTryToCopySameAgendaAgain() {
        agendaPO.clickButton("Copy agenda");
        iTryToCopyAnAgenda();
    }

    @Then("the agenda is copied to my project")
    public void theAgendaIsCopiedToMyProject() {
        // todo: Jag får overlap-mdl här. Har fått vid körningar under "Dag" och "Kväll". Spara ner toast för att dokumentera om det bara är vid vissa tillfällen (timeslots) det smäller.
        List<String> toasts = agendaPO.getToasts();

        if (toasts.contains("overlaps")) {
            throw new IllegalStateException("Toast was spotted when copying Agenda.");
        }

        assertTrue(toasts.contains("Agenda has been created"),
                () -> "The expected toast 'Agenda has been created' was not displayed after copying Agenda.");

        ProjectSettingsAgendasPanel panel = agendaPO.getAgendasPanel();
        assertNotNull(panel.getAgendasTable().getContent(),
                () -> "No agendas in /settings/agendas, i.e., no agenda was copied to project.");
    }

    @Then("I will not get {string}")
    public void iWillNotGetError(String errorMsg) {
        Navigate.project(context().getProject().getId())
                .settings()
                .agenda(context().getAgendas().getFirst().getId())
                .get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // Create the critical hour, ie the hour before now, rounded down to full hour
        LocalDateTime criticalHour = LocalDateTime.now().minusHours(1L).withMinute(0).withSecond(0).withNano(0);
        // Create the start hour for ts_1 or end hour for ts_2
        LocalDateTime fiveHoursAgo = LocalDateTime.now().minusHours(5L).withMinute(0).withSecond(0).withNano(0);
        // Create the end hour for ts_1 or the start hour for ts_2
        LocalDateTime twoHoursLater = LocalDateTime.now().plusHours(2L).withMinute(0).withSecond(0).withNano(0);

        if (errorMsg.equals("ts_2 overlaps with ts_1")) {

            // Build the ts_1 that ends the full hour before the latest passed
            createTimeslot("ts_1", fiveHoursAgo.format(formatter), criticalHour.format(formatter), days);
            // Build the ts_2 that shall wrongly overlap with ts_1
            createTimeslot("ts_2", criticalHour.format(formatter), twoHoursLater.format(formatter), days);

        } else if (errorMsg.equals("ts_1 overlaps with ts_2")) {

            // Build the ts_1 that starts the full hour before the latest passed
            createTimeslot("ts_1", criticalHour.format(formatter), twoHoursLater.format(formatter), days);
            // Build the ts_2 that shall wrongly overlap with ts_1
            createTimeslot("ts_2", fiveHoursAgo.format(formatter), criticalHour.format(formatter), days);
        }

        // Make sure the create Timeslot gets us back to agenda settings panel
        AgendaSettingsPanel panel = agendaPO.getAgendaSettingsPanel();
        assertEquals(context().getAgendas().getFirst().getName(), panel.getPanelHeader().getHeaderText());
        assertEquals(context().getProject().getDescription(), panel.getPreface().getText());
    }


    // .../project/10523/settings/agendas/28495
    private void createTimeslot(final String timeSlotName, final String from, final String to, final String[] days) {
        agendaPO.clickButton("Add time slot");
        PlaywrightActions.sleep(1);

        // Type the timeslot name
        agendaPO.setValueToInput("//*[@data-qa-id='name']", timeSlotName);

        // Open the dropdown, and select option
        agendaPO.selectDropdownByHeader("From *", from);

        // Open the dropdown, and select option
        agendaPO.selectDropdownByHeader("To *", to);

        // Set the days that the timeslot should be active on
        assertDaysAreSelected(days);

        agendaPO.clickButton("Save");
        PlaywrightActions.sleep(1);
    }

    private void assertDaysAreSelected(String[] days) {
        AgendaAddTimeslotPanel panel = agendaPO.getAgendaAddTimeslotPanel();
        FieldWrapper repeatWrapper = panel.getRepeatWrapper();

        // 7 day buttons, make sure each button has state
        repeatWrapper.getButtons().forEach(button -> {

            boolean buttonShouldBeSelected = Arrays.stream(days).toList().contains(button.getText());
            if (buttonShouldBeSelected) {

                boolean buttonIsNotSelected = button.getBackgroundColour().equals(DARK);
                if (buttonIsNotSelected) {
                    // If button is in the list, and not selected -> select it
                    agendaPO.clickButton(button.getText());
                }

            } else {

                boolean buttonIsSelected = button.getBackgroundColour().equals(POSITIVE);
                if (buttonIsSelected) {
                    // If a button is not in the list, and is selected -> deselect it
                    agendaPO.clickButton(button.getText());
                }

            }
        });

    }
}
