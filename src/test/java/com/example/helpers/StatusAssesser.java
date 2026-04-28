package com.example.helpers;

import com.example.api.models.blast.Blast;
import com.example.api.models.device.Device;
import com.example.api.models.measuringpoint.MeasuringPoint;
import com.example.api.models.measuringpoint.Sensor;
import com.example.api.models.project.Project;
import com.example.api.models.user.User;
import com.example.playwright.enums.ColourSchema;
import com.example.playwright.enums.ProviderType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.example.helpers.StatusAssesser.Status.*;

public class StatusAssesser {

/*
Logic:
                                  | all_ok        | old_device    | old_mp        | old_project   | mp_off        | project_off   | device_off    | device_mp_off | device_project_off  | device_project_mp_off |
Actual:         ProjectDate       | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | OUTSIDE_DATES | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES        | INSIDE_DATES          |
Actual:         ProjectToggle     | ON            | ON            | ON            | ON            | ON            | OFF           | ON            | ON            | OFF                 | OFF                   |
Expected:       ProjectStatus     | ACTIVE        | ACTIVE        | ACTIVE        | INACTIVE      | ACTIVE        | INACTIVE      | ACTIVE        | ACTIVE        | INACTIVE            | INACTIVE              |

Actual:         MpDate            | INSIDE_DATES  | INSIDE_DATES  | OUTSIDE_DATES | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES        | INSIDE_DATES          |
Actual:         MpToggle          | ON            | ON            | ON            | ON            | OFF           | ON            | ON            | OFF           | ON                  | OFF                   |
Actual:         ConnDeviceDate    | INSIDE_DATES  | OUTSIDE_DATES | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES  | INSIDE_DATES        | INSIDE_DATES          |
Expected:       MpStatus          | ACTIVE        | INACTIVE      | INACTIVE      | ACTIVE        | INACTIVE      | ACTIVE        | ACTIVE        | INACTIVE      | ACTIVE              | INACTIVE              |

Actual:         DeviceMonState    | MONON         | MONON         | MONON         | MONON         | MONON         | MONON         | MONOFF        | MONOFF        | MONOFF              | MONOFF                |
Expected:       Device            | ACTIVE        | NOT_PRESENT   | NOT_PRESENT   | NOT_PRESENT   | NOT_PRESENT   | NOT_PRESENT   | INACTIVE      | NOT_PRESENT   | NOT_PRESENT         | NOT_PRESENT           |

*/

    /**
     * Status is an assessment on how various states (eg. time_frame, on/off- or in/active-toggle, monon/off) affects a device, mp, project, etc.
     * E.g., to deduct what state a mp is in in GUI, we can use leftIcon-colour, mainText-colour and monStatus-icon.
     * To deduct which state a mp should be in, we use ProjectDate, MpDate, MpToggle and ConnDeviceDate.
     */
    public enum Status {
        ACTIVE, INACTIVE, WARNING, NO_WARNING, NOT_PRESENT, PRESENT,        //map_icons and list colours
        DISABLED, NON_CLICKABLE, CLICKABLE, NO_ACCESS, ACCESS, INVISIBLE, FILLABLE, READONLY,
        ON, OFF,                                                //project, mp toggles
        MONON, MONOFF,                                          //mp, device mon_status
        INSIDE_DATES, OUTSIDE_DATES,                            //sensor, mp, project dates
        RUNNING, FINISHED,                                      //dataReport/search
        ONGOING,                                                // messageRule
        SHARED, SAVED, TEMPORARY,                               //dataReport
        PLANNED, PASSED, ABORTED,
        UNCOMMITTED, COMMITTED,                                 // device banners
        FUTURE, GREEN, UNSELECTED,                              // project overview empty mp-list, button
        UNCHECKED, MIXED, CHECKED                               // Checkboxes
    }

