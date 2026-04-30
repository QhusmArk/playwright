package com.example.playwright.components.panels.project;

import com.example.playwright.components.parts.NoticeItem;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * .../project/10523/settings/agendas/26460/timeslot/add
 */
@Getter
@Setter
@NoArgsConstructor
public class AgendaAddTimeslotPanel {

    PanelHeader panelHeader;
    NoticeItem overlapMessage;
    Preface preface;

    FieldWrapper timeSlotWrapper;
    FieldWrapper repeatWrapper;
}
