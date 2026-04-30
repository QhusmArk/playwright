package com.example.playwright.pageObjects;


import com.example.playwright.components.panels.device.DeviceDetailsPanel;
import com.example.playwright.components.parts.*;
import com.example.playwright.components.parts.panelParts.FieldWrapper;
import com.example.playwright.components.parts.panelParts.PanelHeader;
import com.example.playwright.components.parts.panelParts.Preface;
import com.example.playwright.helpers.enums.DeviceType;

import java.util.ArrayList;
import java.util.List;


public class DeviceDetailsPO extends CommonPO {

    public DeviceDetailsPanel getDeviceDetailsPanel(String type) {
        return getDeviceDetailsPanel();
    }

    public DeviceDetailsPanel getDeviceDetailsPanel() {
        DeviceDetailsPanel deviceDetailsPanel = new DeviceDetailsPanel();

        // PanelHeader
        PanelHeader panelHeader = getPanelHeader();
        deviceDetailsPanel.setPanelHeader(panelHeader);

        // PanelBody
        String panelPartPath = "//form[1]/div[@data-qa-id='panel'] //div[@data-qa-id='panel-body']/div";

        // First find out if the panel have notices like 'Manage uncommitted changes' or 'Device using an external power source'
        boolean hasNoticeItem = actions().countHowManyElements(panelPartPath) == 3;
        System.out.println("hasNoticeItem: " + hasNoticeItem);
        // The DOM inject 1 <div> when there is noticeItems
        if (hasNoticeItem) {
            List<NoticeItem> noticeItems = getDeviceDetailNoticeItems(panelPartPath + "[1]");
            deviceDetailsPanel.setNoticeItems(noticeItems);
        }

        // Preface
        Preface preface = getPreface();
        deviceDetailsPanel.setPreface(preface);

        DeviceType deviceType = panelHeader.deductDeviceTypeFromPanelHeader();

        // Details
        switch (deviceType) {
            case POINT -> {

                // POINT is fragile as 'unboxing', 'first contact' and 'no sensor connected' can lead to no details showing
                boolean hasDetails = actions().elementExistAndVisible(panelPartPath + "[last()]" + "/div/div", false, 0);

                if (hasDetails) {

                    String deviceDetailsPath = panelPartPath + "[last()]";
                    setLoggerDeviceDetailButtons(deviceDetailsPanel, deviceType, deviceDetailsPath);

                    // Add the button/settingsItem
                    SettingsItem settingsItem = getSettingsItemByDataQaId("company_device_general_settings"); // //div[@data-qa-id='panel-body']/div[3]/div/a
                    deviceDetailsPanel.setSettings(settingsItem);
                }
            }
            case C20, C22, C50, D10, MINI, MASTER, C10_LOGGER, C12_LOGGER -> {

                String deviceDetailsPath = panelPartPath + "[last()]";
                setLoggerDeviceDetailButtons(deviceDetailsPanel, deviceType, deviceDetailsPath);

                // Add the button/settingsItem
                SettingsItem settingsItem = getSettingsItemByDataQaId("company_device_general_settings"); // //div[@data-qa-id='panel-body']/div[3]/div/a
                deviceDetailsPanel.setSettings(settingsItem);

            }
            case VS10, VS12 -> {
                //todo: dessa har alltid 'No settings to configure'
            }
            default -> {    // Legacy sensors

                FieldWrapper monitoringWrapper = getSensorDeviceDetailsMonitoring(panelPartPath + "[3]");
                deviceDetailsPanel.setMonitoring(monitoringWrapper);

                List<FieldWrapper> channelWrappers = getSensorDeviceDetailsChannels(panelPartPath + "[3]");
                deviceDetailsPanel.setChannels(channelWrappers);
            }
        }

        // Get the connected sensor panel that cabel'd loggers have
        switch (deviceType) {
            // POINT is fragile as 'unboxing', 'first contact' and 'no sensor connected' can lead to no details showing
            case POINT, D10, MINI, MASTER, C10_LOGGER, C12_LOGGER -> {
                DeviceDetailsPanel.SensorPanel sensorPanel = getSensorPanel(deviceType);
                deviceDetailsPanel.setSensorPanel(sensorPanel);
            }
        }

        return deviceDetailsPanel;
    }

