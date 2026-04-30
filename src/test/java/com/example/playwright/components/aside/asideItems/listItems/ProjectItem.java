package com.example.playwright.components.aside.asideItems.listItems;

import com.example.playwright.components.aside.asideItems.AsideItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectItem extends AsideItem {

    @JsonIgnore
    public String getProjectName() {
        return super.getMainText();
    }

    @JsonIgnore
    public String getProjectId() {
        String subText = super.getSubText();
        return extractProjectId(subText);
    }

    private String extractProjectId(String projectSubText) {
        // Finding the index of "ID: " to extract and set projectId
        int idDelimiter = projectSubText.indexOf("ID: ");
        // Extracting substring after "ID: "
        return projectSubText.substring(idDelimiter + "ID: ".length()).trim();
    }

    private String extractDescription(String projectSubText) {
        // Using ", ID" as the delimiter to extract, and set, description
        int descrDelimiter = projectSubText.indexOf(", ID");
        if (descrDelimiter == -1) {
            return "";
        } else {
            return projectSubText.substring(0, descrDelimiter).trim();
        }
    }
}
