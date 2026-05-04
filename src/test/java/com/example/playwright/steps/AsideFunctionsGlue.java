package com.example.playwright.steps;

import com.example.api.endpoints.ProjectApi;
import com.example.helpers.AssertionHelpers;
import com.example.helpers.builders.BuilderFactory;
import com.example.helpers.builders.ProjectBuilder;
import com.example.playwright.components.aside.asideItems.listItems.DeviceItem;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.helpers.Navigate;
import com.example.playwright.helpers.PlaywrightActions;
import com.example.playwright.helpers.enums.DeviceType;
import com.example.playwright.helpers.enums.FilterType;
import com.example.playwright.helpers.enums.ProviderType;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static com.example.playwright.helpers.enums.IconType.*;
import static com.example.playwright.helpers.enums.IconType.PROJECT;
import static com.example.playwright.helpers.enums.ProviderType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Class that services List-tests such as Search, Sort, Filter
 */
public class AsideFunctionsGlue extends BaseGlue {
    
    @And("this filter {string} is active")
    public void thisFilterIsActive(String expectedFilterText) {
        List<String> activeFilters = filterPO.getActiveFilters();
        assertTrue(activeFilters.size() < 2,
                "Expected no or one active filter, but found " + activeFilters.size());

        // If activeFilters is empty, then no filter was active, hence "".
        String actualFilterText = activeFilters.isEmpty()
                ? ""
                : activeFilters.getFirst();

        assertEquals(expectedFilterText, actualFilterText,
                () -> "expectedFilterText/actualFilterText: '" + expectedFilterText + "', '" + actualFilterText + "'");
    }

    // todo: skillnad på denna och iSetToFilter()?
    @When("I change to filter {string}")
    public void iChangeToFilter(String filterText) {
        filterPO.changeFilter(filterText);
    }

    @When("I set filter {string}")
    public void iSetToFilter(String filterText) {
        FilterType filter = FilterType.fromText(filterText);
        filterPO.setFilter(filter);
    }

    @When("I set these filters")
    public void iSetBelowFilters(DataTable table) {
        List<String> filterList = table.row(0);

        Navigate.company()
                .devices()
                .get();

        // Click on a filter.
        filterList.forEach(filterPO::changeFilter);
    }

    @Then("These filters are active")
    public void theseFiltersAreActive(DataTable table) {
        List<String> expectedActiveFilters = table.row(0);

        List<String> actualActiveFilters = filterPO.getActiveFilters();

//        boolean equalLists = areListsEqual(expectedActiveFilters, actualActiveFilters);
//        assertTrue(equalLists);
        assertTrue(AssertionHelpers.areTrimmedAndSortedListsIdentical(expectedActiveFilters, actualActiveFilters));
    }

//    /**
//     * Comparing two String lists by checking size and then, after sorting, checks contents.
//     * @param list1
//     * @param list2
//     * @return If the lists are equal
//     */
//    static boolean areListsEqual(List<String> list1, List<String> list2) {
//        if (list1 == null || list2 == null) {
//            return list1 == list2;
//        }
//
//        if (list1.size() != list2.size()) {
//            return false;
//        }
//
//        List<String> modifiableList1 = new ArrayList<>(list1);
//        List<String> modifiableList2 = new ArrayList<>(list2);
//
//        Collections.sort(modifiableList1);
//        Collections.sort(modifiableList2);
//
//        return modifiableList1.equals(modifiableList2);
//    }

    @And("That the device filters are {string} can be counted using")
    public void thatTheDeviceFiltersAreFourCanBeCountedUsing(String input, DataTable table) {
        List<String> countValidators = table.row(0);
        int expectedActiveFilters = Integer.parseInt(input);

        for (String countValidator : countValidators) {

            Integer actualActiveFilters = switch (countValidator) {
                case "Filter size" ->                                 // Get the active filters count by the size of found active filters
                    filterPO.getActiveFilters().size();
                case "Filter listItem counter" -> filterPO.getAmountOfSelectedFiltersForDevice();        // Get the active filters count by the Device.'selected' listItem
                case "Filter button counter" -> filterPO.getFilterButtonNumber();                        // Get the active filters count by the number on the filter button
                default -> throw new IllegalArgumentException("Unexpected value: " + countValidator);
            };

            assertEquals(actualActiveFilters, expectedActiveFilters);
        }
    }

