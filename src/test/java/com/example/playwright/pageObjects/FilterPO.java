package com.example.playwright.pageObjects;

import com.example.playwright.components.parts.Checkbox;
import com.example.playwright.components.parts.FilterItem;
import com.example.playwright.components.parts.Icon;
import com.example.playwright.helpers.enums.FilterType;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Most filters will close automatically after interaction.
     * Device Checkbox filters and DataReport Checked filters do not close automatically after interaction.
     * Nb. This method do not take into consideration if a filter is blocking other filter options, e.g., 'Communicating devices' blocks 'S50'
     */
    public void changeFilter(String filterText) {
//        System.out.println("filterText: " + filterText);
        // Find out if filter menu will close automatically or if we have to do it.
        FilterType filterType = FilterType.fromText(filterText);
        boolean filterMenuWillCloseAfterInteraction = filterType.getAutocloseOnFilterInteraction();
//        System.out.println("filterMenuWillCloseAfterInteraction: " + filterMenuWillCloseAfterInteraction);

        openFilterMenu();

        selectFilter(filterText);

        if (!filterMenuWillCloseAfterInteraction) {
            closeFilterMenu();
        }

        // Make sure filter menu is closed
        boolean filterMenuIsOpen = actions().elementExistAndVisible("//div[@role='menu']", false, 1);
//        System.out.println("filterMenuIsOpen: " + filterMenuIsOpen);
        if (filterMenuIsOpen) {
            closeFilterMenu();
        }
    }

    /**
     * Most filters will close automatically after interaction.
     * Device Checkbox filters and DataReport Checked filters do not close automatically after interaction.
     */
    public void setFilter(FilterType filterType) {
        String filterText = filterType.getText();
        boolean filterMenuWillCloseAfterInteraction = filterType.getAutocloseOnFilterInteraction();

//        System.out.println("filterText: " + filterText);
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

    public String getFilterButtonNumber() {
        return actions().findOneElementsText(LIST_HEADER_FILTER_BUTTON);
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
     * @return All filters.
     */
    public List<FilterItem> getAllFilters() {

        openFilterMenu();

        List<FilterItem> filterItems = getAllMenuFilterItems();

        closeFilterMenu();

        return filterItems;
    }

    // todo: is Menu-component
    public List<FilterItem> getAllMenuFilterItems() {
        List<FilterItem> filterItems = new ArrayList<>();

        String filtersPath = "//div[@role='menu'] //div[@role='listitem']";

        int filterRows =  actions().countHowManyElements(filtersPath);

        for (int row = 1; row <= filterRows; row++) {
            String filterRowPath = "(" + "//div[@role='menu'] //div[@role='listitem'])[" + row + "]";

            FilterItem filter = getFilterItem(filterRowPath);
            filterItems.add(filter);
        }

        return filterItems;
    }

    private FilterItem getFilterItem(String filterRowPath) {
        FilterItem filter = new FilterItem();

        // First check for check-icon, those are more common
        boolean hasIcon = actions().elementExistAndVisible(filterRowPath + "//i", false, 0);
        if (hasIcon) {
            Icon icon = getIcon(filterRowPath + "//i");
            filter.setIcon(icon);
        } else {
            boolean hasCheckbox = actions().elementExistAndVisible(filterRowPath + "//div[@role='checkbox']", false, 0);
            if (hasCheckbox) {
                Checkbox checkbox = getCheckbox(filterRowPath + "//div[@role='checkbox']");
                filter.setCheckbox(checkbox);
            }
        }

        // Text
        String text = actions().findOneElementsText(filterRowPath + " //div[contains(@class, 'text-body')]");
        filter.setText(text);

        return filter;
    }

    /**
     * Method that are custom-made for FilterPanel.
     * @param filterText The text in the filter that we want to click on.
     */
    public void selectFilter(String filterText) {
        System.out.println("***************** Selecting filter **********************");
        actions().makeClick("//div[@role='menu'] //*[contains(text(), '" + filterText + "')]");
    }

    /**
     * If any filters are selected the listItem is clickable, else abort.
     * Works only for Device filter.
     */
    private void clearAllFilters() {
        actions().makeClick(SELECTED_FILTER_CLEAR);
    }

    public boolean selectColumnsButtonExist() {
        return actions().elementExistAndVisible(SELECT_COLUMNS, false, 0);

    }
}
