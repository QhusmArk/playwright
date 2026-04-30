package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class SettingsItem  {

    private Icon leftIcon;
    private String mainText;
    private String subText;

    private Icon expansionIcon;

    Map<String, String> blastCalculatedValuesMap;
    //    private StatusAssesser.Status status;   //  UNCLICKABLE, CLICKABLE    // to be implemented
}
