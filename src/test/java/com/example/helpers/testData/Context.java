package com.example.helpers.testData;

import com.example.api.endpoints.DeviceApi;
import com.example.api.models.ScheduledReport;
import com.example.api.models.agenda.Agenda;
import com.example.api.models.blast.Blast;
import com.example.api.models.comment.Comment;
import com.example.api.models.device.Device;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.measuringpoint.Sensor;
import com.example.api.models.message.MessageRule;
import com.example.api.models.message.NotificationMpValue;
import com.example.api.models.project.Project;
import com.example.api.models.report.DataReport;
import com.example.api.models.report.Search;
import com.example.api.models.user.User;
import com.example.playwright.config.DeviceProperties;
import com.example.playwright.enums.DeviceType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.playwright.enums.DeviceType.D10;
import static com.example.playwright.enums.DeviceType.POINT;

@Data
public class Context {

    // Providers created in TestDataBuilder
    private Project project;
    private List<Agenda> agendas;
    private List<Blast> blasts;
    private List<Comment> comments;
    private List<Search> searches;
    private List<DataReport> reports;
    private List<MeasuringPoint> measuringPoints;
    private List<MessageRule> messageRules;
    private List<NotificationMpValue> notificationMpValues;
    private List<User> users;
    private List<ScheduledReport> scheduledReports;

    // Providers created for ease of use
    private Set<Device> devices;

    public void addAgenda(final Agenda agenda) {
        if (agendas == null) {
            agendas = new ArrayList<>();
        }
        agendas.add(agenda);
    }

    public void addSearch(final Search search) {
        if (searches == null) {
            searches = new ArrayList<>();
        }
        searches.add(search);
    }

    public void addReport(final DataReport report) {
        if (reports == null) {
            reports = new ArrayList<>();
        }
        reports.add(report);
    }

    public Agenda getLastAgenda() {
        return agendas.getLast();
    }

    public void deleteAgenda(final int index) {
        agendas.remove(index);
    }

    public void replaceAgenda(final int index, final Agenda agenda) {
        if (agendas == null) {
            agendas = new ArrayList<>();
        }
        agendas.set(index, agenda);
    }

    public void addBlast(final Blast blast) {
        if (blasts == null) {
            blasts = new ArrayList<>();
        }
        blasts.add(blast);
    }

    public Blast getLastBlast() {
        return blasts.getLast();
    }

    public void deleteBlast(final int index) {
        blasts.remove(index);
    }

    public void replaceBlast(final int index, final Blast blast) {
        if (blasts == null) {
            blasts = new ArrayList<>();
        }
        blasts.set(index, blast);
    }

    public void addComment(final Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }

    public Comment getLastComment() {
        return comments.getLast();
    }

    public void deleteComment(final int index) {
        comments.remove(index);
    }

    public void replaceComments(final int index, final Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.set(index, comment);
    }

    public Search getLastSearch() {
        return searches.getLast();
    }

    public void addDevice(final Device device) {
        if (devices == null) {
            devices = new HashSet<>();
        }
        devices.add(device);
    }

    public List<Device> getDevicesAsList() {
        return new ArrayList<>(devices);
    }

    public void deleteDevice(final Device deviceToRemove) {
        devices.remove(deviceToRemove);
    }

    public void replaceDevice(final Device deviceToRemove, final Device deviceToAdd) {
        try {
            devices.remove(deviceToRemove);
            devices.add(deviceToAdd);
        } catch (Exception e) {
            System.out.println("Could not remove or add device.");
        }
    }

    public void addMeasuringPoint(final MeasuringPoint measuringPoint) {
        if (measuringPoints == null) {
            measuringPoints = new ArrayList<>();
        }
        measuringPoints.add(measuringPoint);
    }

    public MeasuringPoint getLastMeasuringPoint() {
        return measuringPoints.getLast();
    }

    public void deleteMeasuringPoint(final int index) {
        measuringPoints.remove(index);
    }

    public void replaceMeasuringPoint(final int index, final MeasuringPoint measuringPoint) {
        if (measuringPoints == null) {
            measuringPoints = new ArrayList<>();
        }
        measuringPoints.set(index, measuringPoint);
    }

    public void addMessageRule(final MessageRule messageRule) {
        if (messageRules == null) {
            messageRules = new ArrayList<>();
        }
        messageRules.add(messageRule);
    }

    public MessageRule getLastMessageRule() {
        return messageRules.getLast();
    }

    public void deleteMessageRule(final int index) {
        messageRules.remove(index);
    }