    @When("Filter and validate on one of the following devices")
    public void filterAndValidateOnOneOfTheFollowingDevices(final DataTable table) {
        List<String> expected = table.row(0);

        Navigate.company()
                .devices()
                .get();

        // Open the filter menu (it shall be open the full test)
        filterPO.openFilterMenu();

        // For each device, filter on that one, get all list items, make all validations. Then take the next one...
        expected.forEach(filterValue -> {
            System.out.println("***********Filtering on " + filterValue + " **********************");

            // Make sure no filters are set
            filterPO.resetDeviceFilters();

            // We cannot filter on _SENSOR
            String filterOnThisDeviceType = (filterValue.contains("_SENSOR"))
                    ? filterValue.replace("_SENSOR", "")
                    : filterValue;

            // Set the filter
            filterPO.selectFilter(filterOnThisDeviceType);

            // Now get all deviceItems after filter is been applied
//            List<DeviceItem> deviceItems = asidePO.getAside().getDeviceItems();
            List<DeviceItem> deviceItems = asidePO.getAside(10).getDeviceItems();

            // Use the filter value to deduct DeviceType
            DeviceType expectedDeviceType = DeviceType.fromType(filterValue);

            deviceItems.forEach(deviceItem -> {
                if (deviceItem.getSerial().equals("79049")) {
                    // avoid this unit as it's a C10_sensor but a C12_logger :-(
                } else {
                    // Use the device items text and monIcon to assess devicetype
                    DeviceType actualDeviceType = deviceItem.getDeviceType();

                    assertEquals(expectedDeviceType, actualDeviceType,
                            () -> "expectedDeviceType/actualDeviceType: " + expectedDeviceType + "/" + actualDeviceType);
                }
            });
        });
    }

    @Then("create new mp link should not be visible")
    public void createNewMpLinkShouldNotBeVisible() {
        String emptyMpListText = asidePO.getEmptyListText();

        assertEquals("No measuring point created", emptyMpListText);
    }

    @Then("create new blast link should be visible")
    public void createNewBlastLinkShouldBeVisible() {
        String emptyBlastListText = asidePO.getEmptyListText();

        assertTrue(emptyBlastListText.contains("Create new"));
    }

    @And("selects all columns")
    public void selectsAllColumns() {
        asidePO.selectAllColumnGroupsForAsideTable();
    }

    @When("I select columns")
    public void iSelectColumns(DataTable dataTable) {
        List<String> columnNames = dataTable.row(0);

        asidePO.selectTheseColumnsForAsideTable(columnNames);
    }


    @And("aside is {string}")
    public void setAsideSize(String asideSize) {
        asidePO.setAsideSize(asideSize);
    }

    @When("I do not get search hit in account {string} I get {string}")
    public void iDoNotGetSearchHitIGet(String endpoint, String expectedMessage) {
        ProviderType type = ProviderType.fromEndpoint(endpoint);
        Navigate.company()
                .provider(type)
                .get();

        asidePO.makeSearchInAside("iacghlniargg");

        String actualMessage = asidePO.getEmptyListBySearchText();

        assertEquals(expectedMessage, actualMessage,
                () -> "expectedMessage/actualMessage: " + expectedMessage + "/" + actualMessage);
    }

    @When("I do not get search hit in project {string} I get {string}")
    public void iDoNotGetSearchHitInProjectIGet(String endpoint, String expectedMessage) {
        ProviderType type = ProviderType.fromEndpoint(endpoint);
        Navigate.project(context().getProject().getId())
                .provider(type)
                .get();

        asidePO.makeSearchInAside("iacghlniargg");

        String actualMessage = asidePO.getEmptyListBySearchText().replace("\n", " ");

        assertEquals(expectedMessage, actualMessage,
                () -> "expectedMessage/actualMessage: " + expectedMessage + "/" + actualMessage);
    }

    @When("I go to project {string} I get {string}")
    public void iGoToProjectIGet(String endpoint, String expectedMessage) {
        // Add blast standard at the end of the test, so that there is no auto.trans.report
        if (endpoint.equals("blasts")) {
            ProjectBuilder builder = BuilderFactory.getBuilder(
                    BuilderFactory.Providers.PROJECT,
                    ProjectBuilder.class);
            builder.setProvider(context().getProject());
            builder.withBlastStandard("SS4604866").build();

            ProjectApi.updateProject(context().getProject().getId(), builder.buildJson());

            Navigate.refreshBrowser();
            PlaywrightActions.sleep(2);
        }

        ProviderType type = ProviderType.fromEndpoint(endpoint);
        Navigate.project(context().getProject().getId())
                .provider(type)
                .get();

        String actualMessage = asidePO.getEmptyListBySearchText().replace("\n", " ");

        assertEquals(expectedMessage, actualMessage,
                () -> "expectedMessage/actualMessage: " + expectedMessage + "/" + actualMessage);
    }

