package com.example.playwright.components.panels.message_rule;

import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MessageRuleDetailsPanel  {

    private PanelHeader panelHeader;
    private Preface preface;            // with on/off toggle

//    private Map<String, Object> mpSummary;
    Button mpSummaryButton;
//    private Map<String, Object> recipientSummary;
    Button recipientSummaryButton;
    private List<String> content;

    private SettingsItem settings;
}