    public void replaceMessageRule(final int index, final MessageRule messageRule) {
        if (messageRules == null) {
            messageRules = new ArrayList<>();
        }
        messageRules.set(index, messageRule);
    }

    public void addNotificationMpValue(final NotificationMpValue notificationMpValue) {
        if (notificationMpValues == null) {
            notificationMpValues = new ArrayList<>();
        }
        notificationMpValues.add(notificationMpValue);
    }

    public void deleteNotificationMpValue(final int index) {
        notificationMpValues.remove(index);
    }

    public void addUser(final User user) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(user);
    }

    public User getLastUser() {
        return users.getLast();
    }

    public void deleteUser(final int index) {
        users.remove(index);
    }

    public void replaceUser(final int index, final User user) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.set(index, user);
    }

    public void replaceSearch(int index, Search search) {
        if (search == null) {
            searches = new ArrayList<>();
        }
        searches.set(index, search);
    }

    public void addScheduledReport(ScheduledReport sdr) {
        if (scheduledReports == null) {
            scheduledReports = new ArrayList<>();
        }
        scheduledReports.add(sdr);
    }

    /**
     *
     * @param type A sensor type, e.g., "C22", "V12".
     * @return the first measuring points, that has a sensor of matching type, id.
     */
    public String getMeasuringPointIdForThisType(final String type) {
        List<MeasuringPoint> mps = this.measuringPoints;

        // Find the Mp that uses 'type' as sensor
        MeasuringPoint mpWithThisSensor = mps.stream()
                .filter(mp -> mp.getSensorType().equals(type))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("No matching mp found for " + type)
                );

        return String.valueOf(mpWithThisSensor.getId());
    }

    public void storeMeasuringPointDevice(final MeasuringPoint measuringPoint) {
        if (measuringPoints != null) {
            //To ease access to devices used in Mp's, add them to context().
            List<String> mpSensorsSerial = measuringPoint.getSensors().stream()             // for each mp
                    .map(Sensor::getSerial)                                     // get the sensor serial
                    .toList();

            mpSensorsSerial.forEach(sensorSerial -> {
                com.example.api.models.device.Sensor deviceSensor = DeviceApi.getDeviceSensor(sensorSerial);
                DeviceType type = DeviceType.valueOf(deviceSensor.getType());
                Device device = DeviceApi.getDevice(type, sensorSerial);

                if (!type.isCommunicatingDevice()) {    //S50, VS12, V12, etc.
                    if (type.getFamily().equals("LEGACY")) {
                        Device logger = DeviceApi.getDevice("D10", DeviceProperties.getConnectedSerial(D10));
                        this.addDevice(logger);
                    } else if (type.getFamily().equals("CREATOR")) {
                        Device logger = DeviceApi.getDevice("POINT", DeviceProperties.getConnectedSerial(POINT));
                        this.addDevice(logger);
                    } else {
                        System.out.println("Unknown device type: " + type);
                    }
                }

                this.addDevice(device);
            });
        }
    }

    /**
     * Store all devices used in measuring points.
     * If mp.sensor is cabled, then add both the mp.sensor and the logger device.
     */
    public void storeMeasuringPointDevices() {
        if (measuringPoints != null) {
            //To ease access to devices used in Mp's, add them to context().
            List<String> mpSensorsSerial = measuringPoints.stream()             // for each mp
                    .flatMap(mp -> mp.getSensors().stream())      // for each sensor in the mp
                    .map(Sensor::getSerial)                                     // get the sensor serial
                    .toList();

            mpSensorsSerial.forEach(sensorSerial -> {
                com.example.api.models.device.Sensor deviceSensor = DeviceApi.getDeviceSensor(sensorSerial);
                DeviceType type = DeviceType.valueOf(deviceSensor.getType());
                Device device = DeviceApi.getDevice(type, sensorSerial);
                // Add the mp.sensor device to the context
                this.addDevice(device);

                if (!type.isCommunicatingDevice()) {    //S50, VS12, V12, etc.
                    if (type.getFamily().equals("LEGACY")) {
                        Device logger = DeviceApi.getDevice("D10", DeviceProperties.getConnectedSerial(D10));
                        this.addDevice(logger);
                    } else if (type.getFamily().equals("CREATOR")) {
                        Device logger = DeviceApi.getDevice("POINT", DeviceProperties.getConnectedSerial(POINT));
                        this.addDevice(logger);
                    } else {
                        System.out.println("Unknown device type: " + type);
                    }
                }
            });
        }
    }
}
