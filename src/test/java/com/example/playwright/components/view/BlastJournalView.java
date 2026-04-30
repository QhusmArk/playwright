package com.example.playwright.components.view;

import com.example.playwright.components.parts.Icon;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * https://sigicom.test.indev.sigicom.net/#/project/10523/views/21d76c04-be18-4973-8ccf-785ff361509f/blasts/61117
 */
@Getter
@Setter
@NoArgsConstructor
public class BlastJournalView {

    private PanelHeader panelHeader;
    private Settings blastSettings;
    private Settings holesSettings;
    private Settings chargeSettings;
    private Settings calculatedValuesSettings;
    private Table mpTable;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Settings {
        private Icon leftIcon;
        private String settingsHeader;
        private Map<String, String> settingsData;
        private Icon arrowUpDownIcon;
    }
}
