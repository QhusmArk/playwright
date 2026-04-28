package com.example.api.models.measuringpoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Gen gen;
    private Wgs84 wgs84;
    private String description;
}
