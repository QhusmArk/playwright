package com.example.playwright.components.map;

import com.example.playwright.helpers.enums.IconType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MainPane {

    private List<IconType> zoomButtons;
    private List<IconType> buttonsInRightBottomCorner;
    private List<IconType> mapIcons;

    public void setZoomButton(String zoomButtonClassName) {
        if (zoomButtons == null) {
            zoomButtons = new ArrayList<>();
        }
        zoomButtons.add(IconType.fromClassName(zoomButtonClassName));
    }

    public void setButtonInRightBottomCorner(String className, String buttonText) {
        if (buttonsInRightBottomCorner == null) {
            buttonsInRightBottomCorner = new ArrayList<>();
        }
        buttonsInRightBottomCorner.add(IconType.fromWebElement(className, buttonText));
    }

    public void setMapIcon(String visibleMapMarkersSrc) {
        if (mapIcons == null) {
            mapIcons = new ArrayList<>();
        }
        mapIcons.add(IconType.fromClassName(visibleMapMarkersSrc));
    }

    @Override
    public String toString() {
        return "MainPane{" +
                "\nzoomButtons=" + zoomButtons +
                ", \nbuttonsInRightBottomCorner=" + buttonsInRightBottomCorner +
                ", \nmapIcons=" + mapIcons +
                '}';
    }
}
