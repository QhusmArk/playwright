package com.example.playwright.pageObjects;

import com.example.playwright.helpers.enums.FilterType;
import com.example.playwright.helpers.enums.ProviderType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.playwright.helpers.enums.ProviderType.DEVICE;

public class FilterPO extends CommonPO {

    static final String LIST_HEADER_FILTER_BUTTON = "//button[@data-qa-id='filter-button']";
    static final String FILTER_BUTTON_COUNTER = "//i[@class='q-icon icon-filter']";

    static final String FILTER_PANEL = "//div[@role='menu']";
    static final String FILTER_DEVICE_HEADER = "(//div[@role='menu'] //div[@role='listitem'])[1] //div[2]";
    static final String FILTER_LIST = "//div[@role='menu'] //div[@role='listitem']";
    static final String FILTER_LIST_NO_HEADER = "//div[@role='menu'] //div[contains(@class, 'scroll')] //div[@role='listitem']";

    static final String FILTER_TICKED_CHECKBOXES = "//div[@role='menu'] //div[@class='scroll'] //div[@role='listitem'] //div[@role='checkbox' and @aria-checked='true']";
    static final String SELECTED_FILTER_CLEAR = "//i[@class='q-icon icon-close']";

    static final String SEARCH = "//div[@data-qa-id='search-button']";
    static final String SELECT_COLUMNS = "//div[@data-qa-id='select-columns-button']";

//    protected final SeleniumApi selenium;
//
//    public FilterPO() {
//        this.selenium = PlaywrightActions.getInstance();
//    }

    /**
     * Most filters will close automatically after interaction.
     * Device Checkbox filters and DataReport Checked filters do not close automatically after interaction.
     * Nb. This method do not take into consideration if a filter is blocking other filter options, e.g., 'Communicating devices' blocks 'S50'
     */
    public void changeFilter(String filterText) {
        System.out.println("filterText: " + filterText);
        // Find out if filter menu will close automatically or if we have to do it.
        FilterType filterType = FilterType.fromText(filterText);
        boolean filterMenuWillCloseAfterInteraction = filterType.getAutocloseOnFilterInteraction();
        System.out.println("filterMenuWillCloseAfterInteraction: " + filterMenuWillCloseAfterInteraction);

        openFilterMenu();

        selectFilter(filterText);

        if (!filterMenuWillCloseAfterInteraction) {
            closeFilterMenu();
        }

        // Make sure filter menu is closed
        boolean filterMenuIsOpen = actions().elementExistAndVisible("//div[@role='menu']");
        System.out.println("filterMenuIsOpen: " + filterMenuIsOpen);
        if (filterMenuIsOpen) {
            closeFilterMenu();
        }
    }

    public void setOnlyThisFilter(String filter) {
        // filters 'Communicating devices' o/e 'Monitoring devices' bör inte vara aktiva
        clearAllDeviceFilters();
        changeFilter(filter);
    }

    /**
     * Most filters will close automatically after interaction.
     * Device Checkbox filters and DataReport Checked filters do not close automatically after interaction.
     */
    public void setFilter(FilterType filterType) {
        String filterText = filterType.getText();
        boolean filterMenuWillCloseAfterInteraction = filterType.getAutocloseOnFilterInteraction();

        System.out.println("filterText: " + filterText);
        openFilterMenu();

        selectFilter(filterText);

        // Find out if filter menu will close automatically or if we have to do it.
        if (!filterMenuWillCloseAfterInteraction) {
            closeFilterMenu();
        }
    }


    public void openFilterMenu() {
        actions().makeClick(LIST_HEADER_FILTER_BUTTON);
    }

    private void closeFilterMenu() {
        // If we close filter String clicking on filter button there seem to be some kind of "residual" effect
        // that intervene next time we want to click on filter button
        actions().makeClick("//header");
    }

    public int getAmountOfSelectedFiltersForDevice() {
        openFilterMenu();

        int filterCount = getDeviceFilterSelectedCounter();

        closeFilterMenu();

        return filterCount;
    }

    public int getFilterButtonNumber() {
        return getTheNumberOnTheFilterButton();
    }

    public void resetDeviceFilters() {
        clearAllFilters();
    }

    public void clearAllDeviceFilters() {
        openFilterMenu();
        resetDeviceFilters();
        closeFilterMenu();
    }

