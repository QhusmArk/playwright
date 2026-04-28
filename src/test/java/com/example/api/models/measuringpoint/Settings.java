package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settings {
    @JsonProperty("0")
    private Map<String, Integer> map0;

    @JsonProperty("1")
    private Map<String, Integer> map1;

    @JsonProperty("2")
    private Map<String, Integer> map2;

    @JsonProperty("3")
    private Map<String, Integer> map3;

    @JsonProperty("4")
    private Map<String, Integer> map4;

    @JsonProperty("5")
    private Map<String, Integer> map5;

    @JsonProperty("6")
    private Map<String, Integer> map6;

    @JsonProperty("7")
    private Map<String, Integer> map7;

    @JsonProperty("8")
    private Map<String, Integer> map8;

    @JsonProperty("9")
    private Map<String, Integer> map9;

    public void addSetting(final int accumulationSpan, final int baseline, final int trigg, final int counter) {
        Map<String, Integer> map = new HashMap<>();
        map.put("accumulation_span", accumulationSpan);
        map.put("baseline", baseline);
        map.put("trigg", trigg);

        switch (counter) {
            case 0:
                map0 = map;
                break;
            case 1:
                map1 = map;
                break;
            case 2:
                map2 = map;
                break;
            case 3:
                map3 = map;
                break;
            case 4:
                map4 = map;
                break;
            case 5:
                map5 = map;
                break;
            case 6:
                map6 = map;
                break;
            case 7:
                map7 = map;
                break;
            case 8:
                map8 = map;
                break;
            case 9:
                map9 = map;
                break;
        }
    }
}
