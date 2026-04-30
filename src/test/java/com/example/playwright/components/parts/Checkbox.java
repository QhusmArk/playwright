package com.example.playwright.components.parts;

import com.example.helpers.StatusAssesser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Checkbox {

    StatusAssesser.Status status;       // UNCHECKED, CHECKED, MIXED, DISABLED(SDR)
    String text;
}
