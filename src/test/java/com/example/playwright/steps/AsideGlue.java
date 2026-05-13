package com.example.playwright.steps;

import com.example.api.endpoints.DeviceApi;
import com.example.api.endpoints.ProjectApi;
import com.example.api.endpoints.SearchApi;
import com.example.api.endpoints.UserApi;
import com.example.api.models.device.Device;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.message.MessageRule;
import com.example.api.models.project.Project;
import com.example.api.models.user.User;
import com.example.helpers.*;
import com.example.helpers.StatusAssesser.Status;
import com.example.playwright.components.aside.Aside;
import com.example.playwright.components.aside.asideItems.listItems.*;
import com.example.playwright.components.panels.TableColumnSettingsPanel;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Icon;
import com.example.playwright.components.parts.Table;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.AsideSize;
import com.example.playwright.helpers.enums.ColourSchema;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.helpers.enums.IconType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.function.Predicate;

import static com.example.helpers.StatusAssesser.Status.*;
import static com.example.helpers.StatusAssesser.assessDeviceMonitoringStatus;
import static com.example.helpers.StatusAssesser.assessProjectStatus;
import static com.example.playwright.helpers.enums.AsideSize.COMPACT;
import static com.example.playwright.helpers.enums.ColourSchema.POSITIVE;
import static com.example.playwright.helpers.enums.IconType.SETTINGS;
import static com.example.playwright.helpers.enums.IconType.VIEW;
import static com.example.playwright.helpers.enums.ProviderType.PROJECT;
import static com.example.playwright.helpers.enums.ProviderType.USER;
import static org.junit.jupiter.api.Assertions.*;

public class AsideGlue extends BaseGlue {


