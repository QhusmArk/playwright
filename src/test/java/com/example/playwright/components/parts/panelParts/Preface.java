package com.example.playwright.components.parts.panelParts;

import com.example.playwright.components.parts.Icon;
import com.example.playwright.components.parts.ToggleField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preface {

    private Icon leftIcon;
    private String text;
    private List<String> subTexts;
    private ToggleField toggleField;
    private Icon expansionIcon;

}
