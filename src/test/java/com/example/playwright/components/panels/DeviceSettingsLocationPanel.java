package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /devices/C22/101915/settings/coordinates
 */
@Data
@NoArgsConstructor
public class DeviceSettingsLocationPanel {
    PanelHeader panelHeader;
    Preface preface;

    FieldWrapper positionSettingsWrapper;
    FieldWrapper locationWrapper;
}
