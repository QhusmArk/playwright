package com.example.api.models.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trigger {

    private String name;
    private Boolean trig_recording_enable;
    //todo: should be Double, as trigger value can be with digits
    private Double trig_recording_value;
}
