package com.example.api.models.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wgs84 {
    private Double lat;
    private Double lng;
    private Double elevation;
}
