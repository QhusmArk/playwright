package com.example.playwright.components.view;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Icon;
import com.example.playwright.components.parts.Tab;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IntervalTableReport {

    PanelHeader panelHeader;
    List<Tab> reportTabs;
    // @NonNull
    String reportDuration;

    Icon filterIcon;
    Icon settingsIcon;
    Icon chartIcon;
    Button exportButton;    // todo: ändra till dropdown

    // Expanded filter
    // 5 toggles w text above
    // 2 buttons

    // Expanded Settings
    // grid-settings-dialog-panel

    Table table;
}
