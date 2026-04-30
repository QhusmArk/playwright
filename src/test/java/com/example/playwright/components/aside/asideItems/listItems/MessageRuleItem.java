package com.example.playwright.components.aside.asideItems.listItems;

import com.example.playwright.components.aside.asideItems.AsideItem;
import com.example.playwright.components.parts.Banner;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRuleItem extends AsideItem {

    @JsonIgnore
    public Banner getBanner() {
        return super.getBanners().getFirst();
    }

    @JsonIgnore
    public String getName() {
        return this.getMainText();
    }
}