    @And("listitem menu has these options")
    public void listitemMenuHasTheseOptions(DataTable table) {
        List<String> expectedMenuOptions = table.row(0);

        List<String> actualMenuOptions = asidePO.getMenuOptions();

        assertTrue(expectedMenuOptions.containsAll(actualMenuOptions));
    }

    @When("I select menu option {string}")
    public void iSelectMenuOption(String menuSelection) {
        asidePO.clickOnTopListItemMenuAndSelect(menuSelection);
        PlaywrightActions.sleep(7);
    }


    @When("I click on Create MP from Aside Header")
    public void iClickOnCreateMPFromAsideHeaderPlus() {
        asidePO.clickOnOverviewListPlus(MEASURING_POINT);
    }

    @When("I select all {string} for bulk action")
    public void iSelectAllMeasuringPointsForBulkAction(String type) {
        ProviderType providerType = ProviderType.fromEndpoint(type);
        asidePO.createBulkActionForAll(providerType);
    }

    @When("I check a {string} checkbox icon")
    public void iCheckADeviceCheckboxIcon(String listItemType) {
        // Tick first checkbox
        asidePO.selectTheseForBulkAction(1);
    }

    @When("I select these and make {string} bulk action")
    public void iSelectTheseAndMakeBulkAction(String bulkActionType, DataTable table) {
        // First select which listItems we're to check
        iSelectForBulkAction(table);

        // Then select which bulk action button to press
        clickOnBulkActionIcon(bulkActionType);

        // a bulk action Create Data report takes a few seconds to redirect to the new views url
        if (bulkActionType.equals("Create data report")) {
            PlaywrightActions.sleep(8);
        }
    }

    @And("I select these for bulk action")
    public void iSelectForBulkAction(DataTable table) {
        ProviderType providerType = Navigate.getProviderTypeFromUrl();

        // Open search field
        asidePO.clickAsideHeaderIcon(SEARCH);

        for (int i = 0; i < table.row(0).size(); i++) {

            String searchPhrase = table.row(0).get(i);
            searchPhrase = (providerType.equals(DEVICE) || providerType.equals(DEVICE_INCL_BANNER))
                    ? DeviceProperties.getConnectedSerial(searchPhrase)
                    : searchPhrase;

            asidePO.makeSearch(searchPhrase);

            // Assume no checkbox is already checked, when making the first loop
            asidePO.makeCheckbox(i == 0);

            // When a search is done, we need to reset search with cancel-button. Else list do not show all selected.
            asidePO.clickAsideHeaderIcon(CANCEL);
        }

    }

    @And("click on bulk action {string} icon")
    public void clickOnBulkActionIcon(String bulkActionType) {
        // Then select which bulk action button to press
        switch (bulkActionType) {
            case "Create project" -> {
                asidePO.clickAsideHeaderActionIcon(PROJECT);
            }
            case "Create data report" -> {
                asidePO.clickAsideHeaderActionIcon(REPORTS);
            }
            case "Edit time frame" -> {}
            default -> throw new IllegalArgumentException("Unexpected bulkActionType: " + bulkActionType);
        }
    }

    @Then("these devices will be included in Create measuring points list")
    public void theseDevicesWillBeIncludedInCreateMeasuringPointsList(DataTable table) {
        // Get the list of new measuring points to be created
        List<String> actualMpDevices = projectPO.getBulkActionMpList();

        table.row(0).forEach(device -> {
            // Make sure the actualMpDevices contains the correct devices
            String expectedDeviceForList = device + " #" + DeviceProperties.getConnectedSerial(device);
            assertTrue(actualMpDevices.contains(expectedDeviceForList),
                    expectedDeviceForList + " was not in the measuring point list. Was it excluded?");
        });
    }

    @And("I select all device columns")
    public void iSelectAllDeviceColumns() {
        asidePO.selectAllColumnGroupsForAsideTable();
    }

    @And("I remove all device filters")
    public void iRemoveAllDeviceFilters() {
        filterPO.clearAllDeviceFilters();
    }
}