    private FieldWrapper getSensorDeviceDetailsMonitoring(String panelPartPath) {
        FieldWrapper monitoring = getFieldWrapperCommonPartsByHeader("Monitoring");

        Button settings = getButton(panelPartPath + "/div/div //button");
        monitoring.addButton(settings);

        String monitoringPartsPath = panelPartPath + "/div[1]/div/div[2]/label";

        int monInfoPartsCount = actions().countHowManyElements(monitoringPartsPath);

        for (int p = 1; p <= monInfoPartsCount; p++) {

            String partPath = monitoringPartsPath + "["+p+"]";

            InputField monitoringInfoPart = completeGetInputField(partPath);
            monitoring.addContent(monitoringInfoPart);

        }

        return monitoring;
    }

    private List<FieldWrapper> getSensorDeviceDetailsChannels(String panelPartPath) {
        List<FieldWrapper> channels = new ArrayList<>();

        int channelsCount = actions().countHowManyElements(panelPartPath + "/div");

        // Start at '2' to avoid the ever present Monitoring FieldWrapper
        for (int c = 2; c <= channelsCount; c++) {
            // We cannot use getFieldWrapperCommonParts() as we do not know which channels will be present
            FieldWrapper channel = new FieldWrapper();

            String channelPath = panelPartPath + "/div["+c+"]";

            Icon wrapperIcon = completeGetIcon(channelPath);
            channel.setLeftIcon(wrapperIcon);

            String wrapperHeader = actions().findOneElementsText(channelPath + "/div/div[1]");
            channel.setHeader(wrapperHeader);

            InputField highTrigger = completeGetInputField(channelPath + "/div/div[2]/label");
            channel.addContent(highTrigger);

            channels.add(channel);
        }

        return channels;
    }

    // There has been D10 wo form[2], perhaps bc never any sensor?
    private DeviceDetailsPanel.SensorPanel getSensorPanel(DeviceType type) {
        DeviceDetailsPanel.SensorPanel sensorPanel = new DeviceDetailsPanel.SensorPanel();

        String connectedSensorPath = "//form[2]";

        boolean hasSensorsPanel = actions().elementExistAndVisible(connectedSensorPath, false, 0);
        System.out.println("hasSensorsPanel: " + hasSensorsPanel);
        if (hasSensorsPanel) {
            String sensorPanelHeader = actions().findOneElementsText(connectedSensorPath + "//div[@class='text-subheading q-ma-md']");
            sensorPanel.setHeader(sensorPanelHeader);

            Table sensorTable = getConnectedSensorsTable(connectedSensorPath);
            sensorPanel.setSensorsTable(sensorTable);
        }

        return sensorPanel;
    }

    private Table getConnectedSensorsTable(String connectedSensorPath) {
        Table connectedSensorTable = new Table();

        Table.TableRow headerRow = getConnectedSensorsTableHeader(connectedSensorPath);
        connectedSensorTable.setHeader(headerRow);

        List<Table.TableRow> connectedSensorRows = getConnectedSensorRows(headerRow, connectedSensorPath);
        connectedSensorTable.setContent(connectedSensorRows);

        return connectedSensorTable;

    }

    private List<Table.TableRow> getConnectedSensorRows(Table.TableRow headerRow, String connectedSensorPath) {
        List<Table.TableRow> rows = new ArrayList<>();

        int sensorRowCount = actions().countHowManyElements(connectedSensorPath + " //tbody/tr");
        int columnCount = headerRow.getObjects().size();

        for (int r = 1; r <= sensorRowCount; r++) {
            Table.TableRow sensorRow = new Table.TableRow();

            String sensorRowPath = connectedSensorPath + " //tbody/tr["+r+"]";

            for (int c = 1; c <= columnCount; c++) {
                String cellPath = sensorRowPath + "/td["+c+"]";

                switch (c) {
                    case 1 -> {
                        Table.TableCell tableCell = new Table.TableCell();

                        Icon leftIcon = completeGetIcon(cellPath + "/div/div[1]");
                        tableCell.addCellIcon(leftIcon);

                        String text = actions().findOneElementsText(cellPath + "/div/div[2]");
                        tableCell.addCellText(text);

                        sensorRow.addContent(tableCell);
                    }
                    default -> {
                        // POINT undefined sensor has no 'Calibration date'
                        boolean hasText = actions().elementExistAndVisible(cellPath, false, 0);
                        if (hasText) {
                            String text = actions().findOneElementsText(cellPath);
                            sensorRow.addContent(text);
                        } else {
                            sensorRow.addContent(null);
                        }
                    }
                }
                rows.add(sensorRow);
            }
        }
        return rows;
    }

