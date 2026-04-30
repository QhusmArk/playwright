package com.example.helpers.builders;

import com.example.api.models.device.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChangeBuilder implements BuilderInterface<Device> {

    private Device device;
    private Integer serial;
    private String calibrationDate;
    private String type;
    private String customName;          //User selected text in physical device
    private String description;         //User selected text in client
    private Boolean disabled;
    private String lastCommunication;
    private BigInteger created;

    private String configId;
    private Vib vib;

    // todo: channels hör till Vib, inte Device?!
    private List<Channel> channels;
    private List<Map<String, String>> monState;
    private List<Map<String, String>> batteryLevel;
    private List<Map<String, String>> batteryVoltage;
    private Logger logger;
    private Gps gps;
    private String hash;
    private String sensorsHash;
    private List<Sensor> sensors;
    private List<Object> smsRecipients;

    public ChangeBuilder withHash(String hash) {
        this.hash = hash;
        return this;
    }

    public ChangeBuilder withSensorsHash(final String sensorsHash) {
        this.sensorsHash = sensorsHash;
        return this;
    }

    public ChangeBuilder withLastCommunication(final String lastCommunication) {
        this.lastCommunication = lastCommunication;
        return this;
    }

    public ChangeBuilder withCalibrationDate(final String calibrationDate) {
        this.calibrationDate = calibrationDate;
        return this;
    }

    public ChangeBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public ChangeBuilder addBatteryLevelMap(final Map<String, String> batteryLevelMap) {
        if (this.batteryLevel == null) {
            this.batteryLevel = new ArrayList<>();
        }
        this.batteryLevel.add(batteryLevelMap);
        return this;
    }

    public ChangeBuilder addBatteryVoltage(final Map<String, String> batteryVoltageMap) {
        if (this.batteryVoltage == null) {
            this.batteryVoltage = new ArrayList<>();
        }
        this.batteryVoltage.add(batteryVoltageMap);
        return this;
    }

    public ChangeBuilder addMonStateMap(final Map<String, String> monStateMap) {
        if (this.monState == null) {
            this.monState = new ArrayList<>();
        }
        this.monState.add(monStateMap);
        return this;
    }

    public ChangeBuilder withSerial(final int serial) {
        this.serial = serial;
        return this;
    }

    public ChangeBuilder withType(final String type) {
        this.type = type;
        return this;
    }

    public ChangeBuilder withIsDisabled(final boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public ChangeBuilder withCreated(final BigInteger created) {
        this.created = created;
        return this;
    }

    public ChangeBuilder withConfigId(final String configId) {
        this.configId = configId;
        return this;
    }

    public ChangeBuilder thenStandard(final String standard) {
        if (vib == null) {
            throw new NullPointerException("Method \"givenVib\" must be set first!");
        }
        vib.setStandard(standard);
        return this;
    }

    public ChangeBuilder thenPostTrigTime(final int postTrigTime) {
        if (vib == null) {
            throw new NullPointerException("Method \"givenVib\" must be set first!");
        }
        vib.setPostTrigTime(postTrigTime);
        return this;
    }

    public ChangeBuilder thenInterval(final int interval) {
        if (vib == null) {
            throw new NullPointerException("Method \"givenVib\" must be set first!");
        }
        vib.setInterval(interval);
        return this;
    }

    public ChangeBuilder thenFrequencyWeighting(final String frequencyWeighting) {
        if (vib == null) {
            throw new NullPointerException("Method \"givenVib\" must be set first!");
        }
        vib.setFrequencyWeighting(frequencyWeighting);
        return this;
    }

    public ChangeBuilder givenVib() {
        vib = Vib.builder().build();
        return this;
    }

    public ChangeBuilder addSensor(int serial) {
        if (sensors == null) {
            this.sensors = new ArrayList<>();
        }
        Sensor s = Sensor.builder().serial(serial)
                .channels(new ArrayList<>()).build();
        sensors.add(s);
        return this;
    }

    public ChangeBuilder removeSmsRecipients() {
        this.smsRecipients = new ArrayList<>();
        return this;
    }

    public ChangeBuilder addSmsRecipient(Integer id) {
        if (smsRecipients == null) {
            this.smsRecipients = new ArrayList<>();
        }
        smsRecipients.add(id);
        return this;
    }


    public ChangeBuilder addSmsRecipient(String name, boolean data, String number, boolean service) {
        if (smsRecipients == null) {
            this.smsRecipients = new ArrayList<>();
        }

        SmsRecipient r = SmsRecipient.builder()
                .service(service)
                .number(number)
                .data(data)
                .comment(name)
                .build();

        smsRecipients.add(r);

        return this;
    }



    public ChangeBuilder withSensorsChannelNameAndTriggerValue(final int serial, final String name, final Boolean triggerEnable, final Double triggerValue) {
        if (sensors == null) {
            throw new NullPointerException("Method \"addSensor\" must be set first!");
        }
        Sensor s = sensors.stream().filter(sensor -> sensor.getSerial() == serial).findFirst().get();
        List<Channel> channelList = s.getChannels();
        channelList.add(Channel.builder().name(name).maxThresholdEnable(triggerEnable).maxThresholdValue(triggerValue).build());

        return this;
    }

    public ChangeBuilder addVibChannel() {
        if (vib == null) {
            throw new NullPointerException("Method \"givenVib\" must be set first!");
        }
        if (channels == null) {
            channels = new ArrayList<>();
        }
        vib.setChannels(channels);
        return this;
    }

    public ChangeBuilder thenChannelNameAndTriggerValue(final String name, final double triggerValue) {
        if (channels == null) {
            throw new NullPointerException("Method \"addChannel\" must be set first!");
        }
        channels.add(Channel.builder().name(name).triggerValue(triggerValue).build());
        return this;
    }

    public ChangeBuilder thenChannelNameAndTriggerValue(final String name, final Double triggerValue, final Boolean triggerEnable) {
        if (channels == null) {
            throw new NullPointerException("Method \"addChannel\" must be set first!");
        }
        channels.add(Channel.builder().name(name).triggerValue(triggerValue).triggerEnable(triggerEnable).build());
        return this;
    }

    public ChangeBuilder givenLogger() {
        logger = Logger.builder().build();
        return this;
    }

    public ChangeBuilder thenLoggerName(final String name) {
        if (logger == null) {
            throw new NullPointerException("Method \"givenLogger\" must be set first!");
        }
        logger.setName(name);
        return this;
    }

    public ChangeBuilder thenLoggerTimeZone(final String timezone) {
        if (logger == null) {
            throw new NullPointerException("Method \"givenLogger\" must be set first!");
        }
        logger.setTimezone(timezone);
        return this;
    }

    public ChangeBuilder thenLoggerSchedule(final Integer[] schedule) {
        if (logger == null) {
            throw new NullPointerException("Method \"givenLogger\" must be set first!");
        }
        logger.setUploadSchedule(schedule);
        return this;
    }

    public ChangeBuilder thenLoggerCommand(final Integer command) {
        if (logger == null) {
            throw new NullPointerException("Method \"givenLogger\" must be set first!");
        }
        logger.setCommand(command);
        return this;
    }

    public ChangeBuilder givenGps() {
        gps = Gps.builder().build();
        return this;
    }

    public ChangeBuilder thenGpsLongitude(final Double longitude) {
        if (gps == null) {
            throw new NullPointerException("Method \"givenGps\" must be set first!");
        }
        gps.setLongitude(longitude);
        return this;
}

    public ChangeBuilder thenGpsLatitude(final Double latitude) {
        if (gps == null) {
            throw new NullPointerException("Method \"givenGps\" must be set first!");
        }
        gps.setLatitude(latitude);
        return this;
    }

    public ChangeBuilder thenGpsAltitude(final Double altitude) {
        if (gps == null) {
            throw new NullPointerException("Method \"givenGps\" must be set first!");
        }
        gps.setAltitude(altitude);
        return this;
    }

    @Override
    public Device getProvider() {
        return device;
    }

    @Override
    public void setProvider(Device provider) {
        this.device = provider;
    }

    @Override
    public void build() {
        if (device == null) {
            device = new Device();
        }
        if (serial != null) {
            device.setSerial(serial);
        }
        if (calibrationDate != null) {
            device.setCalibrationDate(calibrationDate);
        }
        if (type != null) {
            device.setType(type);
        }
        if (description != null) {
            device.setDescription(description);
        }
        if (disabled != null) {
            device.setDisabled(disabled);
        }
        if (created != null) {
            device.setCreated(created);
        }
        if (lastCommunication != null) {
            device.setLastCommunication(lastCommunication);
        }
        if (configId != null) {
            device.setConfigId(configId);
        }
        if (vib != null) {
            device.setVib(vib);
        }
        if (logger != null) {
            device.setLogger(logger);
        }
        if (gps != null) {
            device.setGps(gps);
        }
        if (batteryVoltage != null) {
            device.setBatteryVoltage(batteryVoltage);
        }
        if (batteryLevel != null) {
            device.setBatteryLevel(batteryLevel);
        }
        if (monState != null) {
            device.setMonState(monState);
        }
        if (hash != null) {
            device.setHash(hash);
        }
        if (sensorsHash != null) {
            device.setSensorsHash(sensorsHash);
        }
        if (sensors != null) {
            device.setSensors(sensors);
        }
        if (smsRecipients != null) {
            device.setSmsRecipients(smsRecipients);
        }
    }

    @Override
    public String buildJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(device);
        } catch (JsonProcessingException e) {
            return e.getMessage();
        }
    }


}
