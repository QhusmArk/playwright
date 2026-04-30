package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSettingsPanel {
    PanelHeader panelHeader;
    Preface preface;

}
