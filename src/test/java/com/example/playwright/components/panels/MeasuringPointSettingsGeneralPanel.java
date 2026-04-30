package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /project/10523/measure_points/279616/settings/general
 */
@Data
@NoArgsConstructor
public class MeasuringPointSettingsGeneralPanel {
    private PanelHeader panelHeader;

    private Preface preface;

    FieldWrapper generalSettings;
}
