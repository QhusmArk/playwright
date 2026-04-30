package com.example.playwright.components.parts;

import com.example.helpers.StatusAssesser;
import com.example.playwright.helpers.enums.ColourSchema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Main purpose of a button is to perform an action.
 * In this regard a button is not an icon.
 * A Button is always more than an Icon.
 * If it looks like an Icon, but behaves as a Button, then it's an Icon.
 */
@Getter
@Setter
@NoArgsConstructor
public class Button  {

    Icon icon;
    StatusAssesser.Status status;  // CLICKABLE, DISABLED,                             INVISIBLE? NOT_PRESENT?
    ColourSchema backgroundColour; // WHITE, GREEN, RED, YELLOW
    String text;
    Dropdown dropdown;

//    private ColourSchema textColour;      // not in use
//    private ColourSchema buttonBorder;    // not in use
//    private Status hoverableText;         // not in use

}


