package com.example.playwright.components.parts;

import com.example.playwright.helpers.enums.ColourSchema;
import com.example.playwright.helpers.enums.IconType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Main purpose of an icon is to display affinity or a state.
 * In the DOM a button can have an icon, but for the modeling we go for appearance.
 * Icons can be buttons, but buttons are not always icons.
 */
@Getter
@Setter
@NoArgsConstructor
public class Icon  {

    IconType type;
    ColourSchema colour;
}
