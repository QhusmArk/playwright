package com.example.playwright.components.panels.message_rule;

import com.example.playwright.components.parts.NoticeItem;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Panel for both MessageRuleSettings...Panel and MessageRuleCreate...Panel
 * .../project/10523/message_rules/36491/settings/thresholds
 * .../project/10523/message_rules/create + sending_thresholds-tab
 */
@Getter
@Setter
@NoArgsConstructor
public class MessageRuleSendingThresholdPanel  {
    PanelHeader panelHeader;
    Preface preface;
    NoticeItem transientReportWarningMsg;

    FieldWrapper measuringPointWrapper;
    Table mpTable;
}
