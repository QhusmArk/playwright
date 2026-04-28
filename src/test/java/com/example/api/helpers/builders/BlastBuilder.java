package com.example.api.helpers.builders;

import com.example.api.models.blast.Blast;
import com.example.api.models.blast.Location;
import com.example.api.models.blast.Wgs84;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BlastBuilder implements BuilderInterface<Blast> {

    private Blast blast;

    // General settings
    private String blastId;
    private Double maxInstantaneousCharge;
    private String blastType;
    private Integer supervisorId;
    private String createdBy;

    // Time settings
    private String datetime;
    private Integer timeSpan;

    // Holes
    private Double holeDiameter;
    private Double totalHoles;
    private Double holeAngle;
    private Double burden;
    private Double holeSpacing;
    private Double holeSubdrilling;
    private Double totalLengthDrilled;
    private Double holeRows;
    private Double holeDepthMin;
    private Double holeDepthMax;
    private Double sectionLength;
    private Double totalArea;

    // Charge
    private Double totalChargeWeight;
    private Double totalPrimerWeight;
    private Double chargePerHoleMin;
    private Double chargePerHoleMax;
    private Double chargeConcentrationMin;
    private Double chargeConcentrationMax;
    private Double stemming;
    private String explosiveType;
    private String detonatorType;
    private Double intervals;
    private String flyrockProtection;

    // Measuring units
    private String chargeUnit;
    private String lengthUnit;

    private Location location;
    private String description;
    private Wgs84 wgs84;


    public BlastBuilder withBlastName(final String blastId) {
        this.blastId = blastId;
        return this;
    }

    public BlastBuilder withMaxInstantaneousCharge(final Double maxInstantaneousCharge) {
        this.maxInstantaneousCharge = maxInstantaneousCharge;
        return this;
    }

    public BlastBuilder withBlastType(final String blastType) {
        this.blastType = blastType;
        return this;
    }

    public BlastBuilder withSupervisor(final Integer supervisorId) {
        this.supervisorId = supervisorId;
        return this;
    }

    public BlastBuilder withCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public BlastBuilder withDatetime(final String datetime) {
        this.datetime = datetime;
        return this;
    }

    public BlastBuilder withTimeSpan(final Integer timeSpan) {
        this.timeSpan = timeSpan;
        return this;
    }

    public BlastBuilder withHoleDiameter(final Double holeDiameter) {
        this.holeDiameter = holeDiameter;
        return this;
    }

    public BlastBuilder withTotalHoles(final Double totalHoles) {
        this.totalHoles = totalHoles;
        return this;
    }

    public BlastBuilder withHoleAngle(final Double holeAngle) {
        this.holeAngle = holeAngle;
        return this;
    }

    public BlastBuilder withBurden(final Double burden) {
        this.burden = burden;
        return this;
    }

    public BlastBuilder withHoleSpacing(final Double holeSpacing) {
        this.holeSpacing = holeSpacing;
        return this;
    }

    public BlastBuilder withHoleSubdrilling(final Double holeSubdrilling) {
        this.holeSubdrilling = holeSubdrilling;
        return this;
    }

    public BlastBuilder withTotalLengthDrilled(final Double totalLengthDrilled) {
        this.totalLengthDrilled = totalLengthDrilled;
        return this;
    }

    public BlastBuilder withHoleRows(final Double holeRows) {
        this.holeRows = holeRows;
        return this;
    }

    public BlastBuilder withHoleDepthMin(final Double holeDepthMin) {
        this.holeDepthMin = holeDepthMin;
        return this;
    }

    public BlastBuilder withHoleDepthMax(final Double holeDepthMax) {
        this.holeDepthMax = holeDepthMax;
        return this;
    }

    public BlastBuilder withSectionLength(final Double sectionLength) {
        this.sectionLength = sectionLength;
        return this;
    }

    public BlastBuilder withTotalArea(final Double totalArea) {
        this.totalArea = totalArea;
        return this;
    }
    public BlastBuilder withTotalChargeWeight(final Double totalChargeWeight) {
        this.totalChargeWeight = totalChargeWeight;
        return this;
    }

    public BlastBuilder withTotalPrimerWeight(final Double totalPrimerWeight) {
        this.totalPrimerWeight = totalPrimerWeight;
        return this;
    }

    public BlastBuilder withChargePerHoleMin(final Double chargePerHoleMin) {
        this.chargePerHoleMin = chargePerHoleMin;
        return this;
    }

    public BlastBuilder withChargePerHoleMax(final Double chargePerHoleMax) {
        this.chargePerHoleMax = chargePerHoleMax;
        return this;
    }

    public BlastBuilder withChargeConcentrationMin(final Double chargeConcentrationMin) {
        this.chargeConcentrationMin = chargeConcentrationMin;
        return this;
    }

    public BlastBuilder withChargeConcentrationMax(final Double chargeConcentrationMax) {
        this.chargeConcentrationMax = chargeConcentrationMax;
        return this;
    }

    public BlastBuilder withStemming(final Double stemming) {
        this.stemming = stemming;
        return this;
    }

    public BlastBuilder withExplosiveType(final String explosiveType) {
        this.explosiveType = explosiveType;
        return this;
    }

    public BlastBuilder withDetonatorType(final String detonatorType) {
        this.detonatorType = detonatorType;
        return this;
    }

    public BlastBuilder withIntervals(final Double intervals) {
        this.intervals = intervals;
        return this;
    }

    public BlastBuilder withFlyrockProtection(final String flyrockProtection) {
        this.flyrockProtection = flyrockProtection;
        return this;
    }

    public BlastBuilder withChargeUnit(final String chargeUnit) {
        this.chargeUnit = chargeUnit;
        return this;
    }

    public BlastBuilder withLengthUnit(final String lengthUnit) {
        this.lengthUnit = lengthUnit;
        return this;
    }

    public BlastBuilder givenLocation() {
        this.location = Location.builder().build();
        return this;
    }

    public BlastBuilder thenDescription(final String description) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        this.location.setDescription(description);
        return this;
    }

    public BlastBuilder thenWsg84Lat(final Double lat) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        if (wgs84 == null) {
            wgs84 = Wgs84.builder().build();
        }
        wgs84.setLat(lat);
        location.setWgs84(wgs84);
        return this;
    }

    public BlastBuilder thenWsg84Lng(final Double lng) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        if (wgs84 == null) {
            wgs84 = Wgs84.builder().build();
        }
        wgs84.setLng(lng);
        location.setWgs84(wgs84);
        return this;
    }

    public BlastBuilder thenWsg84Elevation(final Double elevation) {
        if (location == null) {
            throw new NullPointerException("Method \"givenLocation\" must be set first!");
        }
        if (wgs84 == null) {
            wgs84 = Wgs84.builder().build();
        }
        wgs84.setElevation(elevation);
        location.setWgs84(wgs84);
        return this;
    }

    @Override
    public Blast getProvider() {
        return blast;
    }

    @Override
    public void setProvider(Blast provider) {
        this.blast = provider;
    }

    @Override
    public void build() {
        if (blast == null) {
            blast = new Blast();
        }
        if (blastId != null) {
            blast.setBlastId(blastId);
        }
        if (maxInstantaneousCharge != null) {
            blast.setMaxInstantaneousCharge(maxInstantaneousCharge);
        }
        if (blastType != null) {
            blast.setBlastType(blastType);
        }
        if (supervisorId != null) {
            blast.setSupervisor(supervisorId);
        }
        if (createdBy != null) {
            blast.setCreatedBy(createdBy);
        }
        if (datetime != null) {
            blast.setDatetime(datetime);
        }
        if (timeSpan != null) {
            blast.setTimeSpan(timeSpan);
        }
        if (holeDiameter != null) {
            blast.setHoleDiameter(holeDiameter);
        }
        if (totalHoles != null) {
            blast.setTotalHoles(totalHoles);
        }
        if (holeAngle != null) {
            blast.setHoleAngle(holeAngle);
        }
        if (burden != null) {
            blast.setBurden(burden);
        }
        if (holeSpacing != null) {
            blast.setHoleSpacing(holeSpacing);
        }
        if (holeSubdrilling != null) {
            blast.setHoleSubdrilling(holeSubdrilling);
        }
        if (totalLengthDrilled != null) {
            blast.setTotalLengthDrilled(totalLengthDrilled);
        }
        if (holeRows != null) {
            blast.setHoleRows(holeRows);
        }
        if (holeDepthMin != null) {
            blast.setHoleDepthMin(holeDepthMin);
        }
        if (holeDepthMax != null) {
            blast.setHoleDepthMax(holeDepthMax);
        }
        if (sectionLength != null) {
            blast.setSectionLength(sectionLength);
        }
        if (totalArea != null) {
            blast.setTotalArea(totalArea);
        }
        if (totalChargeWeight != null) {
            blast.setTotalChargeWeight(totalChargeWeight);
        }
        if (totalPrimerWeight != null) {
            blast.setTotalPrimerWeight(totalPrimerWeight);
        }
        if (chargePerHoleMin != null) {
            blast.setChargePerHoleMin(chargePerHoleMin);
        }
        if (chargePerHoleMax != null) {
            blast.setChargePerHoleMax(chargePerHoleMax);
        }
        if (chargeConcentrationMin != null) {
            blast.setChargeConcentrationMin(chargeConcentrationMin);
        }
        if (chargeConcentrationMax != null) {
            blast.setChargeConcentrationMax(chargeConcentrationMax);
        }
        if (stemming != null) {
            blast.setStemming(stemming);
        }
        if (explosiveType != null) {
            blast.setExplosiveType(explosiveType);
        }
        if (detonatorType != null) {
            blast.setDetonatorType(detonatorType);
        }
        if (intervals != null) {
            blast.setIntervals(intervals);
        }
        if (flyrockProtection != null) {
            blast.setFlyrockProtection(flyrockProtection);
        }
        if (chargeUnit != null) {
            blast.setChargeUnit(chargeUnit);
        }
        if (lengthUnit != null) {
            blast.setLengthUnit(lengthUnit);
        }
        if (location != null) {
            blast.setLocation(location);
        }
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(blast);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
