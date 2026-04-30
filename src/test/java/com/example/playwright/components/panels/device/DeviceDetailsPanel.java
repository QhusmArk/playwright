package com.example.playwright.components.panels.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.playwright.components.parts.Button;
import com.example.playwright.components.parts.NoticeItem;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.Table;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.helpers.enums.IconType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
public class DeviceDetailsPanel  {

    PanelHeader panelHeader;
    List<NoticeItem> noticeItems;
    Preface preface;

    Button monStatusButton;
    Button lastReadButton;
    Button batteryButton;
    Button gsmButton;
    Button projectButton;
    Button notificationButton;
    Button humidityButton;
    Button temperatureButton;
    Button dataButton;
    Button favouriteButton;

    SettingsItem settings;              // Loggers

    FieldWrapper monitoring;            // VS-, Legacy-sensors
    List<FieldWrapper> channels;

    private SensorPanel sensorPanel;        // D10, IM, COMPACT, POINT

    public List<Button> getDeviceDetails() {
        // Collect non-null buttons into a list
        return Stream.of(
                        monStatusButton,
                        lastReadButton,
                        batteryButton,
                        gsmButton,
                        projectButton,
                        notificationButton,
                        humidityButton,
                        temperatureButton,
                        dataButton,
                        favouriteButton
                )
                .filter(Objects::nonNull)
                .toList();
    }

    // As can be found in DeviceSettingsPanel
    @JsonIgnore
    public NoticeItem getNoticeItem(String text) {
        return (noticeItems == null)
                ? null
                : noticeItems.stream()
                .filter(noticeItem -> noticeItem.getText().contains(text))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return null if list is null or do not contain matching noticeitem, else a matching noticeitem
     */
    @JsonIgnore
    public NoticeItem getNoticeItem(IconType iconType) {
        return (noticeItems == null)
                ? null
                : noticeItems.stream()
                .filter(noticeItem -> noticeItem.getLeftIcon().getType().equals(iconType))
                .findFirst()
                .orElse(null);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SensorPanel  {
        private String header;
        Table sensorsTable;
    }
}
