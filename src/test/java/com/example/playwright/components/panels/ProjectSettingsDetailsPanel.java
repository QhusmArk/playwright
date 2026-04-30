package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NB. Panel header and url do not match for this object.
 * /project/id/settings/general
 */
@Data
@NoArgsConstructor
public class ProjectSettingsDetailsPanel {
    private PanelHeader panelHeader;

    private Preface preface;

    private FieldWrapper generalWrapper;

    private FieldWrapper customerSettingsWrapper;

    private FieldWrapper timeFrameWrapper;

    private FieldWrapper activeBlastPartWrapper;
}
