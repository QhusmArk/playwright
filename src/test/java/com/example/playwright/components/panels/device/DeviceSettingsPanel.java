package com.example.playwright.components.panels.device;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.playwright.components.parts.NoticeItem;
import com.example.playwright.components.parts.SettingsItem;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.helpers.enums.IconType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeviceSettingsPanel  {

    PanelHeader panelHeader;
    List<NoticeItem> noticeItems;
    Preface preface;

    SettingsItem monitoring;
    SettingsItem uploadSchedule;
    SettingsItem serviceMessage;
    SettingsItem location;
    SettingsItem timeZone;
    SettingsItem advancedSettings;
    SettingsItem remoteOverrides;
    SettingsItem disableDevice;

    // As can be found in DeviceDetailsPanel
    @JsonIgnore
    public NoticeItem getNoticeItem(String text) {
        return (noticeItems == null)
                ? null
                : noticeItems.stream()
                .filter(noticeItem -> noticeItem.getText().contains(text))
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public NoticeItem getNoticeItem(IconType iconType) {
        return (noticeItems == null)
                ? null
                : noticeItems.stream()
                .filter(noticeItem -> noticeItem.getLeftIcon().getType().equals(iconType))
                .findFirst()
                .orElse(null);
    }

}
