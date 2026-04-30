package com.example.playwright.components.parts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A TimeFrame is the combination of from/start- and/or to/end-time plus the until_further_notice-toggle.
 *      Possible combinations:
 *      FromTime + toggle:ON
 *      FromTime + ToTime + toggle:OFF
 *      ToTime + toggle:OFF (mp bulk)
 *      Toggle:ON (mp bulk)
 */
@Getter
@Setter
@NoArgsConstructor
public class TimeFrame {

    private TimeInterval fromDate;
    private TimeInterval fromTime;
    private MenuCalendar fromMenuCalendar;

    private TimeInterval toDate;
    private TimeInterval toTime;
    private MenuCalendar toMenuCalendar;

    private Boolean untilFurtherNoticeToggle;

    @JsonIgnore
    public void setUntilFurtherNoticeToggle(Boolean untilFurtherNoticeToggle) {
        this.untilFurtherNoticeToggle = untilFurtherNoticeToggle;

        // Boolean.TRUE.equals is for avoiding NullPointer
        if ((toDate != null || toTime != null) && Boolean.TRUE.equals(untilFurtherNoticeToggle)) {
            throw new IllegalStateException("untilFurtherNoticeToggle cannot be true if toDate/toTime exist.");
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TimeInterval {
        private String header;
        private String value;
        private String footer;    // timezone, warning or information
    }
}
