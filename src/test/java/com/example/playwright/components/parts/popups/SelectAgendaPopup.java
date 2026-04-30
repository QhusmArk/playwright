package com.example.playwright.components.parts.popups;

import com.example.playwright.helpers.enums.IconType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * /project/id/settings/agendas + Button 'Copy agenda'
 */
@Data
@AllArgsConstructor
public class SelectAgendaPopup {

    private String headerText;

    private List<SelectAgendaPopupItem> agendaMenuItemList;

    @Data
    public static class SelectAgendaPopupItem {
        private IconType leftIcon;
        private String projectName;
        private String agendaName;

        public SelectAgendaPopupItem(String leftIconText, String projectName, String agendaName) {
            this.leftIcon = IconType.fromClassName(leftIconText);
            this.projectName = projectName;
            this.agendaName = agendaName;
        }
    }


}
