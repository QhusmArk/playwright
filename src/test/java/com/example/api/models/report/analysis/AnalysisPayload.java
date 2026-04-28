package com.example.api.models.report.analysis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalysisPayload {

    @JsonProperty("freq_op")
    private FrequencyOperation freqOp;

    private Standard standard;

    @JsonProperty("high_pass")
    private Integer highPass;

    @JsonProperty("low_pass")
    private Integer lowPass;

    @JsonProperty("time_op")
    private String timeOp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FrequencyOperation {
        private String operation;
        private String bw;
        private String resp;
        @JsonProperty("fft_type")
        private String fftType;
        @JsonProperty("fft_win")
        private String fftWin;
        private Integer q;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Standard {
        @JsonProperty("standard_id")
        private String standardId;
        private Boolean noRms;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // optional: ignore nulls

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
