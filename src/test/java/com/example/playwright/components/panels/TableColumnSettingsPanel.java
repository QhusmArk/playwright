package com.example.playwright.components.panels;

import com.example.helpers.StatusAssesser;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Checkbox;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TableColumnSettingsPanel {
    PanelHeader panelHeader;

    List<CheckboxGroup> checkboxGroups;

    Button restoreDefault;
    Button apply;

    /**
     * Returns the text of each checkbox in all checkbox groups.
     */
    @JsonIgnore
    public List<String> getSelectableCheckboxesText() {
        return checkboxGroups.stream()
                .flatMap(group -> group.getCheckboxes().stream())
                .map(Checkbox::getText)
                .toList();
    }

    @JsonIgnore
    public List<String> getSelectableGroupHeadersText() {
        return checkboxGroups.stream()
                .map(CheckboxGroup::getControllerBox)
                .map(Checkbox::getText)
                .toList();
    }

    @JsonIgnore
    public List<String> getCheckedCheckboxes() {
        return checkboxGroups.stream()
                .flatMap(group -> group.getCheckboxes().stream())
                .filter(checkbox -> checkbox.getStatus().equals(StatusAssesser.Status.CHECKED))
                .map(Checkbox::getText)
                .toList();
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class CheckboxGroup {
        Checkbox controllerBox;
        List<Checkbox> checkboxes;
    }
}
