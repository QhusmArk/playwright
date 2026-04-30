package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeasuringPointCreateVibrationReportPanel {

    PanelHeader panelHeader;

    FieldWrapper date;
}
