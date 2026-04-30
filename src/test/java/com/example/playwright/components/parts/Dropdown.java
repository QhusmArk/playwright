package com.example.playwright.components.parts;

import com.example.helpers.StatusAssesser;
import com.example.helpers.StatusAssesser.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
public class Dropdown  {
    String header;
    String text;
    String footer;
    Status status;

    List<String> expandedDropdownContent;

    // todo: gör om till setter
    public Dropdown(Map<String, String> dropdownMap){
        this.header = dropdownMap.get("header");
        this.text = dropdownMap.get("text");
        this.footer = dropdownMap.get("footer");
        this.status = StatusAssesser.getDropdownStatus(dropdownMap.get("className"));
    }
}
