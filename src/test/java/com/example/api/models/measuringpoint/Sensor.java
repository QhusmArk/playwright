package com.example.api.models.measuringpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

// todo: Lägg Mp.Sensor som nästlad klass i Mp, varpå det blir tydligare att Sensor aldrig lever själv.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    private String serial;  // todo: ändra till Integer

    @JsonProperty("infra_timestamp_from")
    private BigInteger infraTimestampFrom;

    @JsonProperty("infra_timestamp_to")
    private BigInteger infraTimestampTo;

    @JsonProperty("timestamp_from")
    private BigInteger timestampFrom;  // todo: ändra till Integer

    @JsonProperty("timestamp_to")
    private BigInteger timestampTo;  // todo: ändra till Integer

    @JsonProperty("datetime_from")
    private String datetimeFrom;

    @JsonProperty("datetime_to")
    private String datetimeTo;

}