    // todo: merge with getStateByClassName?
    public static Status getStatusByClassName(String className) {
        if (className.contains("disabled")) {
            return DISABLED;
        } else if (className.contains("actionable")
                || className.contains("clickable")
                || className.contains("selectable")
                || className.contains("sortable")) {
            return CLICKABLE;
        } else {
            return DISABLED;
        }
    }

    public static Status getStateByClassName(String className) {

        if (className.contains("-active")) { // billing_report and data_report tab
            return ACTIVE;  // aka selected
        } else if (className.contains("-inactive") // billing_report tab
                || className.contains("-clickable")) {  // data_report tab
            return CLICKABLE;   // aka selectable
        } else if (className.contains("disabled")) {
            return DISABLED;
        } else if (className.contains("q-field row no-wrap items-start q-field--standard q-select q-field--auto-height q-select--with-input q-select--without-chips q-select--single q-field--float q-field--labeled")) {
            return CLICKABLE;   // Dropdown without pre-selection
        } else if (className.contains("q-field row no-wrap items-start q-field--standard q-select q-field--auto-height q-select--without-input q-select--without-chips q-select--single q-field--float q-field--labeled")) {
            return CLICKABLE;   // Dropdown with pre-selection
        } else if (className.equals("q-field row no-wrap items-start q-field--outlined q-select q-field--auto-height q-select--without-input q-select--without-chips q-select--single q-field--float q-field--dense quanity-select")) {
            return CLICKABLE;   // Regression chart quantity dropdown
        } else if (className.equals("q-field row no-wrap items-start q-field--filled q-select q-field--auto-height q-select--without-input q-select--without-chips q-select--single q-field--float q-field--dense menu-drop-down text-no-wrap")) {
            return CLICKABLE;   // Aside main-menu-field dropdown
        } else if (className.equals("q-field row no-wrap items-start q-field--outlined q-select q-field--auto-height q-select--without-input q-select--without-chips q-select--single q-field--float filter-field")) {
            return CLICKABLE;   // Transient table meta data dropdown
        } else if (className.equals("q-field row no-wrap items-start q-field--borderless q-select q-field--auto-height q-select--without-input q-select--without-chips q-select--single q-field--float q-field--dense q-table__select inline q-table__bottom-item")) {
            return CLICKABLE;   // Transient table footer dropdown
        } else {
            throw new IllegalArgumentException("Class name not supported: " + className);
        }
    }

    public static Status getCheckboxStatus(String ariaCheckedAttribute) {
        return ariaCheckedAttribute.equals("true")
                ? CHECKED
                : ariaCheckedAttribute.equals("mixed")
                    ? MIXED
                    : UNCHECKED;
    }

    public static Status getToggleStatus(String className) {
        return className.contains("disabled")
                ? DISABLED
                : CLICKABLE;
    }

    public static Status getDropdownStatus(String className) {
        return className.contains("disabled")
                ? DISABLED
                : CLICKABLE;
    }

    public static Status getInputFieldStatus(String className) {
        if (className.contains("readonly")) {
            return READONLY;
        }
        return className.contains("disabled")
                ? DISABLED
                : FILLABLE;
    }



    /**
     * Depending on Creator or Legacy a monitoring device (logger) has monitoring_state in either device.getMonState or
     * in device.Status.getWorkmode.
     * NB. That some devices has both monState and workmode, and that they can be contradictory. In those cases mon_state is correct.
     */
    public static Status assessDeviceMonitoringStatus(Device device) {

        Status deviceStatus = null;

        // Legacy
        if (device.getStatus() != null) {
            deviceStatus = device.getStatus().getWorkMode() == 2
                    ? MONON
                    : MONOFF;  // Monon == 2, all other is monoff
        } else if (device.getMonState() != null) {            // Creator
            // MonStateList is only one element
            Map<String, String> monState = device.getMonState().getFirst();
            // Get the highest timestamps value
            String lastValue = getValueFromTheLatestTimestamp(monState);
            System.out.println("lastValue: " + lastValue);
            deviceStatus = lastValue.equals("monon")
                    ? MONON
                    : MONOFF;
        } else {  // Some devices have neither monState nor Workmode
            deviceStatus = MONOFF;
        }

        return deviceStatus;
    }

