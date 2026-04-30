package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataReportCreatePanel {
    private PanelHeader panelHeader;

    private FieldWrapper dataWrapper;

    private FieldWrapper measuringPointsWrapper;
}
