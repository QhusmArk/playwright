package com.example.api.models.report;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Blast {

    @JsonProperty("project_id")
    private Integer projectId;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("blast_id")
    private String blastId;     //namn

    @JsonProperty("blast_type")
    private String blastType;

    @JsonProperty("supervisor_id")
    private Integer supervisorId;

    @JsonProperty("max_instantaneous_charge")
    private Double maxInstantaneousCharge;

    private String datetime;

    @JsonProperty("time_span")
    private BigInteger timeSpan;

    @JsonProperty("hole_diameter")
    private Double holeDiameter;

    @JsonProperty("total_holes")
    private Double totalHoles;

    @JsonProperty("hole_angle")
    private Double holeAngle;

    private Double burden;

    @JsonProperty("hole_spacing")
    private Double holeSpacing;

    @JsonProperty("hole_subdrilling")
    private Double holeSubdrilling;

    @JsonProperty("total_length_drilled")
    private Double totalLengthDrilled;

    @JsonProperty("hole_rows")
    private Double holeRows;

    @JsonProperty("hole_depth_min")
    private Double holeDepthMin;

    @JsonProperty("hole_depth_max")
    private Double holeDepthMax;

    @JsonProperty("section_length")
    private Double sectionLength;

    @JsonProperty("total_area")
    private Double totalArea;

    @JsonProperty("total_charge_weight")
    private Double totalChargeWeight;

    @JsonProperty("total_primer_weight")
    private Double totalPrimerWeight;

    @JsonProperty("charge_per_hole_min")
    private Double chargePerHoleMin;

    @JsonProperty("charge_per_hole_max")
    private Double chargePerHoleMax;

    @JsonProperty("charge_concentration_min")
    private Double chargeConcentrationMin;

    @JsonProperty("charge_concentration_max")
    private Double chargeConcentrationMax;

    private Integer stemming;

    @JsonProperty("explosive_type")
    private String explosiveType;

    @JsonProperty("detonator_type")
    private String detonatorType;

    private Double intervals;

    @JsonProperty("flyrock_protection")
    private String flyrockProtection;

    @JsonProperty("charge_unit")
    private String chargeUnit;

    @JsonProperty("length_unit")
    private String lengthUnit;

    private Location location;

    @JsonProperty("created_by")
    private String createdBy;

    private String notes;

    @JsonProperty("self_url")
    private String selfUrl;

    @JsonProperty("supervisor_name")
    private String supervisorName;

    private BigInteger timestamp;

    @JsonProperty("timestamp_created")
    private BigInteger timestampCreated;

    @JsonProperty("datetime_created")
    private String datetimeCreated;

    @JsonProperty("infra_timestamp_created")
    private BigInteger infraTimestampCreated;

    @JsonProperty("infra_timestamp_planned")
    private BigInteger infraTimestampPlanned;

    @JsonProperty("infra_timestamp_actual")
    private BigInteger infraTimestampActual;

    private Map<String, String[]> data;
}
