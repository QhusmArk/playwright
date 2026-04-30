package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The expandable charts header that contain information about which mp's that have data, or not.
 * /project/10523/views/0fa0d451-7e05-4c55-bf50-e64235d9029f/intervals/chart
 */
// todo: kan DataViewStatus ersättas med ChartSectionHeader + listor i ChartSectionHeader?
@Getter
@Setter
@NoArgsConstructor
public class DataViewStatus {

    private ChartSectionHeader chartSectionHeader;

    private List<String> dataPresentList;
    private List<String> dataNotPresentList;
}