    private static String getValueFromTheLatestTimestamp(Map<String, String> map) {
        // Find the entry with the highest key value
        Map.Entry<String, String> maxEntry = Collections.max(map.entrySet(), Comparator.comparingLong(e -> Long.parseLong(e.getKey())));

        // Get the value from the entry with the highest key value
        return maxEntry.getValue();
    }

    /**
     * Blasts are either in the past or the future.
     */
    public static Status assessBlastStatus(Blast blast) {
        return assessBlastDateState(blast.getDatetime());
    }

    public static Status assessProjectStatus(final Project project) {
        Status dateState = assessDateStatus(project.getDatetimeFrom(), project.getDatetimeTo());
        Status toggleState = project.getActive() ? ON : OFF;

        Status projectExpectedStatus = dateState.equals(INSIDE_DATES)
                && toggleState.equals(ON)
                ? ACTIVE
                : INACTIVE;

//        System.out.println("***********assessProjectStatus***********");
//        System.out.println("project id: " + project.getId());
//        System.out.println("project name: " + project.getName());
//        System.out.println("project dateState: " + dateState);
//        System.out.println("project toggleState: " + toggleState);
//        System.out.println("projectExpectedStatus = " + projectExpectedStatus);

        return projectExpectedStatus;
    }

    /**
     * A client.role_id=4 + client.isActive=false -> client_wo
     */
    public static Status assessUserStatus(final User user) {
        return user.getIsActive()
                ? ACTIVE
                : INACTIVE;
    }

    //works for context mp's, but not for ListObject-mp's. The latter has not much info.
    public static Status assessMpStatus(final MeasuringPoint mp) {
        Status dateState = assessDateStatus(mp.getDatetimeFrom(), mp.getDatetimeTo());
        Status toggleState = mp.getActive() ? ON : OFF;

        // todo: Vad händer vid framtida sensorer?
        Sensor sensor = getActiveOrFallbackSensor(mp.getSensors());

        Status sensorDateState = assessDateStatus(sensor.getDatetimeFrom(), sensor.getDatetimeTo());

        Status mpExpectedStatus = dateState.equals(INSIDE_DATES)
                && toggleState.equals(ON)
                && sensorDateState.equals(INSIDE_DATES)
                ? ACTIVE
                : INACTIVE;

//        System.out.println("***********assessMpStatus***********");
//        System.out.println("mp name: " + mp.getName());
//        System.out.println("mp dateState: " + dateState);
//        System.out.println("mp toggleState: " + toggleState);
//        System.out.println("mp sensorDateState: " + sensorDateState);
//        System.out.println("mpExpectedStatus = " + mpExpectedStatus);
//        System.out.println("************************************");

        return mpExpectedStatus;
    }

    /**
     * A mp can have multiple sensors.
     * Returns the sensor where 'now' is between datetime_from and datetime_to.
     * If none found, returns the one with the oldest datetime_to in the past.
     * If still none found, returns the one with the nearest datetime_from in the future.
     * Returns null if the list is empty or none qualify.
     * @return The sensor either currently used, or least old.
     */
    public static Sensor getActiveOrFallbackSensor(List<Sensor> sensors) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sensor oldestToSensor = null;
        Sensor nearestFromSensor = null;

        LocalDateTime oldestTo = null;
        LocalDateTime nearestFrom = null;