    private Table.TableRow getConnectedSensorsTableHeader(String connectedSensorPath) {
        Table.TableRow headerRow = new Table.TableRow();
        int columnCount = actions().countHowManyElements(connectedSensorPath + " //thead/tr/th");

        for (int c = 1; c <= columnCount; c++) {
            String header = actions().findOneElementsText(connectedSensorPath + " //thead/tr/th["+c+"]");
            headerRow.addContent(header);
        }
        return headerRow;
    }

//    private void setLoggerDeviceDetailButtons(DeviceDetailsPanel panel, DeviceType type, String deviceDetailsPath) {
//        switch (type) {
//            case C20, C22, C50 -> setCreatorCompactDetailButtons(panel, deviceDetailsPath);
//            case POINT -> setPointDetailButtons(panel, deviceDetailsPath);
//            case D10 -> setD10Details(panel, deviceDetailsPath);
//            case MINI, MASTER -> setIMDetails(panel, deviceDetailsPath);
//            case C10_LOGGER, C12_LOGGER -> setCompactDetailButtons(panel, deviceDetailsPath);
//            default -> throw new IllegalArgumentException("Unsupported type: " + type);
//        }
//    }
//
//    private void setCompactDetailButtons(DeviceDetailsPanel panel, String deviceDetailsPath) {
//        String detailsPath = deviceDetailsPath + "/div/div/div/div";
//
//        Button monStatus = getDeviceDetailsButton(detailsPath + "[1]/div");
//        panel.setMonStatusButton(monStatus);
//
//        Button lastReadButton = getDeviceDetailsButton(detailsPath + "[2]/*");
//        panel.setLastReadButton(lastReadButton);
//
//        Button batteryButton = getDeviceDetailsButton(detailsPath + "[3]/a");
//        panel.setBatteryButton(batteryButton);
//
//        Button gsmButton = getDeviceDetailsButton(detailsPath + "[4]/a");
//        panel.setGsmButton(gsmButton);
//
//        Button projectButton = getDeviceDetailsButton(detailsPath + "[5]/a");
//        panel.setProjectButton(projectButton);
//
//        Button notificationButton = getDeviceDetailsButton(detailsPath + "[6]/a");
//        panel.setNotificationButton(notificationButton);
//
//        Button tempButton = getDeviceDetailsButton(detailsPath + "[7]/a");
//        panel.setTemperatureButton(tempButton);
//
//        Button dataButton = getDeviceDetailsButton(detailsPath + "[8]/div");
//        panel.setDataButton(dataButton);
//
//        Button favButton = getDeviceDetailsButton(detailsPath + "[9]/div");
//        panel.setFavouriteButton(favButton);
//    }
//
//    private void setIMDetails(DeviceDetailsPanel panel, String deviceDetailsPath) {
//        String detailsPath = deviceDetailsPath + "/div/div/div/div";
//
//        Button monStatus = getDeviceDetailsButton(detailsPath + "[1]/div");
//        panel.setMonStatusButton(monStatus);
//
//        Button lastReadButton = getDeviceDetailsButton(detailsPath + "[2]/*");
//        panel.setLastReadButton(lastReadButton);
//
//        Button batteryButton = getDeviceDetailsButton(detailsPath + "[3]/a");
//        panel.setBatteryButton(batteryButton);
//
//        Button gsmButton = getDeviceDetailsButton(detailsPath + "[4]/a");
//        panel.setGsmButton(gsmButton);
//
//        Button projectButton = getDeviceDetailsButton(detailsPath + "[5]/div");
//        panel.setProjectButton(projectButton);
//
//        Button notificationButton = getDeviceDetailsButton(detailsPath + "[6]/a");
//        panel.setNotificationButton(notificationButton);
//
//        Button dataButton = getDeviceDetailsButton(detailsPath + "[7]/div");
//        panel.setDataButton(dataButton);
//
//        Button favButton = getDeviceDetailsButton(detailsPath + "[8]/div");
//        panel.setFavouriteButton(favButton);
//    }
//
//    private void setD10Details(DeviceDetailsPanel panel, String deviceDetailsPath) {
//        String detailsPath = deviceDetailsPath + "/div/div/div/div";
//
//        Button monStatus = getDeviceDetailsButton(detailsPath + "[1]/div");
//        panel.setMonStatusButton(monStatus);
//
//        Button lastReadButton = getDeviceDetailsButton(detailsPath + "[2]/*");
//        panel.setLastReadButton(lastReadButton);
//
//        Button batteryButton = getDeviceDetailsButton(detailsPath + "[3]/a");
//        panel.setBatteryButton(batteryButton);
//
//        Button gsmButton = getDeviceDetailsButton(detailsPath + "[4]/a");
//        panel.setGsmButton(gsmButton);
//
//        Button projectButton = getDeviceDetailsButton(detailsPath + "[5]/div");
//        panel.setProjectButton(projectButton);
//
//        Button notificationButton = getDeviceDetailsButton(detailsPath + "[6]/a");
//        panel.setNotificationButton(notificationButton);
//
//        Button tempButton = getDeviceDetailsButton(detailsPath + "[7]/a");
//        panel.setTemperatureButton(tempButton);
//
//        Button dataButton = getDeviceDetailsButton(detailsPath + "[8]/div");
//        panel.setDataButton(dataButton);
//
//        Button favButton = getDeviceDetailsButton(detailsPath + "[9]/div");
//        panel.setFavouriteButton(favButton);
//    }
//
//
//    private void setCreatorCompactDetailButtons(DeviceDetailsPanel panel, String deviceDetailsPath) {
//        String detailsPath = deviceDetailsPath + "/div/div/div/div";
//
//        Button monStatus = getDeviceDetailsButton(detailsPath + "[1]/div");
//        panel.setMonStatusButton(monStatus);
//
//        Button lastReadButton = getDeviceDetailsButton(detailsPath + "[2]/*");
//        panel.setLastReadButton(lastReadButton);
//
//        Button batteryButton = getDeviceDetailsButton(detailsPath + "[3]/a");
//        panel.setBatteryButton(batteryButton);
//
//        Button gsmButton = getDeviceDetailsButton(detailsPath + "[4]/a");
//        panel.setGsmButton(gsmButton);
//
//        Button projectButton = getDeviceDetailsButton(detailsPath + "[5]/a");
//        panel.setProjectButton(projectButton);
//
//        Button notificationButton = getDeviceDetailsButton(detailsPath + "[6]/a");
//        panel.setNotificationButton(notificationButton);
//
//        Button humidityButton = getDeviceDetailsButton(detailsPath + "[7]/a");
//        panel.setHumidityButton(humidityButton);
//
//        Button tempButton = getDeviceDetailsButton(detailsPath + "[8]/a");
//        panel.setTemperatureButton(tempButton);
//
//        Button dataButton = getDeviceDetailsButton(detailsPath + "[9]/div");
//        panel.setDataButton(dataButton);
//
//        Button favButton = getDeviceDetailsButton(detailsPath + "[10]/div");
//        panel.setFavouriteButton(favButton);
//    }
//
//    private void setPointDetailButtons(DeviceDetailsPanel panel, String deviceDetailsPath) {
//        String detailsPath = deviceDetailsPath + "/div/div/div/div";
//
//        Button monStatus = getDeviceDetailsButton(detailsPath + "[1]/div");
//        panel.setMonStatusButton(monStatus);
//
//        Button lastReadButton = getDeviceDetailsButton(detailsPath + "[2]/*");
//        panel.setLastReadButton(lastReadButton);
//
//        Button batteryButton = getDeviceDetailsButton(detailsPath + "[3]/a");
//        panel.setBatteryButton(batteryButton);
//
//        Button gsmButton = getDeviceDetailsButton(detailsPath + "[4]/a");
//        panel.setGsmButton(gsmButton);
//
//        Button projectButton = getDeviceDetailsButton(detailsPath + "[5]/div");
//        panel.setProjectButton(projectButton);
//
//        Button notificationButton = getDeviceDetailsButton(detailsPath + "[6]/a");
//        panel.setNotificationButton(notificationButton);
//
//        Button humidityButton = getDeviceDetailsButton(detailsPath + "[7]/a");
//        panel.setHumidityButton(humidityButton);
//
//        Button tempButton = getDeviceDetailsButton(detailsPath + "[8]/a");
//        panel.setTemperatureButton(tempButton);
//
//        Button dataButton = getDeviceDetailsButton(detailsPath + "[9]/div");
//        panel.setDataButton(dataButton);
//
//        Button favButton = getDeviceDetailsButton(detailsPath + "[10]/div");
//        panel.setFavouriteButton(favButton);
//    }


