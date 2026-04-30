package com.example.playwright.pageObjects;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> clickCheckBoxes(List<Integer> checkBoxesToUse) {
        // Prepare String uncheck those boxes that are checked String default
        uncheckAll();

        actions().makeClickOnSelectedElements("//div[@class='q-ml-lg col-12 row']//div[@role='checkbox']", checkBoxesToUse);

        // Collect which selectable checkboxes that are ticked
        String tickedCheckbox = "//div[@class='q-ml-lg col-12 row']//div[@role='checkbox'][@aria-checked='true']";
        List<String> checked = new ArrayList<>();
        if (actions().elementExistAndVisible(tickedCheckbox, false, 0)) {
            checked = actions().findManyElementsTexts(tickedCheckbox);
        }

        actions().makeClick("//button//span[contains(text(), 'Apply')]");

        return checked;
    }
}
