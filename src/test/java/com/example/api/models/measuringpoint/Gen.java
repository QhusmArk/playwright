package com.example.api.models.measuringpoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gen {

    private Double x;
    private Double y;
    private Double z;
    private Double unit;

}
