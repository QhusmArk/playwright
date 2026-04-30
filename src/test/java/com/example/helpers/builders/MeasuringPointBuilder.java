package com.example.helpers.builders;

import com.example.api.models.measuringpoint.*;
import com.example.api.models.measuringpoint.Properties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.util.*;

public class MeasuringPointBuilder implements BuilderInterface<MeasuringPoint> {

    private MeasuringPoint mp;
    private String name;
    private String sensorType;
    private String timezone;
    private String timeFrom;
    private String timeTo;
    private Boolean active;
    private Location location;
    private Wgs84 wgs84;
    private List<Sensor> sensorList;
    private Sensor sensor;
    private CustomAgendaSettings customAgendaSettings;
    private NoiseReportSettings noiseReportSettings;
    private VibrationReportSettings vibrationReportSettings;
    private Settings settings;
    private int settingCounter;
    private GraphSettings graphSettings;
    private BlastProperties blastProperties;
    private Integer price;
    private Properties properties;

    public MeasuringPointBuilder withProperties(final Integer dbcorr) {
        if (properties == null) {
            properties = new Properties();
        }
        properties.setDbcorr(dbcorr);
        return this;
    }

    public MeasuringPointBuilder withPrice(final Integer price) {
        this.price = price;
        return this;
    }

    private boolean isValid(Sensor s) {
        return s.getSerial() == null && s.getInfraTimestampFrom() == null && s.getInfraTimestampTo() == null &&
                s.getTimestampFrom() == null && s.getTimestampTo() == null && s.getDatetimeFrom() == null && s.getDatetimeTo() == null;
    }

    public MeasuringPointBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public MeasuringPointBuilder withSensorType(final String sensorType) {
        this.sensorType = sensorType;
        return this;
    }

    public MeasuringPointBuilder withTimezone(final String timezone) {
        this.timezone = timezone;
        return this;
    }

    public MeasuringPointBuilder withTimeFrom(final String timeFrom) {
        this.timeFrom = timeFrom;
        return this;
    }

    public MeasuringPointBuilder withTimeTo(final String timeTo) {
        this.timeTo = timeTo;
        return this;
    }

    public MeasuringPointBuilder withIsActive(final boolean active) {
        this.active = active;
        return this;
    }

    public MeasuringPointBuilder givenLocation(final String description) {
        this.location = Location.builder().description(description).build();
        return this;
    }

    public MeasuringPointBuilder givenLocation() {
        this.location = Location.builder().build();
        return this;
    }

