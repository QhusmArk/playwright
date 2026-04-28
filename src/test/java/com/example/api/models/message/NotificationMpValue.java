package com.example.api.models.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


/**
 * NB. BE attach channels that often is used for it's mp-sensor type.
 * My guesstimate is that these channels are hardcoded in BE,
 * because the channels neither match mp.active channels, nor sensor.channels.
 * <p>
 * In the same way FE adds channels to the POST /project/10523/notification/41862/notification_mp_value/ request payload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationMpValue {

    private Integer id;

    @JsonProperty("measure_point_id")
    private Integer measurePointId;

    @JsonProperty("trig_type")
    private Integer trigType;
    private Integer percent;    // Keep as int. Smthing wrong with api that requires this as 0
    private Integer absolute;   // Keep as int. Smthing wrong with api that requires this as 0

    @JsonProperty("sensor_type")
    private String sensorType;

    // Channels, Properties, High:value
    private Map<String, Map<String, Map<String, Double>>> channels;

    public void setChannels(final String key, Map<String, Map<String, Double>> map) {
        if (channels == null) {
            channels = new HashMap<>();
        }
        channels.put(key, map);
    }
}
