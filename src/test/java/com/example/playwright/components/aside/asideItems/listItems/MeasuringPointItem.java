package com.example.playwright.components.aside.asideItems.listItems;

import com.example.playwright.components.aside.asideItems.AsideItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MeasuringPointItem extends AsideItem {

    @JsonIgnore
    public String getName() {
        return this.getMainText();
    }

}