    /**
     * @return All active filters.
     */
    public List<String> getActiveFilters() {
        String currentUrl = actions().getCurrentUrl();
        ProviderType type = ProviderType.getProviderTypeFromCurrentUrl(currentUrl);

        openFilterMenu();

        List<String> activeFilters = new ArrayList<>();

        // Get checked filters
        activeFilters.addAll(getCheckedFilters(type));

        // Device has checkboxes that need fetching
        if (type.equals(DEVICE)) {
            activeFilters.addAll(getCheckboxedFilters());
        }

        closeFilterMenu();

        return activeFilters;
    }

    /**
     * @return All filters.
     */
    public List<String> getAllFilters(ProviderType type) {
        openFilterMenu();

        List<String> filterTexts;
        if (type.equals(DEVICE)) {
            filterTexts = actions().findManyElementsTexts(FILTER_LIST_NO_HEADER);
        } else {
            filterTexts = actions().findManyElementsTexts(FILTER_LIST);
        }

        closeFilterMenu();

        return filterTexts.stream()
                .map(this::removeCounterFromFilterText)
                .map(this::removeDoneFromFilterText)
                .collect(Collectors.toList());
    }

    /**
     * @return All the filters that are checked or checkboxed.
     */
    private List<String> getCheckedFilters(ProviderType type) {
        List<String> filterTexts;
        if (type.equals(DEVICE)) {
            filterTexts = actions().findManyElementsTexts(FILTER_LIST_NO_HEADER);
        } else {
            filterTexts = actions().findManyElementsTexts(FILTER_LIST);
        }
        return filterTexts.stream()
                .filter(filterText -> filterText.contains("done"))  // 'done' = checked filter
                .map(this::removeCounterFromFilterText)
                .map(this::removeDoneFromFilterText)
                .collect(Collectors.toList());
    }

    /**
     * @return checkboxed filters (device only)
     */
    private List<String> getCheckboxedFilters() {
        return actions().findCheckboxedFilterText(FILTER_TICKED_CHECKBOXES);
    }

    /**
     * @return The number of active filters in Device.filterButton
     */
    public int getTheNumberOnTheFilterButton() {
        return Integer.parseInt(actions().findOneElementsText(LIST_HEADER_FILTER_BUTTON));
    }

    /**
     * Method that are custom-made for FilterPanel.
     * @param filterText The text in the filter that we want to click on.
     */
    public void selectFilter(String filterText) {
        System.out.println("***************** Selecting filter **********************");
        String by = "//div[@role='menu'] //*[contains(text(), '" + filterText + "')]";
        actions().makeClick(by);
    }

    /**
     * Some listItems contain a 'counter' in the end, eg. 'Communication (4)'.
     * @return 'Communication' if input is 'Communication (4)'
     */
    private String removeCounterFromFilterText(String listItemText) {
        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(listItemText);
        if (matcher.find()) {
            return matcher.replaceAll("").trim();
        } else {
            return listItemText;
        }
    }

    /**
     * Checked filters have text 'done' + new line + filter text.
     * @return Filter text as the user sees it.
     */
    private String removeDoneFromFilterText(String input) {
        return input.replace("done\n", "");
    }

    /**
     * Device top row listItem is a non-clickable active filter counter.
     * @return The digit in the row showing how many active filters that are employed.
     */
    public int getDeviceFilterSelectedCounter() {
        String headerText = actions().findOneElementsText(FILTER_DEVICE_HEADER);
        System.out.println("headerText: " + headerText);

        // Return the number from the 'selected'-row
        return getCountFromSelected(headerText);
    }

    /**
     * If any filters are selected the listItem is clickable, else abort.
     * Works only for Device filter.
     */
    private void clearAllFilters() {
        actions().makeClick(SELECTED_FILTER_CLEAR);
    }

    /**
     * Gets the int from eg "3 selected"
     * @return the digit, ie the number of filters
     */
    private int getCountFromSelected(String selectedText) {
        if (selectedText.equals("Filter")) {    // If "Filter" then no filters are applied
            return 0;
        } else {
            String[] parts = selectedText.split(" "); // Split the string around the space
            return Integer.parseInt(parts[0]); // Parse the first part (the digit/s) into an int
        }
    }

    public boolean selectColumnsButtonExist() {
        return actions().elementExistAndVisible(SELECT_COLUMNS, false, 0);

    }
}
