package com.example.playwright.components.panels.project;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * /project/10523/settings/agendas/28495
 */
@Getter
@Setter
@NoArgsConstructor
public class ProjectSettingsAgendaPanel {

    PanelHeader panelHeader;
    Preface preface;

    Table timeslotTable;

    Button addTimeSlot;

    SettingsItem agendaName;
    SettingsItem delete;
}
