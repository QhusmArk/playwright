package com.example.playwright.components.aside.asideItems.listItems;

import com.example.helpers.StatusAssesser;
import com.example.playwright.components.aside.asideItems.AsideItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BlastItem extends AsideItem {

    StatusAssesser.Status status;

    @JsonIgnore
    public String getName() {
        return this.getMainText();
    }
}
