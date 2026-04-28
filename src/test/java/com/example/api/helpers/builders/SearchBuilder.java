package com.example.api.helpers.builders;

import com.example.api.models.report.DataTypes;
import com.example.api.models.report.Device;
import com.example.api.models.report.MeasurePoint;
import com.example.api.models.report.Search;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class SearchBuilder implements BuilderInterface<Search> {

    private Search search;
    private String id;
    private String timeFrom;
    private String timeTo;
    private String name;
    private Boolean shared;
    private DataTypes dataTypes;
    private List<MeasurePoint> measurePoints;   // Project Mp Billing Report
    private List<Device> devices;   // Account Device Billing Report
    private List<String> tags;
    private Integer aggregator;

    public SearchBuilder givenTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return this;
    }

    public SearchBuilder withAggregator(final Integer aggregator) {
        this.aggregator = aggregator;
        return this;
    }

    public SearchBuilder addTag(final String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
        return this;
    }

    // todo: replace with addTag?
    public SearchBuilder addPreload() {
        this.tags = new ArrayList<>();
        this.tags.add("preload");
        return this;
    }

    public SearchBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public SearchBuilder withId(final String id) {
        this.id = id;
        return this;
    }

    public SearchBuilder withDateTimeFrom(final String timeFrom) {
        this.timeFrom = timeFrom;
        return this;
    }

    public SearchBuilder withDateTimeTo(final String timeTo) {
        this.timeTo = timeTo;
        return this;
    }

    public SearchBuilder givenMeasurePoint() {
        if (measurePoints == null) {
            measurePoints = new ArrayList<>();
        }
        return this;
    }

    public SearchBuilder thenMeasuringPointId(final int id) {
        if (measurePoints == null) {
            throw new IllegalStateException("Method \"addMeasurePoint\" must be set first!");
        }
        MeasurePoint measurePoint = MeasurePoint.builder().build();
        measurePoint.setId(id);
        measurePoints.add(measurePoint);
        return this;
    }

    public SearchBuilder thenDevice(final int serial, final String type) {
        if (devices == null) {
            throw new IllegalStateException("Method \"givenDevices\" must be set first!");
        }
        Device device = Device.builder().build();
        device.setSerial(serial);
        device.setType(type);
        devices.add(device);
        return this;
    }

    public SearchBuilder givenDataTypes() {
        dataTypes = DataTypes.builder().build();
        return this;
    }

    public SearchBuilder givenDevices() {
        if (devices == null) {
            devices = new ArrayList<>();
        }
        return this;
    }

    public SearchBuilder thenDataTypeInterval(final boolean interval) {
        if (dataTypes == null) {
            throw new NullPointerException("Method \"givenDataTypes\" must be set first!");
        }
        dataTypes.setInterval(interval);
        return this;
    }

    public SearchBuilder thenDataTypeBlast(final boolean blast) {
        if (dataTypes == null) {
            throw new IllegalStateException("Method \"givenDataTypes\" must be set first!");
        }
        dataTypes.setBlast(blast);
        return this;
    }

    public SearchBuilder thenDataTypeMonon(final boolean monon) {
        if (dataTypes == null) {
            throw new IllegalStateException("Method \"givenDataTypes\" must be set first!");
        }
        dataTypes.setMonon(monon);
        return this;
    }

    public SearchBuilder thenDataTypeSio(final boolean sio) {
        if (dataTypes == null) {
            throw new IllegalStateException("Method \"givenDataTypes\" must be set first!");
        }
        dataTypes.setSio(sio);
        return this;
    }

    public SearchBuilder thenDataTypeTransient(final boolean trans) {
        if (dataTypes == null) {
            throw new IllegalStateException("Method \"givenDataTypes\" must be set first!");
        }
        dataTypes.setTransientData(trans);
        return this;
    }

    public SearchBuilder givenReportName(final String name) {
        this.name = name;
        return this;
    }

    public SearchBuilder thenShared(final boolean shared) {
        this.shared = shared;
        return this;
    }





    @Override
    public Search getProvider() {
        return search;
    }

    @Override
    public void setProvider(Search provider) {
        this.search = provider;
    }

    @Override
    public void build() {
        if (search == null) {
            search = new Search();
        }
        if (timeFrom != null) {
            search.setDatetimeFrom(timeFrom);
        }
        if (timeTo != null) {
            search.setDatetimeTo(timeTo);
        }
        if (name != null) {
            search.setName(name);
        }
        if (id != null) {
            search.setId(id);
        }
        if (shared != null) {
            search.setShared(shared);
        }
        if (measurePoints != null) {
            search.setMeasurePoints(measurePoints);
        }
        if (dataTypes != null) {
            search.setDataTypes(dataTypes);
        }
        if (devices != null) {
            search.setDevices(devices);
        }
        if (tags != null) {
            search.setTags(tags);
        }
        if (aggregator != null) {
            search.setAggregator(aggregator);
        }
    }

    /**
     * NB. All calls to buildJson() must be done after call to build()
     */
    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(search);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }


}
