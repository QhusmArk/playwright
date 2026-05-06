package com.example.playwright.components.aside.asideItems.listItems;

import com.example.helpers.Randomizer;
import com.example.playwright.components.aside.asideItems.AsideItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserItem extends AsideItem {

    @JsonIgnore
    public String getName() {
        return super.getMainText();
    }

    @JsonIgnore
    public String getCustomerContact() {
        return super.getSubText().contains(",")
                ? Randomizer.splitString(1, super.getSubText(), ", ")
                : null;
    }

    @JsonIgnore
    public String getRole() {
        return super.getSubText().contains(",")
                ? Randomizer.splitString(2, super.getSubText(), ", ")
                : super.getSubText();
    }
}
