package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /project/id/blasts/id/settings
 */
@Data
@NoArgsConstructor
public class BlastSettingsGeneralPanel {
    PanelHeader panelHeader;
    Preface preface;
    FieldWrapper general;
}
