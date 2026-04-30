package com.example.playwright.components.panels.message_rule;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Panel for both MessageRuleSettings...Panel and MessageRuleCreate...Panel
 *  * .../project/10523/message_rules/36491/settings/content
 *  * .../project/10523/message_rules/create + content-tab
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRuleContentPanel  {

    private PanelHeader panelHeader;
    private Preface preface;
    private FieldWrapper projectInfoWrapper;
    private FieldWrapper measuringPointInfoWrapper;
    private FieldWrapper measuredValueWrapper;

}
