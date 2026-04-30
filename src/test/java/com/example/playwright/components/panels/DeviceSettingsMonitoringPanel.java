package com.example.playwright.components.panels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeviceSettingsMonitoringPanel  {

    // @NonNull
    PanelHeader panelHeader;

    NoticeItem committedNotice;

    // @NonNull
    Preface preface;

    Dropdown standardDropdown;
    Dropdown presetDropdown;    // C50
    Dropdown frequencyWeightingDropdown;        // C2x

    // @NonNull
    Dropdown intervalTimeDropdown;
    InputField preTrigTimeInputField;       // C2x, POINT
    InputField deviceIdInputField;      // Legacy sensors

    // @NonNull
    InputField postTrigTimeInputField;  // creator
    Dropdown postTrigTimeDropdown;      // legacy

    Dropdown octaveDropdown;    // C50
    Dropdown advLeqDropdown;   // C50
    ToggleField calculatedStatistics;   // C50
    InputField customStatistics;   // C50

    InputField customStatisticsValue;   // C50
    ToggleField customStatisticsTrigger;   // C50

    FieldWrapper timeslotHeaderWrapper; // C50

    private List<ChannelWrapper> channelWrappers;
    private List<ChannelWrapper> timeslotWrappers; // C50


    /**
     * @return '1A' from '(1A) SS 4604866 Spräng 250mm/s 5-300Hz'
     */
    @JsonIgnore
    public String getStandardNumber() {
        String fullStandardText = this.standardDropdown.getText();
        int start = fullStandardText.indexOf('(');
        int end = fullStandardText.indexOf(')', start + 1);

        if (start != -1 && end != -1 && end > start) {
            return fullStandardText.substring(start + 1, end);
        } else {
            throw new IllegalStateException("Could not get standard number from standard dropdown text");
        }
    }

    /**
     * A non-C50 has one Channel per ChannelWrapper
     * A C50 have two Channel per ChannelWrapper (Lmax, Leq) as the ChannelWrapper for C50 show timeslot name
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class ChannelWrapper {    // vue: Field set

        Icon leftIcon;
        String header;  // timeslotName, channelName

        // non-C50
        List<Channel> channels;

        // C50
        List<String> timeslotDuration;  // for custom timeslot

        InputField baseline;
        Dropdown accumulationSpan;  // for Acc. LAeq-settings
        Dropdown rollingWindow;     // for Rolling LAeq-settings


        public void addChannel(Channel channel) {
            if (channels == null) {
                channels = new ArrayList<>();
            }
            channels.add(channel);
        }

        @JsonIgnore
        public String getTimeslotTime() {
            return timeslotDuration.getFirst();
        }

        @JsonIgnore
        public List<String> getTimeslotDays() {
            // The first element is hours, eg '08:00 - 17:00'
            timeslotDuration.remove(0);

            // The rest are the days
            return timeslotDuration;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Channel {
        // C50
        String channelName;
        // All
        InputField inputField;
        ToggleField toggleField;

        /**
         * @return '0.4' from footer:'Min: 0.4, Max: 220'
         */
        @JsonIgnore
        public Double getMinRange() {
            return getMinorMaxValueFromTriggerValueFooter("Min: ", this.inputField.getFooter());
        }

        /**
         * @return '220.0' from footer:'Min: 0.4, Max: 220'
         */
        @JsonIgnore
        public Double getMaxRange() {
            return getMinorMaxValueFromTriggerValueFooter("Max: ", this.inputField.getFooter());
        }

        /**
         * Delimiter for min- and max-value is always a dot. Therefore we can use comma as string delimiter.
         * @param part  'Min: ' or 'Max: '
         * @param triggerValueFooter e.g., "Min: 0.001, Max: 0.75", "Min: 1, Max: 1000"
         */
        @JsonIgnore
        private double getMinorMaxValueFromTriggerValueFooter(String part, String triggerValueFooter) {
            int startIndex = triggerValueFooter.indexOf(part) + part.length();

            int endIndex = switch (part) {
                case "Min: " -> triggerValueFooter.indexOf(",", startIndex);
                case "Max: " -> triggerValueFooter.length();
                default -> throw new IllegalStateException("Unexpected part: " + part);
            };

            String value = triggerValueFooter.substring(startIndex, endIndex);
            return Double.parseDouble(value);
        }
    }
}