    @Then("I see all project measuring points in {string} aside")
    public void iSeeAllMeasuringPointsInAside(String asideSize) {
        Navigate.project(context().getProject().getId())
                .measurePoints()
                .get();

        //get expected
        List<String> expectedMeasuringPointNames = context().getMeasuringPoints().stream()
                .map(MeasuringPoint::getName)
                .toList();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        // get actual
        Aside aside = asidePO.getAside();
        Table table = aside.getTable();

        boolean isCompact = "COMPACT".equals(asideSize);

        List<String> actualMeasuringPointNames = (isCompact)
                ? aside.getMeasuringPointItems().stream()
                    .map(MeasuringPointItem::getName)
                    .toList()
                : table.getContent().stream()
                    .map(row -> row.getStringByTableHeader(table.getHeader(), "Name"))
                    .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMeasuringPointNames, actualMeasuringPointNames));
    }

    @And("I see all project users in {string} aside")
    public void iSeeAllUsersInAside(String asideSize) {
        Navigate.project(context().getProject().getId())
                .users()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        //get expected
        List<String> expectedUsers = context().getUsers().stream()
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .toList();

        boolean isCompact = "COMPACT".equals(asideSize);

        // get actual
        Aside aside = asidePO.getAside();
        Table table = aside.getTable();

        List<String> actualUserNames = (isCompact)
                ? aside.getUserItems().stream()
                .map(UserItem::getName)
                .toList()
                : table.getContent().stream()
                .map(row -> row.getTableCellByTableHeader(table.getHeader(), "Name"))
                .map(Table.TableCell::getCellText)
                .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedUsers, actualUserNames));
    }

    @And("I see all project monitoring devices in {string} aside")
    public void iSeeAllMonitoringDevicesInAside(String asideSize) {
        //get expected, and use Set to exclude duplicates
        Set<Device> devicesInContext = context().getDevices();

        List<String> expectedMonitoringDeviceSerials = devicesInContext.stream()
                .filter(device -> DeviceType.fromType(device.getType()).isMonitoringDevice())
                .map(device -> String.valueOf(device.getSerial()))
                .toList();

        Navigate.project(context().getProject().getId())
                .devices()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        //make sure we only see monitoring devices(ie. sensors)
        filterPO.changeFilter("Monitoring devices");

        boolean isCompact = "COMPACT".equals(asideSize);

        //get actual
        Aside aside = asidePO.getAside();
        Table table = aside.getTable();

        List<String> actualDevicesSerials = (isCompact)
                ? aside.getDeviceItems().stream()
                    .map(DeviceItem::getSerial)
                    .toList()
                : table.getContent().stream()
                    .map(row -> row.getStringByTableHeader(table.getHeader(), "Serial number"))
                    .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMonitoringDeviceSerials, actualDevicesSerials));
    }

    @Then("I see all project message rules in {string} aside")
    public void iSeeAllMessageRulesInAside(String asideSize) {
        Navigate.project(context().getProject().getId())
                .messageRules()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        //get expected
        List<String> expectedMessageRuleNames = context().getMessageRules().stream()
                .map(MessageRule::getName)
                .toList();

        boolean isCompact = "COMPACT".equals(asideSize);

        // get actual
        Aside aside = asidePO.getAside();
        Table table = aside.getTable();

        List<String> actualMessageRuleNames = (isCompact)
                ? aside.getMessageRuleItems().stream()
                .map(MessageRuleItem::getName)
                .toList()
                : table.getContent().stream()
                .map(row -> row.getTableCellByTableHeader(table.getHeader(), "Name"))
                .map(Table.TableCell::getCellText)
                .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedMessageRuleNames, actualMessageRuleNames));
    }

    @And("I see all project data reports in {string} aside")
    public void iSeeAllDataReportsInAside(String asideSize) {
        Navigate.project(context().getProject().getId())
                .views()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        filterPO.changeFilter("Saved");

        // Get each Search's name
        List<String> expectedReportsNames = context().getSearches().stream()
                .map(search -> SearchApi.getSearch(context().getProject().getId(), search.getId()).getName())
                .toList();

        boolean isCompact = "COMPACT".equals(asideSize);

        // get actual
        Aside aside = asidePO.getAside();
        Table table = aside.getTable();

        List<String> actualReportNames = (isCompact)
                ? aside.getDataReportItems().stream()
                    .map(DataReportItem::getName)
                    .filter(name -> !name.contains("Temporary report"))
                    .toList()
                : table.getContent().stream()
                    .map(row -> row.getStringByTableHeader(table.getHeader(), "Name"))
                    .filter(name -> !name.contains("Temporary report"))
                    .toList();

        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedReportsNames, actualReportNames));
    }

    @Then("I see all account projects in {string} aside")
    public void iSeeAllAccountProjectsInAside(String asideSize) {
        Navigate.company()
                .projects()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        //get expected
        List<Project> expectedActiveProjects = ProjectApi.getActiveProjects().stream()
                .filter(project -> assessProjectStatus(project) == ACTIVE)
                .toList();

        if ("COMPACT".equals(asideSize)) {

            //get actual
            List<ProjectItem> actualProjects = asidePO.getAside().getProjectItems();

            // Validate that project name and id is mapped correctly
            expectedActiveProjects.forEach(expectedProject -> {
                String expectedProjectName = expectedProject.getName().trim();
                String expectedProjectId = expectedProject.getProjectId().trim();

                actualProjects.forEach(foundProject -> {
                    String actualProjectName = foundProject.getProjectName().trim();
                    String actualProjectId = foundProject.getProjectId().trim();

                    if (actualProjectId.equals(expectedProjectId)) {
                        assertTrue(actualProjectName.contains(expectedProjectName));
                    }
                });
            });

            assertEquals(expectedActiveProjects.size(), actualProjects.size(),
                    () -> "Projects expected/actual: " + expectedActiveProjects.size() +  " / " + actualProjects.size());

        } else {
            Aside aside = asidePO.getAside();
            List<Table.TableRow> actualProjectRows = aside.getTable().getContent();
            Table.TableRow headerRow = aside.getTable().getHeader();

            // Map the actual to Project
            expectedActiveProjects.forEach(expectedProject -> {
                String expectedProjectName = expectedProject.getName().trim();
                String expectedProjectId = expectedProject.getProjectId().trim();

                actualProjectRows.forEach(projectRow -> {
                    String actualProjectName = projectRow.getStringByTableHeader(headerRow, "Project name").trim();
                    String actualProjectId = projectRow.getStringSharedByIconByTableHeader(headerRow, "Project ID").trim();

                    if (actualProjectId.equals(expectedProjectId)) {
                        assertTrue(actualProjectName.contains(expectedProjectName));
                    }
                });
            });

            assertEquals(expectedActiveProjects.size(), actualProjectRows.size(),
                    () -> "Projects expected/actual: " + expectedActiveProjects.size() +  " / " + actualProjectRows.size());

        }
    }

    @And("I see all account communicating devices in {string} aside")
    public void iSeeAllAccountCommunicatingDevicesInAside(String asideSize) {
        Navigate.company()
                .devices()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        //get all devices
        List<Device> expectedDevices = DeviceApi.getDevices();
        // Filter out the communicating devices
        List<Device> communicatingDevices = DeviceAssesser.filterOnApiCommunicatingDevices(expectedDevices);

        if ("COMPACT".equals(asideSize)) {

            //get actual
            List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

            assertEquals(communicatingDevices.size(), actualDevices.size(),
                    () -> "Devices expected/actual: " + communicatingDevices.size() +  " / " + actualDevices.size());

        } else {
            Aside aside = asidePO.getAside();
            List<Table.TableRow> actualDeviceRows = aside.getTable().getContent();

            assertEquals(communicatingDevices.size(), actualDeviceRows.size(),
                    () -> "Devices expected/actual: " + communicatingDevices.size() +  " / " + actualDeviceRows.size());
        }
    }

    @And("I see all account users in {string} aside")
    public void iSeeAllAccountUsersInAside(String asideSize) {
        Navigate.company()
                .users()
                .get();

        //prepare the list
        asidePO.setAsideSize(asideSize);

        //get expected
        List<User> expectedUsers = UserApi.getUsers();

        if ("COMPACT".equals(asideSize)) {
            //get actual
            List<UserItem> actualUsers = asidePO.getAside().getUserItems();

        assertEquals(expectedUsers.size(), actualUsers.size(),
                () -> "Users expected/actual: " + expectedUsers.size() +  " / " + actualUsers.size());
        } else {
            Aside aside = asidePO.getAside();
            List<Table.TableRow> actualUserRows = aside.getTable().getContent();

            assertEquals(expectedUsers.size(), actualUserRows.size(),
                () -> "Users expected/actual: " + expectedUsers.size() +  " / " + actualUserRows.size());
        }
    }

    @Then("projects has correct status")
    public void projectsHasCorrectStatus() {
        // todo: paused size check until scroll function is back in business. See commit before 2025:10:04 to see how that was done.

        // Get expected project data
        List<Project> expectedProjects = ProjectApi.getProjects();

        Navigate.company()
                .projects()
                .get();

        // Set filter to retrieve all projects
        filterPO.changeFilter("All projects");

        // Get actual project data
        List<ProjectItem> actualProjects = asidePO.getAside().getProjectItems();

        for (ProjectItem foundProject : actualProjects) {

            String foundProjectsProjectID = foundProject.getProjectId();

            Optional<Project> matchedProjectOpt = expectedProjects.stream()
                    .filter(eP -> eP.getProjectId().trim().equals(foundProjectsProjectID))
                    .findFirst();

            if (matchedProjectOpt.isPresent()) {
                Project matchedProject = matchedProjectOpt.get();

                // Assess and compare statuses
                Status expectedStatus = assessProjectStatus(matchedProject);

                ColourSchema foundProjectsCS = foundProject.getLeftIcon().getColour();
                Status foundProjectsStatus = StatusAssesser.assessAsideItemColour(PROJECT, foundProjectsCS);

                assertEquals(expectedStatus, foundProjectsStatus,
                        () -> "Expected/Actual Status: " + expectedStatus + "/" + foundProjectsStatus);
            } else {
                throw new IllegalStateException("Actual project '" + foundProjectsProjectID + "' not found in expectedProjects.");
            }
        }
    }

    @And("communicating devices in account has correct status")
    public void devicesHasCorrectStatus() {
        Navigate.company()
                .devices()
                .get();

        //get all devices
        List<Device> allDevices = DeviceApi.getDevices();
        // Filter out the communicating devices
        List<Device> communicatingDevices = DeviceAssesser.filterOnApiCommunicatingDevices(allDevices);

        //get actual
        List<DeviceItem> actualDevices = asidePO.getAside().getDeviceItems();

        actualDevices.forEach(actualDevice -> {
            //use serial to find the expected device
            Predicate<Device> matchesOnSerial = eD -> eD.getSerial() == actualDevice.getSerialNumber();

            Optional<Device> expectedDeviceOpt = communicatingDevices.stream()
                    .filter(matchesOnSerial)
                    .findFirst();

            Status expectedDeviceStatus = assessDeviceMonitoringStatus(expectedDeviceOpt.get());

            // This if is to save test after I changed so getListItemStatus() returns ACTIVE instead of MONON
            Status actualDeviceStatus = (actualDevices.isEmpty())
                    ? NOT_PRESENT
                    : (actualDevice.getListItemIcon().getColour().equals(POSITIVE))
                        ? MONON
                        : MONOFF;

            assertEquals(actualDeviceStatus, expectedDeviceStatus,
                    () -> "expStatus/actStatus: " + expectedDeviceStatus + "/" + actualDeviceStatus +
                    "\n" + actualDevice);
        });
    }

    @And("users in account has correct status")
    public void usersInAccountHasCorrectStatus() {
        Navigate.company()
                .users()
                .get();

        // Get all users
        List<User> expectedUsers = UserApi.getUsers();

        // Get actual users
        List<UserItem> actualUsers = asidePO.getAside().getUserItems();

        for (UserItem foundUser : actualUsers) {
            String foundUserName = foundUser.getName();
            String foundUserRole = foundUser.getRole().toLowerCase();

            Optional<User> matchedUserOpt = expectedUsers.stream()
                    .filter(eU -> foundUserName.equals(eU.getFirstName().trim() + " " + eU.getLastName().trim()))
                    .filter(eU -> foundUserRole.equals(eU.getUserRole().toLowerCase()) || foundUserRole.equals("administrator"))
                    .findFirst();

            if (matchedUserOpt.isPresent()) {
                User matchedUser = matchedUserOpt.get();

                // Assess and compare statuses
                Status expectedStatus = StatusAssesser.assessUserStatus(matchedUser);

                ColourSchema foundUsersCS = foundUser.getLeftIcon().getColour();
                Status foundUsersStatus = StatusAssesser.assessAsideItemColour(USER, foundUsersCS);

                assertEquals(expectedStatus, foundUsersStatus,
                        () -> "Expected/Actual Status: " + expectedStatus + "/" + foundUsersStatus);
            } else {
                throw new IllegalStateException("Actual user '" + foundUserName + "' not found in expectedUsers.");
            }
        }
    }

    @Then("cogwheel is visible in {asideSize} {string}")
    public void cogwheelIsVisible(final AsideSize asideSize, final String expected) {
        if (asideSize.equals(COMPACT)) {
            Aside aside = asidePO.getAside(5, true);

            // Get the first listitem and check that the hover exposed the hidden settings icon
            assertEquals(SETTINGS, aside.getAsideItems().getFirst().getRightButton().getIcon().getType());

        } else {
            asidePO.setAsideSize(asideSize);
            Aside aside = asidePO.getAside(5);

            // Assert that all device rows have a settings icon
            aside.getTable().getContent().forEach(deviceRow -> {
                assertEquals(SETTINGS, deviceRow.getRowRightButton().getIcon().getType());
            });
        }
    }

    @And("no other headers can be selected")
    public void noOtherHeadersCanBeSelected() {
        assertFalse(filterPO.selectColumnsButtonExist());
    }

    @Then("list header contains icons")
    public void listHeaderContainsIcons(DataTable table) {
        // Get expected headers
        List<IconType> expectedIconTypes = table.row(0).stream()
                .map(IconType::valueOf)
                .toList();

        // Get actual headers
        Aside aside = asidePO.getAside(1);

        List<IconType> actualIconTypes = aside.getHeader().getButtons().stream()
                .map(Button::getIcon)
                .map(Icon::getType)
                .toList();

        assertTrue(expectedIconTypes.containsAll(actualIconTypes));
    }

    @And("blast list header also has Load latest data")
    public void blastListHeaderAlsoHasLoadLatestData() {
        Aside.AsideAction asideAction = asidePO.getAside().getAction();

        // Check IconType
        IconType actualLoadLatestIcon = asideAction.getLoadLatestIcon().getType();
        assertEquals(VIEW, actualLoadLatestIcon,
                () -> "expectedLoadLatestIcon/actualLoadLatestIcon: " + VIEW + "/" + actualLoadLatestIcon);

        // Check Text
        String expectedText = "Load latest 48 h of data";
        String actualText = asideAction.getLoadLatestText();

        assertEquals(expectedText, actualText,
                () -> "expectedText/actualText: " + expectedText + "/" + actualText);
    }

    @And("bulk action checkbox is {string} and text is {string} and icons are")
    public void bulkActionCheckboxIsMixedAndTextIsSelectedAndIconsAre(String expectedCheckbox, String expectedText, DataTable table) {
        // Get expected header/s
        List<IconType> expectedAsideActionIcons = table.row(0).stream()
                .map(IconType::valueOf)
                .toList();

        Aside.AsideAction asideAction = asidePO.getAside(3).getAction();

        List<IconType> actualAsideActionIconTypes = asideAction.getButtons().stream()
                .map(Button::getIcon)
                .map(Icon::getType)
                .toList();

        Status expectedStatus = StatusAssesser.getCheckboxStatus(expectedCheckbox);
        assertEquals(expectedStatus, asideAction.getCheckbox().getStatus(),
                () -> "expected/actual: " + expectedStatus + "/" + asideAction.getCheckbox().getStatus());

        assertEquals(expectedText, asideAction.getSummaryText(),
                () -> "expectedText/actualText: " + expectedText + "/" + asideAction.getSummaryText());

        assertTrue(actualAsideActionIconTypes.containsAll(expectedAsideActionIcons));
    }

    @When("I click Search icon header contains icon and default text {string}")
    public void searchListHeaderContains(String expectedText, DataTable table) {
        // Click on search icon
        asidePO.clickOnAListHeaderIcon("search-button");
        asideHeaderContains(expectedText, table);
    }

    @Then("aside header contains icons and default text {string}")
    public void asideHeaderContains(String expectedText, DataTable table) {
        // Get expected headers
        List<IconType> expectedIconTypes = table.row(0).stream()
                .map(IconType::valueOf)
                .toList();

        // Get actual headers
        Aside.AsideHeader asideHeader = asidePO.getAside(3).getHeader();

        List<IconType> actualIconTypes = asideHeader.getButtons().stream()
                .map(Button::getIcon)
                .map(Icon::getType)
                .toList();

        assertTrue(expectedIconTypes.containsAll(actualIconTypes));

        String actualText = asideHeader.getSearchValue();

        assertEquals(expectedText, actualText,
                () -> "expectedText/actualText: " + expectedText + "/" + actualText);
    }
    @And("there are {string} {string} in aside")
    public void thereAreMeasuringPointsInAside(String number, String providerType) {
        int expectedCount = Integer.parseInt(number);
        int actualCount = asidePO.getAside().getMeasuringPointItems().size();

        assertEquals(expectedCount, actualCount,
                () -> "There are not " + expectedCount + " in the list but " + actualCount);
    }

    @Then("each device has value in column {string}")
    public void eachTableItemHasValueInColumn(String columnName) {
        Table table = asidePO.getAside(15).getTable();

        List<Table.TableRow> deviceRows = table.getContent();

        deviceRows.forEach(deviceRow -> {

            String firmwareVersion = deviceRow.getStringByTableHeader(table.getHeader(), columnName);
            assertFalse(firmwareVersion.isEmpty(),
                    "Expected firmware version to be not empty.");
        });
    }

    @Then("sorting devices is {string} on {string}")
    public void sortingDevicesIsOn(String sortingOrder, String sortingOn) {
        Aside aside = asidePO.getAside();

        if (aside.isCompact()) {
            List<DeviceItem> deviceItems = aside.getDeviceItems();
            boolean sortedCorrect = validateDeviceItemSorting(deviceItems, sortingOrder, sortingOn);
            assertTrue(sortedCorrect,
                    "Sorting result not as expected in List");
        } else {
            Table.TableRow headerRow = aside.getTable().getHeader();
            List<Table.TableRow> deviceRows = aside.getTable().getContent();

            boolean sortedCorrect = validateTableRowSorting(headerRow, deviceRows, sortingOrder, sortingOn);
            assertTrue(sortedCorrect,
                    "Sorting result not as expected in Table");
        }
    }

    private static boolean validateTableRowSorting(Table.TableRow tableRow, List<Table.TableRow> deviceRows, String sortingOrder, String sortingOn) {
        // Copy list to avoid modifying the original list
        List<Table.TableRow> sortedList = new ArrayList<>(deviceRows);
        JsonUtil.createJsonAndSave(sortedList);
        // Assume the list is not sorted and sort it the way we want
        switch (sortingOn) {
            case "Last read" -> // Sort the copied list based on the lastRead value parsed to minutes
            {
                sortedList.sort(
                        (d1, d2) -> compareLastRead(
                                d1.getStringByTableHeader(tableRow, "Last read"),
                                d2.getStringByTableHeader(tableRow, "Last read"))
                );
            }
            case "Project name" -> // Sort the copied list based on the lastRead value parsed to minutes
                    sortedList.sort(
                            (d1, d2) -> compareLastRead(
                                    d1.getStringByTableHeader(tableRow, "Project name"),
                                    d2.getStringByTableHeader(tableRow, "Project name"))
                    );
            default -> throw new IllegalStateException("Unexpected sortingOn value: " + sortingOn);
        }

        if (sortingOrder.equals("descending")) {
            // If sorting is descending, reverse the sorted list
            Collections.reverse(sortedList);
        }

        // Compare the sorted list with the original list
        return sortedList.equals(deviceRows);
    }

    private static boolean validateDeviceItemSorting(List<DeviceItem> devices, String sortingOrder, String sortingOn) {
        // Copy list to avoid modifying the original list
        List<DeviceItem> sortedList = new ArrayList<>(devices);

        // Assume the list is not sorted and sort it the way we want
        switch (sortingOn) {
            case "Last read" -> // Sort the copied list based on the lastRead value parsed to minutes
                    sortedList.sort((d1, d2) -> compareLastRead(
                            d1.getLastRead(),
                            d2.getLastRead()));
            default -> throw new IllegalStateException("Unexpected sortingOn value: " + sortingOn);
        }

        if (sortingOrder.equals("descending")) {
            // If sorting is descending, reverse the sorted list
            Collections.reverse(sortedList);
        }

        // Compare the sorted list with the original list
        return sortedList.equals(devices);
    }

    /**
     * If called with data from DeviceItem, then params can be null.
     * If called with data from DeviceTable, then params can be empty.
     */
    private static int compareLastRead(String lastRead1, String lastRead2) {
        // COMPACT
        if (lastRead1 == null && lastRead2 == null) {
            return 0;   // If both are null, do not sort
        } else if (lastRead1 == null) {
            return 1; // Null values go at the bottom (larger)
        } else if (lastRead2 == null) {
            return -1; // Null values go at the bottom (larger)
        }

        // MEDIUM or FULL
        if (lastRead1.isEmpty() && lastRead2.isEmpty()) {
            return 0;   // If both are null, do not sort
        } else if (lastRead1.isEmpty()) {
            return 1; // Null values go at the bottom (larger)
        } else if (lastRead2.isEmpty()) {
            return -1; // Null values go at the bottom (larger)
        }

        // Parse the lastRead strings into a comparable time duration in minutes
        long seconds1 = TimeConverter.convertToSeconds(lastRead1);
        long seconds2 = TimeConverter.convertToSeconds(lastRead2);

        // Compare the parsed durations
        return Long.compare(seconds1, seconds2);
    }

    // .../devices
    @When("I click on {string}, I am redirected to {string}")
    public void iClickOnDeviceIAmRedirectedToDetails(String device, String expectedUrl, DataTable table) {
        List<String> asideSizes = table.row(0);
        String serial = DeviceProperties.getConnectedSerial(device);

        asideSizes.forEach(asideSize -> {
            // Fix prerequisites
            asidePO.setAsideSize(asideSize);
            filterPO.clearAllDeviceFilters();

            // Search and click
            asidePO.makeSearchInAside(serial);
            asidePO.clickOnThisAsideItem(serial);

            AssertionHelpers.urlContains(expectedUrl, Navigate.getCurrentUrl());

            // Reset
            asidePO.clickButton("panel close");
            asidePO.clickAsideHeaderIcon(IconType.CANCEL);
            PlaywrightActions.sleep(2);
        });
    }

    @Then("aside {string} has counter")
    public void asideHasCounter(String asideItem) {
        List<OverviewItem> overviewItems = asidePO.getAside().getOverviewItems();
        OverviewItem overviewItem = overviewItems.stream()
                .filter(item -> item.getText().contains(asideItem))
                .toList().getFirst();

        // Make sure there are numbers before ' asideItem', and that the number can be cast to Integer
        Assertions.assertDoesNotThrow(() -> Integer.parseInt(overviewItem.getText().replace(asideItem, "").trim()),
                "String in " + overviewItem.getText() + " cannot be parsed to Integer");
    }

    @And("make search in aside for {string}")
    public void makeSearchInAside(String type) {
        String serial = DeviceProperties.getConnectedSerial(type);

        asidePO.makeSearchInAside(serial);
    }

    @Then("these {string} are {string}")
    public void theseColumnsAreDefault(String checkboxDescription, String expectedState, DataTable table) {
        TableColumnSettingsPanel panel = asidePO.getTableColumnSettingsPanel();

        // Get expected group headers
        List<String> expectedCheckboxes = table.row(0);

        List<String> actualCheckboxes = switch (expectedState) {
            case "default" -> {
                yield panel.getCheckedCheckboxes();
            }
            case "selectable" -> {
                if (checkboxDescription.equals("groups")) {
                    yield panel.getSelectableGroupHeadersText();
                } else if (checkboxDescription.equals("headers")) {
                    yield panel.getSelectableCheckboxesText();
                } else {
                    throw new IllegalArgumentException("Unexpected value: " + checkboxDescription);
                }
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + expectedState);
        };

        assertTrue(expectedCheckboxes.containsAll(actualCheckboxes), "Checked checkboxes did not match.");
    }

    /**
     * In the left menu there is the option to display the list as a compact, medium or full screen list.
     * The method's argument is compared to what appears in the list.
     * The sorting function has default settings in browser:local storage
     */
    @Then("these headers are default")
    public void headersInMedOrFullList(DataTable table) {
        List<String> expectedHeaders = table.row(0);

        Aside aside = asidePO.getAside(12);

        // These are loaded by default, as each chrome session has no history
        List<String> actualHeaders = aside.getAsideTableHeaders();

        AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedHeaders, actualHeaders);
    }

    // from Table column settings panel
    @And("these headers are selectable")
    public void theseHeadersAreSelectable(DataTable table) {
        TableColumnSettingsPanel panel = asidePO.getTableColumnSettingsPanel();

        // Get expected headers
        List<String> rows = table.row(0);

        List<String> columns = panel.getSelectableCheckboxesText();
        assertTrue(rows.containsAll(columns), "Headers and columns did not match.");
    }


}