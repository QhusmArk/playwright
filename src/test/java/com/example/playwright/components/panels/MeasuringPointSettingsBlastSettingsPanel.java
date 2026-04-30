package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeasuringPointSettingsBlastSettingsPanel {
    private PanelHeader panelHeader;

    private Preface preface;

    private FieldWrapper distance;
    private FieldWrapper guideValue;
    private FieldWrapper alertAndAlarm;
}