        for (Sensor sensor : sensors) {
            LocalDateTime from = LocalDateTime.parse(sensor.getDatetimeFrom(), formatter);
            LocalDateTime to = LocalDateTime.parse(sensor.getDatetimeTo(), formatter);

            // Level 1: Check if now is between from and to
            if (!now.isBefore(from) && !now.isAfter(to)) {
                return sensor;
            }

            // Level 2: Track the sensor with the oldest datetime_to in the past
            if (to.isBefore(now)) {
                if (oldestTo == null || to.isBefore(oldestTo)) {
                    oldestTo = to;
                    oldestToSensor = sensor;
                }
            }

            // Level 3: Track the sensor with the nearest datetime_from in the future
            if (from.isAfter(now)) {
                if (nearestFrom == null || from.isBefore(nearestFrom)) {
                    nearestFrom = from;
                    nearestFromSensor = sensor;
                }
            }
        }

        return oldestToSensor != null
                ? oldestToSensor
                : nearestFromSensor;
    }

    /**
     * Loops through the max five objects in mon_state.
     * @param device The device we want mon_state data from.
     * @return The mon_state for the last mon_change.
     */
    public static Status getDeviceCurrentMonStatus(final Device device) {
        int highestTimestamp = 0;
        String currentStatus = "";
        for (Map.Entry<String, String> pair : device.getMonState().getFirst().entrySet()) {
            int timeStamp = Integer.parseInt(pair.getKey());
            String value = pair.getValue();
            if (timeStamp > highestTimestamp) {
                highestTimestamp = timeStamp;
                currentStatus = value;
            }
        }
        return currentStatus.equals("monon") ? MONON : MONOFF;
    }

    /**
     * At some level of bad battery the warning on Device list item is lit + the device map icon is "warning".
     * Its unclear what percent governs the warning. In this method I've set warningBelowThisPercent to 5 %.
     * Feel free to change this.
     */
    public static Status getDeviceBatteryStatus(final Device device) {
        int highestTimestamp = 0;
        String batteryLevel = "";
        for (Map.Entry<String, String> pair : device.getBatteryLevel().getFirst().entrySet()) {
            int timeStamp = Integer.parseInt(pair.getKey());
            String value = pair.getValue();
            if (timeStamp > highestTimestamp) {
                highestTimestamp = timeStamp;
                batteryLevel = value;
            }
        }
        int warningBelowThisPercent = 5;
        return Integer.parseInt(batteryLevel) > warningBelowThisPercent ? NO_WARNING : WARNING;
    }

    /**
     *
     * @param device    Has to be device in CET
     * @return WARNING if the device has not communicated within the last 24 hours.
     */
    public static Status deductDeviceCommunicationStatus(Device device) {
        String timeZone = TimeConverter.deductTimezone(device.getTimezone());

        LocalDateTime lastCommunication = TimeConverter.fromTimestamp(device.getLastCommunication(), timeZone);
        System.out.println(lastCommunication);

        return (TimeConverter.isMoreThan24HoursBefore(lastCommunication, TimeConverter.getLocalDateTime(timeZone)))
                ? WARNING
                : NO_WARNING;
    }

    public static Status assessDeviceMapStatus(final Status projectStatus, final Status mpStatus, final Status connectedDeviceMonStatus, final Status connectedDeviceBatteryStatus, final Status connectedDeviceConnectionStatus) {
        // Unless the project or mp is inactive, then device status should not be found
        if (projectStatus.equals(INACTIVE) || mpStatus.equals(INACTIVE)) {
            return NOT_PRESENT;
        }

        // If device is in monoff, then device map should be gray
        if (connectedDeviceMonStatus.equals(MONOFF)) {
            return INACTIVE;
        } else if (connectedDeviceBatteryStatus.equals(WARNING) || connectedDeviceConnectionStatus.equals(WARNING)) {
            return WARNING;
        } else {
            return ACTIVE;
        }
    }

    public static Status assessDeviceListStatus(final Status projectStatus, final Status mpStatus, final Status connectedDeviceMonStatus) {
        // First assume the device's status is ACTIVE
        Status deviceStatus = ACTIVE;

        // If device is in monoff, then device map should be gray
        if (connectedDeviceMonStatus.equals(MONOFF)) {
            deviceStatus = INACTIVE;
        }
        // Unless the project or mp is inactive, then device status should not be found
        if (projectStatus.equals(INACTIVE) || mpStatus.equals(INACTIVE)) {
            deviceStatus = NOT_PRESENT;
        }

//        System.out.println("***********device map status***********");
//        System.out.println("project Status: " + projectStatus);
//        System.out.println("mp Status: " + mpStatus);
//        System.out.println("mp connectedDeviceStatus: " + connectedDeviceMonStatus);
//        System.out.println("device status = " + deviceStatus);
//        System.out.println("************************************");

        return deviceStatus;
    }

    public static Status assessDateStatus(final String fromThisDate, final String toThisDate) {
        LocalDateTime from = LocalDateTime.parse(fromThisDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime to = LocalDateTime.parse(toThisDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return from.isBefore(LocalDateTime.now()) && to.isAfter(LocalDateTime.now()) ? INSIDE_DATES : OUTSIDE_DATES;
    }

    public static Status assessBlastDateState(final String dateTime) {
        LocalDateTime dateTimeLDT = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return dateTimeLDT.isBefore(LocalDateTime.now()) ? PASSED : PLANNED;
    }

    public static Status assessAsideItemColour(ProviderType providerType, ColourSchema iconColour) {
            return switch (providerType) {
                case BLAST -> assessBlastItemColour(iconColour);
                case DEVICE -> assessDeviceListColour(iconColour);
                case DATA_REPORT -> assessDataReportItemColour(iconColour);
                case MEASURING_POINT -> assessMpItemIconColour(iconColour);
                case USER -> assessUserItemColour(iconColour);
                case PROJECT -> assessProjectItemColour(iconColour);
                case MESSAGE_RULE -> assessMrItemColour(iconColour);
                case OVERVIEW -> assessOverviewItemColour(iconColour);
                case COMMENT, BILLING_REPORT, DEVICE_INCL_BANNER, SCHEDULED_REPORT -> throw new IllegalArgumentException("Unsupported providerType: " + providerType);
            };
    }

    private static Status assessOverviewItemColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case PRIMARY -> ACTIVE;
            case POSITIVE -> GREEN;
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }

    private static Status assessBlastItemColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case LIGHT_BLUE -> PLANNED;
            case PRIMARY -> PASSED;
            case DISABLED -> PASSED;  // todo: remove this case when SSD-3013 is Done. Blast should only be passed or planned
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }

    private static Status assessDeviceListColour(ColourSchema iconColour) {
        if (iconColour != null) {
            return switch (iconColour) {
                case PRIMARY -> ACTIVE;
                case DISABLED -> INACTIVE;
                default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
            };
        } else {
            return null;
        }

    }

    private static Status assessDataReportItemColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case NEGATIVE -> ABORTED;
            case PRIMARY, SECONDARY -> FINISHED;
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }

    private static Status assessMrItemColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case PRIMARY -> ACTIVE;
            case DISABLED -> INACTIVE;
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }

    private static Status assessProjectItemColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case PRIMARY -> ACTIVE;
            case DISABLED -> INACTIVE;
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }

    // In List an MP is always ACTIVE, but in Table a Mp can be INACTIVE.
    private static Status assessMpItemIconColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case PRIMARY -> ACTIVE;
            case DISABLED -> INACTIVE;
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }

    /**
     * IN/ACTIVE represents colour in aside list/table, but also the access to InfraNET.
     * An INACTIVE user is a role:client, role_id:4, isActive:false, i.e., a client_without_access
     */
    private static Status assessUserItemColour(ColourSchema iconColour) {
        return switch (iconColour) {
            case PRIMARY -> ACTIVE;
            case DISABLED -> INACTIVE;
            default -> throw new IllegalStateException("Unexpected case value: " + iconColour);
        };
    }
}
