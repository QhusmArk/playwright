package com.example.api.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Change {
    @JsonProperty("config_id")
    private String configId;
    // todo: ändra detta så det även mappar för icke-vib-sensorer
    private Vib vib;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("company_id")
    private String companyId;
    private Integer created;

    @JsonProperty("self_url")
    private String selfUrl;

    @JsonProperty("sensors_hash")
    private String sensorsHash;
    private String hash;
    private List<Sensor> sensors;
    private String state;
    private String special;
    private Logger logger;
    private String serial;

    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
