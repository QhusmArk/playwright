package com.example.playwright.components.parts;

import com.example.helpers.StatusAssesser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A ToggleField is the on/off toggle + the associated text on left or right + header text above.
 */
@Getter
@Setter
@NoArgsConstructor
public class ToggleField {
    String headerText;
    private String sideText;

    private Boolean state;
    StatusAssesser.Status status;

    Toggle toggle;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Toggle {
        private Boolean state;  //true/false:ON/OFF
    }
}
