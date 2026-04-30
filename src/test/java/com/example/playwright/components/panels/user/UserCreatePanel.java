package com.example.playwright.components.panels.user;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * /project/id/users/create
 * /company/users/create
 */
@Getter
@Setter
@NoArgsConstructor
public class UserCreatePanel {

    PanelHeader panelHeader;

    FieldWrapper generalWrapper;
    FieldWrapper projectAccessWrapper;
    FieldWrapper messageRuleAccessWrapper;
}
