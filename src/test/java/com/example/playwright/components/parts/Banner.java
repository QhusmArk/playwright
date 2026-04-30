package com.example.playwright.components.parts;

import com.example.playwright.helpers.enums.ColourSchema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Placeholder object for GUI-parts like '@1 of 1 measuring points have data' in IntervalChartReport, or status information in DeviceList/Table
 * The tag that is a Banner has bg-information, and text in either the tag, or a child-tag.
 *
 * Or at least I thought so. It seems that bg-light-red for RegressionAnalysis is added programmatically, and not set in the DOM.
 * So color is not to be captured until there is a workaround.
 */
@Data
@NoArgsConstructor
public class Banner  {

    String text;
    ColourSchema colour;
    Dropdown dropdown; // eg. Regression analysis chart
}
