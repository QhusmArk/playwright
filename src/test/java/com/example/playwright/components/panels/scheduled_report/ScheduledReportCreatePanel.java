package com.example.playwright.components.panels.scheduled_report;

import com.example.playwright.components.parts.Tab;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ScheduledReportCreatePanel is either of these three panels:
 * ScheduledReportGeneralPanel
 * ScheduledReportMpPanel
 * ScheduledReportRecipientsPanel
 * + a row of Tabs (General, Measuring Points, Recipients)
 */
@Getter
@Setter
@NoArgsConstructor
public class ScheduledReportCreatePanel {

    Tab generalTab;
    Tab measuringPointsTab;
    Tab recipientsTab;

    // One of the following
    ScheduledReportGeneralPanel generalPanel;
    ScheduledReportMpPanel mpPanel;
    ScheduledReportRecipientsPanel recipientsPanel;
}
