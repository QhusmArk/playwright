package com.example.playwright.components.panels.project;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * /projects/id/settings/agendas
 */
@Getter
@Setter
@NoArgsConstructor
public class ProjectSettingsAgendasPanel {
    private PanelHeader panelHeader;
    private Preface preface;

//    private FieldWrapper agendasWrapper;
    private Button copyAgendaButton;
    private Button createAgendaButton;

    private Table agendasTable;
}
