package com.example.playwright.components.panels.measuringPoint;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// /project/10523/measure_points/280287/settings/agenda-settings
@Getter
@Setter
@Data
public class MeasurePointSettingsAgendaSettingsPanel {
    PanelHeader panelHeader;
    Preface preface;

    FieldWrapper agendaFieldWrapper;
    List<FieldWrapper> timeslotFieldWrappers;
}