    public MeasuringPointBuilder thenWsg84Lat(final Double lat) {
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

    public MeasuringPointBuilder thenWsg84Lng(final Double lng) {
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

    public MeasuringPointBuilder thenWsg84Elevation(final Double elevation) {
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

    public MeasuringPointBuilder addSensor() {
        if (sensorList == null) {
            sensorList = new ArrayList<>();
        }
        sensor = Sensor.builder().build();
        sensorList.add(sensor);
        return this;
    }

    public MeasuringPointBuilder thenSensorSerial(final String serial) {
        if (sensor == null) {
            throw new NullPointerException("Method \"givenSensors\" must be set first!");
        }
        sensor.setSerial(serial);
        return this;
    }

    public MeasuringPointBuilder thenSensorTimeFrom(final String timeFrom) {
        if (sensor == null) {
            throw new NullPointerException("Method \"givenSensors\" must be set first!");
        }
        sensor.setDatetimeFrom(timeFrom);
        return this;
    }

    public MeasuringPointBuilder thenSensorTimeTo(final String timeTo) {
        if (sensor == null) {
            throw new NullPointerException("Method \"givenSensors\" must be set first!");
        }
        sensor.setDatetimeTo(timeTo);
        return this;
    }

    public MeasuringPointBuilder givenNoiseReportSettings() {
        noiseReportSettings = NoiseReportSettings.builder().build();
        return this;
    }

    public MeasuringPointBuilder withNoiseReportAgendaId(final String agendaId) {
        if (noiseReportSettings == null) {
            throw new NullPointerException("Method \"givenNoiseReportSettings\" must be set first!");
        }
        noiseReportSettings.setAgendaId(agendaId);
        return this;
    }

    public MeasuringPointBuilder addNoiseReportSetting(final int leqThreshold) {
        if (noiseReportSettings == null) {
            throw new NullPointerException("Method \"givenNoiseReportSettings\" must be set first!");
        }

        Map<String, Map<String, Integer>> noiseSettings = (noiseReportSettings.getSettings() == null)
                ? new HashMap<>()
                : noiseReportSettings.getSettings();

        // Deduct how many maps in settings, and create a new counter number
        int mapsInSettings = noiseSettings.size();
        String counter = String.valueOf(mapsInSettings + 1);

        Map<String, Integer> setting = new HashMap<>();
        setting.put("leq_threshold", leqThreshold);

        noiseSettings.put(counter, setting);

        noiseReportSettings.setSettings(noiseSettings);

        return this;
    }

    public MeasuringPointBuilder givenVibrationAgendaSettings() {
        this.vibrationReportSettings = VibrationReportSettings.builder().build();
        return this;
    }

    public MeasuringPointBuilder withVibrationAgendaSettingsId(final String agendaId) {
        if (vibrationReportSettings == null) {
            throw new NullPointerException("Method \"givenVibrationAgendaSettings\" must be set first!");
        }
        vibrationReportSettings.setAgendaId(agendaId);
        return this;
    }

    //**************************

    public MeasuringPointBuilder addVibrationReportSetting(final int vperThreshold) {
        if (vibrationReportSettings == null) {
            throw new NullPointerException("Method \"givenNoiseReportSettings\" must be set first!");
        }

        Map<String, Map<String, Integer>> vibrationSettings = (vibrationReportSettings.getSettings() == null)
                ? new HashMap<>()
                : vibrationReportSettings.getSettings();

        // Deduct how many maps in settings, and create a new counter number
        int mapsInSettings = vibrationSettings.size();
        String counter = String.valueOf(mapsInSettings + 1);

        Map<String, Integer> setting = new HashMap<>();
        setting.put("vper_threshold", vperThreshold);

        vibrationSettings.put(counter, setting);

        vibrationReportSettings.setSettings(vibrationSettings);

        return this;
    }

    //**************************




    public MeasuringPointBuilder givenCustomAgendaSettings() {
        customAgendaSettings = CustomAgendaSettings.builder().build();
        return this;
    }

    public MeasuringPointBuilder withCustomAgendaSettingsType(final String type) {
        if (customAgendaSettings == null) {
            throw new NullPointerException("Method \"givenCustomAgendaSettings\" must be set first!");
        }
        customAgendaSettings.setType(type);
        return this;
    }

    public MeasuringPointBuilder withCustomAgendaSettingsId(final String agendaId) {
        if (customAgendaSettings == null) {
            throw new NullPointerException("Method \"givenCustomAgendaSettings\" must be set first!");
        }
        customAgendaSettings.setAgendaId(agendaId);
        return this;
    }

    public MeasuringPointBuilder addSetting(final int accumulationSpan, final int baseline, final int trigg) {
        if (customAgendaSettings == null) {
            throw new NoSuchElementException("Method \"givenCustomAgendaSettings\" must be set first!");
        }
        if (settings == null) {
            settings = Settings.builder().build();
        }
        settings.addSetting(accumulationSpan, baseline, trigg, settingCounter);
        customAgendaSettings.setSettings(settings);
        settingCounter++;
        return this;
    }

    /**
     * Delete Agenda from Measuring Point
     */
    public void removeCustomAgendaSettings() {
        settings = null;
        customAgendaSettings = null;
        if(mp != null) {
            mp.setCustomAgendaSettings(null);
        }
    }

    public MeasuringPointBuilder givenGraphSettings() {
        graphSettings = GraphSettings.builder().build();
        return this;
    }

    public MeasuringPointBuilder thenGraphSettingsGraphType(final String graphType) {
        if (graphSettings == null) {
            throw new NullPointerException("Method \"givenGraphSettings\" must be set first!");
        }
        graphSettings.setGraphType(graphType);
        return this;
    }

    public MeasuringPointBuilder thenGraphSettingsLayoutMode(final String layoutMode) {
        if (graphSettings == null) {
            throw new NullPointerException("Method \"givenGraphSettings\" must be set first!");
        }
        graphSettings.setLayoutMode(layoutMode);
        return this;
    }

    public MeasuringPointBuilder thenGraphSettingsYmin(final int yMin) {
        if (graphSettings == null) {
            throw new NullPointerException("Method \"givenGraphSettings\" must be set first!");
        }
        graphSettings.setYMin(yMin);
        return this;
    }

    public MeasuringPointBuilder thenGraphSettingsYmax(final int yMax) {
        if (graphSettings == null) {
            throw new NullPointerException("Method \"givenGraphSettings\" must be set first!");
        }
        graphSettings.setYMax(yMax);
        return this;
    }

    public MeasuringPointBuilder thenGraphSettingsPlaySound(final boolean playSound) {
        if (graphSettings == null) {
            throw new NullPointerException("Method \"givenGraphSettings\" must be set first!");
        }
        graphSettings.setPlaySound(playSound);
        return this;
    }

    public MeasuringPointBuilder thenGraphSettingsShowTransientPpvzx(final boolean showTransientPpvzx) {
        if (graphSettings == null) {
            throw new NullPointerException("Method \"givenGraphSettings\" must be set first!");
        }
        graphSettings.setShowTransientPpvzx(showTransientPpvzx);
        return this;
    }

    public MeasuringPointBuilder givenBlastProperties() {
        blastProperties = BlastProperties.builder().build();
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesAlert(final int alert) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setAlert(alert);
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesAlarm(final Integer alarm) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setAlarm(alarm);
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesMaxValues(final String maxValues) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setMaxValues(maxValues);
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesGuideValue(final Double guideValue) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setGuideValue(guideValue);
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesUncorrectedFrequency(final Integer uncorrectedFrequency) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setUncorrectedFrequency(uncorrectedFrequency);
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesDistanceDependent(final Boolean distanceDependent) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setDistanceDependent(distanceDependent);
        return this;
    }

    public MeasuringPointBuilder thenBlastPropertiesStaticDistance(final Integer staticDistance) {
        if (blastProperties == null) {
            throw new NullPointerException("Method \"givenBlastProperties\" must be set first!");
        }
        blastProperties.setStaticDistance(staticDistance);
        return this;
    }

    @Override
    public void build() {
        if (sensorList != null) {
            boolean emptySensorList = sensorList.stream()
                    .allMatch(s -> isValid(s));
            if (emptySensorList) {
                throw new IllegalStateException("No point in using \"givenSensors\" if it's not followed by a addSensor... method!");
            }
        }

        if (customAgendaSettings != null && settings == null) {
            throw new IllegalStateException("No point in using \"givenCustomAgendaSettings\" if it's not followed by a addSetting method!");
        }

        if (graphSettings != null) {
            if (graphSettings.getGraphType() == null && graphSettings.getLayoutMode() == null) {
                throw new IllegalStateException("No point in using \"givenGraphSettings\" if it's not followed by a withGraphSettings... method!");
            }
        }

        if (mp == null) {
            mp = new MeasuringPoint();
        }
        if (name != null) {
            mp.setName(name);
        }
        if (sensorType != null) {
            mp.setSensorType(sensorType);
        }
        if (timezone != null) {
            mp.setTimezone(timezone);
        }
        if (timeFrom != null) {
            mp.setDatetimeFrom(timeFrom);
        }
        if (timeTo != null) {
            mp.setDatetimeTo(timeTo);
        }
        if (active != null) {
            mp.setActive(active);
        }
        if (location != null) {
            mp.setLocation(location);
        }
        if (sensorList != null) {
            mp.setSensors(sensorList);
        }
        if (customAgendaSettings != null) {
            mp.setCustomAgendaSettings(customAgendaSettings);
        }
        if (noiseReportSettings != null) {
            mp.setNoiseReportSettings(noiseReportSettings);
        }
        if (vibrationReportSettings != null) {
            mp.setVibrationReportSettings(vibrationReportSettings);
        }
        if (graphSettings != null) {
            mp.setGraphSettings(graphSettings);
        }
        if (blastProperties != null) {
            mp.setBlastProperties(blastProperties);
        }
        if (price != null) {
            mp.setPrice(price);
        }
        if (properties != null) {
            mp.setProperties(properties);
        }
    }

    @Override
    public MeasuringPoint getProvider() {
        return mp;
    }

    @Override
    public void setProvider(MeasuringPoint provider) {
        this.mp = provider;
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        //Filter are to prevent serialization of provider-object-attribute that's NOT to be sent to API.
        SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(true);
        simpleFilterProvider.addFilter("mpFilter",
                SimpleBeanPropertyFilter.serializeAllExcept("order", "elevation", "id", "timestamp_from", "timestamp_to", "status_code"));

        try {
            return mapper.writer(simpleFilterProvider).withDefaultPrettyPrinter().writeValueAsString(mp);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }
}
