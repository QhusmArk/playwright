package com.example.playwright.components.panels;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MeasuringPointSettingsActiveChannelsPanel {
    private PanelHeader panelHeader;

    private Preface preface;

    private List<FieldWrapper> channelWrappers;
}
