package com.example.playwright.components.parts;

import com.example.helpers.StatusAssesser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import static com.example.helpers.StatusAssesser.Status.DISABLED;
import static com.example.helpers.StatusAssesser.Status.FILLABLE;

@Getter
@Setter
@NoArgsConstructor
public class InputField   {
    private String header;
    private String text;
    private String unit;
    private String footer;
    private StatusAssesser.Status status;

    public InputField(Map<String, String> dropdownMap) {
        this.header = dropdownMap.get("header");
        this.text = dropdownMap.get("text");
        this.unit = dropdownMap.get("unit");
        this.footer = dropdownMap.get("footer");
        this.status =  dropdownMap.get("className").contains("disabled")
                ? DISABLED
                : FILLABLE;
    }
}
