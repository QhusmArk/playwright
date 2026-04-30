package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * As can be found in create user panel.
 */
@Getter
@Setter
@NoArgsConstructor
public class PanelListItem {
    private Icon leftIcon;
    private String mainText;
    private String subText;
    private ToggleField toggleField;
}
