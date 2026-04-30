package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.*;
import com.example.playwright.components.panels.blast.BlastDetailsPanel;
import com.example.playwright.components.panels.blast.BlastSettingsPanel;
import com.example.playwright.components.parts.Dropdown;
import com.example.playwright.components.parts.InputField;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.Table;
import com.example.playwright.helpers.PlaywrightActions;

import java.util.List;
import java.util.Map;

import static com.example.playwright.helpers.enums.IconType.COLLAPSED;
import static com.example.playwright.helpers.enums.IconType.EXPANDED;

public class BlastPO extends CommonPO {

    public void createBlast(final String name, final double latitude, final double longitude) {
        actions().clearAndType("//*[@data-qa-id='blast_id']", name);
        actions().clearAndType("//*[@data-qa-id='max_instantaneous_charge']", "100");
        actions().clearAndType("//*[@data-qa-id='latitude']", String.valueOf(latitude));
        actions().clearAndType("//*[@data-qa-id='longitude']", String.valueOf(longitude));

        actions().makeClick("//*[text()='Save']");
        PlaywrightActions.sleep(2);
    }

    // .../project/10523/blasts/create
    public void copyBlast(final String presentName, final String newName) {
        actions().makeClick("//div[@data-qa-id='create-blast-event-panel-body'] //*[text()='Copy previous blast']");

        actions().makeClick("//div[@data-qa-id='select_blast_popup'] //span[contains(text(),'" + presentName + "')]");
        actions().clearAndType("//*[@data-qa-id='blast_id']", newName);
        actions().makeClick("//button[@data-qa-id='create-blast-event-panel-btn-save']");
    }

    /**
     * URL in ...project/project_id/blasts/blast_id/settings/general
     */
    public void changeBlastName(String newName) {
        actions().clearAndType("//*[@data-qa-id='blast_id']", newName);
        actions().makeClick("//*[text()='Save']");
    }

     // ...project/project_id/blasts/blast_id/settings
    public void deleteBlast() {
        actions().makeClick("//div[@class='q-item__label'][contains(text(),'Advanced settings')]");
        //todo: detta borde ligga i MenuPO
        actions().makeClick("//div[@class='q-item__label'][contains(text(),'Delete')]");
        actions().makeClick("//div[contains(text(),'I understand')]");
        actions().makeClick("//*[@class='block'][contains(text(),'Delete')]");
    }

    public BlastDetailsPanel getBlastDetailsPanel() {
        BlastDetailsPanel bdp = new BlastDetailsPanel();

        bdp.setPanelHeader(getPanelHeader());
         bdp.setPreface(getPreface());

        Map<String, String> summaryPanel = getBlastSummaryMap();
        bdp.setSummaryMap(summaryPanel);

        SettingsItem calculatedValues = getSettingsItemByDataQaId("Calculated values");

        if (calculatedValues.getExpansionIcon().getType().equals(EXPANDED)) {
            Map<String, String> calculatedValueMap = getBlastCalculatedValuesMap();
            calculatedValues.setBlastCalculatedValuesMap(calculatedValueMap);
        }

        bdp.setCalculatedValues(calculatedValues);

        SettingsItem blastSettings = getSettingsItemByDataQaId("blast_settings");
        bdp.setBlastSettings(blastSettings);

        Table mpTable = getBlastDetailsMeasuringPointsTable();
        bdp.setMpTable(mpTable);

        return bdp;
    }

    /**
     * Collection method to return SettingItem of Calculated Values, depending on if SettingsItem is expanded or not.
     */
    private Map<String, String> getBlastCalculatedValuesMap() {
        List<String> fields = List.of(
                "Bench height",
                "Area",
                "Volume",
                "Specific drilling",
                "Calculated drilling",
                "Powder factor"
        );

        return getSummaryMap(fields);
    }

    /**
     * /project/id/blasts/id/details
     */
    private Map<String, String> getBlastSummaryMap() {
        List<String> fields = List.of(
                "Blasting planned",
                "Blasting occurred",
                "Time span",
                "Type",
                "Total no. of holes",
                "MIC",
                "Total charge",
                "Burden",
                "Spacing",
                "Explosive",
                "Detonator"
        );

        return getSummaryMap(fields);
    }

