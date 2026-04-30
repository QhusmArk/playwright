package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /project/10523/blasts/52704/settings/holes
 */
@Data
@NoArgsConstructor
public class BlastSettingsHolesPanel {
    PanelHeader panelHeader;
    Preface preface;
    FieldWrapper holes;
}
