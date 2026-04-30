package com.example.playwright.components.aside.asideItems.listItems;

import com.example.playwright.components.aside.asideItems.AsideItem;
import com.example.playwright.helpers.enums.IconType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.NoSuchElementException;

@Getter
@Setter
@NoArgsConstructor
public class OverviewItem extends AsideItem {

    @JsonIgnore
    public static OverviewItem getOverviewItemByIconType(List<AsideItem> asideItems, IconType iconType) {
        switch (iconType) {
            case MAP_UNITS, HARDWARE, BLAST, REPORTS, SMS_MAIL, USER, COMMENTS, SETTINGS -> {
                return asideItems.stream()
                        .filter(asideItem -> asideItem.getLeftIcon().getType().equals(iconType))
                        .filter(OverviewItem.class::isInstance)
                        .map(OverviewItem.class::cast)
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("No OverviewItem found with iconType: " + iconType));
            }
            default -> throw new IllegalArgumentException("Unsupported iconType: " + iconType);
        }
    }

    @JsonIgnore
    public String getText() {
        return this.getMainText();
    }

    /**
     * @return subText of Comment Overview Item, ie, the top comment from .../project/10523/comments
     */
    @JsonIgnore
    public String getTopComment() {
        return super.getSubText();
    }
}
