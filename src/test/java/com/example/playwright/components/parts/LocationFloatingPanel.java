package com.example.playwright.components.parts;

import com.example.playwright.helpers.enums.IconType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationFloatingPanel {

    private IconType panelIcon;
    private Double latitude;
    private Double longitude;

    public void setLatitude(String latitudeAsString) {
        this.latitude = Double.parseDouble(latitudeAsString);
    }

    public void setLongitude(String longitudeAsString) {
        this.longitude = Double.parseDouble(longitudeAsString);
    }
}
