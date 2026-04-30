package com.example.playwright.components.parts;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MenuCalendar {
    String month;
    String year;

    List<String> weekdays;
    List<String> daysInMonth;

    String quickSelectorFrom;
    String quickSelectorTo;
}
