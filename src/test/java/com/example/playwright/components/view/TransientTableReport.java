package com.example.playwright.components.view;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.DataViewStatus;
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
public class TransientTableReport {

    PanelHeader panelHeader;

    List<Tab> reportTabs;
    // @NonNull
    String reportDuration;
    Button exportButton;    // todo: ändra till dropdown

    DataViewStatus dataViewStatus;

    Table reportContent;
}
