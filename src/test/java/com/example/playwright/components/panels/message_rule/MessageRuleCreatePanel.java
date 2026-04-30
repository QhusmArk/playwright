package com.example.playwright.components.panels.message_rule;

import com.example.playwright.components.parts.Tab;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRuleCreatePanel  {

    List<Tab> tabs;

    MessageRuleGeneralPanel messageRuleGeneralPanel;
    MessageRuleContentPanel messageRuleContentPanel;
    MessageRuleSendingThresholdPanel messageRuleSendingThresholdPanel;
    MessageRuleRecipientsPanel messageRuleRecipientsPanel;
}
