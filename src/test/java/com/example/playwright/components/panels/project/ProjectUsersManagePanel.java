package com.example.playwright.components.panels.project;

import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * /project/id/users/manage
 */
@Data
@NoArgsConstructor
public class ProjectUsersManagePanel {
    PanelHeader panelHeader;
    Preface preface;

    FieldWrapper usersWrapper;
    FieldWrapper clientPlusWrapper;
    FieldWrapper clientWrapper;
    FieldWrapper blasterWrapper;

}