    public BlastSettingsPanel getBlastSettingsPanel() {
        BlastSettingsPanel bsp = new BlastSettingsPanel();

        bsp.setPanelHeader(getPanelHeader());
         bsp.setPreface(getPreface());

        SettingsItem generalSettings = getSettingsItemByDataQaId("blast_details_settings");
        bsp.setGeneralSettings(generalSettings);

        SettingsItem timeSettings = getSettingsItemByDataQaId("blast_time_settings");
        bsp.setTimeSettings(timeSettings);

        SettingsItem location = getSettingsItemByDataQaId("blast_location_settings");
        bsp.setLocation(location);

        SettingsItem holes = getSettingsItemByDataQaId("blast_holes_settings");
        bsp.setHoles(holes);

        SettingsItem charge = getSettingsItemByDataQaId("blast_charge_settings");
        bsp.setCharge(charge);

        SettingsItem advancedSettings = getSettingsItemByDataQaId("Advanced settings");
        bsp.setAdvancedSettings(advancedSettings);

        if (advancedSettings.getExpansionIcon().equals(COLLAPSED)) {
            SettingsItem measuringUnits = getSettingsItemByDataQaId("blast_settings_units");
            bsp.setMeasuringUnits(measuringUnits);

            SettingsItem delete = getSettingsItemByDataQaId("delete");
            bsp.setDelete(delete);
        }

        return bsp;
    }

    public BlastSettingsChargePanel getBlastSettingsChargePanel() {
        BlastSettingsChargePanel panel = new BlastSettingsChargePanel();

        panel.setPanelHeader(getPanelHeader());
         panel.setPreface(getPreface());

        panel.setCharge(getFieldWrapperCommonPartsByHeader("Charge"));

        InputField totalCharge;
        InputField totalPrimer;
        InputField minChargePerHole;
        InputField maxChargePerHole;
        InputField minChargeConcentratin;
        InputField maxChargeConcentratin;
        InputField stemming;
        InputField mic;
        InputField typeOfExplosive;
        InputField typeOfDetonator;
        InputField intervals;
        InputField flyrockCover;

        return panel;
    }

    public BlastSettingsGeneralPanel getBlastSettingsGeneralPanel() {
        BlastSettingsGeneralPanel panel = new BlastSettingsGeneralPanel();

        panel.setPanelHeader(getPanelHeader());
         panel.setPreface(getPreface());
        panel.setGeneral(getFieldWrapperCommonPartsByHeader("General"));

        InputField name;
        InputField description;
        InputField mic;
        Dropdown type;
        Dropdown supervisor;

        return panel;
    }

    public BlastSettingsHolesPanel getBlastSettingsHolesPanel() {
        BlastSettingsHolesPanel panel = new BlastSettingsHolesPanel();

        panel.setPanelHeader(getPanelHeader());
         panel.setPreface(getPreface());
        panel.setHoles(getFieldWrapperCommonPartsByHeader("Holes"));

        InputField drillholeDiameter;
        InputField totalNoOfHoles;
        InputField holeAngle;
        InputField burden;
        InputField spacing;
        InputField subDrilling;
        InputField totalDrilledLength;
        InputField holeRows;
        InputField minDepth;
        InputField maxDepth;
        InputField sectionLength;

        return panel;
    }

    public BlastSettingsLocationPanel getBlastSettingsLocationPanel() {
        BlastSettingsLocationPanel panel = new BlastSettingsLocationPanel();

        panel.setPanelHeader(getPanelHeader());
         panel.setPreface(getPreface());

        return panel;
    }

    public BlastSettingsTimePanel getBlastSettingsTimePanel() {
        BlastSettingsTimePanel panel = new BlastSettingsTimePanel();

        panel.setPanelHeader(getPanelHeader());
         panel.setPreface(getPreface());

        return panel;
    }


}
