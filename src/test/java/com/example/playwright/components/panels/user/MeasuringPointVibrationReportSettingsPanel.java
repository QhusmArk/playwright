package com.example.playwright.components.panels.user;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MeasuringPointVibrationReportSettingsPanel {

    PanelHeader panelHeader;
    Preface preface;

    FieldWrapper vibrationReport;
    List<FieldWrapper> timeslots;

}