    private void setLoggerDeviceDetailButtons(DeviceDetailsPanel panel, DeviceType type, String deviceDetailsPath) {
        String base = deviceDetailsPath + "/div/div/div/div";

        switch (type) {
            case C20, C22, C50 -> setCreatorCompactDetailButtons(panel, base);
            case POINT -> setPointDetailButtons(panel, base);
            case D10 -> setD10Details(panel, base);
            case MINI, MASTER -> setIMDetails(panel, base);
            case C10_LOGGER, C12_LOGGER -> setCompactDetailButtons(panel, base);
            default -> throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    // Helper to fetch a button using index and suffix
    private Button btn(String detailsPath, int index, String suffix) {
        return getDeviceDetailsButton(detailsPath + "[" + index + "]" + suffix);
    }

    /**
     * COMPACT
     */
    private void setCompactDetailButtons(DeviceDetailsPanel panel, String detailsPath) {
        panel.setMonStatusButton(btn(detailsPath,       1, "/div"));
        panel.setLastReadButton(btn(detailsPath,        2, "/*"));
        panel.setBatteryButton(btn(detailsPath,         3, "/a"));
        panel.setGsmButton(btn(detailsPath,             4, "/a"));
        panel.setProjectButton(btn(detailsPath,         5, "/a"));
        panel.setNotificationButton(btn(detailsPath,    6, "/a"));
        panel.setTemperatureButton(btn(detailsPath,     7, "/a"));
        panel.setDataButton(btn(detailsPath,            8, "/div"));
        panel.setFavouriteButton(btn(detailsPath,       9, "/div"));
    }

    /**
     * MINI, MASTER
     */
    private void setIMDetails(DeviceDetailsPanel panel, String detailsPath) {
        panel.setMonStatusButton(btn(detailsPath,       1, "/div"));
        panel.setLastReadButton(btn(detailsPath,        2, "/*"));
        panel.setBatteryButton(btn(detailsPath,         3, "/a"));
        panel.setGsmButton(btn(detailsPath,             4, "/a"));
        panel.setProjectButton(btn(detailsPath,         5, "/div"));
        panel.setNotificationButton(btn(detailsPath,    6, "/a"));
        panel.setDataButton(btn(detailsPath,            7, "/div"));
        panel.setFavouriteButton(btn(detailsPath,       8, "/div"));
    }

    /**
     * D10
     */
    private void setD10Details(DeviceDetailsPanel panel, String detailsPath) {
        panel.setMonStatusButton(btn(detailsPath,       1, "/div"));
        panel.setLastReadButton(btn(detailsPath,        2, "/*"));
        panel.setBatteryButton(btn(detailsPath,         3, "/a"));
        panel.setGsmButton(btn(detailsPath,             4, "/a"));
        panel.setProjectButton(btn(detailsPath,         5, "/div"));
        panel.setNotificationButton(btn(detailsPath,    6, "/a"));
        panel.setTemperatureButton(btn(detailsPath,     7, "/a"));
        panel.setDataButton(btn(detailsPath,            8, "/div"));
        panel.setFavouriteButton(btn(detailsPath,       9, "/div"));
    }

    /**
     * C2X, C50
     */
    private void setCreatorCompactDetailButtons(DeviceDetailsPanel panel, String detailsPath) {
        panel.setMonStatusButton(btn(detailsPath,       1, "/div"));
        panel.setLastReadButton(btn(detailsPath,        2, "/*"));
        panel.setBatteryButton(btn(detailsPath,         3, "/a"));
        panel.setGsmButton(btn(detailsPath,             4, "/a"));
        panel.setProjectButton(btn(detailsPath,         5, "/a"));
        panel.setNotificationButton(btn(detailsPath,    6, "/a"));
        panel.setHumidityButton(btn(detailsPath,        7, "/a"));
        panel.setTemperatureButton(btn(detailsPath,     8, "/a"));
        panel.setDataButton(btn(detailsPath,            9, "/div"));
        panel.setFavouriteButton(btn(detailsPath,       10, "/div"));
    }

    /**
     * POINT
     */
    private void setPointDetailButtons(DeviceDetailsPanel panel, String detailsPath) {
        panel.setMonStatusButton(btn(detailsPath,       1, "/div"));
        panel.setLastReadButton(btn(detailsPath,        2, "/*"));
        panel.setBatteryButton(btn(detailsPath,         3, "/a"));
        panel.setGsmButton(btn(detailsPath,             4, "/a"));
        panel.setProjectButton(btn(detailsPath,         5, "/div"));
        panel.setNotificationButton(btn(detailsPath,    6, "/a"));
        panel.setHumidityButton(btn(detailsPath,        7, "/a"));
        panel.setTemperatureButton(btn(detailsPath,     8, "/a"));
        panel.setDataButton(btn(detailsPath,            9, "/div"));
        panel.setFavouriteButton(btn(detailsPath,       10, "/div"));
    }

}
