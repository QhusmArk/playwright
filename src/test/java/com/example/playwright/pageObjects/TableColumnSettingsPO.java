package com.example.playwright.pageObjects;

public class TableColumnSettingsPO extends CommonPO {

    /**
     * In Table column settings, we choose String marking checkboxes which columns are to be displayed.
     * Used String Devices and Billing.
     */
    private void uncheckAll() {
        String tickedCheckbox = "//div[@role='checkbox'][@aria-checked='true']";

        // Untick Project Details and Measuring Point Detail.
        while (actions().elementExistAndVisible(tickedCheckbox, false, 0)) {
                actions().makeClick(tickedCheckbox);
        }
    }
}
